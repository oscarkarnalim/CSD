package language;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import support.AdditionalKeywordsManager;
import support.pythonantlr.Python3Lexer;
import tuple.LibTuple;

public class LibPythonExtractor {

	public static ArrayList<LibTuple> getDefaultTokenString(String filePath,
			String additionalKeywordsPath) {
		// take all tokens including comments and whitespaces and keep some
		// tokens as keywords
		try {
			ArrayList<LibTuple> result = new ArrayList<>();
			// build the lexer
			Lexer lexer = new Python3Lexer(new ANTLRFileStream(filePath));
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// only till size-1 as the last one is EOF token
			for (int index = 0; index < tokens.size() - 1; index++) {
				Token token = tokens.get(index);
				String type = Python3Lexer.VOCABULARY.getDisplayName(token.getType());

				// this is used to make the generated tokens similar to Java's
				if (type.equals("NAME"))
					type = "Identifier";
				else if (type.equals("FLOAT_NUMBER"))
					type = "FloatingPointLiteral";

				// remove all whitespace tokens as these tokens are the
				// summarised version of whitespaces
				if (type.equals("93") || type.equals("94") || type.equals("NEWLINE"))
					continue;

				// take all tokens excluding whitespaces
				result.add(new LibTuple(token.getText(), type, token.getLine(),
						token.getCharPositionInLine()));
			}

			if (additionalKeywordsPath != null && additionalKeywordsPath != "") {
				// read the additional keywords
				ArrayList<ArrayList<String>> additionalKeywords = AdditionalKeywordsManager
						.readAdditionalKeywords(additionalKeywordsPath);
				for (int i = 0; i < result.size(); i++) {
					// if it is the beginning of keywords
					int pos = AdditionalKeywordsManager.p4IndexOf(i, result, additionalKeywords);
					if (pos != -1) {
						// for each involved token
						for (int j = 0; j < additionalKeywords.get(pos).size(); j++) {
							LibTuple cur = result.get(i + j);
							// return back to its original value
							cur.setType("additional_keyword");
						}

						// skip the position, reduced by 1 to deal with "for"
						// loop
						// behaviour
						i += (additionalKeywords.get(pos).size() - 1);
					}
				}
			}

			// add whitespace and comment tokens
			result.addAll(_generateCommentAndWhitespaceTokens(filePath));

			// sort the result
			Collections.sort(result);

			// merging adjacent whitespaces
			for (int i = 0; i < result.size() - 1; i++) {
				LibTuple cur = result.get(i);
				LibTuple next = result.get(i + 1);
				// if there are two adjacent whitespaces, merge them
				if (cur.getType().equals("WS") && next.getType().equals("WS")) {
					// merge the text for next token
					next.setText(cur.getText() + next.getText());
					next.setRawText(cur.getRawText() + next.getRawText());
					// set line
					next.setLine(cur.getLine());
					// and remove the current one
					result.remove(i);
					i--;
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static ArrayList<LibTuple> getDefaultTokenString(String filePath,
			ArrayList<ArrayList<String>> additionalKeywords) {
		// take all tokens including comments and whitespaces and keep some
		// tokens as keywords
		try {
			ArrayList<LibTuple> result = new ArrayList<>();
			// build the lexer
			Lexer lexer = new Python3Lexer(new ANTLRFileStream(filePath));
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// only till size-1 as the last one is EOF token
			for (int index = 0; index < tokens.size() - 1; index++) {
				Token token = tokens.get(index);
				String type = Python3Lexer.VOCABULARY.getDisplayName(token.getType());

				// this is used to make the generated tokens similar to Java's
				if (type.equals("NAME"))
					type = "Identifier";
				else if (type.equals("FLOAT_NUMBER"))
					type = "FloatingPointLiteral";

				// remove all whitespace tokens as these tokens are the
				// summarised version of whitespaces
				if (type.equals("93") || type.equals("94") || type.equals("NEWLINE"))
					continue;

				// take all tokens excluding whitespaces
				result.add(new LibTuple(token.getText(), type, token.getLine(),
						token.getCharPositionInLine()));
			}

			if (additionalKeywords != null && additionalKeywords.size() != 0) {
				for (int i = 0; i < result.size(); i++) {
					// if it is the beginning of keywords
					int pos = AdditionalKeywordsManager.p4IndexOf(i, result, additionalKeywords);
					if (pos != -1) {
						// for each involved token
						for (int j = 0; j < additionalKeywords.get(pos).size(); j++) {
							LibTuple cur = result.get(i + j);
							// return back to its original value
							cur.setType("additional_keyword");
						}

						// skip the position, reduced by 1 to deal with "for"
						// loop
						// behaviour
						i += (additionalKeywords.get(pos).size() - 1);
					}
				}
			}

			// add whitespace and comment tokens
			result.addAll(_generateCommentAndWhitespaceTokens(filePath));

			// sort the result
			Collections.sort(result);

			// merging adjacent whitespaces
			for (int i = 0; i < result.size() - 1; i++) {
				LibTuple cur = result.get(i);
				LibTuple next = result.get(i + 1);
				// if there are two adjacent whitespaces, merge them
				if (cur.getType().equals("WS") && next.getType().equals("WS")) {
					// merge the text for next token
					next.setText(cur.getText() + next.getText());
					next.setRawText(cur.getRawText() + next.getRawText());
					// set line
					next.setLine(cur.getLine());
					// and remove the current one
					result.remove(i);
					i--;
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private static ArrayList<LibTuple> _generateCommentAndWhitespaceTokens(
			String filePath) {
		/*
		 * generate comment and whitespace token list and return them as one
		 */
		ArrayList<LibTuple> commentWhitespaceTokens = new ArrayList<>();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

			boolean isInTripleQuoteString = false;

			int curLine = 1; // row starts from 1 but column from 0
			String line;
			String lineWithoutComment;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("'''")) {
					if (isInTripleQuoteString) {
						// closing triple quote
						int closingPos = line.indexOf("'''");
						// replace all chars in that region as -, we will skip
						// that anyway
						String o = "";
						for (int k = 0; k < closingPos + 3; k++)
							o += "-";
						o = o + line.substring(closingPos + 3, line.length());
						// set as the line
						line = o;
						// mark to be out of that quote string
						isInTripleQuoteString = false;
					} else {
						// opening triple quote
						int openingPos = line.lastIndexOf("'''");
						// replace all chars in that region as -, we will skip
						// that anyway
						String o = "";
						for (int k = openingPos; k < line.length(); k++)
							o += "-";
						o = line.substring(0, openingPos) + o;
						// set as the line
						line = o;
						// mark to be out of that quote string
						isInTripleQuoteString = true;
					}
				} else {
					// if it is still in triple quote, skip
					if (isInTripleQuoteString) {
						// increment curLine
						curLine++;
						// skip this iteration
						continue;
					}
				}

				int commentPos = -1;
				if (isInTripleQuoteString == false)
					commentPos = getCommentStartCol(line);

				if (commentPos != -1) {
					// if there is a comment, create a line which comment is
					// removed
					lineWithoutComment = line.substring(0, commentPos);
				} else {
					lineWithoutComment = line;
				}

				// embed all whitespace tokens on that line
				String whitespacecontent = "";
				boolean isInSingleQuoteString = false;
				boolean isInDoubleQuoteString = false;
				for (int col = 0; col < lineWithoutComment.length(); col++) {
					char c = lineWithoutComment.charAt(col);
					if ((c == ' ' || c == '\t') && isInDoubleQuoteString == false && isInSingleQuoteString == false) {
						whitespacecontent += c;
					} else {
						if (c == '\'') {
							// dealing if that is escape character
							if (col > 0 && lineWithoutComment.charAt(col - 1) == '\\')
								continue;

							// dealing with spacing in single quote string
							// literal
							if (isInSingleQuoteString) {
								isInSingleQuoteString = false;
							} else if (isInDoubleQuoteString) {
								// do nothing
							} else {
								isInSingleQuoteString = true;
							}
						} else if (c == '\"') {
							// dealing if that is escape character
							if (col > 0 && lineWithoutComment.charAt(col - 1) == '\\')
								continue;

							// dealing with spacing in single quote string
							// literal
							if (isInDoubleQuoteString) {
								isInDoubleQuoteString = false;
							} else if (isInSingleQuoteString) {
								// do nothing
							} else {
								isInDoubleQuoteString = true;
							}
						}
						if (whitespacecontent.length() > 0) {
							commentWhitespaceTokens.add(new LibTuple(whitespacecontent,
									"WS", curLine, col - whitespacecontent.length()));
							whitespacecontent = "";

						}
					}
				}

				if (commentPos != -1) {
					// add the last whitespace content
					if (whitespacecontent.length() > 0) {
						int col = lineWithoutComment.length();
						commentWhitespaceTokens.add(new LibTuple(whitespacecontent, "WS",
								curLine, col - whitespacecontent.length() + 1));
					}

					// add the comment
					commentWhitespaceTokens.add(new LibTuple(line.substring(commentPos),
							"COMMENT", curLine, commentPos));

					/*
					 * add newline as it ends the line now. startPos is assured to be non-negative
					 * as for each row transition, we assume it ends at the first column of the next
					 * line.
					 */
					commentWhitespaceTokens.add(new LibTuple(System.lineSeparator(),
							"WS", curLine, Math.max(0, line.length())));
				} else {
					/*
					 * add the last whitespace content with a newline. startPos is assured to be
					 * non-negative as for each row transition, we assume it ends at the first
					 * column of the next line.
					 */
					if (isInTripleQuoteString == false) {
						whitespacecontent += System.lineSeparator();
						commentWhitespaceTokens.add(new LibTuple(whitespacecontent, "WS",
								curLine, Math.max(0, line.length() - 1)));
					}
				}

				// increment curLine
				curLine++;

			}

			// Always close files.
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return commentWhitespaceTokens;
	}
	
	public static int getCommentStartCol(String line) {
		boolean isInDoubleQuote = false;
		boolean isInSingleQuote = false;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (isInDoubleQuote) {
				if (c == '\"')
					isInDoubleQuote = false;
			} else if (isInSingleQuote) {
				if (c == '\'')
					isInSingleQuote = false;
			} else {
				if (c == '\"')
					isInDoubleQuote = true;
				else if (c == '\'')
					isInSingleQuote = true;
				else if (c == '#')
					return i;
			}
		}

		return -1;
	}

}
