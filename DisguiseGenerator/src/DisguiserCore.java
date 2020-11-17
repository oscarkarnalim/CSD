
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import disguisegenerator.CodeObfuscatorComment;
import disguisegenerator.CodeObfuscatorConstantAndDataTypeChange;
import disguisegenerator.CodeObfuscatorCore;
import disguisegenerator.CodeObfuscatorIdentifier;
import disguisegenerator.CodeObfuscatorWhitespace;
import language.LibJavaExtractor;
import language.LibPythonExtractor;
import support.AdditionalKeywordsManager;
import tuple.JavaDefaultObfuscatorSettingTuple;
import tuple.LibTuple;
import tuple.ObfuscatorSettingTuple;
import tuple.PythonDefaultObfuscatorSettingTuple;

public class DisguiserCore {

	public static ArrayList<Integer> getRandomisedDisguises(int numDisForComment, int numDisForWhitespace,
			int numDisForIdentifier, int numDisForConstantDataType, ObfuscatorSettingTuple set,
			ArrayList<LibTuple> tokens) {
		Random r = new Random();

		// diagnose
		ApplicableObfuscationDiagnosticResult diagresult = ApplicableObfuscationDiagnosticResult
				.generateDiagnosticResult(tokens, set);

		// get the applicability per disguise
		boolean[] diagresultPerDisguise = ApplicableObfuscationDiagnosticResult.getApplicableDisguises(diagresult, set);

		// get only the applicable ones and get some of the applicable ones
		// randomly

		// to store all selected disguises
		ArrayList<Integer> combinedResults = new ArrayList<>();

		// for comment
		addDisguises(0, 27, diagresultPerDisguise, numDisForComment, r, combinedResults);
		// for whitespace
		addDisguises(27, 35, diagresultPerDisguise, numDisForWhitespace, r, combinedResults);
		// for identifier
		addDisguises(35, 45, diagresultPerDisguise, numDisForIdentifier, r, combinedResults);
		// for constant and data type change
		addDisguises(45, 60, diagresultPerDisguise, numDisForConstantDataType, r, combinedResults);

		return combinedResults;
	}

	private static void addDisguises(int startDisguiseIndex, int finishDisguiseIndex, boolean[] diagresultPerDisguise,
			int maxDisguises, Random r, ArrayList<Integer> combinedResults) {
		/*
		 * This method selects some disguises randomly and then put them in combined
		 * results. startDisguiseIndex and finishDisguiseIndex represent range of
		 * applicable disguises in diagresultPerDisguise. maxDisguises refers to max
		 * number of selected disguises.
		 */

		// get only the applicable ones
		ArrayList<Integer> applicableDisguises = new ArrayList<>();
		for (int i = startDisguiseIndex; i < finishDisguiseIndex; i++) {
			if (diagresultPerDisguise[i] == true)
				applicableDisguises.add(i);
		}

		// set the max of applicable disguise
		if (maxDisguises > applicableDisguises.size())
			maxDisguises = applicableDisguises.size();
		// select some of the disguises
		while (maxDisguises > 0) {
			// get a random number based on applicableDisguises' size
			int num = r.nextInt(applicableDisguises.size());
			// add the selected pos and remove that pos from applicableDisguises
			combinedResults.add(applicableDisguises.remove(num));
			// reduce the need of disguises
			maxDisguises--;
		}
	}

	public static void applyDisguises(String inputpath, String additionalKeywordsPath,
			ArrayList<Integer> selectedDisguises, ObfuscatorSettingTuple set, String fileExtension) {

		// read the additionalkeywords
		ArrayList<ArrayList<String>> additionalKeywords = null;
		if (additionalKeywordsPath != null && additionalKeywordsPath != "") {
			// read the additional keywords
			additionalKeywords = AdditionalKeywordsManager.readAdditionalKeywords(additionalKeywordsPath);
		}

		// to store all identifier names
		HashMap<String, String> namePairs = new HashMap<String, String>();

		// iterate the directory
		File inputDir = new File(inputpath);
		Stack<File> s = new Stack<File>();
		s.push(inputDir);
		while (s.empty() == false) {
			File cur = s.pop();
			if (cur.isDirectory()) {
				// if directory, add the children
				File[] children = cur.listFiles();
				for (File c : children) {
					s.push(c);
				}
			} else {
				// if it is the desired file, process and replace the file
				if (cur.getName().toLowerCase().endsWith(fileExtension))
					applyDisguisesPerFile(cur.getAbsolutePath(), cur.getAbsolutePath(), additionalKeywords,
							selectedDisguises, set, namePairs);
			}
		}
	}

