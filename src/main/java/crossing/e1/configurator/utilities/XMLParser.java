/**
 * Copyright 2015 Technische Universität Darmstadt
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
package crossing.e1.configurator.utilities;

import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.instance.InstanceClafer;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.featuremodel.clafer.ClaferModelUtils;
import crossing.e1.featuremodel.clafer.PropertiesMapperUtil;

/**
 * @author Ram
 *
 */
public class XMLParser implements Labels {
	/**
	 *
	 * @param inst
	 * @param value
	 * @return
	 */
	public String displayInstanceValues(final InstanceClafer inst, String value) {
		InstanceClafer childInstance = null;

		if (inst.hasChildren()) {
			childInstance = (InstanceClafer) inst.getChildren()[0].getRef();
			final String taskName = childInstance.getType().getName();
			value = "<Task description=\"" + ClaferModelUtils.removeScopePrefix(taskName) + "\">\n";

		} else {
			value = "<Task>\n";
		}
		if (childInstance != null && childInstance.hasChildren()) {
			for (final InstanceClafer in : childInstance.getChildren()) {
				if (!in.getType().getRef().getTargetType().isPrimitive()) {
					value += "<" + Constants.ALGORITHM + " type=\""
							+ ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getName())
							+ "\"> \n";
					value += displayInstanceXML(in, "");
					value += "</" + Constants.ALGORITHM + "> \n";
				} else {
					value += displayInstanceXML(in, "");
				}
			}

		}
		value += "</Task>";
		return value;
	}

	/**
	 *
	 * @param inst
	 * @param value
	 * @return
	 */
	public String displayInstanceXML(final InstanceClafer inst, String value) {
		try {
			if (inst.getType().hasRef()) {
				if (getSuperClaferName(inst.getType().getRef().getTargetType())) {
					System.out.println("YES => " + inst);
				}
			}
			if (inst.hasChildren()) {
				for (final InstanceClafer in : inst.getChildren()) {
					value += displayInstanceXML(in, "");
				}

			} else if (inst.hasRef() && inst.getType().isPrimitive() != true
					&& inst.getRef().getClass().toString().contains(Constants.INTEGER) == false
					&& inst.getRef().getClass().toString().contains(Constants.STRING) == false
					&& inst.getRef().getClass().toString().contains(Constants.BOOLEAN) == false) {
				value += displayInstanceXML((InstanceClafer) inst.getRef(), "");
			} else if(PropertiesMapperUtil.getenumMap().keySet().contains(inst.getType().getSuperClafer())){
				
				if(inst.hasRef()){
					// For group properties
					return "\t<" + ClaferModelUtils.removeScopePrefix(inst.getType().getSuperClafer().getName()) + ">"
							+ ClaferModelUtils.removeScopePrefix(inst.getType().toString()).replace("\"", "") + "</"
							+ ClaferModelUtils.removeScopePrefix(inst.getType().getSuperClafer().getName()) + ">\n";
				}else{
					//enums that don't have a reference type (e.g., Mode, Padding etc)
					
					return "\t<" + ClaferModelUtils.removeScopePrefix(inst.getType().getSuperClafer().getName()) + ">"
					+ ClaferModelUtils.removeScopePrefix(inst.getType().toString()).replace("\"", "") + "</"
					+ ClaferModelUtils.removeScopePrefix(inst.getType().getSuperClafer().getName()) + ">\n";
				}
			} else {
				if (inst.hasRef()) {
					return "\t<" + ClaferModelUtils.removeScopePrefix(inst.getType().getName()) + ">"
							+ inst.getRef().toString().replace("\"", "") + "</"
							+ ClaferModelUtils.removeScopePrefix(inst.getType().getName()) + ">\n";
				} else {
					return "\t<"
							+ ClaferModelUtils
									.removeScopePrefix(((AstConcreteClafer) inst.getType()).getParent().getName())
							+ ">"
							+ ClaferModelUtils.removeScopePrefix(inst.getType().getName()) + "</" + ClaferModelUtils
									.removeScopePrefix(((AstConcreteClafer) inst.getType()).getParent().getName())
							+ ">\n";
				}

			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		return value;
	}

	/**
	 *
	 * @param inst
	 * @param value
	 * @return
	 */
	public String getInstanceProperties(final InstanceClafer inst, String value) {
		InstanceClafer instan = null;

		if (inst.hasChildren()) {
			instan = (InstanceClafer) inst.getChildren()[0].getRef();
		}
		if (instan != null && instan.hasChildren()) {
			for (final InstanceClafer in : instan.getChildren()) {
				if (!in.getType().getRef().getTargetType().isPrimitive()) {
					value += Constants.ALGORITHM + " :"
							+ ClaferModelUtils.removeScopePrefix(in.getType().getRef().getTargetType().getName())
							+ Constants.lineSeparator;
					value += getInstancePropertiesDetails(in, "");
					value += Constants.lineSeparator;
				} else {
					value += getInstancePropertiesDetails(in, "");
				}
			}

		}

		return value;
	}

	/**
	 *
	 * @param inst
	 * @param value
	 * @return
	 */
	public String getInstancePropertiesDetails(final InstanceClafer inst, String value) {
		try {
			// if (inst.getType().hasRef()) {
			// if (getSuperClaferName(inst.getType().getRef().getTargetType()))
			// {
			//
			// }
			// }
			if (inst.hasChildren()) {
				for (final InstanceClafer in : inst.getChildren()) {
					value += getInstancePropertiesDetails(in, "");
				}

			} else if (inst.hasRef() && inst.getType().isPrimitive() != true
					&& inst.getRef().getClass().toString().contains(Constants.INTEGER) == false
					&& inst.getRef().getClass().toString().contains(Constants.STRING) == false
					&& inst.getRef().getClass().toString().contains(Constants.BOOLEAN) == false) {
				value += getInstancePropertiesDetails((InstanceClafer) inst.getRef(), "");
			} else if(PropertiesMapperUtil.getenumMap().keySet().contains(inst.getType().getSuperClafer())){
				
				if(inst.hasRef()){
					// For group properties
					return "\t"+ClaferModelUtils.removeScopePrefix(inst.getType().getSuperClafer().getName()) + ":"
							+ ClaferModelUtils.removeScopePrefix(inst.getType().toString()).replace("\"", "")
							+ Constants.lineSeparator;
				}else{
					//enums that don't have a reference type (e.g., Mode, Padding etc)
					return "\t"
						+ ClaferModelUtils.removeScopePrefix(
								((AstConcreteClafer) inst.getType()).getSuperClafer().getName())
						+ " : " + ClaferModelUtils.removeScopePrefix(inst.getType().getName())
						+ Constants.lineSeparator;
				}
			} else {
				if (inst.hasRef()) {
					return "\t" + ClaferModelUtils.removeScopePrefix(inst.getType().getName()) + " : "
							+ inst.getRef().toString().replace("\"", "") + Constants.lineSeparator;
				} else {
					return "\t"
							+ ClaferModelUtils.removeScopePrefix(
									((AstConcreteClafer) inst.getType()).getParent().getName())
							+ " : " + ClaferModelUtils.removeScopePrefix(inst.getType().getName())
							+ Constants.lineSeparator;
				}

			}
		} catch (final Exception e) {
			Activator.getDefault().logError(e);
		}
		return value;
	}

	/**
	 *
	 * @param astClafer
	 * @return
	 */
	private boolean getSuperClaferName(final AstClafer astClafer) {
		if (astClafer.getSuperClafer() != null) {
			getSuperClaferName(astClafer.getSuperClafer());
		}
		if (astClafer.getName().contains("_" + Constants.ALGORITHM)) {
			return true;
		}
		return false;

	}
}
