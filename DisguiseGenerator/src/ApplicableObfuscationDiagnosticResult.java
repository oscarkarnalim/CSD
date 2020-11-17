

import java.util.ArrayList;
import java.util.HashSet;

import disguisegenerator.CodeObfuscatorIdentifier;
import language.LibJavaExtractor;
import language.LibPythonExtractor;
import support.NaturalLanguageProcesser;
import tuple.LibTuple;
import tuple.ObfuscatorSettingTuple;

public class ApplicableObfuscationDiagnosticResult {
	// for comment
	private boolean hasSingleLineComment, hasMultiLineComment;
	private boolean hasBlankLinesBeforeStatement;
	private boolean hasPunctuationInSingleLineComment,
			hasMoreThanNCharsInSingleLineComment;
	private boolean hasPunctuationInMultiLineComment,
			hasMoreThanNCharsInMultiLineComment;
	private boolean hasNewlineInMultiLineComment;
	private boolean hasLowercasedFirstWordCharInComment;
	private boolean hasLowercasedCharsInComment, hasUppercasedCharsInComment;
	private boolean hasConjunctionSymbolInComment, hasConjunctionWordInComment;
	private boolean hasMathSymbolInComment, hasMathWordInComment;
	private boolean hasSmallNumberInComment, hasSmallNumberWordInComment;

	// for whitespace
	private boolean hasBlankLines;
	private boolean hasTabsOrSpacesBeforeStatement;
	private boolean hasSpaces, hasTabs, hasNewlines, hasNSpaces;

	// for identifier
	private boolean hasIdentifiers;
	private boolean hasStopWordInIdentifier, has_InIdentifier,
			hasNumberInIdentifier;
	private boolean hasLowercasedCharInIdentifier,
			hasUppercasedCharInIdentifier;
	private boolean has_ForTransitionInIdentifier,
			hasCapitalisationForTransitionInIdentifier;
	private boolean hasIdentifiersWithLengthMoreThan1;
	private boolean hasVocalsToBeRemovedForAcronymInIdentifiers;

	// for constant and data type changes
	private boolean hasNonLargestDataTypeForNonFloating,
			hasNonLargestDataTypeForFloating;
	private boolean hasStringLiteral;
	private boolean hasLowercasedFirstWordCharInString;
	private boolean hasLowercasedCharsInString, hasUppercasedCharsInString;
	private boolean hasConjunctionSymbolInString, hasConjunctionWordInString;
	private boolean hasMathSymbolInString, hasMathWordInString;
	private boolean hasSmallNumberInString, hasSmallNumberWordInString;
	private boolean hasFloatingNumber;