	public static void applyDisguisesPerFile(String inputpath, String outputpath, String additionalKeywordsPath,
			ArrayList<Integer> selectedDisguises, ObfuscatorSettingTuple set) {

		// read the additionalkeywords
		ArrayList<ArrayList<String>> additionalKeywords = null;
		if (additionalKeywordsPath != null && additionalKeywordsPath != "") {
			// read the additional keywords
			additionalKeywords = AdditionalKeywordsManager.readAdditionalKeywords(additionalKeywordsPath);
		}

		applyDisguisesPerFile(inputpath, outputpath, additionalKeywords, selectedDisguises, set,
				new HashMap<String, String>());
	}

	public static void applyDisguisesPerFile(String inputpath, String outputpath,
			ArrayList<ArrayList<String>> additionalKeywords, ArrayList<Integer> selectedDisguises,
			ObfuscatorSettingTuple set, HashMap<String, String> globalNamePairs) {

		// 34 reformat the whitespaces.
		if (selectedDisguises.contains(34)) {
			// reformat the whitespaces
			try {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorWhitespace.w08JavaCodeFormatting(inputpath, outputpath);
				else
					CodeObfuscatorWhitespace.w08PythonCodeFormatting(inputpath, outputpath);
			} catch (Exception e) {
				// if unable to format, do nothing
			}
		}

		String filename = new File(inputpath).getName();
		// remove the extension
		filename = filename.substring(0, filename.lastIndexOf("."));

		// reread the file
		ArrayList<LibTuple> tokens;
		if (set instanceof JavaDefaultObfuscatorSettingTuple)
			tokens = LibJavaExtractor.getDefaultTokenString(outputpath, additionalKeywords);
		else
			tokens = LibPythonExtractor.getDefaultTokenString(outputpath, additionalKeywords);

		// dealing with other disguises
		for (Integer i : selectedDisguises) {
			// System.out.println(getDisguiseName(i));

			// comment 0-26

			// 0 removing some single-line comments
			if (i == 0) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c01JavaRemovingSomeSingleLineComments(tokens,
							set.getSingleLineCommentRemovalProb());
				else
					CodeObfuscatorComment.c01PythonRemovingSomeSingleLineComments(tokens,
							set.getSingleLineCommentRemovalProb());
			}

			// 1 removing all single-line comments
			if (i == 1) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c02JavaRemovingAllSingleLineComments(tokens);
				else
					CodeObfuscatorComment.c02PythonRemovingAllSingleLineComments(tokens);
			}

