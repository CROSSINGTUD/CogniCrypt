package de.cognicrypt.codegenerator.primitive.wizard;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * This class is responsible for displaying the methods related to the custom algorithm For instance, in case of primitive of type symmetric block cipher, the required methods are
 * encryption an decryption.
 * 
 * @author Ahmed Ben Tahar
 */

public class MethodSelectorPage extends WizardPage {

	private Label question;
	private File javaFile;

	public MethodSelectorPage(File file) {
		super("Methods Selector");
		setTitle("Methods Selector");
		setDescription("Getting methods-related algorithm");
		this.javaFile = file;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);

		question = new Label(container, SWT.NULL);
		question.setBounds(10, 33, 214, 21);
		question.setText("Please select the encryption method");

		Combo combo = new Combo(container, SWT.NONE);
		combo.setBounds(10, 60, 188, 23);

		Label label = new Label(container, SWT.NONE);
		label.setText("Please select the decryption method");
		label.setBounds(10, 123, 214, 21);

		Combo combo_1 = new Combo(container, SWT.NONE);
		combo_1.setBounds(10, 150, 188, 23);
		Class cls;
		try {
			cls = loadClass(this.javaFile.getPath());
			combo_1.add(cls.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	public Class loadClass( String ClassFolder) throws Exception {
		return null;
//		URLClassLoader loader = new URLClassLoader(new URL []{
//			new URL("file://"+this.javaFile)
//		});
//		  String className = je.getName().substring(0,je.getName().length()-6);
//		    className = className.replace('/', '.');
//		    Class c = cl.loadClass(className);
//		return loader.getClass();
	}
}