	public static ApplicableObfuscationDiagnosticResult generateDiagnosticResult(
			ArrayList<LibTuple> tokens,
			ObfuscatorSettingTuple set) {
		ApplicableObfuscationDiagnosticResult result = new ApplicableObfuscationDiagnosticResult();

		// for hasBlankLinesBeforeStatement
		HashSet<Integer> commentedLines = new HashSet<>();

		for (int i = 0; i < tokens.size(); i++) {
			LibTuple cur = tokens.get(i);
			String text = cur.getText();
			if (cur.getType().endsWith("COMMENT")) {
				if (cur.getText().startsWith(set.getPrefixSingleLineComment())) {
					// single line comment

					// remove the comment prefix
					text = text.substring(set.getPrefixSingleLineComment()
							.length());

					// for hasSingleLineComment
					result.setHasSingleLineComment(true);

					// for hasPunctuationInSingleLineComment
					if (text.contains(".") || text.contains("?")
							|| text.contains("!") || text.contains(";"))
						result.setHasPunctuationInSingleLineComment(true);

					// for asMoreThanNCharsInSingleLineComment
					if (text.length() > set
							.getnForMaxCharsInSingleLineComment()){
						result.setHasMoreThanNCharsInSingleLineComment(true);
					}
					
					// for hasBlankLinesBeforeStatement
					commentedLines.add(cur.getLine());
				} else if (cur.getText().startsWith(
						set.getPrefixMultiLineComment())) {
					// multi line comment

					// remove the comment prefix and postfix
					text = text.substring(set.getPrefixMultiLineComment()
							.length());
					text = text.substring(0, text.length()
							- set.getPostfixMultiLineComment().length());

					// for hasMultiLineComment
					if (set.isFacilitatingMultiLineComment())
						result.setHasMultiLineComment(true);

					// for hasPunctuationInMultiLineComment
					if (text.contains(".") || text.contains("?")
							|| text.contains("!") || text.contains(";"))
						if (set.isFacilitatingMultiLineComment())
							result.setHasPunctuationInMultiLineComment(true);

					// for hasMoreThanNCharsInMultiLineComment
					if (text.length() > set.getnForMaxCharsInMultiLineComment())
						if (set.isFacilitatingMultiLineComment())
							result.setHasMoreThanNCharsInMultiLineComment(true);

					// for hasNewlineInMultiLineComment
					if (text.contains("\n"))
						if (set.isFacilitatingMultiLineComment())
							result.setHasNewlineInMultiLineComment(true);
					
					// for hasBlankLinesBeforeStatement
					int counter = text.split("\n").length;
					for(int j=0;j<counter;j++)
						commentedLines.add(cur.getLine() + j);
				}

				for (int j = 0; j < text.length(); j++) {
					char c = text.charAt(j);

					// for hasLowercasedFirstWordCharInComment (1 of 2)
					if (c == ' ' && j + 1 < text.length()
							&& text.charAt(j + 1) >= 97
							&& text.charAt(j + 1) <= 122)
						result.setHasLowercasedFirstWordCharInComment(true);

					if (c >= 97 && c <= 122) {
						// for hasLowercasedCharsInComment
						result.setHasLowercasedCharsInComment(true);

						// for hasLowercasedFirstWordCharInComment (2 of 2)
						if (j == 0)
							result.setHasLowercasedFirstWordCharInComment(true);
					}

					// for hasUppercasedCharsInComment
					if (c >= 65 && c <= 90)
						result.setHasUppercasedCharsInComment(true);
				}

				String[] commentWords = text.toLowerCase().split("\\s+");
				for (int k = 0; k < commentWords.length; k++) {
					String w = commentWords[k];

					// for hasConjunctionSymbolInComment
					if (w.equals("&") || w.equals("/"))
						result.setHasConjunctionSymbolInComment(true);

					// for hasMathSymbolInComment
					if (w.equals("+") || w.equals("-") || w.equals("*")
							|| w.equals("/") || w.equals("="))
						result.setHasMathSymbolInComment(true);
				}

				commentWords = text.toLowerCase().split("[^A-Za-z0-9]");
				for (int k = 0; k < commentWords.length; k++) {
					String w = commentWords[k];
					if (set.getHumanLanguage().equals("en")) {
						// english

						// for hasConjunctionWordInComment
						if (w.equals("and") || w.equals("or"))
							result.setHasConjunctionWordInComment(true);

						// for hasMathWordInComment
						if (w.equals("plus")
								|| w.equals("minus")
								|| w.equals("times")
								|| (w.equals("divided")
										&& k + 1 < commentWords.length && commentWords[k + 1]
										.equals("by"))
								|| (w.equals("equals")
										&& k + 1 < commentWords.length && commentWords[k + 1]
										.equals("to")))
							result.setHasMathWordInComment(true);

						// for hasSmallNumberWordInComment
						if (w.equals("zero") || w.equals("one")
								|| w.equals("two") || w.equals("three")
								|| w.equals("four") || w.equals("five")
								|| w.equals("six") || w.equals("seven")
								|| w.equals("eight") || w.equals("nine")
								|| w.equals("ten") || w.equals("eleven")
								|| w.equals("twelve"))
							result.setHasSmallNumberWordInComment(true);
					} else {
						// indonesian

						// for hasConjunctionWordInComment
						if (w.equals("dan") || w.equals("atau"))
							result.setHasConjunctionWordInComment(true);

						// for hasMathWordInComment(
						if (w.equals("tambah")
								|| w.equals("kurang")
								|| w.equals("kali")
								|| (w.equals("dibagi")
										&& k + 1 < commentWords.length && commentWords[k + 1]
										.equals("dengan"))
								|| (w.equals("sama")
										&& k + 1 < commentWords.length && commentWords[k + 1]
										.equals("dengan")))
							result.setHasMathWordInComment(true);

						// for hasSmallNumberWordInComment
						if (w.equals("nol")
								|| w.equals("satu")
								|| w.equals("dua")
								|| w.equals("tiga")
								|| w.equals("empat")
								|| w.equals("lima")
								|| w.equals("enam")
								|| w.equals("tujuh")
								|| w.equals("delapan")
								|| w.equals("sembilan")
								|| w.equals("sepuluh")
								|| w.equals("sebelas")
								|| (w.equals("dua")
										&& k + 1 < commentWords.length && commentWords[k + 1]
										.equals("belas")))
							result.setHasSmallNumberWordInComment(true);
					}

					// for hasSmallNumberWordInComment
					if (w.equals("0") || w.equals("1") || w.equals("2")
							|| w.equals("3") || w.equals("4") || w.equals("5")
							|| w.equals("6") || w.equals("7") || w.equals("8")
							|| w.equals("9") || w.equals("10")
							|| w.equals("11") || w.equals("12"))
						result.setHasSmallNumberInComment(true);
				}
			} else if (cur.getType().equals("WS")) {
				// for whitespaces

				for (int j = 0; j < text.length(); j++) {
					char c = text.charAt(j);
					// for hasNewlines
					if (c == '\n')
						result.setHasNewlines(true);
					// for hasTabs
					else if (c == '\t')
						result.setHasTabs(true);
					// for hasSpaces
					else if (c == ' ')
						result.setHasSpaces(true);
				}

				// for hasBlankLines
				int firstIndex = text.indexOf("\n");
				int lastIndex = text.lastIndexOf("\n");
				if (firstIndex != -1 && lastIndex != -1
						&& firstIndex != lastIndex)
					result.setHasBlankLines(true);

				// for hasNSpaces
				String nSpaces = "";
				for (int z = 0; z < set.getnForSpacesReplacedByTab(); z++)
					nSpaces += " ";
				if (text.contains(nSpaces))
					result.setHasNSpaces(true);

				// for hasTabsOrSpacesBeforeStatement
				lastIndex = text.lastIndexOf("\n");
				if (lastIndex != -1
						&& lastIndex + System.lineSeparator().length() < text
								.length())
					if (set.isWhitespacesIgnorable())
						result.setHasTabsOrSpacesBeforeStatement(true);
			} else if (cur.getType().equals("Identifier")) {
				// for identifiers

				// for hasIdentifiers
				result.setHasIdentifiers(true);

				// for hasIdentifiersWithLengthMoreThan1
				if (text.length() > 1)
					result.setHasIdentifiersWithLengthMoreThan1(true);

				ArrayList<String> identWords = CodeObfuscatorIdentifier
						.tokenizeIdentifier(text);
				for (String w : identWords) {
					// for hasStopWordInIdentifier
					if (NaturalLanguageProcesser.isStopWord(w.toLowerCase(),
							set.getHumanLanguage())) {
						result.setHasStopWordInIdentifier(true);
					}
				}

				// for has_InIdentifier
				if (text.contains("_"))
					result.setHas_InIdentifier(true);

				// for hasNumberInIdentifier
				if (text.contains("0") || text.contains("1")
						|| text.contains("2") || text.contains("3")
						|| text.contains("4") || text.contains("5")
						|| text.contains("6") || text.contains("7")
						|| text.contains("8") || text.contains("9"))
					result.setHasNumberInIdentifier(true);

				for (int j = 0; j < text.length(); j++) {
					char c = text.charAt(j);

					// for hasLowercasedCharInIdentifier
					if (c >= 97 && c <= 122)
						result.setHasLowercasedCharInIdentifier(true);

					if (c >= 65 && c <= 90) {
						// for hasUppercasedCharInIdentifier
						result.setHasUppercasedCharInIdentifier(true);

						/*
						 * if it is located not in the first pos and the
						 * previous one is lowercased one, mark
						 * hasCapitalisationForTransitionInIdentifier
						 */
						if (j != 0 && text.charAt(j - 1) >= 97
								&& text.charAt(j - 1) <= 122)
							result.setHasCapitalisationForTransitionInIdentifier(true);
					}

					/*
					 * if _ is located in the middle of identifier, mark
					 * has_ForTransitionInIdentifier
					 */
					if (c == '_' && j != 0 && j != text.length() - 1)
						result.setHas_ForTransitionInIdentifier(true);

					/*
					 * if at least a vocal is located in the middle of
					 * identifier, mark
					 * hasVocalsToBeRemovedForAcronymInIdentifiers
					 */
					if ((c == 'a' || c == 'A' || c == 'i' || c == 'I'
							|| c == 'u' || c == 'U' || c == 'e' || c == 'E'
							|| c == 'o' || c == 'O')
							&& j != 0 && j != text.length() - 1)
						result.setHasVocalsToBeRemovedForAcronymInIdentifiers(true);
				}
			} else if (set.isDataTypeSensitive()
					&& (cur.getType().equals("'byte'")
							|| cur.getType().equals("'short'") || cur.getType()
							.equals("'int'"))) {
				// for hasNonLargestDataTypeForNonFloating
				result.setHasNonLargestDataTypeForNonFloating(true);

			} else if (set.isDataTypeSensitive()
					&& cur.getType().equals("'float'")) {
				// for hasNonLargestDataTypeForFloating
				result.setHasNonLargestDataTypeForFloating(true);

			} else if (cur.getText().startsWith("\"")
					|| (set.isSingleQuoteAlsoStringSeparator() && cur.getText()
							.startsWith("'"))) {
				// for string literal

				// remove the text's prefix and postfix
				text = text.substring(1, text.length() - 1);

				// for hasStringLiteral
				result.setHasStringLiteral(true);

				for (int j = 0; j < text.length(); j++) {
					char c = text.charAt(j);

					// for hasLowercasedFirstWordCharInString (1 of 2)
					if (c == ' ' && j + 1 < text.length()
							&& text.charAt(j + 1) >= 97
							&& text.charAt(j + 1) <= 122)
						result.setHasLowercasedFirstWordCharInString(true);

					if (c >= 97 && c <= 122) {
						// for hasLowercasedCharsInString
						if (!(j > 0 && text.charAt(j - 1) == '\\'))
							result.setHasLowercasedCharsInString(true);

						// for hasLowercasedFirstWordCharInString (2 of 2)
						if (j == 0)
							result.setHasLowercasedFirstWordCharInString(true);
					}

					// for hasUppercasedCharsInString
					if (c >= 65 && c <= 90)
						result.setHasUppercasedCharsInString(true);
				}

				String[] StringLiteralWords = text.toLowerCase().split("\\s+");
				for (int k = 0; k < StringLiteralWords.length; k++) {
					String w = StringLiteralWords[k];
					if (set.getHumanLanguage().equals("en")) {
						// english

						// for hasConjunctionWordInString
						if (w.equals("and") || w.equals("or"))
							result.setHasConjunctionWordInString(true);

						// for hasMathWordInString
						if (w.equals("plus")
								|| w.equals("minus")
								|| w.equals("times")
								|| (w.equals("divided")
										&& k + 1 < StringLiteralWords.length && StringLiteralWords[k + 1]
										.equals("by"))
								|| (w.equals("equals")
										&& k + 1 < StringLiteralWords.length && StringLiteralWords[k + 1]
										.equals("to")))
							result.setHasMathWordInString(true);

						// for hasSmallNumberWordInString
						if (w.equals("zero") || w.equals("one")
								|| w.equals("two") || w.equals("three")
								|| w.equals("four") || w.equals("five")
								|| w.equals("six") || w.equals("seven")
								|| w.equals("eight") || w.equals("nine")
								|| w.equals("ten") || w.equals("eleven")
								|| w.equals("twelve"))
							result.setHasSmallNumberWordInString(true);
					} else {
						// indonesian

						// for hasConjunctionWordInString
						if (w.equals("dan") || w.equals("atau"))
							result.setHasConjunctionWordInString(true);

						// for hasMathWordInString
						if (w.equals("tambah")
								|| w.equals("kurang")
								|| w.equals("kali")
								|| (w.equals("dibagi")
										&& k + 1 < StringLiteralWords.length && StringLiteralWords[k + 1]
										.equals("dengan"))
								|| (w.equals("sama")
										&& k + 1 < StringLiteralWords.length && StringLiteralWords[k + 1]
										.equals("dengan")))
							result.setHasMathWordInString(true);

						// for hasSmallNumberWordInString
						if (w.equals("nol")
								|| w.equals("satu")
								|| w.equals("dua")
								|| w.equals("tiga")
								|| w.equals("empat")
								|| w.equals("lima")
								|| w.equals("enam")
								|| w.equals("tujuh")
								|| w.equals("delapan")
								|| w.equals("sembilan")
								|| w.equals("sepuluh")
								|| w.equals("sebelas")
								|| (w.equals("dua")
										&& k + 1 < StringLiteralWords.length && StringLiteralWords[k + 1]
										.equals("belas")))
							result.setHasSmallNumberWordInString(true);
					}

					// for hasConjunctionSymbolInString
					if (w.equals("&") || w.equals("/"))
						result.setHasConjunctionSymbolInString(true);

					// for hasMathSymbolInString
					if (w.equals("+") || w.equals("-") || w.equals("*")
							|| w.equals("/") || w.equals("="))
						result.setHasMathSymbolInString(true);

					// for hasSmallNumberWordInString
					if (w.equals("0") || w.equals("1") || w.equals("2")
							|| w.equals("3") || w.equals("4") || w.equals("5")
							|| w.equals("6") || w.equals("7") || w.equals("8")
							|| w.equals("9") || w.equals("10")
							|| w.equals("11") || w.equals("12"))
						result.setHasSmallNumberInString(true);

				}
			} else if (cur.getType().equals("FloatingPointLiteral")) {
				// for hasFloatingNumber
				result.setHasFloatingNumber(true);
			}

			// ident or keyword
			if (cur.getText().matches("[a-zA-Z0-9]+")) {
				// for hasBlankLinesBeforeStatement
				LibTuple prevSyntaxT = null;
				for (int j = i - 1; j >= 0; j--) {
					LibTuple temp = tokens.get(j);
					if (!temp.getType().endsWith("COMMENT")
							&& !temp.getType().equals("WS")) {
						prevSyntaxT = temp;
						break;
					}
				}
				if (prevSyntaxT != null
						&& cur.getLine() - prevSyntaxT.getLine() >= 2
						&& !commentedLines.contains(cur.getLine() - 1))
					result.setHasBlankLinesBeforeStatement(true);
			}
		}

		return result;
	}