			// 2 removing some multi-line comments.
			if (i == 2 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c03JavaRemovingSomeMultiLineComments(tokens,
						set.getMultiLineCommentRemovalProb());

			// 3 removing all multi-line comments.
			if (i == 3 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c04JavaRemovingAllMultiLineComments(tokens);

			// 4 removing some comments.
			if (i == 4 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c05JavaRemovingSomeComments(tokens, set.getAllCommentRemovalProb());

			// 5 removing all comments.
			if (i == 5 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c06JavaRemovingAllComments(tokens);

			// 6 adding a single-line comment for each line with syntax, which
			// content is randomly generated.
			if (i == 6) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c07JavaAddingSingleLineComment(tokens, set.getHumanLanguage());
				else
					CodeObfuscatorComment.c07PythonAddingSingleLineComment(tokens, set.getHumanLanguage());
			}

			// 7 adding a multi-line comment for each line with syntax, which
			// content is randomly generated.
			if (i == 7 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c08JavaAddingMultiLineComment(tokens, set.getHumanLanguage());

			// 8 changing each single-line comment to the multi-line one.
			if (i == 8 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c09JavaReplacingSingleToMultiComments(tokens);

			// 9 changing each single-line comment to the multi-line one with
			// '.', '?', '!', and ';' as the line separators.
			if (i == 9 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c10JavaReplacingSingleToMultiCommentsWithPunctuationsAsLineDelimiters(tokens);

			// 10 changing each single-line comment to the multi-line one with n
			// characters per line.
			if (i == 10 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c11JavaReplacingSingleToMultiCommentsWithNCharsPerLine(tokens,
						set.getnForMaxCharsInSingleLineComment());

			// 11 splitting each single-line comment to several single-line
			// comments with '.', '?', '!', and ';' as the line separators.
			if (i == 11) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple) {
					CodeObfuscatorComment.c12JavaReplacingSingleToNSingleCommentsWithPunctuationsAsDelimiters(tokens);
				} else {
					CodeObfuscatorComment.c12PythonReplacingSingleToNSingleCommentsWithPunctuationsAsDelimiters(tokens);
				}
			}

			// 12 splitting each single-line comment to several single-line
			// comments with n characters per line.
			if (i == 12) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple) {
					CodeObfuscatorComment.c13JavaReplacingSingleToNSingleLineCommentsWithNCharsPerLine(tokens,
							set.getnForMaxCharsInSingleLineComment());
				} else {
					CodeObfuscatorComment.c13PythonReplacingSingleToNSingleCommentsWithNCharsPerLine(tokens,
							set.getnForMaxCharsInSingleLineComment());
				}
			}

			// 13 changing each multi-line comment to the single-line one with
			// all newlines removed.
			if (i == 13 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c14JavaReplacingMultiToSingleCommentsWithNewlinesRemoved(tokens);

			// 14 splitting each multi-line comment to several single-line
			// comments with newline as the line separators.
			if (i == 14 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c15JavaSplitMultiToNSingleCommentsWithNewlineAsSeparator(tokens);

			// 15 splitting each multi-line comment to several single-line
			// comments with '.', '?', '!', and ';' as the line separators.
			if (i == 15 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c16JavaSplitMultiToNSingleCommentsWithPunctuationsAsDelimiters(tokens);

			// 16 splitting each multi-line comment to several single-line
			// comments with n characters per line.
			if (i == 16 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorComment.c17JavaReplacingMultiToSeveralSingleCommentsWithNCharsPerLine(tokens,
						set.getnForMaxCharsInMultiLineComment());

			// 17 capitalising the first character of each comment word.
			if (i == 17) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c18JavaCapitalisingFirstCharEachWord(tokens);
				else
					CodeObfuscatorComment.c18PythonCapitalisingFirstCharEachWord(tokens);
			}

			// 18 capitalising all comment characters.
			if (i == 18)
				CodeObfuscatorComment.c19CapitalisingAllChars(tokens);

			// 19 decapitalising all comment characters.
			if (i == 19)
				CodeObfuscatorComment.c20DecapitalisingAllChars(tokens);

			// 20 replacing conjuction symbols with their corresponding words in
			// comments.
			if (i == 20) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c21JavaReplacingConjuctionSymbolsWithWords(tokens, set.getHumanLanguage());
				else
					CodeObfuscatorComment.c21PythonReplacingConjuctionSymbolsWithWords(tokens, set.getHumanLanguage());
			}

			// 21 replacing conjuction words with their corresponding symbols in
			// comments.
			if (i == 21) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c22JavaReplacingConjuctionWordsWithSymbols(tokens, set.getHumanLanguage());
				else
					CodeObfuscatorComment.c22PythonReplacingConjuctionWordsWithSymbols(tokens, set.getHumanLanguage());
			}

			// 22 replacing math operators with their corresponding words in
			// comments. (+,-,*,/,=)
			if (i == 22) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c23JavaReplacingMathSymbolsWithWords(tokens, set.getHumanLanguage());
				else
					CodeObfuscatorComment.c23PythonReplacingMathSymbolsWithWords(tokens, set.getHumanLanguage());
			}

			// 23 replacing math words with their corresponding operators in
			// comments. (+,-,*,/,=)
			if (i == 23) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c24JavaReplacingMathWordsWithSymbols(tokens, set.getHumanLanguage());
				else
					CodeObfuscatorComment.c24PythonReplacingMathWordsWithSymbols(tokens, set.getHumanLanguage());
			}

			// 24 replacing small numbers (<12) with their corresponding words
			// in comments.
			if (i == 24) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c25JavaReplacingSmallNumbersWithWords(tokens, set.getHumanLanguage());
				else
					CodeObfuscatorComment.c25PythonReplacingSmallNumbersWithWords(tokens, set.getHumanLanguage());
			}

			// 25 replacing small number words (<12) with their corresponding
			// numbers in comments.
			if (i == 25) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c26JavaReplacingSmallNumberWordsWithNumbers(tokens, set.getHumanLanguage());
				else
					CodeObfuscatorComment.c26PythonReplacingSmallNumberWordsWithNumbers(tokens, set.getHumanLanguage());
			}

