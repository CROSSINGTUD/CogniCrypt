package de.cognicrypt.codegenerator.taskintegrator.models;

import de.cognicrypt.codegenerator.Constants;

public class XSLAttribute {
	
	private String XSLAttributeName;	
	private String XSLAttributeData;
	
	/**
	 * For consistency, the name for XSLAttribute should come from the Array of String for the attributes associated to the {@code XSLTags} in {@link Constants} class.
	 * 
	 * @param xSLAttributeName
	 *        the name of the XSLAttribute.
	 * @param xSLAttributeData
	 *        the value for the XSLAttribute.
	 */
	public XSLAttribute(String xSLAttributeName, String xSLAttributeData) {
		super();
		setXSLAttributeName(xSLAttributeName);
		setXSLAttributeData(xSLAttributeData);
	}
	
	/**
	 * @return the name of the XSLAttribute.
	 */
	public String getXSLAttributeName() {
		return XSLAttributeName;
	}
	
	/**
	 * @param xSLAttributeName
	 *        set the name of the XSLAttribute. For consistency, the name for XSLAttribute should come from the Array of String for the attributes associated to the {@code XSLTags}
	 *        in {@link Constants} class.
	 * 
	 */
	public void setXSLAttributeName(String xSLAttributeName) {
		XSLAttributeName = xSLAttributeName;
	}
	
	/**
	 * @return the XSLAttribute value.
	 */
	public String getXSLAttributeData() {
		return XSLAttributeData;
	}
	
	/**
	 * @param xSLAttributeData
	 *        set the value for the XSLAttribute.
	 */
	public void setXSLAttributeData(String xSLAttributeData) {
		XSLAttributeData = xSLAttributeData;
	}
	


}