	public String toString() {
		String out = "";

		out += "comment:\n";
		out += "hasSingleLineComment:                " + hasSingleLineComment
				+ "\n";
		out += "[j]hasMultiLineComment:              " + hasMultiLineComment
				+ "\n";
		out += "hasBlankLinesBeforeStatement:        "
				+ hasBlankLinesBeforeStatement + "\n";
		out += "hasPunctuationInSingleLineComment:   "
				+ hasPunctuationInSingleLineComment + "\n";
		out += "hasMoreThanNCharsInSingleLineComment:"
				+ hasMoreThanNCharsInSingleLineComment + "\n";
		out += "[j]hasPunctuationInMultiComment:     "
				+ hasPunctuationInMultiLineComment + "\n";
		out += "[j]hasMoreThanNCharsInMultiComment:  "
				+ hasMoreThanNCharsInMultiLineComment + "\n";
		out += "[j]hasNewlineInMultiLineComment:     "
				+ hasNewlineInMultiLineComment + "\n";
		out += "hasLowercasedFirstWordCharInComment: "
				+ hasLowercasedFirstWordCharInComment + "\n";
		out += "hasLowercasedCharsInComment:         "
				+ hasLowercasedCharsInComment + "\n";
		out += "hasUppercasedCharsInComment:         "
				+ hasUppercasedCharsInComment + "\n";
		out += "hasConjunctionSymbolInComment:       "
				+ hasConjunctionSymbolInComment + "\n";
		out += "hasConjunctionWordInComment:         "
				+ hasConjunctionWordInComment + "\n";
		out += "hasMathSymbolInComment:              " + hasMathSymbolInComment
				+ "\n";
		out += "hasMathWordInComment:                " + hasMathWordInComment
				+ "\n";
		out += "hasSmallNumberInComment:             "
				+ hasSmallNumberInComment + "\n";
		out += "hasSmallNumberWordInComment:         "
				+ hasSmallNumberWordInComment + "\n";

		out += "\nwhitespace:\n";
		out += "hasBlankLines:                       " + hasBlankLines + "\n";
		out += "[j]hasTabsOrSpacesBeforeStatement:   "
				+ hasTabsOrSpacesBeforeStatement + "\n";
		out += "hasSpaces:                           " + hasSpaces + "\n";
		out += "hasTabs:                             " + hasTabs + "\n";
		out += "hasNewlines:                         " + hasNewlines + "\n";
		out += "hasNSpaces:                          " + hasNSpaces + "\n";

		out += "\nidentifier:\n";
		out += "hasIdentifiers:                      " + hasIdentifiers + "\n";
		out += "hasStopWordInIdentifier:             "
				+ hasStopWordInIdentifier + "\n";
		out += "has_InIdentifier:                    " + has_InIdentifier
				+ "\n";
		out += "hasNumberInIdentifier:               " + hasNumberInIdentifier
				+ "\n";
		out += "hasLowercasedCharInIdentifier:       "
				+ hasLowercasedCharInIdentifier + "\n";
		out += "hasUppercasedCharInIdentifier:       "
				+ hasUppercasedCharInIdentifier + "\n";
		out += "has_ForTransitionInIdentifier:       "
				+ has_ForTransitionInIdentifier + "\n";
		out += "hasCapitalForTransitionInIdentifier: "
				+ hasCapitalisationForTransitionInIdentifier + "\n";
		out += "hasIdentifiersWithLengthMoreThan1:   "
				+ hasIdentifiersWithLengthMoreThan1 + "\n";
		out += "hasVocalsForAcronymInIdentifiers:    "
				+ hasVocalsToBeRemovedForAcronymInIdentifiers + "\n";

		out += "\n[j]hasNonLargestDataTypeForNonFloat: "
				+ hasNonLargestDataTypeForNonFloating + "\n";
		out += "[j]hasNonLargestDataTypeForFloat:    "
				+ hasNonLargestDataTypeForFloating + "\n";
		out += "hasStringLiteral:                    " + hasStringLiteral
				+ "\n";
		out += "hasLowercasedFirstWordCharInString:  "
				+ hasLowercasedFirstWordCharInString + "\n";
		out += "hasLowercasedCharsInString:          "
				+ hasLowercasedCharsInString + "\n";
		out += "hasUppercasedCharsInString:          "
				+ hasUppercasedCharsInString + "\n";
		out += "hasConjunctionSymbolInString:        "
				+ hasConjunctionSymbolInString + "\n";
		out += "hasConjunctionWordInString:          "
				+ hasConjunctionWordInString + "\n";
		out += "hasMathSymbolInString:               " + hasMathSymbolInString
				+ "\n";
		out += "hasMathWordInString:                 " + hasMathWordInString
				+ "\n";
		out += "hasSmallNumberInString:              " + hasSmallNumberInString
				+ "\n";
		out += "hasSmallNumberWordInString:          "
				+ hasSmallNumberWordInString + "\n";
		out += "hasFloatingNumber:                   " + hasFloatingNumber
				+ "\n";

		return out;
	}

