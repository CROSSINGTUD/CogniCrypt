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
package crossing.e1.featuremodel.clafer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.clafer.ast.AstConcreteClafer;

public class StringLabelMapper {
	private static Map<String, AstConcreteClafer> task = null;
	private static Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> properties = null;
	private static Map<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>> groupProperties = null;

	private StringLabelMapper() {

	}

	public static Map<String, AstConcreteClafer> getTaskLabels() {
		if (task == null) {
			task = new HashMap<String, AstConcreteClafer>();
		}
		return task;
	}

	public static Map<AstConcreteClafer, ArrayList<AstConcreteClafer>> getPropertyLabels() {
		if (properties == null) {
			properties = new HashMap<AstConcreteClafer, ArrayList<AstConcreteClafer>>();
		}
		return properties;
	}

	public static void resetProperties() {
		properties = null;
	}

	public static Map<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>> getGroupProperties() {
		if (groupProperties == null) {
			groupProperties = new HashMap<AstConcreteClafer, Map<ArrayList<AstConcreteClafer>, Integer>>();
		}
		return groupProperties;
	}

	public static void resetGroupProperties() {
		StringLabelMapper.groupProperties = null;
	}

}
