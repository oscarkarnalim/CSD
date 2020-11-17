package disguisegenerator;

import java.util.ArrayList;

import tuple.LibTuple;

public class CodeObfuscatorConstantAndDataTypeChange {

	public static void cd01JavaChangingNonFloatingTypesToTheLargest(
			ArrayList<LibTuple> tokenString) {
		// change byte, short, and int to long. Does not guarantee to work in
		// all occasions.
		for (LibTuple t : tokenString) {
			if (t.getType().equals("'byte'") || t.getType().equals("'short'")
					|| t.getType().equals("'int'")) {
				t.setType("'long'");
				t.setText("long");
			}
		}
	}

	public static void cd02JavaChangingFloatingTypesToTheLargest(
			ArrayList<LibTuple> tokenString) {
		// change float to double Does not guarantee to work in all occasions.
		for (LibTuple t : tokenString) {
			if (t.getType().equals("'float'")) {
				t.setType("'double'");
				t.setText("double");
			}
		}
	}

	public static void cd03AddingSpaceAtEndStringLiteral(
			ArrayList<LibTuple> tokenString,
			boolean isSingleQuoteAlsoSeparator) {
		// add a space at the end of a string
		for (LibTuple t : tokenString) {
			if (t.getText().startsWith("\"")) {
				t.setText(t.getText().substring(0, t.getText().length() - 1)
						+ " \"");
			} else if (isSingleQuoteAlsoSeparator
					&& t.getText().startsWith("'")) {
				t.setText(t.getText().substring(0, t.getText().length() - 1)
						+ " \'");
			}
		}
	}

	public static void cd04AddingNewlineAtEndStringLiteral(
			ArrayList<LibTuple> tokenString,
			boolean isSingleQuoteAlsoSeparator) {
		// add a space at the end of a string
		for (LibTuple t : tokenString) {
			if (t.getText().startsWith("\"")) {
				t.setText(t.getText().substring(0, t.getText().length() - 1)
						+ "\\n\"");
			} else if (isSingleQuoteAlsoSeparator
					&& t.getText().startsWith("'")) {
				t.setText(t.getText().substring(0, t.getText().length() - 1)
						+ "\\n'");
			}
		}
	}