	public static boolean[] getApplicableDisguises(
			ApplicableObfuscationDiagnosticResult d, ObfuscatorSettingTuple set) {
		/*
		 * this method returns an array of booleans in which each element
		 * represents whether a particular disguise is applicable.
		 */
		boolean[] result = new boolean[60];

		// comment 0-26

		// 0 removing some single-line comments
		if (d.isHasSingleLineComment())
			result[0] = true;

		// 1 removing all single-line comments
		if (d.isHasSingleLineComment())
			result[1] = true;

		// 2 removing some multi-line comments.
		if (d.isHasMultiLineComment() && set.isFacilitatingMultiLineComment())
			result[2] = true;

		// 3 removing all multi-line comments.
		if (d.isHasMultiLineComment() && set.isFacilitatingMultiLineComment())
			result[3] = true;

		// 4 removing some comments.
		if ((d.isHasSingleLineComment() || d.isHasMultiLineComment())
				&& set.isFacilitatingMultiLineComment())
			result[4] = true;

		// 5 removing all comments.
		if ((d.isHasSingleLineComment() || d.isHasMultiLineComment())
				&& set.isFacilitatingMultiLineComment())
			result[5] = true;

		// 6 adding a single-line comment for each line with syntax, which
		// content is randomly generated.
		if (d.isHasBlankLinesBeforeStatement())
			result[6] = true;

		// 7 adding a multi-line comment for each line with syntax, which
		// content is randomly generated.
		if (d.isHasBlankLinesBeforeStatement()
				&& set.isFacilitatingMultiLineComment())
			result[7] = true;

		// 8 changing each single-line comment to the multi-line one.
		if (d.isHasSingleLineComment() && set.isFacilitatingMultiLineComment())
			result[8] = true;

		// 9 changing each single-line comment to the multi-line one with '.',
		// '?', '!', and ';' as the line separators.
		if (d.isHasPunctuationInSingleLineComment()
				&& set.isFacilitatingMultiLineComment())
			result[9] = true;

		// 10 changing each single-line comment to the multi-line one with n
		// characters per line.
		if (d.isHasMoreThanNCharsInSingleLineComment()
				&& set.isFacilitatingMultiLineComment())
			result[10] = true;

		// 11 splitting each single-line comment to several single-line
		// comments with '.', '?', '!', and ';' as the line separators.
		if (d.isHasPunctuationInSingleLineComment())
			result[11] = true;

		// 12 splitting each single-line comment to several single-line
		// comments with n characters per line.
		if (d.isHasMoreThanNCharsInSingleLineComment())
			result[12] = true;

		// 13 changing each multi-line comment to the single-line one with all
		// newlines removed.
		if (d.isHasNewlineInMultiLineComment()
				&& set.isFacilitatingMultiLineComment())
			result[13] = true;

		// 14 splitting each multi-line comment to several single-line comments
		// with newline as the line separators.
		if (d.isHasNewlineInMultiLineComment()
				&& set.isFacilitatingMultiLineComment())
			result[14] = true;

		// 15 splitting each multi-line comment to several single-line comments
		// with '.', '?', '!', and ';' as the line separators.
		if (d.isHasPunctuationInMultiLineComment()
				&& set.isFacilitatingMultiLineComment())
			result[15] = true;

		// 16 splitting each multi-line comment to several single-line comments
		// with n characters per line.
		if (d.isHasMoreThanNCharsInMultiLineComment()
				&& set.isFacilitatingMultiLineComment())
			result[16] = true;

		// 17 capitalising the first character of each comment word.
		if (d.isHasLowercasedFirstWordCharInComment())
			result[17] = true;

		// 18 capitalising all comment characters.
		if (d.isHasLowercasedCharsInComment())
			result[18] = true;

		// 19 decapitalising all comment characters.
		if (d.isHasUppercasedCharsInComment())
			result[19] = true;

		// 20 replacing conjuction symbols with their corresponding words in
		// comments.
		if (d.isHasConjunctionSymbolInComment())
			result[20] = true;

		// 21 replacing conjuction words with their corresponding symbols in
		// comments.
		if (d.isHasConjunctionWordInComment())
			result[21] = true;

		// 22 replacing math operators with their corresponding words in
		// comments. (+,-,*,/,=)
		if (d.isHasMathSymbolInComment())
			result[22] = true;

		// 23 replacing math words with their corresponding operators in
		// comments. (+,-,*,/,=)
		if (d.isHasMathWordInComment())
			result[23] = true;

		// 24 replacing small numbers (<12) with their corresponding words in
		// comments.
		if (d.isHasSmallNumberInComment())
			result[24] = true;

		// 25 replacing small number words (<12) with their corresponding
		// numbers in comments.
		if (d.isHasSmallNumberWordInComment())
			result[25] = true;

		// 26 anonymising all comment contents as 'anonymised comments'
		if (d.isHasSingleLineComment() || d.isHasMultiLineComment())
			result[26] = true;

		// whitespace 27-34

		// 27 removing all blank newlines.
		if (d.isHasBlankLines())
			result[27] = true;

		// 28 removing all tabs and spaces before each statement. Not applicable
		// for Python.
		if (d.isHasTabsOrSpacesBeforeStatement()
				&& set.isWhitespacesIgnorable())
			result[28] = true;

		// 29 replacing each space with n spaces.
		if (d.isHasSpaces())
			result[29] = true;

		// 30 replacing each tab with n tabs.
		if (d.isHasTabs())
			result[30] = true;

		// 31 replacing each newline with n newlines.
		if (d.isHasNewlines())
			result[31] = true;

		// 32 replacing all tabs with n spaces.
		if (d.isHasTabs())
			result[32] = true;

		// 33 replacing all n spaces with tabs.
		if (d.isHasNSpaces())
			result[33] = true;

		// 34 reformat the whitespaces. Always possible without checking.
		result[34] = true;

		// identifier 35-44

		// 35 removing all stop words from the identifiers' sub-words if these
		// sub-words are separated by underscore or next character
		// capitalisation.
		if (d.isHasStopWordInIdentifier())
			result[35] = true;

		// 36 removing all underscores from the identifiers.
		if (d.isHas_InIdentifier())
			result[36] = true;

		// 37 removing all numbers from the identifiers.
		if (d.isHasNumberInIdentifier())
			result[37] = true;

		// 38 capitalising all identifier's characters.
		if (d.isHasLowercasedCharInIdentifier())
			result[38] = true;

		// 39 decapitalising all identifier's characters.
		if (d.isHasUppercasedCharInIdentifier())
			result[39] = true;

		// 40 replacing all identifiers' sub-word transitions from underscore to
		// next character capitalisation (e.g., 'this_is_var' to 'thisIsVar').
		if (d.isHas_ForTransitionInIdentifier())
			result[40] = true;

		// 41 replacing all identifiers' sub-word transitions from next
		// character capitalisation to underscore (e.g., 'thisIsVar' to
		// 'this_is_var').
		if (d.isHasCapitalisationForTransitionInIdentifier())
			result[41] = true;

		// 42 renaming all identifiers by keeping only the first character each.
		if (d.isHasIdentifiersWithLengthMoreThan1())
			result[42] = true;

		// 43 renaming all identifiers by keeping their acronyms (generated by
		// removing all vocals except the first char).
		if (d.isHasVocalsToBeRemovedForAcronymInIdentifiers())
			result[43] = true;

		// 44 anonymising all identifiers by renaming them as 'anonymisedIdent'.
		if (d.isHasIdentifiers())
			result[44] = true;

		// constant and data type change 45-59

		// 45 changing all non-floating data types to the largest data type.
		if (d.isHasNonLargestDataTypeForNonFloating()
				&& set.isDataTypeSensitive())
			result[45] = true;

		// 46 changing all floating data types to the largest data type.
		if (d.isHasNonLargestDataTypeForFloating() && set.isDataTypeSensitive())
			result[46] = true;

		// 47 adding a blank space at the end of each string literal.
		if (d.isHasStringLiteral())
			result[47] = true;

		// 48 adding a newline at the end of each string literal.
		if (d.isHasStringLiteral())
			result[48] = true;

		// 49 capitalising the first character of each string literal word.
		if (d.isHasLowercasedFirstWordCharInString())
			result[49] = true;

		// 50 capitalising all string characters.
		if (d.isHasLowercasedCharsInString())
			result[50] = true;

		// 51 decapitalising all string characters.
		if (d.isHasUppercasedCharsInString())
			result[51] = true;

		// 52 replacing conjuction symbols with their corresponding words in
		// strings.
		if (d.isHasConjunctionSymbolInString())
			result[52] = true;

		// 53 replacing conjuction words with their corresponding symbols in
		// strings.
		if (d.isHasConjunctionWordInString())
			result[53] = true;

		// 54 replacing math operators with their corresponding words in
		// strings. (+,-,*,/,=)
		if (d.isHasMathSymbolInString())
			result[54] = true;

		// 55 replacing math words with their corresponding operators in
		// strings. (+,-,*,/,=)
		if (d.isHasMathWordInString())
			result[55] = true;

		// 56 replacing small numbers (<12) with their corresponding words in
		// strings.
		if (d.isHasSmallNumberInString())
			result[56] = true;

		// 57 replacing small number words (<12) with their corresponding
		// numbers in strings.
		if (d.isHasSmallNumberWordInString())
			result[57] = true;

		// 58 anonymising all string contents as 'anonymised string content'
		if (d.isHasStringLiteral())
			result[58] = true;

		// 59 adding more precision for floating constants.
		if (d.isHasFloatingNumber())
			result[59] = true;

		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputpath = "C:\\Users\\c3287347\\Desktop\\testing data\\ObfExample.java";
		String additionalkeywordspath = "java input output keywords.txt";
		ArrayList<LibTuple> tokens = LibJavaExtractor
				.getDefaultTokenString(inputpath, additionalkeywordspath);
		ApplicableObfuscationDiagnosticResult result = generateDiagnosticResult(
				tokens, new ObfuscatorSettingTuple(10, 2, 2, "//", "/*", "*/",
						"en", true, false, true, 0.5, 0.5, 0.5, 2, 2, 2, 2));

		inputpath = "C:\\Users\\c3287347\\Desktop\\testing data\\ObfExample.py";
		additionalkeywordspath = "python input output and JES keywords.txt";
		tokens = LibPythonExtractor.getDefaultTokenString(inputpath,
				additionalkeywordspath);
		result = generateDiagnosticResult(tokens, new ObfuscatorSettingTuple(
				10, 2, 2, "#", "", "", "id", false, true, false, 0.5, 0.5, 0.5,
				2, 2, 2, 2));

		System.out.println(result);

	}