			// 26 anonymising all comment contents as 'anonymised comments'
			if (i == 26) {
				if (set instanceof JavaDefaultObfuscatorSettingTuple)
					CodeObfuscatorComment.c27JavaAnonymisingCommentContents(tokens, set.getHumanLanguage());
				else
					CodeObfuscatorComment.c27PythonAnonymisingCommentContents(tokens, set.getHumanLanguage());
			}

			// whitespace 27-34, except 34 as it should be specifically handled.

			// 27 removing all blank newlines.
			if (i == 27)
				CodeObfuscatorWhitespace.w01RemovingBlankLines(tokens);

			// 28 removing all tabs and spaces before each statement. Not
			// applicable for Python.
			if (i == 28 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorWhitespace.w02JavaRemovingTabsAndSpacesBeforeEachStatement(tokens);

			// 29 replacing each space with n spaces.
			if (i == 29)
				CodeObfuscatorWhitespace.w03ReplacingEachSpaceWithNSpaces(tokens, set.getNumReplacingSpaces());

			// 30 replacing each tab with n tabs.
			if (i == 30)
				CodeObfuscatorWhitespace.w04ReplacingEachTabWithNTabs(tokens, set.getNumReplacingTabs());

			// 31 replacing each newline with n newlines.
			if (i == 31)
				CodeObfuscatorWhitespace.w05ReplacingEachNewLineWithNLines(tokens, set.getNumReplacingNewlines());

			// 32 replacing all tabs with n spaces.
			if (i == 32)
				CodeObfuscatorWhitespace.w06ReplacingEachTabWithNSpaces(tokens, set.getNumReplacingSpacesForTabs());

			// 33 replacing all n spaces with tabs.
			if (i == 33)
				CodeObfuscatorWhitespace.w07ReplacingNSpacesWithTab(tokens, set.getnForSpacesReplacedByTab());

			// constant and data type change 45-59

			// 45 changing all non-floating data types to the largest data type.
			if (i == 45 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorConstantAndDataTypeChange.cd01JavaChangingNonFloatingTypesToTheLargest(tokens);

			// 46 changing all floating data types to the largest data type.
			if (i == 46 && set instanceof JavaDefaultObfuscatorSettingTuple)
				CodeObfuscatorConstantAndDataTypeChange.cd02JavaChangingFloatingTypesToTheLargest(tokens);

			// 47 adding a blank space at the end of each string literal.
			if (i == 47)
				CodeObfuscatorConstantAndDataTypeChange.cd03AddingSpaceAtEndStringLiteral(tokens,
						set.isSingleQuoteAlsoStringSeparator());

			// 48 adding a newline at the end of each string literal.
			if (i == 48)
				CodeObfuscatorConstantAndDataTypeChange.cd04AddingNewlineAtEndStringLiteral(tokens,
						set.isSingleQuoteAlsoStringSeparator());

			// 49 capitalising the first character of each string literal word.
			if (i == 49)
				CodeObfuscatorConstantAndDataTypeChange.cd05CapitalisingFirstCharEachWord(tokens,
						set.isSingleQuoteAlsoStringSeparator());

			// 50 capitalising all string characters.
			if (i == 50)
				CodeObfuscatorConstantAndDataTypeChange.cd06CapitalisingAllChars(tokens,
						set.isSingleQuoteAlsoStringSeparator());

			// 51 decapitalising all string characters.
			if (i == 51)
				CodeObfuscatorConstantAndDataTypeChange.cd07DecapitalisingAllChars(tokens,
						set.isSingleQuoteAlsoStringSeparator());

			// 52 replacing conjuction symbols with their corresponding words in
			// strings.
			if (i == 52)
				CodeObfuscatorConstantAndDataTypeChange.cd08ReplacingConjuctionSymbolsWithWords(tokens,
						set.getHumanLanguage(), set.isSingleQuoteAlsoStringSeparator());

			// 53 replacing conjuction words with their corresponding symbols in
			// strings.
			if (i == 53)
				CodeObfuscatorConstantAndDataTypeChange.cd09ReplacingConjuctionWordsWithSymbols(tokens,
						set.getHumanLanguage(), set.isSingleQuoteAlsoStringSeparator());

			// 54 replacing math operators with their corresponding words in
			// strings. (+,-,*,/,=)
			if (i == 54)
				CodeObfuscatorConstantAndDataTypeChange.cd10ReplacingMathSymbolsWithWords(tokens,
						set.getHumanLanguage(), set.isSingleQuoteAlsoStringSeparator());

			// 55 replacing math words with their corresponding operators in
			// strings. (+,-,*,/,=)
			if (i == 55)
				CodeObfuscatorConstantAndDataTypeChange.cd11ReplacingMathWordsWithSymbols(tokens,
						set.getHumanLanguage(), set.isSingleQuoteAlsoStringSeparator());

			// 56 replacing small numbers (<12) with their corresponding words
			// in strings.
			if (i == 56)
				CodeObfuscatorConstantAndDataTypeChange.cd12ReplacingSmallNumbersWithWords(tokens,
						set.getHumanLanguage(), set.isSingleQuoteAlsoStringSeparator());

			// 57 replacing small number words (<12) with their corresponding
			// numbers in strings.
			if (i == 57)
				CodeObfuscatorConstantAndDataTypeChange.cd13ReplacingSmallNumberWordsWithNumbers(tokens,
						set.getHumanLanguage(), set.isSingleQuoteAlsoStringSeparator());

			// 58 anonymising all string contents as 'anonymised string content'
			if (i == 58)
				CodeObfuscatorConstantAndDataTypeChange.cd14AnonymisingStringContents(tokens, set.getHumanLanguage(),
						set.isSingleQuoteAlsoStringSeparator());

			// 59 adding more precision for floating constants.
			if (i == 59)
				CodeObfuscatorConstantAndDataTypeChange.cd15AddingExtraPrecisionForFloatingConstants(tokens);

		}

		// dealing with identifiers
		CodeObfuscatorIdentifier.applyIdentiifierObfuscation(tokens, set.getHumanLanguage(), globalNamePairs, filename,
				selectedDisguises);

		// get new filename
		String newOutputFilename = globalNamePairs.get(filename);

		File outputFile = new File(outputpath);
		String fileExt = outputpath.substring(outputpath.lastIndexOf("."));
		String newOutputFilepath = outputFile.getParentFile().getAbsolutePath() + File.separator + newOutputFilename
				+ fileExt;
		// delete the old file
		outputFile.delete();

		// write the code
		CodeObfuscatorCore.writeCode(newOutputFilepath, tokens);
	}

