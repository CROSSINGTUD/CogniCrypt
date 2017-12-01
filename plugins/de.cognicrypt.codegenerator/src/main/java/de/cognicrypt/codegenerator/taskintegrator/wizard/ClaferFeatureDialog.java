package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.Constants.FeatureType;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldSmallerUIElements;

public class ClaferFeatureDialog extends TitleAreaDialog {

	private Text txtFeatureName;
	private CompositeToHoldSmallerUIElements featuresComposite;
	private CompositeToHoldSmallerUIElements constraintsComposite;
	private Button btnRadioAbstract;
	private Button btnRadioConcrete;

	private Label lblInheritance;
	private Combo comboInheritance;

	private ClaferFeature resultClafer;
	private ArrayList<ClaferFeature> otherClaferFeatures;


	public ClaferFeatureDialog(Shell parentShell, ClaferFeature modifiableClaferFeature, ArrayList<ClaferFeature> listOfExistingClaferFeatures) {
		super(parentShell);
		setShellStyle(SWT.CLOSE);

		resultClafer = modifiableClaferFeature;

		otherClaferFeatures = new ArrayList<ClaferFeature>(listOfExistingClaferFeatures.size());

		// get abstract Clafer features that have already been created
		// exclude the feature currently being modified
		for (ClaferFeature cfr : listOfExistingClaferFeatures) {
			if (cfr.getFeatureType() == Constants.FeatureType.ABSTRACT && !cfr.equals(resultClafer)) {
				otherClaferFeatures.add(cfr);
			}
		}

		create();
	}

	public ClaferFeatureDialog(Shell shell, ArrayList<ClaferFeature> listOfExistingClaferFeatures) {
		this(shell, new ClaferFeature(FeatureType.ABSTRACT, "", ""), listOfExistingClaferFeatures);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));

		setTitle("Variability modeling");
		setMessage("Message");

		new Label(container, 0);
		new Label(container, 0);

		Label lblType = new Label(container, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Select the type");

		btnRadioAbstract = new Button(container, SWT.RADIO);
		btnRadioAbstract.setSelection(true);
		btnRadioAbstract.setText("Class");

		btnRadioAbstract.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				resultClafer.setFeatureType(FeatureType.ABSTRACT);
				validate();
				super.widgetSelected(e);
			}
		});

		btnRadioConcrete = new Button(container, SWT.RADIO);
		btnRadioConcrete.setText("Instance");
		btnRadioConcrete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				resultClafer.setFeatureType(FeatureType.CONCRETE);
				validate();
				super.widgetSelected(e);
			}
		});

		if (resultClafer.getFeatureType() == FeatureType.ABSTRACT) {
			btnRadioAbstract.setSelection(true);
			btnRadioConcrete.setSelection(false);
		} else {
			btnRadioAbstract.setSelection(false);
			btnRadioConcrete.setSelection(true);
		}

		Label lblFeatureName = new Label(container, SWT.NONE);
		lblFeatureName.setText("Type in the name");

		txtFeatureName = new Text(container, SWT.BORDER);
		txtFeatureName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtFeatureName.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				resultClafer.setFeatureName(txtFeatureName.getText());
				super.focusLost(e);
			}
		});

		txtFeatureName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				validate();
			}
		});

		txtFeatureName.setText(resultClafer.getFeatureName());

		lblInheritance = new Label(container, SWT.NONE);
		lblInheritance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		comboInheritance = new Combo(container, SWT.NONE);
		comboInheritance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		// add existing abstract features to inheritance combo
		for (ClaferFeature cfr : otherClaferFeatures) {
			comboInheritance.add(cfr.getFeatureName().toString());
		}

		comboInheritance.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				resultClafer.setFeatureInheritance(comboInheritance.getText());
				super.focusLost(e);
			}
		});

		comboInheritance.setText(resultClafer.getFeatureInheritance());

		Button btnAddProperty = new Button(container, SWT.NONE);
		btnAddProperty.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addClaferProperty();
			}
		});
		btnAddProperty.setText("Add property");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		featuresComposite = new CompositeToHoldSmallerUIElements(container, SWT.NONE, resultClafer.getfeatureProperties(), true, otherClaferFeatures);
		featuresComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		featuresComposite.setMinHeight(200);

		Button btnAddConstraint = new Button(container, SWT.NONE);
		btnAddConstraint.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addClaferConstraint();
			}
		});
		btnAddConstraint.setText("Add constraint");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		constraintsComposite = new CompositeToHoldSmallerUIElements(container, SWT.NONE, resultClafer.getFeatureConstraints(), true, otherClaferFeatures);
		constraintsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		constraintsComposite.setMinHeight(200);

		validate();

		return container;
	}

	private void validate() {
		if (txtFeatureName.getText().contains(" ")) {
			setMessage("The name must not contain any spaces", IMessageProvider.WARNING);
			if (getButton(IDialogConstants.OK_ID) != null) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}

		} else if (txtFeatureName.getText().isEmpty()) {
			setMessage("Please enter a name", IMessageProvider.WARNING);
			if (getButton(IDialogConstants.OK_ID) != null) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		} else {
			setMessage(null);
			if (getButton(IDialogConstants.OK_ID) != null) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		}

		if (lblInheritance != null) {
			if (btnRadioAbstract.getSelection()) {
				lblInheritance.setText("Inherits from");

			} else if (btnRadioConcrete.getSelection()) {
				lblInheritance.setText("Implements");
			}
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 700);
	}

	private void addClaferProperty() {
		FeatureProperty featureProperty = new FeatureProperty("", "");
		featuresComposite.addFeatureProperty(featureProperty, true, otherClaferFeatures);
		resultClafer.setFeatureProperties(featuresComposite.getFeatureProperties());
	}

	private void addClaferConstraint() {
		ClaferConstraintDialog cfrConstraintDialog = new ClaferConstraintDialog(getShell(), resultClafer, otherClaferFeatures);

		// blocking call to Dialog.open() the dialog
		// it returns 0 on success
		if (cfrConstraintDialog.open() == 0) {
			constraintsComposite.addFeatureConstraint(cfrConstraintDialog.getResult(), true);
		}
	}

	@Override
	protected void okPressed() {
		resultClafer.setFeatureName(txtFeatureName.getText());
		resultClafer.setFeatureInheritance(comboInheritance.getText());
		super.okPressed();
	}

	public ClaferFeature getResult() {
		// remove empty properties and constraints
		featuresComposite.getFeatureProperties().removeIf(featureProp -> featureProp.getPropertyName().equals("") && featureProp.getPropertyType().equals(""));
		constraintsComposite.getFeatureConstraints().removeIf(constraint -> constraint.getConstraint().equals(""));

		resultClafer.setFeatureProperties(featuresComposite.getFeatureProperties());
		resultClafer.setFeatureConstraints(constraintsComposite.getFeatureConstraints());

		return resultClafer;
	}
}
