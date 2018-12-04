/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;

public class ClaferCompiler {

	public static String getClaferExecutable() {
		// TODO test on different operating systems
		final String osString = System.getProperty("os.name");

		if (osString.contains("Linux")) {
			return CodeGenUtils.getResourceFromWithin("src/main/resources/bin/clafer-linux").getAbsolutePath();
		} else if (osString.contains("Mac OS X")) {
			return CodeGenUtils.getResourceFromWithin("src/main/resources/bin/clafer-macos").getAbsolutePath();
		} else if (osString.contains("Windows 7") || osString.contains("Windows 8") || osString.contains("Windows 10")) {
			return CodeGenUtils.getResourceFromWithin("src/main/resources/bin/clafer-windows.exe").getAbsolutePath();
		}

		// rely on the PATH variable if full path cannot be found
		return "clafer";
	}

	public static boolean execute(final String filename) {
		final String claferExectuable = getClaferExecutable();

		// try compilation
		try {
			final ProcessBuilder processBuilder = new ProcessBuilder(claferExectuable, "-k", "-m", "choco", filename);
			processBuilder.redirectErrorStream(true);
			final Process compilerProcess = processBuilder.start();

			compilerProcess.waitFor();

			// store compilation output in string
			final StringBuilder sb = new StringBuilder();
			final InputStream processStdOutput = compilerProcess.getInputStream();
			final BufferedReader br = new BufferedReader(new InputStreamReader(processStdOutput));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			if (compilerProcess.exitValue() != 0) {
				System.out.println(sb.toString());
				return false;
			}

		} catch (final Exception ex) {
			Activator.getDefault().logError(ex);
			return false;
		}

		return true;
	}

}
