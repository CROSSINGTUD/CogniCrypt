/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import java.util.ArrayList;
import java.util.Collections;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;

public class SegregatesQuestionsIntoPages {

	private ArrayList<Question> listOfAllQuestions;
	private ArrayList<Page> pages;
	private int pageId = 0;

	public SegregatesQuestionsIntoPages(final ArrayList<Question> listOfAllQuestions) {
		setListOfAllQuestions(listOfAllQuestions);
		this.pages = new ArrayList<>();
		setPages(this.pages);
		/**
		 * following loop adds the questions to different pages
		 */
		for (final Question qstn : listOfAllQuestions) {
			// executes when question doesn't exists in any page
			if (!questionExistsInAnyPage(qstn)) {
				addQuestionToPage(qstn);
			}
		}

		/**
		 * following loop checks whether the questions in the page has branch or not,if not then updates the page next Id
		 */
		for (final Page page : this.pages) {
			boolean updatePageNextId = false;
			for (final Question question : page.getContent()) {
				if (!questionHasBranch(question)) {
					updatePageNextId = true;
				}
			}
			if (updatePageNextId) {
				updatePageNextID(page);
			}
		}

	}

	/**
	 * @return the lists of all questions
	 */
	public ArrayList<Question> getListOfAllQuestions() {
		return this.listOfAllQuestions;
	}

	/**
	 * sets the list of all question
	 *
	 * @param listOfAllQuestions the list containing all the questions
	 */
	public void setListOfAllQuestions(final ArrayList<Question> listOfAllQuestions) {
		this.listOfAllQuestions = listOfAllQuestions;
	}

