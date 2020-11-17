package disguisegenerator;

import java.util.ArrayList;
import java.util.HashSet;

import tuple.LibTuple;

public class CodeObfuscatorComment {
	private static String[] addedCommentAlternativesEN = new String[] { "code for a particular task",
			"process for a particular task", "do the task", "do a particular task", "processing", "process",
			"this section works", "this runs well", "this code works", "this code runs well", "this perfectly works" };
	private static String[] addedCommentAlternativesID = new String[] { "kode untuk task tertentu",
			"proses untuk task tertentu", "penyelesaian task", "penyelesaian task tertentu", "proses", "pemrosesan",
			"bagian ini berfungsi dengan baik", "ini berfungsi dengan baik", "kode ini berfungsi",
			"kode ini berjalan dengan semestinya", "ini berfungsi sempurna" };

	public static void c01JavaRemovingSomeSingleLineComments(
			ArrayList<LibTuple> tokenString, double probability) {
		/*
		 * remove some single line comments from java token string in which the
		 * probability defines the chance of removal.
		 */
		_commentRemovingSomeComments(tokenString, probability, "//");
	}

	public static void c01PythonRemovingSomeSingleLineComments(
			ArrayList<LibTuple> tokenString, double probability) {
		/*
		 * remove some single line comments from Python token string in which the
		 * probability defines the chance of removal.
		 */
		_commentRemovingSomeComments(tokenString, probability, "#");
	}

