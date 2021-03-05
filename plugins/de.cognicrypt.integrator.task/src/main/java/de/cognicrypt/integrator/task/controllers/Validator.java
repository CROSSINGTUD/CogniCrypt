/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class Validator {

	/*
	 * Returns true if @taskName has already been added
	 */
	public static boolean checkIfTaskNameAlreadyExists(final String taskName) {
		final List<Task> existingTasks = TaskJSONReader.getTasks(); // Required to validate the task name that is chosen by the user.
		boolean taskNameAlreadyExists = false;

		// Validation : check whether the name already exists.
		for (final Task task : existingTasks) {
			if (task.getName().toLowerCase().equals(taskName.toLowerCase()) || task.getDescription().toLowerCase().equals(taskName.toLowerCase())) {
				taskNameAlreadyExists = true;
				break;
			}
		}

		return taskNameAlreadyExists;
	}
	
	/*
	 * Returns true if an added template is not used
	 */
	public static boolean checkForUnusedIdentifiers() {
		ArrayList<Question> questions = IntegratorModel.getInstance().getQuestions();
		
		HashSet<String> identifiers = new HashSet<>();
		
		for(Question q : questions) {
			for(Answer a : q.getAnswers()) {
				if (a.getOption() != null)
					identifiers.add(a.getOption());
			}
		}
		
		return identifiers.size() < IntegratorModel.getInstance().getIdentifiers().size();
	}
}
