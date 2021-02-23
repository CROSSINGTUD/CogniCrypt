/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.io.File;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.wizard.TaskIntegratorWizardPage;

public class FileBrowserComposite extends Composite {

	private TaskIntegratorWizardPage wizardPage; // this is needed to set whether the page has been completed yet or not.
	private ControlDecoration decFilePath; // Decoration variable to be able to access it in the events.

	private Listener onFileChangedListener;

	private Text pathText;
	private Text optionalText;

	public FileBrowserComposite(final Composite parent, final int style, final String labelText,
			final String[] fileTypes, final String stringOnFileDialog, final TaskIntegratorWizardPage wizardPage,
			final Listener listener) {

		this(parent, style, labelText, fileTypes, stringOnFileDialog, wizardPage);
		this.onFileChangedListener = listener;
	}

	/**
	 * Pass the file types that need to be selected, and the string that needs to be
	 * displayed. Pass null in the fileTypes if you wish to select a directory.
	 *
	 * @param parent
	 * @param style
	 * @param labelText
	 * @param fileTypes
	 * @param stringOnDialog
	 * @param wizardPage
	 */
	public FileBrowserComposite(Composite parent, int style, String labelText, String[] fileTypes,
			String stringOnDialog, TaskIntegratorWizardPage wizardPage) {
		super(parent, style);

		init(parent, style, labelText, fileTypes, stringOnDialog, wizardPage);
	}

	public FileBrowserComposite(Composite parent, int style, String labelText, String[] fileTypes,
			String stringOnDialog, TaskIntegratorWizardPage theContainerpageForValidation,
			TaskInformationComposite comp) {
		super(parent, style);

		init(parent, style, labelText, fileTypes, stringOnDialog, theContainerpageForValidation);
	}

	private void init(final Composite parent, final int style, final String labelText, final String[] fileTypes,
			final String stringOnDialog, final TaskIntegratorWizardPage theContainerpageForValidation) {
		setTheLocalContainerPage(theContainerpageForValidation);
		final GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 8;
		setLayout(gridLayout);

		final Label label = new Label(this, SWT.NONE);
		label.setText(labelText);

		// Initialize the decorator for the label for the text box.
		setDecFilePath(new ControlDecoration(label, SWT.TOP | SWT.RIGHT));
		getDecFilePath().setShowOnlyOnFocus(false);

		// Initial error state.
		getDecFilePath().setImage(Constants.DEC_ERROR);
		getDecFilePath().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_BLANK_FILE_NAME);
		getDecFilePath().showHoverText(getDecFilePath().getDescriptionText());

		this.pathText = new Text(this, SWT.BORDER);
		final GridData gdTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);

		// do not claim space for all the text if not available
		gdTextBox.widthHint = 500;
		this.pathText.setLayoutData(gdTextBox);
		this.optionalText = new Text(this, SWT.BORDER);
		final GridData gdTextBoxOption = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);

		// do not claim space for all the text if not available
		gdTextBoxOption.widthHint = 100;
		this.optionalText.setLayoutData(gdTextBoxOption);
		if (!labelText.equals(Constants.WIDGET_DATA_LOCATION_OF_CRYSLTEMPLATE_FILE)) {
			this.optionalText.setVisible(false);
		}
		final Button browseButton = new Button(this, SWT.NONE);
		browseButton.setText(Constants.LABEL_BROWSE_BUTTON);

		browseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				String selectedPath = "";
				// If null is passed in the file types, the directory selection dialog will be
				// displayed.
				if (fileTypes == null) {
					selectedPath = openDirectoryDialog(stringOnDialog);
					if (selectedPath != null) {
						FileBrowserComposite.this.pathText.setText(selectedPath);
						if (FileBrowserComposite.this.onFileChangedListener != null) {
							FileBrowserComposite.this.onFileChangedListener.handleEvent(new Event());
						}
					}
				} else {
					selectedPath = openFileDialog(fileTypes, stringOnDialog);
					if (selectedPath != null) {
						FileBrowserComposite.this.pathText.setText(selectedPath);
					}
				}

			}
		});

		pathText.addModifyListener(e -> {

			File locationOfCryslTemplate = new File(pathText.getText());
			final File tempFileVariable = locationOfCryslTemplate;
			// Validate the file IO. The directory check is removed.
			if ((!tempFileVariable.exists() || !tempFileVariable.canRead()) && pathText.getParent().isVisible()) {//
				getDecFilePath().setImage(Constants.DEC_ERROR);
				getDecFilePath().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_UNABLE_TO_READ_FILE);
				getDecFilePath().showHoverText(getDecFilePath().getDescriptionText());
				// Check if the page can be set to completed.
				getTheLocalContainerPage().checkPageComplete();
			} else {
				// If there are no problems with the file, revert the error decoration and store
				// the locations.
				getDecFilePath().setImage(null);
				getDecFilePath().setDescriptionText("");
				getDecFilePath().showHoverText("");
				switch (labelText) {
				case Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE:
					IntegratorModel.getInstance().setLocationOfJSONFile(tempFileVariable);
					break;
				case Constants.WIDGET_DATA_LOCATION_OF_PNG_FILE:
					IntegratorModel.getInstance().setLocationOfIconFile(tempFileVariable);
					break;
				}

				// Check if the page can be set to completed.
				getTheLocalContainerPage().checkPageComplete();
			}
		});
	}

	/**
	 * Open the file dialog and return the file path as a string.
	 *
	 * @param fileTypes
	 * @param stringOnFileDialog
	 * @return The path selected
	 */
	private String openFileDialog(final String[] fileTypes, final String stringOnFileDialog) {
		final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(fileTypes);
		fileDialog.setText(stringOnFileDialog);
		return fileDialog.open();
	}

	/**
	 * Open the directorDialog for the custom library, and return the path.
	 *
	 * @param stringOnDirectoryDialog
	 * @return the selected path.
	 */
	private String openDirectoryDialog(final String stringOnDirectoryDialog) {
		final DirectoryDialog directoryDialog = new DirectoryDialog(getShell(), SWT.OPEN);
		directoryDialog.setText(stringOnDirectoryDialog);
		return directoryDialog.open();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Return the container wizard page object.
	 *
	 * @return the theLocalContainerPage
	 */
	public TaskIntegratorWizardPage getTheLocalContainerPage() {
		return this.wizardPage;
	}

	/**
	 * This object is required to set the completion of the page for the mode
	 * selection page behavior.
	 *
	 * @param theLocalContainerPage the theLocalContainerPage to set
	 */
	public void setTheLocalContainerPage(final TaskIntegratorWizardPage theLocalContainerPage) {
		this.wizardPage = theLocalContainerPage;
	}

	public String getPathText() {
		return this.pathText.getText();
	}

	public String getOptionalText() {
		return optionalText.getText();
	}

	/**
	 * @return the decNameOfTheTask
	 */
	public ControlDecoration getDecFilePath() {
		return this.decFilePath;
	}

	/**
	 * Keep the decorator object as global to allow access in the event listeners.
	 *
	 * @param decNameOfTheTask the decNameOfTheTask to set
	 */
	private void setDecFilePath(final ControlDecoration decFilePath) {
		this.decFilePath = decFilePath;
	}

	/**
	 * Change Path Option.
	 * 
	 * @param text String to set ID
	 */
	public void setPathText(String text) {
		pathText.setText(text);
	}

	/**
	 * Change Identifier Option.
	 * 
	 * @param text String to set ID
	 */
	public void setOptionalText(String text) {
		optionalText.setText(text);
	}
}