	private static void _commentRemovingSomeComments(ArrayList<LibTuple> tokenString,
			double probability, String prefix) {
		/*
		 * remove some comments from java token string in which the probability defines
		 * the chance of removal and the prefix defines what kind of comments should be
		 * removed.
		 */

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith(prefix)) {
					// this is single-line comment,
					double guess = CodeObfuscatorCore.r.nextDouble();
					if (guess <= probability) {
						/*
						 * if the generated number is lower than the probability, remove the comment
						 */
						tokenString.remove(i);

						/*
						 * count the number of newlines in the comment
						 */
						int countline = 0;
						for (int k = 0; k < t.getText().length(); k++) {
							if (t.getText().charAt(k) == '\n')
								countline++;
						}

						// merge the next WS to the last one if any

						// to mark whether this process leads to the removal of
						// one WS token
						boolean isAdjacentWSTokenRemoved = false;
						if (i > 0 && i < tokenString.size()) {
							LibTuple prev = tokenString.get(i - 1);
							t = tokenString.get(i);
							// if prev and t are both whitespaces, merge t to prev and remove t
							if (prev.getType().equals("WS") && t.getType().equals("WS")) {
								prev.setText(prev.getText() + t.getText());
								tokenString.remove(i);
								i--;
								isAdjacentWSTokenRemoved = true;
							}
						}

						// reduce the line pos of remaining tokens
						int j = i + 2;
						if (isAdjacentWSTokenRemoved)
							j = i + 1;
						for (; j < tokenString.size(); j++) {
							LibTuple temp = tokenString.get(j);
							temp.setLine(temp.getLine() - countline);
						}
					}
				}
			}
		}
	}

	public static void c02JavaRemovingAllSingleLineComments(
			ArrayList<LibTuple> tokenString) {
		// remove all single line comments from java token string
		c01JavaRemovingSomeSingleLineComments(tokenString, 1);
	}

	public static void c02PythonRemovingAllSingleLineComments(
			ArrayList<LibTuple> tokenString) {
		// remove all single line comments from python token string
		c01PythonRemovingSomeSingleLineComments(tokenString, 1);
	}

	public static void c03JavaRemovingSomeMultiLineComments(
			ArrayList<LibTuple> tokenString, double probability) {
		/*
		 * remove some multi line comments from java token string in which the
		 * probability defines the chance of removal.
		 */
		_commentRemovingSomeComments(tokenString, probability, "/*");
	}

	public static void c04JavaRemovingAllMultiLineComments(
			ArrayList<LibTuple> tokenString) {
		// remove all multi line comments from java token string
		c03JavaRemovingSomeMultiLineComments(tokenString, 1);
	}

	public static void c05JavaRemovingSomeComments(ArrayList<LibTuple> tokenString,
			double probability) {
		// remove all comments in which its probability is defined from the
		// parameter
		c01JavaRemovingSomeSingleLineComments(tokenString, probability);
		c03JavaRemovingSomeMultiLineComments(tokenString, probability);
	}

	public static void c06JavaRemovingAllComments(ArrayList<LibTuple> tokenString) {
		// remove all comments
		c02JavaRemovingAllSingleLineComments(tokenString);
		c04JavaRemovingAllMultiLineComments(tokenString);
	}

	public static void c07JavaAddingSingleLineComment(ArrayList<LibTuple> tokenString,
			String language) {
		// quite similar with the called method except it deals with single-line
		// comments.
		_commentAddingComment(tokenString, "//", "", language);
	}

	public static void c07PythonAddingSingleLineComment(ArrayList<LibTuple> tokenString,
			String language) {
		// quite similar with the called method except it deals with single-line
		// comments.
		_commentAddingComment(tokenString, "#", "", language);
	}

	private static void _commentAddingComment(ArrayList<LibTuple> tokenString,
			String prefix, String postfix, String language) {
		/*
		 * READ THE CODE TO KNOW THE DETAILS adding a comment for each blank line
		 * followed with keyword or identifier, which content is about the closest
		 * identifier name after that comment. It will be written as 'code for [ident]'
		 * where the [ident]'s length should be longer than 2 characters to not select
		 * iterators).
		 */

		String[] addedCommentAlternatives = addedCommentAlternativesEN;
		if (language.equals("id"))
			addedCommentAlternatives = addedCommentAlternativesID;

		// store the lines that have been commented
		HashSet<Integer> commentedLines = new HashSet<>();

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);

			if (t.getType().endsWith("COMMENT")) {
				// if comment, mark the line
				commentedLines.add(t.getLine());
			} else if (t.getText().matches("[a-zA-Z0-9]+")) {
				// if keyword or identifier

				// search previous syntax token
				LibTuple prevSyntaxT = null;
				for (int j = i - 1; j >= 0; j--) {
					LibTuple temp = tokenString.get(j);
					if (!temp.getType().endsWith("COMMENT") && !temp.getType().equals("WS")) {
						prevSyntaxT = temp;
						break;
					}
				}
				// if not found, move to next token
				if (prevSyntaxT == null)
					continue;

				// if they have two or more lines difference, it can be the
				// beginning of a block and a comment can be added
				if (t.getLine() - prevSyntaxT.getLine() >= 2) {
					// if the previous line has no comment
					if (!commentedLines.contains(t.getLine() - 1)) {
						int randompos = CodeObfuscatorCore.r.nextInt(addedCommentAlternatives.length);
						// embed the new comment
						tokenString.add(i,
								new LibTuple(prefix + " "
										+ addedCommentAlternatives[randompos] + " " + postfix + System.lineSeparator(),
										"LINE_COMMENT", t.getLine()));
						// mark the line as commented
						commentedLines.add(t.getLine());
						// increment i as a token added
						i++;

						// add new whitespace token for t if any
						if (i - 2 >= 0 && tokenString.get(i - 2).getType().equals("WS")) {
							LibTuple prevWhitespace = tokenString.get(i - 2);
							// create a copied whitespace token
							LibTuple ntok = new LibTuple(
									prevWhitespace.getText().substring(prevWhitespace.getText().lastIndexOf("\n") + 1,
											prevWhitespace.getText().length()),
									prevWhitespace.getType(), prevWhitespace.getLine() + 1);
							// only get whitespaces after last newline
							tokenString.add(i, ntok);
							// increment i as a token added
							i++;
						}

						// increment the line pos of remaining tokens
						for (int k = i; k < tokenString.size(); k++) {
							tokenString.get(k).incrementLine();
						}

					}
				}
			}
		}
	}

	public static void c08JavaAddingMultiLineComment(ArrayList<LibTuple> tokenString,
			String language) {
		// quite similar with the called method except it deals with multi-line
		// comments.
		_commentAddingComment(tokenString, "/*", "*/", language);

	}

	public static void c09JavaReplacingSingleToMultiComments(
			ArrayList<LibTuple> tokenString) {
		// replace all single line comments to the multi line ones (only the
		// marker)

		String oldprefix = "//";
		String newprefix = "/*";
		String newpostfix = "*/";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith(oldprefix)) {
					String text = t.getText();
					// remove the old prefix and postfix
					text = text.substring(text.indexOf(oldprefix) + oldprefix.length(), text.length()).trim();
					// add the new ones
					t.setText(newprefix + " " + text + " " + newpostfix);
				}
			}
		}
	}

	public static void c10JavaReplacingSingleToMultiCommentsWithPunctuationsAsLineDelimiters(
			ArrayList<LibTuple> tokenString) {
		/*
		 * replace all single line comments to the multi line ones with '.', '?', '!',
		 * and ';' as the line separators.
		 */

		String oldprefix = "//";
		String newprefix = "/*";
		String newpostfix = "*/";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith(oldprefix)) {
					String text = t.getText();
					// remove the old prefix and postfix
					text = text.substring(text.indexOf(oldprefix) + oldprefix.length(), text.length()).trim();

					// to count how many lines have been added
					int addedLineCounter = 0;

					// start splitting the text
					String processedText = "";
					for (int k = 0; k < text.length(); k++) {
						char c = text.charAt(k);
						processedText += c;
						if (c == '.' || c == '?' || c == '!' || c == ';') {
							// once a punctuation ending a line is found, add a
							// new line
							processedText += System.lineSeparator();
							// add line counter
							addedLineCounter++;
						}
					}

					// merge the text
					t.setText(newprefix + " " + processedText + " " + newpostfix);

					for (int j = i + 1; j < tokenString.size(); j++) {
						LibTuple temp = tokenString.get(j);
						temp.setLine(temp.getLine() + addedLineCounter);
					}
				}
			}
		}
	}

	public static void c11JavaReplacingSingleToMultiCommentsWithNCharsPerLine(
			ArrayList<LibTuple> tokenString, int n) {
		/*
		 * replace all single line comments to the multi line ones with n characters per
		 * line.
		 */

		String oldprefix = "//";
		String newprefix = "/*";
		String newpostfix = "*/";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith(oldprefix)) {
					String text = t.getText();
					// remove the old prefix and postfix
					text = text.substring(text.indexOf(oldprefix) + oldprefix.length(), text.length()).trim();

					// to count how many lines have been added
					int addedLineCounter = 0;

					// start splitting the text

					// for all text
					String processedText = "";
					// text per line
					String lineText = "";

					String[] words = text.split("\\s+");
					for (String w : words) {
						if (lineText.length() + w.length() + 1 <= n) {
							// add each word to lineText if the length is lower
							// or equal to n
							lineText += (w + " ");
						} else {
							// otherwise, add the line to final text with a
							// newline
							processedText += (lineText + System.lineSeparator());
							// reset the line text to only contain w
							lineText = (w + " ");
							// add the line counter
							addedLineCounter++;
						}
					}
					// for the remaining words in linetext
					if (lineText.length() > 0) {
						processedText += lineText;
					}

					// merge the text
					t.setText(newprefix + " " + processedText + newpostfix);

					for (int j = i + 1; j < tokenString.size(); j++) {
						LibTuple temp = tokenString.get(j);
						temp.setLine(temp.getLine() + addedLineCounter);
					}
				}
			}
		}
	}

	public static void c12JavaReplacingSingleToNSingleCommentsWithPunctuationsAsDelimiters(
			ArrayList<LibTuple> tokenString) {
		_commentReplacingCommentToSeveralSingleLineCommentsWithSomePunctuationsAsLineDelimiters(tokenString, "//", "",
				"//");
	}

	public static void c12PythonReplacingSingleToNSingleCommentsWithPunctuationsAsDelimiters(
			ArrayList<LibTuple> tokenString) {
		_commentReplacingCommentToSeveralSingleLineCommentsWithSomePunctuationsAsLineDelimiters(tokenString, "#", "",
				"#");
	}

	private static void _commentReplacingCommentToSeveralSingleLineCommentsWithSomePunctuationsAsLineDelimiters(
			ArrayList<LibTuple> tokenString, String oldprefix, String oldpostfix,
			String singleLinePrefix) {
		/*
		 * split each single/multi line comments to several single line comments with
		 * '.', '?', '!', and ';' as the line separators.
		 */

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith(oldprefix)) {
					String text = t.getText();

					if (text.contains(".") || text.contains("?") || text.contains("!") || text.contains(";")) {
						int lastpos = text.length(); // single line
						if (!oldpostfix.equals("")) { // multi line
							lastpos = text.lastIndexOf(oldpostfix);
						}

						// remove the old prefix and postfix
						text = text.substring(text.indexOf(oldprefix) + oldprefix.length(), lastpos).trim()
								.replaceAll("\n", "");

						String remainingWhitespacesForLastNewComment = "";

						// remove the comment token
						tokenString.remove(i);
						// get the content of the following whitespace token
						remainingWhitespacesForLastNewComment = tokenString.get(i).getText();
						// remove the following whitespace token
						tokenString.remove(i);

						// define how many comments have been added
						int addedLineCounter = 0;
						// define the content for each token
						String lineText = "";
						// start splitting the text and add each of those
						for (int k = 0; k < text.length(); k++) {
							char c = text.charAt(k);
							lineText += c;
							if (c == '.' || c == '?' || c == '!' || c == ';') {
								// once a punctuation ending a line is found,
								// add as a new comment with its newline token
								tokenString.add(i,
										new LibTuple(
												singleLinePrefix + " " + lineText.trim(), t.getType(),
												t.getLine() + addedLineCounter));
								i++;
								tokenString.add(i, new LibTuple(System.lineSeparator(),
										"WS", t.getLine() + addedLineCounter));
								i++;
								// clean the line text
								lineText = "";
								// add line counter
								addedLineCounter++;
							}
						}
						if (lineText.trim().length() > 0) {
							// add a new comment
							tokenString.add(i,
									new LibTuple(
											singleLinePrefix + " " + lineText.trim(), t.getType(),
											t.getLine() + addedLineCounter));
							i++;
							// and the whitepace
							tokenString.add(i, new LibTuple(System.lineSeparator(), "WS",
									t.getLine() + addedLineCounter));
							i++;
							// add line counter
							addedLineCounter++;
						}

						/*
						 * set the remaining whitespaces to the last token (i-1 because the index is
						 * added for each new additional token)
						 */
						tokenString.get(i - 1).setText(remainingWhitespacesForLastNewComment);

						// update the line pos for remaining tokens
						for (int j = i; j < tokenString.size(); j++) {
							LibTuple temp = tokenString.get(j);
							temp.setLine(temp.getLine() + addedLineCounter - 1);
						}

						// reduce the i by one to keep the iteration back
						i--;
					}
				}
			}
		}
	}

	public static void c13JavaReplacingSingleToNSingleLineCommentsWithNCharsPerLine(
			ArrayList<LibTuple> tokenString, int n) {
		_commentReplacingCommentToSeveralSingleLineCommentsWithNCharsPerLine(tokenString, n, "//", "", "//");
	}

	public static void c13PythonReplacingSingleToNSingleCommentsWithNCharsPerLine(
			ArrayList<LibTuple> tokenString, int n) {
		_commentReplacingCommentToSeveralSingleLineCommentsWithNCharsPerLine(tokenString, n, "#", "", "#");
	}

	private static void _commentReplacingCommentToSeveralSingleLineCommentsWithNCharsPerLine(
			ArrayList<LibTuple> tokenString, int n, String oldprefix, String oldpostfix,
			String newPrefix) {
		/*
		 * split each comments to several single line comments with n characters per
		 * line.
		 */
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith(oldprefix)) {
					String text = t.getText();

					if (text.length() > n) {
						int lastpos = text.length(); // single line
						if (!oldpostfix.equals("")) { // multi line
							lastpos = text.lastIndexOf(oldpostfix);
						}

						// remove the old prefix and postfix
						text = text.substring(text.indexOf(oldprefix) + oldprefix.length(), lastpos).trim()
								.replaceAll("\n", "");

						String remainingWhitespacesForLastNewComment = "";

						// remove the comment token
						tokenString.remove(i);
						// get the content of the following whitespace token
						remainingWhitespacesForLastNewComment = tokenString.get(i).getText();
						// remove the following whitespace token
						tokenString.remove(i);

						// define how many comments have been added
						int addedLineCounter = 0;
						// define the content for each token
						String lineText = "";
						// start splitting the text to words and add each of
						// those
						String[] words = text.split("\\s+");
						for (String w : words) {
							if (lineText.length() + w.length() + 1 <= n) {
								// add each word to lineText if the length is
								// lower
								// or equal to n
								lineText += (w + " ");
							} else {
								if (lineText.trim().length() > 0) {
									// otherwise, if lineText has content, add
									// the line as a comment
									tokenString.add(i,
											new LibTuple(
													newPrefix + " " + lineText.trim(), t.getType(),
													t.getLine() + addedLineCounter));
									i++;
									// add a newline
									tokenString.add(i, new LibTuple(
											System.lineSeparator(), "WS", t.getLine() + addedLineCounter));
									i++;
									// add the line counter
									addedLineCounter++;
								}
								// reset the line text to only contain w
								lineText = (w + " ");
							}
						}
						if (lineText.trim().length() > 0) {
							// add a new comment
							tokenString.add(i, new LibTuple(
									newPrefix + " " + lineText.trim(), t.getType(), t.getLine() + addedLineCounter));
							i++;
							// and the whitepace
							tokenString.add(i, new LibTuple(System.lineSeparator(), "WS",
									t.getLine() + addedLineCounter));
							i++;
							// add line counter
							addedLineCounter++;
						}

						/*
						 * set the remaining whitespaces to the last token (i-1 because the index is
						 * added for each new additional token)
						 */
						tokenString.get(i - 1).setText(remainingWhitespacesForLastNewComment);

						// update the line pos for remaining tokens
						for (int j = i; j < tokenString.size(); j++) {
							LibTuple temp = tokenString.get(j);
							temp.setLine(temp.getLine() + addedLineCounter - 1);
						}

						// reduce the i by one to keep the iteration back
						i--;
					}
				}
			}
		}
	}

	public static void c14JavaReplacingMultiToSingleCommentsWithNewlinesRemoved(
			ArrayList<LibTuple> tokenString) {
		String oldprefix = "/*";
		String oldpostfix = "*/";
		String newprefix = "//";
		String newpostfix = "";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith(oldprefix)) {
					String text = t.getText();
					// remove the old prefix and postfix
					int postfix = text.lastIndexOf(oldpostfix);
					text = text.substring(text.indexOf(oldprefix) + oldprefix.length(), postfix).trim();

					// remove all newlines in the comment and count the number of removed lines
					String out = "";
					int removedLines = -1;
					for (int k = 0; k < text.length(); k++) {
						char c = text.charAt(k);
						if (c == '\n')
							removedLines++;
						else if (c != '\r')
							out += text.charAt(k);
					}

					// get the number of removed lines and updates all remaining
					// tokens
					for (int j = i + 1; j < tokenString.size(); j++) {
						LibTuple temp = tokenString.get(j);
						temp.setLine(temp.getLine() - removedLines);
					}

					// add the new ones
					t.setText(newprefix + " " + out + " " + newpostfix);
				}
			}
		}
	}

	public static void c15JavaSplitMultiToNSingleCommentsWithNewlineAsSeparator(
			ArrayList<LibTuple> tokenString) {
		String oldprefix = "/*";
		String oldpostfix = "*/";
		String newprefix = "//";
		String newpostfix = "";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith(oldprefix)) {
					// remove the token
					tokenString.remove(i);
					// get the text
					String text = t.getText();

					// remove the old prefix and postfix
					int postfix = text.lastIndexOf(oldpostfix) - 1;
					text = text.substring(text.indexOf(oldprefix) + oldprefix.length(), postfix).trim();

					// split based on newlines
					ArrayList<String> lines = new ArrayList<>();
					String tmp = "";
					for (int k = 0; k < text.length(); k++) {
						char c = text.charAt(k);
						if (c == '\n') {
							lines.add(tmp);
							tmp = "";
						} else if (c != '\r')
							tmp += text.charAt(k);
					}
					if (tmp.length() > 0)
						lines.add(tmp);

					for (int k = 0; k < lines.size(); k++) {
						String l = newprefix + " " + lines.get(k) + " " + newpostfix;
						// for each line, add as a new comment token, followed
						// by a whitespace token
						LibTuple temp = new LibTuple(l,
								t.getType(), t.getLine() + k);
						tokenString.add(i + 2 * k, temp);
						if (k != lines.size() - 1) {
							// if not the last, add newline token
							// the last one will use the existing one
							LibTuple ws = new LibTuple(
									System.lineSeparator(), "WS", t.getLine() + k);
							tokenString.add(i + 2 * k + 1, ws);
						}
					}

					// add the new ones
					t.setText(newprefix + " " + text + " " + newpostfix);
				}
			}
		}
	}

	public static void c16JavaSplitMultiToNSingleCommentsWithPunctuationsAsDelimiters(
			ArrayList<LibTuple> tokenString) {
		_commentReplacingCommentToSeveralSingleLineCommentsWithSomePunctuationsAsLineDelimiters(tokenString, "/*", "*/",
				"//");
	}

	public static void c17JavaReplacingMultiToSeveralSingleCommentsWithNCharsPerLine(
			ArrayList<LibTuple> tokenString, int n) {
		_commentReplacingCommentToSeveralSingleLineCommentsWithNCharsPerLine(tokenString, n, "/*", "*/", "//");
	}

	public static void c18JavaCapitalisingFirstCharEachWord(
			ArrayList<LibTuple> tokenString) {
		/*
		 * for each comment, split based on whitespace and uppercase the first char of
		 * each word.
		 */
		_capitalisingFirstCharEachWord(tokenString, "//", "/*", "*/");
	}

	public static void c18PythonCapitalisingFirstCharEachWord(
			ArrayList<LibTuple> tokenString) {
		/*
		 * for each comment, split based on whitespace and uppercase the first char of
		 * each word.
		 */
		_capitalisingFirstCharEachWord(tokenString, "#", "", "");
	}

	private static void _capitalisingFirstCharEachWord(ArrayList<LibTuple> tokenString,
			String commentSinglePrefix, String commentMultiPrefix, String commentMultiPostfix) {
		/*
		 * for each comment, split based on whitespace and uppercase the first char of
		 * each word.
		 */

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				// remove comment prefix and postfix
				String text = t.getText();
				if (text.startsWith(commentSinglePrefix))
					text = text.substring(commentSinglePrefix.length());
				else if (text.startsWith(commentMultiPrefix))
					text = text.substring(commentMultiPrefix.length(), text.length() - commentMultiPostfix.length());

				String unProcessedText = text;
				// split the comment text, capitalise, and merge back
				String out = "";
				String[] textArr = text.split("[^A-Za-z0-9]");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						String c = (textArr[j].charAt(0) + "").toUpperCase();
						if (textArr[j].length() > 1)
							c += textArr[j].substring(1, textArr[j].length());
						// put the non-alphanumerics before textArr[j]
						int pos = unProcessedText.indexOf(textArr[j]);
						out += unProcessedText.substring(0, pos);
						// add the content
						out += c;
						// cut the unprocessed text
						unProcessedText = unProcessedText.substring(pos + textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				// add the comment prefix and postfix
				if (t.getText().startsWith(commentSinglePrefix))
					out = commentSinglePrefix + out;
				else
					out = commentMultiPrefix + out + commentMultiPostfix;

				// set the text
				t.setText(out);

			}
		}
	}

	public static void c19CapitalisingAllChars(ArrayList<LibTuple> tokenString) {
		// capitalise all chars
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				t.setText(t.getText().toUpperCase());
			}
		}
	}

	public static void c20DecapitalisingAllChars(ArrayList<LibTuple> tokenString) {
		// decapitalise all chars
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				t.setText(t.getText().toLowerCase());
			}
		}
	}

	public static void c21JavaReplacingConjuctionSymbolsWithWords(
			ArrayList<LibTuple> tokenString, String language) {
		_replacingConjuctionSymbolsWithWords(tokenString, language, "//", "/*", "*/");
	}

	public static void c21PythonReplacingConjuctionSymbolsWithWords(
			ArrayList<LibTuple> tokenString, String language) {
		_replacingConjuctionSymbolsWithWords(tokenString, language, "#", "", "");
	}

	private static void _replacingConjuctionSymbolsWithWords(
			ArrayList<LibTuple> tokenString, String language, String commentSinglePrefix,
			String commentMultiPrefix, String commentMultiPostfix) {
		// replace all conjunction symbol with word

		// match the conjuction words based on the language
		String andWord = "and";
		String orWord = "or";
		if (language.equals("id")) {
			andWord = "dan";
			orWord = "atau";
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				// remove comment prefix and postfix
				String text = t.getText();
				if (text.startsWith(commentSinglePrefix))
					text = text.substring(commentSinglePrefix.length());
				else if (text.startsWith(commentMultiPrefix))
					text = text.substring(commentMultiPrefix.length(), text.length() - commentMultiPostfix.length());

				String unProcessedText = text;
				// split the comment text, replace, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the non-alphanumerics before textArr[j]
						int pos = unProcessedText.indexOf(textArr[j]);
						out += unProcessedText.substring(0, pos);

						// add the content
						if (textArr[j].trim().equals("&"))
							out += andWord;
						else if (textArr[j].trim().equals("/"))
							out += orWord;
						else
							out += textArr[j];

						// cut the unprocessed text
						unProcessedText = unProcessedText.substring(pos + textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				// add the comment prefix and postfix
				if (t.getText().startsWith(commentSinglePrefix))
					out = commentSinglePrefix + out;
				else
					out = commentMultiPrefix + out + commentMultiPostfix;

				t.setText(out);
			}
		}
	}

	public static void c22JavaReplacingConjuctionWordsWithSymbols(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all conjunction symbol with word
		_replacingConjuctionWordsWithSymbols(tokenString, language, "//", "/*", "*/");
	}

	public static void c22PythonReplacingConjuctionWordsWithSymbols(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all conjunction symbol with word
		_replacingConjuctionWordsWithSymbols(tokenString, language, "#", "", "");
	}

	private static void _replacingConjuctionWordsWithSymbols(
			ArrayList<LibTuple> tokenString, String language, String commentSinglePrefix,
			String commentMultiPrefix, String commentMultiPostfix) {
		// replace all conjunction symbol with word

		// match the conjuction words based on the language
		String andWord = "and";
		String orWord = "or";
		if (language.equals("id")) {
			andWord = "dan";
			orWord = "atau";
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				// remove comment prefix and postfix
				String text = t.getText();
				if (text.startsWith(commentSinglePrefix))
					text = text.substring(commentSinglePrefix.length());
				else if (text.startsWith(commentMultiPrefix))
					text = text.substring(commentMultiPrefix.length(), text.length() - commentMultiPostfix.length());

				String unProcessedText = text;
				// split the comment text, replace, and merge back
				String out = "";
				String[] textArr = text.split("[^A-Za-z0-9]");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the non-alphanumerics before textArr[j]
						int pos = unProcessedText.indexOf(textArr[j]);
						out += unProcessedText.substring(0, pos);

						// add the content
						if (textArr[j].equalsIgnoreCase(andWord))
							out += "&";
						else if (textArr[j].equalsIgnoreCase(orWord))
							out += "/";
						else
							out += textArr[j];

						// cut the unprocessed text
						unProcessedText = unProcessedText.substring(pos + textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				// add the comment prefix and postfix
				if (t.getText().startsWith(commentSinglePrefix))
					out = commentSinglePrefix + out;
				else
					out = commentMultiPrefix + out + commentMultiPostfix;

				t.setText(out);
			}
		}
	}

	public static void c23JavaReplacingMathSymbolsWithWords(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all math symbol with word
		_replacingMathSymbolsWithWords(tokenString, language, "//", "/*", "*/");
	}

	public static void c23PythonReplacingMathSymbolsWithWords(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all math symbol with word
		_replacingMathSymbolsWithWords(tokenString, language, "#", "", "");
	}

	private static void _replacingMathSymbolsWithWords(ArrayList<LibTuple> tokenString,
			String language, String commentSinglePrefix, String commentMultiPrefix, String commentMultiPostfix) {
		// replace all math symbol with word

		// match the operator words based on the language
		String plusWord = "plus";
		String minusWord = "minus";
		String timesWord = "times";
		String dividedWord = "divided by";
		String equalWord = "equals to";
		if (language.equals("id")) {
			plusWord = "tambah";
			minusWord = "kurang";
			timesWord = "kali";
			dividedWord = "dibagi dengan";
			equalWord = "sama dengan";
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				// remove comment prefix and postfix
				String text = t.getText();
				if (text.startsWith(commentSinglePrefix))
					text = text.substring(commentSinglePrefix.length());
				else if (text.startsWith(commentMultiPrefix))
					text = text.substring(commentMultiPrefix.length(), text.length() - commentMultiPostfix.length());

				String unProcessedText = text;
				// split the comment text, replace, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the non-alphanumeric chars before textArr[j]
						int pos = unProcessedText.indexOf(textArr[j]);
						out += unProcessedText.substring(0, pos);

						// add the content
						if (textArr[j].equals("+"))
							out += plusWord;
						else if (textArr[j].equals("-"))
							out += minusWord;
						else if (textArr[j].equals("*"))
							out += timesWord;
						else if (textArr[j].equals("/"))
							out += dividedWord;
						else if (textArr[j].equals("="))
							out += equalWord;
						else
							out += textArr[j];

						// cut the unprocessed text
						unProcessedText = unProcessedText.substring(pos + textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				// add the comment prefix and postfix
				if (t.getText().startsWith(commentSinglePrefix))
					out = commentSinglePrefix + out;
				else
					out = commentMultiPrefix + out + commentMultiPostfix;

				t.setText(out);
			}
		}
	}

	public static void c24JavaReplacingMathWordsWithSymbols(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all math symbol with word
		_replacingMathWordsWithSymbols(tokenString, language, "//", "/*", "*/");
	}

	public static void c24PythonReplacingMathWordsWithSymbols(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all math symbol with word
		_replacingMathWordsWithSymbols(tokenString, language, "#", "", "");
	}

	private static void _replacingMathWordsWithSymbols(ArrayList<LibTuple> tokenString,
			String language, String commentSinglePrefix, String commentMultiPrefix, String commentMultiPostfix) {
		// replace all math symbol with word

		// match the operator words based on the language
		String plusWord = "plus";
		String minusWord = "minus";
		String timesWord = "times";
		String dividedWord1 = "divided";
		String dividedWord2 = "by";
		String equalWord1 = "equals";
		String equalWord2 = "to";
		if (language.equals("id")) {
			plusWord = "tambah";
			minusWord = "kurang";
			timesWord = "kali";
			dividedWord1 = "dibagi";
			dividedWord2 = "dengan";
			equalWord1 = "sama";
			equalWord2 = "dengan";
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				// remove comment prefix and postfix
				String text = t.getText();
				if (text.startsWith(commentSinglePrefix))
					text = text.substring(commentSinglePrefix.length());
				else if (text.startsWith(commentMultiPrefix))
					text = text.substring(commentMultiPrefix.length(), text.length() - commentMultiPostfix.length());

				String unProcessedText = text;
				// split the comment text, replace, and merge back
				String out = "";
				String[] textArr = text.split("[^A-Za-z0-9]");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the non-alphanumeric chars before textArr[j]
						int pos = unProcessedText.indexOf(textArr[j]);
						out += unProcessedText.substring(0, pos);

						boolean isTwoSymbols = false;
						// add the content
						if (textArr[j].equalsIgnoreCase(plusWord))
							out += "+";
						else if (textArr[j].equalsIgnoreCase(minusWord))
							out += "-";
						else if (textArr[j].equalsIgnoreCase(timesWord))
							out += "*";
						else if (textArr[j].equalsIgnoreCase(dividedWord1) && j + 1 < textArr.length
								&& textArr[j + 1].equals(dividedWord2)) {
							// if this word is dividedWord1 and the next one is
							// dividedWord2
							out += "/";
							// skip next word as it has been used
							j++;
							// mark it as dividedBySymbol
							isTwoSymbols = true;
						} else if (textArr[j].equalsIgnoreCase(equalWord1) && j + 1 < textArr.length
								&& textArr[j + 1].equals(equalWord2)) {
							// if this word is equalWord1 and the next one is
							// equalWord2
							out += "=";
							// skip next word as it has been used
							j++;
							// mark it as twoSymbols
							isTwoSymbols = true;
						} else
							out += textArr[j];

						// cut the unprocessed text
						if (isTwoSymbols == false)
							unProcessedText = unProcessedText.substring(pos + textArr[j].length());
						else
							// if dividedby, cut two words and a space
							unProcessedText = unProcessedText
									.substring(pos + textArr[j - 1].length() + 1 + textArr[j].length());

					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				// add the comment prefix and postfix
				if (t.getText().startsWith(commentSinglePrefix))
					out = commentSinglePrefix + out;
				else
					out = commentMultiPrefix + out + commentMultiPostfix;

				t.setText(out);
			}
		}
	}

	public static void c25JavaReplacingSmallNumbersWithWords(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all small numbers with word
		_replacingSmallNumbersWithWords(tokenString, language, "//", "/*", "*/");
	}

	public static void c25PythonReplacingSmallNumbersWithWords(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all small numbers with word
		_replacingSmallNumbersWithWords(tokenString, language, "#", "", "");
	}

	private static void _replacingSmallNumbersWithWords(ArrayList<LibTuple> tokenString,
			String language, String commentSinglePrefix, String commentMultiPrefix, String commentMultiPostfix) {
		// replace all small numbers with word

		// match the number words based on the language
		String w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12;
		w0 = "zero";
		w1 = "one";
		w2 = "two";
		w3 = "three";
		w4 = "four";
		w5 = "five";
		w6 = "six";
		w7 = "seven";
		w8 = "eight";
		w9 = "nine";
		w10 = "ten";
		w11 = "eleven";
		w12 = "twelve";
		if (language.equals("id")) {
			w0 = "nol";
			w1 = "satu";
			w2 = "dua";
			w3 = "tiga";
			w4 = "empat";
			w5 = "lima";
			w6 = "enam";
			w7 = "tujuh";
			w8 = "delapan";
			w9 = "sembilan";
			w10 = "sepuluh";
			w11 = "sebelas";
			w12 = "dua belas";
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				// remove comment prefix and postfix
				String text = t.getText();
				if (text.startsWith(commentSinglePrefix))
					text = text.substring(commentSinglePrefix.length());
				else if (text.startsWith(commentMultiPrefix))
					text = text.substring(commentMultiPrefix.length(), text.length() - commentMultiPostfix.length());

				String unProcessedText = text;
				// split the comment text, replace, and merge back
				String out = "";
				String[] textArr = text.split("[^A-Za-z0-9]");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the non-alphanumeric chars before textArr[j]
						int pos = unProcessedText.indexOf(textArr[j]);
						out += unProcessedText.substring(0, pos);

						// add the content
						if (textArr[j].equals("0"))
							out += w0;
						else if (textArr[j].equals("1"))
							out += w1;
						else if (textArr[j].equals("2"))
							out += w2;
						else if (textArr[j].equals("3"))
							out += w3;
						else if (textArr[j].equals("4"))
							out += w4;
						else if (textArr[j].equals("5"))
							out += w5;
						else if (textArr[j].equals("6"))
							out += w6;
						else if (textArr[j].equals("7"))
							out += w7;
						else if (textArr[j].equals("8"))
							out += w8;
						else if (textArr[j].equals("9"))
							out += w9;
						else if (textArr[j].equals("10"))
							out += w10;
						else if (textArr[j].equals("11"))
							out += w11;
						else if (textArr[j].equals("12"))
							out += w12;
						else
							out += textArr[j];

						// cut the unprocessed text
						unProcessedText = unProcessedText.substring(pos + textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				// add the comment prefix and postfix
				if (t.getText().startsWith(commentSinglePrefix))
					out = commentSinglePrefix + out;
				else
					out = commentMultiPrefix + out + commentMultiPostfix;

				t.setText(out);
			}
		}
	}

	public static void c26JavaReplacingSmallNumberWordsWithNumbers(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all small number words with numbers
		_replacingSmallNumberWordsWithNumbers(tokenString, language, "//", "/*", "*/");
	}

	public static void c26PythonReplacingSmallNumberWordsWithNumbers(
			ArrayList<LibTuple> tokenString, String language) {
		// replace all small number words with numbers
		_replacingSmallNumberWordsWithNumbers(tokenString, language, "#", "", "");
	}

	private static void _replacingSmallNumberWordsWithNumbers(
			ArrayList<LibTuple> tokenString, String language, String commentSinglePrefix,
			String commentMultiPrefix, String commentMultiPostfix) {
		// replace all small number words with numbers

		// match the number words based on the language
		String w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w12bID = "";
		w0 = "zero";
		w1 = "one";
		w2 = "two";
		w3 = "three";
		w4 = "four";
		w5 = "five";
		w6 = "six";
		w7 = "seven";
		w8 = "eight";
		w9 = "nine";
		w10 = "ten";
		w11 = "eleven";
		w12 = "twelve";
		if (language.equals("id")) {
			w0 = "nol";
			w1 = "satu";
			w2 = "dua";
			w3 = "tiga";
			w4 = "empat";
			w5 = "lima";
			w6 = "enam";
			w7 = "tujuh";
			w8 = "delapan";
			w9 = "sembilan";
			w10 = "sepuluh";
			w11 = "sebelas";
			w12 = "duabelas";
			w12bID = "belas";
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				// remove comment prefix and postfix
				String text = t.getText();
				if (text.startsWith(commentSinglePrefix))
					text = text.substring(commentSinglePrefix.length());
				else if (text.startsWith(commentMultiPrefix))
					text = text.substring(commentMultiPrefix.length(), text.length() - commentMultiPostfix.length());

				String unProcessedText = text;
				// split the comment text, replace, and merge back
				String out = "";
				String[] textArr = text.split("[^A-Za-z0-9]");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the non-alphanumeric chars before textArr[j]
						int pos = unProcessedText.indexOf(textArr[j]);
						out += unProcessedText.substring(0, pos);

						boolean isDuaBelas = false;
						// add the content
						if (language.equals("en")) {
							if (textArr[j].equalsIgnoreCase(w0))
								out += "0";
							else if (textArr[j].equalsIgnoreCase(w1))
								out += "1";
							else if (textArr[j].equalsIgnoreCase(w2))
								out += "2";
							else if (textArr[j].equalsIgnoreCase(w3))
								out += "3";
							else if (textArr[j].equalsIgnoreCase(w4))
								out += "4";
							else if (textArr[j].equalsIgnoreCase(w5))
								out += "5";
							else if (textArr[j].equalsIgnoreCase(w6))
								out += "6";
							else if (textArr[j].equalsIgnoreCase(w7))
								out += "7";
							else if (textArr[j].equalsIgnoreCase(w8))
								out += "8";
							else if (textArr[j].equalsIgnoreCase(w9))
								out += "9";
							else if (textArr[j].equalsIgnoreCase(w10))
								out += "10";
							else if (textArr[j].equalsIgnoreCase(w11))
								out += "11";
							else if (textArr[j].equalsIgnoreCase(w12))
								out += "12";
							else
								out += textArr[j];
						} else {
							// indonesian
							if (textArr[j].equalsIgnoreCase(w0))
								out += "0";
							else if (textArr[j].equalsIgnoreCase(w1))
								out += "1";
							else if (textArr[j].equalsIgnoreCase(w2)) {
								// dealing with 'dua' dan 'dua belas'
								if (j + 1 < textArr.length && textArr[j + 1].equalsIgnoreCase(w12bID)) {
									// add the number
									out += "12";
									// skip the next word
									j++;
									// mark it as dua belas
									isDuaBelas = true;
								} else
									out += "2";
							} else if (textArr[j].equalsIgnoreCase(w3))
								out += "3";
							else if (textArr[j].equalsIgnoreCase(w4))
								out += "4";
							else if (textArr[j].equalsIgnoreCase(w5))
								out += "5";
							else if (textArr[j].equalsIgnoreCase(w6))
								out += "6";
							else if (textArr[j].equalsIgnoreCase(w7))
								out += "7";
							else if (textArr[j].equalsIgnoreCase(w8))
								out += "8";
							else if (textArr[j].equalsIgnoreCase(w9))
								out += "9";
							else if (textArr[j].equalsIgnoreCase(w10))
								out += "10";
							else if (textArr[j].equalsIgnoreCase(w11))
								out += "11";
							else
								out += textArr[j];
						}

						// cut the unprocessed text
						if (isDuaBelas == false)
							unProcessedText = unProcessedText.substring(pos + textArr[j].length());
						else
							// if dividedby, cut two words and a space
							unProcessedText = unProcessedText
									.substring(pos + textArr[j - 1].length() + 1 + textArr[j].length());

					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				// add the comment prefix and postfix
				if (t.getText().startsWith(commentSinglePrefix))
					out = commentSinglePrefix + out;
				else
					out = commentMultiPrefix + out + commentMultiPostfix;

				t.setText(out);
			}
		}
	}

	public static void c27JavaAnonymisingCommentContents(ArrayList<LibTuple> tokenString,
			String language) {
		// anonymise comment content in Java

		String out = "anonymised comment content";
		if (language.equals("id"))
			out = "konten komentar yang dianonimkan";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				if (t.getText().startsWith("//")) {
					// single line comment
					t.setText("// " + out);
				} else {
					// multi line comment

					// get remaining lines that need to be substracted to other
					// tokens
					int reducedNumOfLines = t.getText().split(System.lineSeparator()).length - 1;
					// update the line pos of remaining tokens
					for (int j = i + 1; j < tokenString.size(); j++) {
						tokenString.get(j).setLine(tokenString.get(j).getLine() - reducedNumOfLines);
					}

					// change the comment content
					t.setText("/* " + out + " */");
				}
			}
		}
	}

	public static void c27PythonAnonymisingCommentContents(
			ArrayList<LibTuple> tokenString, String language) {
		// anonymise comment content in Python

		String out = "anonymised comment content";
		if (language.equals("id"))
			out = "konten komentar yang dianonimkan";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getType().endsWith("COMMENT")) {
				// single line comment
				t.setText("# " + out);
			}
		}
	}

}
