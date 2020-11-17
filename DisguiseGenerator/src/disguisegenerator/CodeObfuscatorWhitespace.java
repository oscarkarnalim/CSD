package disguisegenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.google.googlejavaformat.java.Formatter;

import support.PythonCodeFormatter;
import tuple.LibTuple;

public class CodeObfuscatorWhitespace {

	public static void w01RemovingBlankLines(
			ArrayList<LibTuple> tokenString) {
		// removes all blank newlines
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is whitespace token
			if (t.getType().endsWith("WS")) {
				String text = t.getText();

				// get the first newline pos
				int firstIndex = text.indexOf("\n");
				// get the last newline pos
				int lastIndex = text.lastIndexOf("\n");

				// if the text contains less than two newlines, skip
				if (firstIndex == -1 || lastIndex == -1
						|| firstIndex == lastIndex)
					continue;

				// merge the text outside the first and the last newline pos
				String resultedText = text.substring(0, firstIndex)
						+ text.substring(lastIndex, text.length());
				// set the text
				t.setText(resultedText);

				// count the number of reduced lines
				int reducedLine = 0;
				text = text.substring(firstIndex, lastIndex);
				for (int k = 0; k < text.length(); k++) {
					char c = text.charAt(k);
					if (c == '\n')
						reducedLine++;
				}

				// update the remaining tokens
				for (int j = i + 1; j < tokenString.size(); j++) {
					tokenString.get(j).setLine(
							tokenString.get(j).getLine() - reducedLine);
				}

			}
		}
	}

	public static void w02JavaRemovingTabsAndSpacesBeforeEachStatement(
			ArrayList<LibTuple> tokenString) {
		// remove all tabs and spaces before each statement

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is whitespace token
			if (t.getType().endsWith("WS")) {
				String text = t.getText();
				// get the last newline pos
				int lastIndex = text.lastIndexOf("\n");
				// if not found, skip
				if (lastIndex == -1){
					continue;
				}
				// set the text
				t.setText(text.substring(0, lastIndex-1) + System.lineSeparator());
			}
		}
	}

	public static void w03ReplacingEachSpaceWithNSpaces(
			ArrayList<LibTuple> tokenString, int n) {
		// replace each space with n spaces

		// generate the string for n spaces
		String s = "";
		for (int i = 0; i < n; i++)
			s += " ";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is whitespace token
			if (t.getType().endsWith("WS")) {
				String text = t.getText();
				t.setText(text.replaceAll(" ", s));
			}
		}
	}

	public static void w04ReplacingEachTabWithNTabs(
			ArrayList<LibTuple> tokenString, int n) {
		// replace each tab with n tabs

		// generate the string for n tabs
		String s = "";
		for (int i = 0; i < n; i++)
			s += "\t";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is whitespace token
			if (t.getType().endsWith("WS")) {
				String text = t.getText();
				String out = "";
				for(int k=0;k<text.length();k++){
					char c = text.charAt(k);
					if(c == '\t')
						out += s;
					else
						out += c;
				}
				t.setText(out);
			}
		}
	}

	public static void w05ReplacingEachNewLineWithNLines(
			ArrayList<LibTuple> tokenString, int n) {
		// replace each newline with n lines

		// generate the string for n lines
		String s = "";
		for (int i = 0; i < n; i++)
			s += System.lineSeparator();

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is whitespace token
			if (t.getType().endsWith("WS")) {
				String text = t.getText();
				/*
				 * set the updated version and count the number of affected
				 * newline
				 */
				String out = "";
				int affectedLines = 0;
				for (int k = 0; k < text.length(); k++) {
					char c = text.charAt(k);
					if (c == '\n') {
						affectedLines++;
						out += s;
					} else if(c != '\r')
						out += c;
				}
				
				// set the new text
				t.setText(out);

				// update the line pos of remaining tokens
				int addedLines = (n - 1) * affectedLines; // minus one to
															// compensate the
															// original newline
				for (int j = i + 1; j < tokenString.size(); j++) {
					tokenString.get(j).setLine(
							tokenString.get(j).getLine() + addedLines);
				}
			}
		}
	}

	public static void w06ReplacingEachTabWithNSpaces(
			ArrayList<LibTuple> tokenString, int n) {
		// replace each tab with n spaces

		// generate the string for n tabs
		String s = "";
		for (int i = 0; i < n; i++)
			s += " ";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is whitespace token
			if (t.getType().endsWith("WS")) {
				String text = t.getText();
				t.setText(text.replaceAll("\t", s));
			}
		}
	}

	public static void w07ReplacingNSpacesWithTab(
			ArrayList<LibTuple> tokenString, int n) {
		// replace each tab with n spaces

		// generate the string for n tabs
		String s = "";
		for (int i = 0; i < n; i++)
			s += " ";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is whitespace token
			if (t.getType().endsWith("WS")) {
				String text = t.getText();
				t.setText(text.replaceAll(s, "\t"));
			}
		}
	}

	public static void w08JavaCodeFormatting(String inputpath, String outputpath) {
		// code formatting the java file using Google Java Format 1.0. Adapted
		// from JavaCodeFormatter

		String content = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					inputpath)));
			String line;
			while ((line = br.readLine()) != null) {
				// only consider lines with non-whitespaces
				if (line.trim().length() > 0) {
					content = content + line + "\n";
				}
			}
			br.close();
			content = new Formatter().formatSource(content);
			// write it as a file
			FileWriter fw = new FileWriter(new File(outputpath));
			fw.write(content.replaceAll("\n\n", "\n"));
			fw.close();
		} catch (Exception e) {
			try {
				FileWriter fw = new FileWriter(new File(outputpath));
				fw.write(content);
				fw.close();
			} catch (Exception e2) {

			}
		}
	}

	public static void w08PythonCodeFormatting(String inputpath,
			String outputpath) {
		/*
		 * Convert the code to its beautiful format with the help of YAPF from
		 * Google VERSION 0.28.0. Adapted from PythonCodeFormatter.
		 */

		try {
			/*
			 * create the copy of the file
			 */
			String line;
			BufferedReader br = new BufferedReader(new FileReader(new File(
					inputpath)));
			FileWriter fw = new FileWriter(new File(outputpath));
			while ((line = br.readLine()) != null) {
				fw.write(line + System.lineSeparator());
			}
			fw.close();
			br.close();

			// start formatting the copied code
			// execute the formatter
			Process p = Runtime.getRuntime().exec(
					new String[] { PythonCodeFormatter.pythonCompilerPath,
							PythonCodeFormatter.pythonFormatterPath,
							outputpath,
							PythonCodeFormatter.pythonStyleSettingPath });

			// wait the process till finished
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