	public boolean isHasSingleLineComment() {
		return hasSingleLineComment;
	}

	public void setHasSingleLineComment(boolean hasSingleLineComment) {
		this.hasSingleLineComment = hasSingleLineComment;
	}

	public boolean isHasMultiLineComment() {
		return hasMultiLineComment;
	}

	public void setHasMultiLineComment(boolean hasMultiLineComment) {
		this.hasMultiLineComment = hasMultiLineComment;
	}

	public boolean isHasBlankLinesBeforeStatement() {
		return hasBlankLinesBeforeStatement;
	}

	public void setHasBlankLinesBeforeStatement(
			boolean hasBlankLinesBeforeStatement) {
		this.hasBlankLinesBeforeStatement = hasBlankLinesBeforeStatement;
	}

	public boolean isHasPunctuationInSingleLineComment() {
		return hasPunctuationInSingleLineComment;
	}

	public void setHasPunctuationInSingleLineComment(
			boolean hasPunctuationInSingleLineComment) {
		this.hasPunctuationInSingleLineComment = hasPunctuationInSingleLineComment;
	}

	public boolean isHasMoreThanNCharsInSingleLineComment() {
		return hasMoreThanNCharsInSingleLineComment;
	}

	public void setHasMoreThanNCharsInSingleLineComment(
			boolean hasMoreThanNCharsInSingleLineComment) {
		this.hasMoreThanNCharsInSingleLineComment = hasMoreThanNCharsInSingleLineComment;
	}

