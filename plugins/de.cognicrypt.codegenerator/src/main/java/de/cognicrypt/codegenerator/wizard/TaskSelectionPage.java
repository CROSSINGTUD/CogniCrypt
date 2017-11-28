/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Ram Kamath
 *
 */
package de.cognicrypt.codegenerator.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.wizard.PrimitiveIntegrationWizard;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.Labels;
import de.cognicrypt.codegenerator.utilities.Utils;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button advancedModeCheckBox;
	private Label selectTaskLabel;
	private IProject selectedProject = null;

	public TaskSelectionPage() {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TASK_LIST);
		setDescription(Labels.DESCRIPTION_TASK_SELECTION_PAGE);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {

		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 200, 300);
		final GridLayout layout = new GridLayout(2, false);
		//layout.numColumns = 4;
		this.container.setLayout(layout);

		this.selectTaskLabel = new Label(this.container, SWT.NONE);
		this.selectTaskLabel.setText(Constants.SELECT_JAVA_PROJECT);

		ComboViewer projectComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		projectComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		Map<String, IProject> javaProjects = new HashMap<String, IProject>();
		for (IProject project : Utils.createListOfJavaProjectsInCurrentWorkspace()) {
			javaProjects.put(project.getName(), project);
		}

		if (javaProjects.isEmpty()) {
			String[] errorMessage = { Constants.ERROR_MESSAGE_NO_PROJECT };
			projectComboSelection.setInput(errorMessage);
			projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
		} else {
			projectComboSelection.setInput(javaProjects.keySet().toArray());
			projectComboSelection.addSelectionChangedListener(event -> {
				final IStructuredSelection selected = (IStructuredSelection) event.getSelection();
				this.selectedProject = javaProjects.get((String) selected.getFirstElement());
				projectComboSelection.refresh();

			});

			IProject currentProject = Utils.getCurrentProject();
			if (currentProject == null) {
				projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
			} else {
				projectComboSelection.setSelection(new StructuredSelection(currentProject.getName()));
			}
		}

		this.selectTaskLabel = new Label(this.container, SWT.NONE);
		this.selectTaskLabel.setText(Constants.SELECT_TASK);

		this.taskComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		final List<Task> tasks = TaskJSONReader.getTasks();

		this.taskComboSelection.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object task) {
				if (task instanceof Task) {
					final Task current = (Task) task;
					return current.getDescription();
				}
				return super.getText(task);
			}
		});

		this.taskComboSelection.setInput(tasks);

		this.taskComboSelection.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			final Task selectedTask = (Task) selection.getFirstElement();

			TaskSelectionPage.this.taskComboSelection.refresh();
			setPageComplete(selectedTask != null && this.selectedProject != null);
		});

		this.taskComboSelection.setSelection(new StructuredSelection(tasks.get(0)));

		this.advancedModeCheckBox = new Button(this.container, SWT.CHECK);
		this.advancedModeCheckBox.setText(Constants.ADVANCED_MODE);
		this.advancedModeCheckBox.setSelection(false);
		setControl(this.container);

		Button btnPrimitiveIntegration = new Button(container, SWT.NONE);
		btnPrimitiveIntegration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				 WizardDialog wizardDialog = new WizardDialog(parent.getShell(),new  PrimitiveIntegrationWizard());
		           if (wizardDialog.open() == Window.CANCEL) {
		               System.out.println("Ok pressed");
		           } else {
		               System.out.println("Cancel pressed");
		           }
			}
		});
		btnPrimitiveIntegration.setText("Primitive Integration");
		new Label(container, SWT.NONE);

	
	}
	 
	


	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	public Task getSelectedTask() {
		return (Task) ((IStructuredSelection) this.taskComboSelection.getSelection()).getFirstElement();
	}

	/**
	 * Helper method to UI , this flag decides the second page of the wizard
	 *
	 * @return
	 */
	public boolean isAdvancedMode() {
		return this.advancedModeCheckBox.getSelection();
	}
}