	/**
	 * checks if question exists in any page or not
	 *
	 * @param qstn the question
	 * @return true if the qstn exists in any page otherwise false
	 */
	private boolean questionExistsInAnyPage(final Question qstn) {
		for (final Page page : this.pages) {
			for (final Question question : page.getContent()) {
				if (question.getId() == qstn.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Decides whether the question should be added to new page or to a existing page
	 *
	 * @param qstn quetsion that is to be added to ap page
	 */

	private void addQuestionToPage(final Question qstn) {

		/**
		 * case 1: if question is the first question then adds it to new page
		 */
		if (isFirstQuestion(qstn)) {
			addQuestionToNewPage(qstn);
		}
		/**
		 * case 2: if question has branch then adds it to a new page
		 */
		else if (questionHasBranch(qstn)) {
			addQuestionToNewPage(qstn);
		} else {
			final Question previousQuestion = findPreviousQuestion(qstn);
			final Page previousQuestionPage = findPageContainingPreviousQuestion(previousQuestion);
			/**
			 * case 3: executes when previous question has branch then adds the current question to a new page
			 */
			if (questionHasBranch(previousQuestion)) {
				addQuestionToNewPage(qstn);
			}
			/**
			 * case 4: executes when page containing previous question has 3 question then adds the current question to the new page
			 */
			else if (previousQuestionPage.getContent().size() == 3) {
				addQuestionToNewPage(qstn);
			}
			/**
			 * if all four case are false then adds current question to the page containing previous question
			 */
			else {
				addQuestionToExistingPage(previousQuestionPage, qstn);
			}
		}

	}

	/**
	 * Adds the question to a new page
	 *
	 * @param qstn
	 */
	private void addQuestionToNewPage(final Question qstn) {
		final Page page = new Page();
		page.setId(this.pageId);
		this.pageId++;
		final ArrayList<Question> question = new ArrayList<>();
		page.setContent(question);
		page.getContent().add(qstn);
		this.pages.add(page);
		/**
		 * executes when question has branch
		 */
		if (questionHasBranch(qstn)) {
			updateAnsNextIdToPageId(qstn);
		}

	}

	/**
	 * @param qstn
	 * @return true if the current question is the first question in the list of Questions
	 */
	private boolean isFirstQuestion(final Question qstn) {
		if (qstn.getId() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @param qstn
	 * @return the previous question
	 */
	private Question findPreviousQuestion(final Question qstn) {
		for (final Question question : this.listOfAllQuestions) {
			if (question.getId() == qstn.getId() - 1) {
				return question;
			}
		}
		return null;
	}

	/**
	 * @param qstn
	 * @return true if question has branch otherwise false
	 */
	private boolean questionHasBranch(final Question qstn) {
		if (qstn.getElement().equals(Constants.GUIElements.text)) {
			return false;
		} else {
			final ArrayList<Answer> answers = qstn.getAnswers();
			for (int i = 1; i < answers.size(); i++) {
				if (answers.get(0).getNextID() != answers.get(i).getNextID()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * adds the current question to the page which contains the previous question
	 *
	 * @param previousQuestionPage
	 * @param question
	 */
	private void addQuestionToExistingPage(final Page previousQuestionPage, final Question qstn) {
		previousQuestionPage.getContent().add(qstn);
	}

	/**
	 * Updates the answer nextID to the page ID containing the question which answer next ID points to
	 *
	 * @param qstn
	 */
	private void updateAnsNextIdToPageId(final Question qstn) {

		if (!qstn.getElement().equals(Constants.GUIElements.text)) {
			/**
			 * if the question is the last question then sets the answer next ID to -1
			 */
			if (qstn.getId() == this.listOfAllQuestions.size() - 1) {
				for (final Answer ans : qstn.getAnswers()) {
					ans.setNextID(Constants.ANSWER_NO_FOLLOWING_QUESTION_NEXT_ID);
				}
			}
			/**
			 * executes when the current question is not the last question
			 */
			else if (qstn.getId() != this.listOfAllQuestions.size() - 1) {
				final ArrayList<Integer> ansNextIds = new ArrayList<>();
				for (final Answer ans : qstn.getAnswers()) {
					ansNextIds.add(ans.getNextID());
				}
				// sort the array to create the pages and add questions in it in order
				Collections.sort(ansNextIds);

				for (final int nextId : ansNextIds) {
					// executes only if the next id points to some question in the list
					if (nextId >= 0) {
						final Answer ans = findAnswer(qstn.getAnswers(), nextId);
						if (ans.getNextID() != Constants.ANSWER_NO_FOLLOWING_QUESTION_NEXT_ID) {
							/**
							 * if the user has not selected any option of comboForLinkAnswer in LinkAnswerDialog box then Sets the answer next Id to the next questionID in the listOfAllQuestions
							 */
							if (ans.getNextID() == Constants.ANSWER_NO_NEXT_ID) {
								ans.setNextID(qstn.getId() + 1);
							}
							final Question linkedQuestion = questionWithId(ans.getNextID());
							/**
							 * if linkQuestion exists in any page then update the answer next id to the page id containing the question
							 */
							if (questionExistsInAnyPage(linkedQuestion)) {
								final Page pg = findPageContainingPreviousQuestion(linkedQuestion);
								ans.setNextID(pg.getId());
							}

							/**
							 * executes when linked question is not in the list of pages
							 */
							else {
								ans.setNextID(this.pageId);
								addQuestionToNewPage(linkedQuestion);
							}
						}
					}

				}

			}

		}

	}

	/**
	 * Return the question with ID equal to id
	 *
	 * @param id
	 * @return the question
	 */
	private Question questionWithId(final int id) {
		for (final Question question : this.listOfAllQuestions) {
			if (question.getId() == id) {
				return question;
			}
		}
		return null;
	}

	/**
	 * find the page which contains the given question
	 *
	 * @param qstn the question
	 * @return the page
	 */

	private Page findPageContainingPreviousQuestion(final Question qstn) {
		for (final Page page : this.pages) {
			for (final Question question : page.getContent()) {
				if (question.equals(qstn)) {
					return page;
				}
			}
		}
		return null;
	}

	/**
	 * Update the page next ID
	 *
	 * @param page
	 */
	private void updatePageNextID(final Page page) {
		/**
		 * case 1: if the page is the last page in the list then sets the page nextID to -1
		 */
		if (page.getId() == this.pages.size() - 1) {
			page.setNextID(Constants.QUESTION_PAGE_LAST_PAGE_NEXT_ID);
		}
		/**
		 * case 2: if the page is not last page then sets the current page NextId to next pageID in the list
		 */
		else {
			page.setNextID(page.getId() + 1);
		}
	}

	/**
	 * @param answers list of answers
	 * @param nextId answer next id
	 * @return the answer whose next id is equal to nextId
	 */
	private Answer findAnswer(final ArrayList<Answer> answers, final int nextId) {
		for (final Answer answer : answers) {
			if (answer.getNextID() == nextId) {
				return answer;
			}
		}
		return null;

	}

	/**
	 * @return the page array
	 */

	public ArrayList<Page> getPages() {
		return this.pages;
	}

	/**
	 * Sets the page array
	 *
	 * @param pages
	 */
	public void setPages(final ArrayList<Page> pages) {
		this.pages = pages;
	}

}