	public static void printDisguises(ArrayList<Integer> selectedDisguises) {
		for (Integer i : selectedDisguises) {
			String out = getDisguiseName(i);
			System.out.println(out);
		}
	}

	/*
	 * return the disguise name based on given index from an array of applicable
	 * disguises.
	 */
	public static String getDisguiseName(int i) {
		String out = "";
		switch (i) {
		case 0:
			out = "Removing some single-line comments with 50% probability";
			break;
		case 1:
			out = "Removing all single-line comments";
			break;
		case 2:
			out = "Removing some multi-line comments with 50% probability";
			break;
		case 3:
			out = "Removing all multi-line comments";
			break;
		case 4:
			out = "Removing some comments with 50% probability";
			break;
		case 5:
			out = "Removing all comments";
			break;
		case 6:
			out = "Adding a random single-line comment for each blank line before syntax";
			break;
		case 7:
			out = "Adding a random multi-line comment for each blank line before syntax";
			break;
		case 8:
			out = "Changing each single-line comment to the multi-line one";
			break;
		case 9:
			out = "Changing each single-line comment to the multi-line one with punctuations as the line separators";
			break;
		case 10:
			out = "Changing each single-line comment to the multi-line one with 80 characters per line";
			break;
		case 11:
			out = "Splitting each single-line comment to several single-line comments with punctuations as the line separators";
			break;
		case 12:
			out = "Splitting each single-line comment to several single-line comments with 80 characters per line";
			break;
		case 13:
			out = "Changing each multi-line comment to the single-line one with all newlines removed";
			break;
		case 14:
			out = "Splitting each multi-line comment to several single-line comments with newlines as the line separators";
			break;
		case 15:
			out = "Splitting each multi-line comment to several single-line comments with punctuations as the line separators";
			break;
		case 16:
			out = "Splitting each multi-line comment to several single-line comments with 80 characters per line";
			break;
		case 17:
			out = "Capitalising the first character of each comment word";
			break;
		case 18:
			out = "Capitalising all comment characters";
			break;
		case 19:
			out = "Decapitalising all comment characters";
			break;
		case 20:
			out = "Replacing conjuction symbols with their corresponding words in comments";
			break;
		case 21:
			out = "Replacing conjuction words with their corresponding symbols in comments";
			break;
		case 22:
			out = "Replacing math symbols with their corresponding words in comments";
			break;
		case 23:
			out = "Replacing math words with their corresponding symbols in comments";
			break;
		case 24:
			out = "Replacing small numbers with their corresponding words in comments";
			break;
		case 25:
			out = "Replacing words representing small numbers with their corresponding numbers in comments";
			break;
		case 26:
			out = "Anonymising all comment contents";
			break;
		case 27:
			out = "Removing all blank newlines";
			break;
		case 28:
			out = "Removing all tabs and spaces before each statement";
			break;
		case 29:
			out = "Replacing each space with 2 spaces";
			break;
		case 30:
			out = "Replacing each tab with 2 tabs";
			break;
		case 31:
			out = "Replacing each newline with 2 newlines";
			break;
		case 32:
			out = "Replacing each tab with 2 spaces";
			break;
		case 33:
			out = "Replacing each 2 spaces with a tab";
			break;
		case 34:
			out = "Reformat the whitespaces based on the programming language's guideline;\n"
					+ "    Reformatting Python code requires Python compiler so please "
					+ "update 'pythoncompilerpath.txt'\n    with the absolute path of that compiler";
			break;
		case 35:
			out = "Removing all stop words from the identifiers' sub-words";
			break;
		case 36:
			out = "Removing all underscores from the identifiers";
			break;
		case 37:
			out = "Removing all numbers from the identifiers";
			break;
		case 38:
			out = "Capitalising all identifiers' characters";
			break;
		case 39:
			out = "Decapitalising all identifiers' characters";
			break;
		case 40:
			out = "Replacing all identifiers' sub-word transitions from 'this_is_var' to 'thisIsVar'";
			break;
		case 41:
			out = "Replacing all identifiers' sub-word transitions from 'thisIsVar' to 'this_is_var'";
			break;
		case 42:
			out = "Renaming all identifiers with their corresponding first characters";
			break;
		case 43:
			out = "Renaming all identifiers with their corresponding acronyms";
			break;
		case 44:
			out = "Anonymising all identifiers";
			break;
		case 45:
			out = "Changing all non-floating numeric data types to the largest data type";
			break;
		case 46:
			out = "Changing all floating numeric data types to the largest data type";
			break;
		case 47:
			out = "Adding a blank space at the end of each string literal";
			break;
		case 48:
			out = "Adding a newline at the end of each string literal";
			break;
		case 49:
			out = "Capitalising the first character of each word in string literals";
			break;
		case 50:
			out = "Capitalising all characters in string literals";
			break;
		case 51:
			out = "Decapitalising all characters in string literals";
			break;
		case 52:
			out = "Replacing conjuction symbols with their corresponding words in string literals";
			break;
		case 53:
			out = "Replacing conjuction words with their corresponding symbols in string literals";
			break;
		case 54:
			out = "Replacing math symbols with their corresponding words in string literals";
			break;
		case 55:
			out = "Replacing math words with their corresponding symbols in string literals";
			break;
		case 56:
			out = "Replacing small numbers with their corresponding words in string literals";
			break;
		case 57:
			out = "Replacing words representing small numbers with their corresponding numbers in string literals";
			break;
		case 58:
			out = "Anonymising all string literal contents";
			break;
		case 59:
			out = "Adding more precision for each floating constant";
			break;
		}
		return i + ": " + out;
	}