	public boolean isHasPunctuationInMultiLineComment() {
		return hasPunctuationInMultiLineComment;
	}

	public void setHasPunctuationInMultiLineComment(
			boolean hasPunctuationInMultiLineComment) {
		this.hasPunctuationInMultiLineComment = hasPunctuationInMultiLineComment;
	}

	public boolean isHasMoreThanNCharsInMultiLineComment() {
		return hasMoreThanNCharsInMultiLineComment;
	}

	public void setHasMoreThanNCharsInMultiLineComment(
			boolean hasMoreThanNCharsInMultiLineComment) {
		this.hasMoreThanNCharsInMultiLineComment = hasMoreThanNCharsInMultiLineComment;
	}

	public boolean isHasNewlineInMultiLineComment() {
		return hasNewlineInMultiLineComment;
	}

	public void setHasNewlineInMultiLineComment(
			boolean hasNewlineInMultiLineComment) {
		this.hasNewlineInMultiLineComment = hasNewlineInMultiLineComment;
	}

	public boolean isHasLowercasedFirstWordCharInComment() {
		return hasLowercasedFirstWordCharInComment;
	}

	public void setHasLowercasedFirstWordCharInComment(
			boolean hasLowercasedFirstWordCharInComment) {
		this.hasLowercasedFirstWordCharInComment = hasLowercasedFirstWordCharInComment;
	}