	public static void cd05CapitalisingFirstCharEachWord(
			ArrayList<LibTuple> tokenString,
			boolean isSingleQuoteAlsoSeparator) {
		/*
		 * for each string literal, split based on whitespace and uppercase the
		 * first char of each word.
		 */

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				// remove string prefix and postfix
				String text = t.getText();
				text = text.substring(1, text.length() - 1);

				String unProcessedText = text;
				// split the comment text, capitalise, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						String c = (textArr[j].charAt(0) + "").toUpperCase();
						if (textArr[j].length() > 1)
							c += textArr[j].substring(1, textArr[j].length());
						// put the whitespaces before textArr[j]
						int pos = unProcessedText.indexOf(textArr[j]);
						out += unProcessedText.substring(0, pos);
						// add the content
						out += c;
						// cut the unprocessed text
						unProcessedText = unProcessedText.substring(pos
								+ textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				if (t.getText().startsWith("\""))
					out = "\"" + out + "\"";
				else
					out = "'" + out + "'";

				// set the text
				t.setText(out);

			}
		}
	}

	public static void cd06CapitalisingAllChars(
			ArrayList<LibTuple> tokenString,
			boolean isSingleQuoteAlsoSeparator) {
		// capitalise all chars
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is string literal
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				String out = "";
				for (int k = 0; k < t.getText().length(); k++) {
					char c = t.getText().charAt(k);
					if (c >= 97 && c <= 122) {
						if (!(k > 0 && t.getText().charAt(k - 1) == '\\'))
							c -= 32;
					}
					out += c;
				}
				t.setText(out);
			}
		}
	}

	public static void cd07DecapitalisingAllChars(
			ArrayList<LibTuple> tokenString,
			boolean isSingleQuoteAlsoSeparator) {
		// decapitalise all chars
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is string literal
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				// this automatically deals with escape sequences as they are written in lowercase.
				t.setText(t.getText().toLowerCase());
			}
		}
	}

	public static void cd08ReplacingConjuctionSymbolsWithWords(
			ArrayList<LibTuple> tokenString, String language,
			boolean isSingleQuoteAlsoSeparator) {
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
			// check whether t is string literal
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				// remove string prefix and postfix
				String text = t.getText();
				text = text.substring(1, text.length() - 1);

				String unProcessedText = text;
				// split the text, capitalise, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the whitespaces before textArr[j]
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
						unProcessedText = unProcessedText.substring(pos
								+ textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				if (t.getText().startsWith("\""))
					out = "\"" + out + "\"";
				else
					out = "'" + out + "'";

				t.setText(out);
			}
		}
	}

	public static void cd09ReplacingConjuctionWordsWithSymbols(
			ArrayList<LibTuple> tokenString, String language,
			boolean isSingleQuoteAlsoSeparator) {
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
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				// remove string prefix and postfix
				String text = t.getText();
				text = text.substring(1, text.length() - 1);

				String unProcessedText = text;
				// split the text, capitalise, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the whitespaces before textArr[j]
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
						unProcessedText = unProcessedText.substring(pos
								+ textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				if (t.getText().startsWith("\""))
					out = "\"" + out + "\"";
				else
					out = "'" + out + "'";

				t.setText(out);
			}
		}
	}

	public static void cd10ReplacingMathSymbolsWithWords(
			ArrayList<LibTuple> tokenString, String language,
			boolean isSingleQuoteAlsoSeparator) {
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
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				// remove string prefix and postfix
				String text = t.getText();
				text = text.substring(1, text.length() - 1);

				String unProcessedText = text;
				// split the text, capitalise, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the whitespaces before textArr[j]
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
						unProcessedText = unProcessedText.substring(pos
								+ textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				if (t.getText().startsWith("\""))
					out = "\"" + out + "\"";
				else
					out = "'" + out + "'";

				t.setText(out);
			}
		}
	}

	public static void cd11ReplacingMathWordsWithSymbols(
			ArrayList<LibTuple> tokenString, String language,
			boolean isSingleQuoteAlsoSeparator) {
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
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				// remove string prefix and postfix
				String text = t.getText();
				text = text.substring(1, text.length() - 1);

				String unProcessedText = text;
				// split the text, capitalise, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the whitespaces before textArr[j]
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
						else if (textArr[j].equalsIgnoreCase(dividedWord1)
								&& j + 1 < textArr.length
								&& textArr[j + 1].equals(dividedWord2)) {
							// if this word is dividedWord1 and the next one is
							// dividedWord2
							out += "/";
							// skip next word as it has been used
							j++;
							// mark it as dividedBySymbol
							isTwoSymbols = true;
						} else if (textArr[j].equalsIgnoreCase(equalWord1)
								&& j + 1 < textArr.length
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
							unProcessedText = unProcessedText.substring(pos
									+ textArr[j].length());
						else
							// if dividedby, cut two words and a space
							unProcessedText = unProcessedText.substring(pos
									+ textArr[j - 1].length() + 1
									+ textArr[j].length());

					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				if (t.getText().startsWith("\""))
					out = "\"" + out + "\"";
				else
					out = "'" + out + "'";

				t.setText(out);
			}
		}
	}

	public static void cd12ReplacingSmallNumbersWithWords(
			ArrayList<LibTuple> tokenString, String language,
			boolean isSingleQuoteAlsoSeparator) {
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
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				// remove string prefix and postfix
				String text = t.getText();
				text = text.substring(1, text.length() - 1);

				String unProcessedText = text;
				// split the text, capitalise, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the whitespaces before textArr[j]
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
						unProcessedText = unProcessedText.substring(pos
								+ textArr[j].length());
					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				if (t.getText().startsWith("\""))
					out = "\"" + out + "\"";
				else
					out = "'" + out + "'";

				t.setText(out);
			}
		}
	}

	public static void cd13ReplacingSmallNumberWordsWithNumbers(
			ArrayList<LibTuple> tokenString, String language,
			boolean isSingleQuoteAlsoSeparator) {
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
			if (t.getText().startsWith("\"")
					|| (isSingleQuoteAlsoSeparator && t.getText().startsWith(
							"'"))) {
				// remove string prefix and postfix
				String text = t.getText();
				text = text.substring(1, text.length() - 1);

				String unProcessedText = text;
				// split the text, capitalise, and merge back
				String out = "";
				String[] textArr = text.split("\\s+");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() > 0) {
						// put the whitespaces before textArr[j]
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
								if (j + 1 < textArr.length
										&& textArr[j + 1].equalsIgnoreCase(w12bID)) {
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
							unProcessedText = unProcessedText.substring(pos
									+ textArr[j].length());
						else
							// if dividedby, cut two words and a space
							unProcessedText = unProcessedText.substring(pos
									+ textArr[j - 1].length() + 1
									+ textArr[j].length());

					}
				}
				// set the remaining whitespace
				if (unProcessedText.length() > 0)
					out += unProcessedText;

				if (t.getText().startsWith("\""))
					out = "\"" + out + "\"";
				else
					out = "'" + out + "'";

				t.setText(out);
			}
		}
	}

	public static void cd14AnonymisingStringContents(
			ArrayList<LibTuple> tokenString, String language,
			boolean isSingleQuoteAlsoSeparator) {
		// anonymise String content in Java

		String out = "anonymised string content";
		if (language.equals("id"))
			out = "konten string yang dianonimkan";

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is comment
			if (t.getText().startsWith("\"")) {
				t.setText("\"" + out + "\"");
			} else if (isSingleQuoteAlsoSeparator
					&& t.getText().startsWith("'")) {
				t.setText("'" + out + "'");
			}
		}
	}

	public static void cd15AddingExtraPrecisionForFloatingConstants(
			ArrayList<LibTuple> tokenString) {
		// add an extra precision for floating constants, does not handle
		// complex floating with e.
		for (LibTuple t : tokenString) {
			if (t.getType().equals("FloatingPointLiteral")) {
				String text = t.getText();

				// check the last char
				char lc = text.charAt(text.length() - 1);
				if (lc == 'f') {
					// float

					int ePos = Math.max(text.indexOf('e'), text.indexOf('E'));
					if (ePos == -1) {
						// standard value
						// if it has . already
						if (text.contains("."))
							t.setText(text.substring(0, text.length() - 1)
									+ "0f");
						else
							// otherwise
							t.setText(text.substring(0, text.length() - 1)
									+ ".0f");
					}
				} else if (lc == 'd') {
					// double
					int ePos = Math.max(text.indexOf('e'), text.indexOf('E'));
					if (ePos == -1) {
						// if it has . already
						if (text.contains("."))
							t.setText(text.substring(0, text.length() - 1)
									+ "0d");
						else
							// otherwise
							t.setText(text.substring(0, text.length() - 1)
									+ ".0d");
					}
				} else {
					// also double but without d
					int ePos = Math.max(text.indexOf('e'), text.indexOf('E'));
					if (ePos == -1) {
						if (text.contains("."))
							t.setText(text.substring(0, text.length()) + "0");
						else
							// otherwise
							t.setText(text.substring(0, text.length()) + ".0");
					}
				}
			}
		}
	}
}