	public static ArrayList<LibTuple> getDefaultTokenStringForDir(String dirPath, String additionalKeywordsPath,
			String fileExtension) {

		// read the additionalkeywords
		ArrayList<ArrayList<String>> additionalKeywords = null;
		if (additionalKeywordsPath != null && additionalKeywordsPath != "") {
			// read the additional keywords
			additionalKeywords = AdditionalKeywordsManager.readAdditionalKeywords(additionalKeywordsPath);
		}

		ArrayList<LibTuple> result = new ArrayList<>();

		File inputDir = new File(dirPath);
		Stack<File> s = new Stack<File>();
		s.push(inputDir);
		while (s.empty() == false) {
			File cur = s.pop();
			if (cur.isDirectory()) {
				// if directory
				// add the children
				File[] children = cur.listFiles();
				for (File c : children) {
					s.push(c);
				}
			} else {
				if (cur.getName().toLowerCase().endsWith(fileExtension)) {
					if (fileExtension.equals(".java")) // Java
						result.addAll(
								LibJavaExtractor.getDefaultTokenString(cur.getAbsolutePath(), additionalKeywords));
					else // Python
						result.addAll(
								LibPythonExtractor.getDefaultTokenString(cur.getAbsolutePath(), additionalKeywords));
				}
			}
		}

		return result;

	}

}