	public boolean isHasLowercasedCharsInComment() {
		return hasLowercasedCharsInComment;
	}

	public void setHasLowercasedCharsInComment(
			boolean hasLowercasedCharsInComment) {
		this.hasLowercasedCharsInComment = hasLowercasedCharsInComment;
	}

	public boolean isHasUppercasedCharsInComment() {
		return hasUppercasedCharsInComment;
	}

	public void setHasUppercasedCharsInComment(
			boolean hasUppercasedCharsInComment) {
		this.hasUppercasedCharsInComment = hasUppercasedCharsInComment;
	}

	public boolean isHasConjunctionSymbolInComment() {
		return hasConjunctionSymbolInComment;
	}

	public void setHasConjunctionSymbolInComment(
			boolean hasConjunctionSymbolInComment) {
		this.hasConjunctionSymbolInComment = hasConjunctionSymbolInComment;
	}

	public boolean isHasConjunctionWordInComment() {
		return hasConjunctionWordInComment;
	}

	public void setHasConjunctionWordInComment(
			boolean hasConjunctionWordInComment) {
		this.hasConjunctionWordInComment = hasConjunctionWordInComment;
	}

	public boolean isHasMathSymbolInComment() {
		return hasMathSymbolInComment;
	}

	public void setHasMathSymbolInComment(boolean hasMathSymbolInComment) {
		this.hasMathSymbolInComment = hasMathSymbolInComment;
	}

	public boolean isHasMathWordInComment() {
		return hasMathWordInComment;
	}

	public void setHasMathWordInComment(boolean hasMathWordInComment) {
		this.hasMathWordInComment = hasMathWordInComment;
	}

	public boolean isHasSmallNumberInComment() {
		return hasSmallNumberInComment;
	}

	public void setHasSmallNumberInComment(boolean hasSmallNumberInComment) {
		this.hasSmallNumberInComment = hasSmallNumberInComment;
	}

	public boolean isHasSmallNumberWordInComment() {
		return hasSmallNumberWordInComment;
	}

	public void setHasSmallNumberWordInComment(
			boolean hasSmallNumberWordInComment) {
		this.hasSmallNumberWordInComment = hasSmallNumberWordInComment;
	}

	public boolean isHasBlankLines() {
		return hasBlankLines;
	}

	public void setHasBlankLines(boolean hasBlankLines) {
		this.hasBlankLines = hasBlankLines;
	}

	public boolean isHasTabsOrSpacesBeforeStatement() {
		return hasTabsOrSpacesBeforeStatement;
	}

	public void setHasTabsOrSpacesBeforeStatement(
			boolean hasTabsOrSpacesBeforeStatement) {
		this.hasTabsOrSpacesBeforeStatement = hasTabsOrSpacesBeforeStatement;
	}

	public boolean isHasSpaces() {
		return hasSpaces;
	}

	public void setHasSpaces(boolean hasSpaces) {
		this.hasSpaces = hasSpaces;
	}

	public boolean isHasTabs() {
		return hasTabs;
	}

	public void setHasTabs(boolean hasTabs) {
		this.hasTabs = hasTabs;
	}

	public boolean isHasNewlines() {
		return hasNewlines;
	}

	public void setHasNewlines(boolean hasNewlines) {
		this.hasNewlines = hasNewlines;
	}

	public boolean isHasNSpaces() {
		return hasNSpaces;
	}

	public void setHasNSpaces(boolean hasNSpaces) {
		this.hasNSpaces = hasNSpaces;
	}

	public boolean isHasIdentifiers() {
		return hasIdentifiers;
	}

	public void setHasIdentifiers(boolean hasIdentifiers) {
		this.hasIdentifiers = hasIdentifiers;
	}

	public boolean isHasStopWordInIdentifier() {
		return hasStopWordInIdentifier;
	}

	public void setHasStopWordInIdentifier(boolean hasStopWordInIdentifier) {
		this.hasStopWordInIdentifier = hasStopWordInIdentifier;
	}

	public boolean isHas_InIdentifier() {
		return has_InIdentifier;
	}

	public void setHas_InIdentifier(boolean has_InIdentifier) {
		this.has_InIdentifier = has_InIdentifier;
	}

	public boolean isHasNumberInIdentifier() {
		return hasNumberInIdentifier;
	}

	public void setHasNumberInIdentifier(boolean hasNumberInIdentifier) {
		this.hasNumberInIdentifier = hasNumberInIdentifier;
	}

	public boolean isHasLowercasedCharInIdentifier() {
		return hasLowercasedCharInIdentifier;
	}

	public void setHasLowercasedCharInIdentifier(
			boolean hasLowercasedCharInIdentifier) {
		this.hasLowercasedCharInIdentifier = hasLowercasedCharInIdentifier;
	}

	public boolean isHasUppercasedCharInIdentifier() {
		return hasUppercasedCharInIdentifier;
	}

	public void setHasUppercasedCharInIdentifier(
			boolean hasUppercasedCharInIdentifier) {
		this.hasUppercasedCharInIdentifier = hasUppercasedCharInIdentifier;
	}

	public boolean isHas_ForTransitionInIdentifier() {
		return has_ForTransitionInIdentifier;
	}

	public void setHas_ForTransitionInIdentifier(
			boolean has_ForTransitionInIdentifier) {
		this.has_ForTransitionInIdentifier = has_ForTransitionInIdentifier;
	}

	public boolean isHasCapitalisationForTransitionInIdentifier() {
		return hasCapitalisationForTransitionInIdentifier;
	}

	public void setHasCapitalisationForTransitionInIdentifier(
			boolean hasCapitalisationForTransitionInIdentifier) {
		this.hasCapitalisationForTransitionInIdentifier = hasCapitalisationForTransitionInIdentifier;
	}

	public boolean isHasIdentifiersWithLengthMoreThan1() {
		return hasIdentifiersWithLengthMoreThan1;
	}

	public void setHasIdentifiersWithLengthMoreThan1(
			boolean hasIdentifiersWithLengthMoreThan1) {
		this.hasIdentifiersWithLengthMoreThan1 = hasIdentifiersWithLengthMoreThan1;
	}

	public boolean isHasVocalsToBeRemovedForAcronymInIdentifiers() {
		return hasVocalsToBeRemovedForAcronymInIdentifiers;
	}

	public void setHasVocalsToBeRemovedForAcronymInIdentifiers(
			boolean hasVocalsToBeRemovedForAcronymInIdentifiers) {
		this.hasVocalsToBeRemovedForAcronymInIdentifiers = hasVocalsToBeRemovedForAcronymInIdentifiers;
	}

	public boolean isHasNonLargestDataTypeForNonFloating() {
		return hasNonLargestDataTypeForNonFloating;
	}

	public void setHasNonLargestDataTypeForNonFloating(
			boolean hasNonLargestDataTypeForNonFloating) {
		this.hasNonLargestDataTypeForNonFloating = hasNonLargestDataTypeForNonFloating;
	}

	public boolean isHasNonLargestDataTypeForFloating() {
		return hasNonLargestDataTypeForFloating;
	}

	public void setHasNonLargestDataTypeForFloating(
			boolean hasNonLargestDataTypeForFloating) {
		this.hasNonLargestDataTypeForFloating = hasNonLargestDataTypeForFloating;
	}

	public boolean isHasStringLiteral() {
		return hasStringLiteral;
	}

	public void setHasStringLiteral(boolean hasStringLiteral) {
		this.hasStringLiteral = hasStringLiteral;
	}

	public boolean isHasLowercasedFirstWordCharInString() {
		return hasLowercasedFirstWordCharInString;
	}

	public void setHasLowercasedFirstWordCharInString(
			boolean hasLowercasedFirstWordCharInString) {
		this.hasLowercasedFirstWordCharInString = hasLowercasedFirstWordCharInString;
	}

	public boolean isHasLowercasedCharsInString() {
		return hasLowercasedCharsInString;
	}

	public void setHasLowercasedCharsInString(boolean hasLowercasedCharsInString) {
		this.hasLowercasedCharsInString = hasLowercasedCharsInString;
	}

	public boolean isHasUppercasedCharsInString() {
		return hasUppercasedCharsInString;
	}

	public void setHasUppercasedCharsInString(boolean hasUppercasedCharsInString) {
		this.hasUppercasedCharsInString = hasUppercasedCharsInString;
	}

	public boolean isHasConjunctionSymbolInString() {
		return hasConjunctionSymbolInString;
	}

	public void setHasConjunctionSymbolInString(
			boolean hasConjunctionSymbolInString) {
		this.hasConjunctionSymbolInString = hasConjunctionSymbolInString;
	}

	public boolean isHasConjunctionWordInString() {
		return hasConjunctionWordInString;
	}

	public void setHasConjunctionWordInString(boolean hasConjunctionWordInString) {
		this.hasConjunctionWordInString = hasConjunctionWordInString;
	}

	public boolean isHasMathSymbolInString() {
		return hasMathSymbolInString;
	}

	public void setHasMathSymbolInString(boolean hasMathSymbolInString) {
		this.hasMathSymbolInString = hasMathSymbolInString;
	}

	public boolean isHasMathWordInString() {
		return hasMathWordInString;
	}

	public void setHasMathWordInString(boolean hasMathWordInString) {
		this.hasMathWordInString = hasMathWordInString;
	}

	public boolean isHasSmallNumberInString() {
		return hasSmallNumberInString;
	}

	public void setHasSmallNumberInString(boolean hasSmallNumberInString) {
		this.hasSmallNumberInString = hasSmallNumberInString;
	}

	public boolean isHasSmallNumberWordInString() {
		return hasSmallNumberWordInString;
	}

	public void setHasSmallNumberWordInString(boolean hasSmallNumberWordInString) {
		this.hasSmallNumberWordInString = hasSmallNumberWordInString;
	}

	public boolean isHasFloatingNumber() {
		return hasFloatingNumber;
	}

	public void setHasFloatingNumber(boolean hasFloatingNumber) {
		this.hasFloatingNumber = hasFloatingNumber;
	}

}
