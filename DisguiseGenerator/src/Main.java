
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import support.PythonCodeFormatter;
import tuple.JavaDefaultObfuscatorSettingTuple;
import tuple.LibTuple;
import tuple.ObfuscatorSettingTuple;
import tuple.PythonDefaultObfuscatorSettingTuple;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		execute(args);

	}

	public static void execute(String[] args) throws Exception {
		// get the path of Python program from 'pythoncompilerpath.txt'
		File f = new File("pythoncompilerpath.txt");
		if (f.exists()) {
			try {
				Scanner sc = new Scanner(f);
				PythonCodeFormatter.pythonCompilerPath = sc.nextLine();
				// System.out.println(PythonCodeFormatter.pythonCompilerPath);
				sc.close();
			} catch (Exception e) {
				// do nothing
			}
		}

		if (args.length == 0) {
			showHelp();
		} else {
			// start checking
			String mode = args[0];
			if (mode.equalsIgnoreCase("disguise")) {
				executeDisguise(args);
			} else if (mode.equalsIgnoreCase("diagnose")) {
				executeDiagnose(args);
			} else if (mode.equalsIgnoreCase("disguiserandom")) {
				executeDisguiseRandom(args);
			} else if (mode.equalsIgnoreCase("availabledisguises")) {
				executeAvailableDisguises(args);
			} else {
				System.err.println("The first argument should be either 'disguise', 'diagnose', 'disguiserandom',");
				System.err.println("  or 'availabledisguises'.");
				System.err.println("Run this software without arguments to show help.");
			}
		}
	}

	public static void executeDisguise(String[] args) {
		if (args.length <= 5) {
			System.err.println("[Disguise code]");
			System.err.println("The number of arguments should be six or more.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}

		String input_project_path = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(input_project_path) == false) {
			System.err.println("[Disguise code]");
			System.err.println("<input_project_path> is not a valid path or refers ");
			System.err.println("  to a nonexistent directory.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[Disguise code]");
			System.err.println("<programming_language> should be either 'java' (for Java)");
			System.err.println("  or 'py' (for Python).");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		String programming_language = args[2];

		temp = isHumanLanguageValid(args[3]);
		if (temp == false) {
			System.err.println("[Disguise code]");
			System.err.println("<human_language> should be either 'en' (for English)");
			System.err.println("  or 'id' (for Indonesian).");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		String human_language = args[3];

		String additional_keywords_path = preparePathOrRegex(args[4]);
		if (additional_keywords_path != null) {
			if (additional_keywords_path.equals("null"))
				additional_keywords_path = null;
			else if (isPathValidAndExist(additional_keywords_path) == false) {
				System.err.println("[Disguise code]");
				System.err.println("<additional_keywords_path> is not a valid path or refers to");
				System.err.println("  a nonexistent file.");
				System.err.println("Run this software without arguments to show help.");
				return;
			}
		}

		ArrayList<Integer> disguiseIDs = new ArrayList<Integer>();

		for (int i = 5; i < args.length; i++) {
			Integer tempN = prepareID(args[i]);
			if (tempN == null) {
				System.err.println("[Disguise code]");
				System.err.println("<disguise_id> '" + args[i] + "' is not a valid non-negative");
				System.err.println("  integer lower than 60.");
				System.err.println("Run this software without arguments to show help.");
				return;
			}
			disguiseIDs.add(tempN);
		}

		// set the obfuscater setting tuple
		ObfuscatorSettingTuple set = null;
		if (programming_language.equalsIgnoreCase("py"))
			set = new PythonDefaultObfuscatorSettingTuple(human_language);
		else
			set = new JavaDefaultObfuscatorSettingTuple(human_language);

		// apply the disguises
		DisguiserCore.applyDisguises(input_project_path, additional_keywords_path, disguiseIDs, set,
				"." + programming_language);

		System.out.println("The command has been successfully executed!");
		System.out.println("Following disguises have been applied: ");
		for (Integer i : disguiseIDs) {
			System.out.println("  " + DisguiserCore.getDisguiseName(i));
		}
	}

	public static void executeDiagnose(String[] args) {
		if (args.length != 5) {
			System.err.println("[Diagnose code for applicable disguises]");
			System.err.println("The number of arguments should be five.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}

		String input_project_path = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(input_project_path) == false) {
			System.err.println("[Diagnose code for applicable disguises]");
			System.err.println("<input_project_path> is not a valid path or refers ");
			System.err.println("  to a nonexistent directory.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[Diagnose code for applicable disguises]");
			System.err.println("<programming_language> should be either 'java' (for Java)");
			System.err.println("  or 'py' (for Python).");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		String programming_language = args[2];

		temp = isHumanLanguageValid(args[3]);
		if (temp == false) {
			System.err.println("[Diagnose code for applicable disguises]");
			System.err.println("<human_language> should be either 'en' (for English)");
			System.err.println("  or 'id' (for Indonesian).");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		String human_language = args[3];

		String additional_keywords_path = preparePathOrRegex(args[4]);
		if (additional_keywords_path != null) {
			if (additional_keywords_path.equals("null"))
				additional_keywords_path = null;
			else if (isPathValidAndExist(additional_keywords_path) == false) {
				System.err.println("[Diagnose code for applicable disguises]");
				System.err.println("<additional_keywords_path> is not a valid path or refers to");
				System.err.println("  a nonexistent file.");
				System.err.println("Run this software without arguments to show help.");
				return;
			}
		}

		// set the setting tuple
		ObfuscatorSettingTuple set = null;
		if (programming_language.equalsIgnoreCase("py"))
			set = new PythonDefaultObfuscatorSettingTuple(human_language);
		else
			set = new JavaDefaultObfuscatorSettingTuple(human_language);

		String fileExtension = "." + programming_language;

		// get token string
		ArrayList<LibTuple> tokens = DisguiserCore.getDefaultTokenStringForDir(input_project_path,
				additional_keywords_path, fileExtension);

		// get the diagnostic result
		ApplicableObfuscationDiagnosticResult diagresult = ApplicableObfuscationDiagnosticResult
				.generateDiagnosticResult(tokens, set);

		// set as array
		boolean[] diagresultPerDisguise = ApplicableObfuscationDiagnosticResult.getApplicableDisguises(diagresult, set);

		System.out.println("The command has been successfully executed!");
		System.out.println("Following disguises are applicable: ");
		for (int i = 0; i < diagresultPerDisguise.length; i++) {
			if (diagresultPerDisguise[i] == true)
				System.out.println("  " + DisguiserCore.getDisguiseName(i));
		}

	}

	public static void executeDisguiseRandom(String[] args) {
		if (args.length != 9) {
			System.err.println("[Disguise code at random]");
			System.err.println("The number of arguments should be nine.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}

		String input_project_path = preparePathOrRegex(args[1]);
		if (isPathValidAndExist(input_project_path) == false) {
			System.err.println("[Disguise code at random]");
			System.err.println("<input_project_path> is not a valid path or refers ");
			System.err.println("  to a nonexistent directory.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}

		boolean temp = isProgrammingLanguageValid(args[2]);
		if (temp == false) {
			System.err.println("[Disguise code at random]");
			System.err.println("<programming_language> should be either 'java' (for Java)");
			System.err.println("  or 'py' (for Python).");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		String programming_language = args[2];

		temp = isHumanLanguageValid(args[3]);
		if (temp == false) {
			System.err.println("[Disguise code at random]");
			System.err.println("<human_language> should be either 'en' (for English)");
			System.err.println("  or 'id' (for Indonesian).");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		String human_language = args[3];

		String additional_keywords_path = preparePathOrRegex(args[4]);
		if (additional_keywords_path != null) {
			if (additional_keywords_path.equals("null"))
				additional_keywords_path = null;
			else if (isPathValidAndExist(additional_keywords_path) == false) {
				System.err.println("[Disguise code at random]");
				System.err.println("<additional_keywords_path> is not a valid path or refers to");
				System.err.println("  a nonexistent file.");
				System.err.println("Run this software without arguments to show help.");
				return;
			}
		}

		Integer tempN = prepareNumDisguise(args[5], 27);
		if (tempN == null) {
			System.err.println("[Disguise code at random]");
			System.err.println("<num_comment_disguises> is not a valid non-negative");
			System.err.println("  integer lower than 27.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		int num_comment_disguises = tempN;

		tempN = prepareNumDisguise(args[6], 8);
		if (tempN == null) {
			System.err.println("[Disguise code at random]");
			System.err.println("<num_whitespace_disguises> is not a valid non-negative");
			System.err.println("  integer lower than 8.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		int num_whitespace_disguises = tempN;

		tempN = prepareNumDisguise(args[7], 10);
		if (tempN == null) {
			System.err.println("[Disguise code at random]");
			System.err.println("<num_ident_disguises> is not a valid non-negative");
			System.err.println("  integer lower than 10.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		int num_ident_disguises = tempN;

		tempN = prepareNumDisguise(args[8], 15);
		if (tempN == null) {
			System.err.println("[Disguise code at random]");
			System.err.println("<num_constant_and_data_type_disguises> is not a valid non-negative");
			System.err.println("  integer lower than 15.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}
		int num_constant_and_data_type_disguises = tempN;

		// set the setting tuple
		ObfuscatorSettingTuple set = null;
		if (programming_language.equalsIgnoreCase("py"))
			set = new PythonDefaultObfuscatorSettingTuple(human_language);
		else
			set = new JavaDefaultObfuscatorSettingTuple(human_language);

		String fileExtension = "." + programming_language;

		// generate the tokens for diagnostic
		ArrayList<LibTuple> tokens = DisguiserCore.getDefaultTokenStringForDir(input_project_path,
				additional_keywords_path, fileExtension);

		ArrayList<Integer> selectedDisguises = DisguiserCore.getRandomisedDisguises(num_comment_disguises,
				num_whitespace_disguises, num_ident_disguises, num_constant_and_data_type_disguises, set, tokens);

		DisguiserCore.applyDisguises(input_project_path, additional_keywords_path, selectedDisguises, set,
				fileExtension);

		System.out.println("The command has been successfully executed!");
		System.out.println("Following disguises have been applied: ");
		for (Integer i : selectedDisguises) {
			System.out.println("  " + DisguiserCore.getDisguiseName(i));
		}

	}

	public static void executeAvailableDisguises(String[] args) {
		if (args.length != 1) {
			System.err.println("[see available disguises]");
			System.err.println("The number of arguments should be one.");
			System.err.println("Run this software without arguments to show help.");
			return;
		}

		System.out.println("Available disguises with their IDs listed prior each entry:");
		for (int i = 0; i < 60; i++) {
			System.out.println(DisguiserCore.getDisguiseName(i));
		}
	}

	private static Integer prepareNumDisguise(String s, int maxNumDisguise) {
		// check whether s is actually a positive decimal and falls in the range.
		try {
			Integer x = Integer.parseInt(s);
			if (x >= 0 && x <= maxNumDisguise)
				return x;
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	private static Integer prepareID(String s) {
		// check whether s is actually a positive decimal and falls in the range.
		try {
			Integer x = Integer.parseInt(s);
			if (x >= 0 && x <= 59)
				return x;
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	private static boolean isProgrammingLanguageValid(String prog) {
		if (prog != null && (prog.equals("java") || prog.equals("py")))
			return true;
		else
			return false;
	}

	private static boolean isHumanLanguageValid(String human) {
		if (human != null && (human.equals("en") || human.equals("id")))
			return true;
		else
			return false;
	}

	private static boolean isPathValidAndExist(String path) {
		// check the validity of the string
		if (isPathValid(path) == false)
			return false;

		// check whether such file exists
		File f = new File(path);
		if (f.exists() == false)
			return false;

		return true;
	}

	private static String preparePathOrRegex(String path) {
		if (path != null && (path.startsWith("'") || path.startsWith("\"")))
			return path.substring(1, path.length() - 1);
		else
			return path;
	}

	private static boolean isPathValid(String path) {
		// check the validity of the string
		if (path == null || path.length() == 0)
			return false;
		else
			return true;
	}

	public static void showHelp() {
		println("Code similarity disguiser (CSD) is a tool to educate students about code similarity for academic");
		println("  integrity in programming. With the tool, learners can independently learn about the many");
		println("  ways that a program can be changed without affecting the underlying code similarity. It");
		println("  can also be used to individualise code-tracing assessments, to anonymise identifying ");
		println("  information in student programs, and to generate data sets for the evaluation of ");
		println("  code similarity detection that incorporates features of programming style. Further");
		println("  details can be seen in the corresponding paper. \n");

		println("CSD provides four modes:");
		println("1. disguise: given a source code project and a list of desired disguises, this mode will");
		println("  disguise the project's code based on the desired disguises. Please be aware that the");
		println("  impact of some disguises might overlap one another. A complete list of disguise IDs can be.");
		println("  seen via the fourth mode.");
		println("  -> Command: disguise <input_project_path> <programming_language> <human_language>");
		println("       <additional_keywords_path> <disguise_id_1> <disguise_id_2> ... <disguise_id_N>");

		println("2. diagnose: given a source code project, this mode will list any applicable disguises.");
		println("  -> Command: diagnose <input_project_path> <programming_language> <human_language>");
		println("       <additional_keywords_path> <disguise_id_1> <disguise_id_2> ... <disguise_id_N>");

		println("3. disguiserandom: this mode is similar to the first one except that the disguises are");
		println("  randomly applied. The code project will be diagnosed first to assure the applicability");
		println("  of the disguises.");
		println("  -> Command: disguiserandom <input_project_path> <programming_language> <human_language>");
		println("        <additional_keywords_path> <num_comment_disguises> <num_whitespace_disguises>");
		println("        <num_ident_disguises> <num_constant_and_data_type_disguises>");

		println("4. availabledisguises: this mode will list all available disguises.");
		println("  -> Command: availabledisguises");

		println("\n\nParameters description (sorted alphabetically):");
		println("  <additional_keywords_path>: a string representing a file containing additional keywords");
		println("    with newline as the delimiter. Keywords with more than one token should be written by");
		println("    embedding spaces between the tokens. For example, 'System.out.print' should be written");
		println("    as 'System . out . print'. If unused, please set this to 'null'.");
		println("  <disguise_id>: a number representing a code disguise. See the complete list at the end of");
		println("    this help.");
		println("    Value: 0-59.");
		println("  <human_language>: a constant depicting the human language used on");
		println("    the applied disguises.");
		println("    Value: 'en' (for English) or 'id' (for Indonesian).");
		println("  <input_project_path>: a string representing the path of the source code projects for input.");
		println("    Please use quotes if the path contains spaces.");
		println("  <num_comment_disguises>: a number depicting the maximum number of comment disguises that");
		println("    will be applied.");
		println("    Value: a non-negative integer lower or equal to 27.");
		println("  <num_constant_and_data_type_disguises>: a number depicting the maximum number of constant");
		println("    disguises that will be applied.");
		println("    Value: a non-negative integer lower or equal to 15.");
		println("  <num_ident_disguises>: a number depicting the maximum number of identifier disguises that will");
		println("    be applied.");
		println("    Value: a non-negative integer lower or equal to 10.");
		println("  <num_whitespace_disguises>: a number depicting the maximum number of whitespace disguises that");
		println("    will be applied.");
		println("    Value: a non-negative integer lower or equal to 8.");
		println("  <programming_language>: a constant depicting the programming language used on");
		println("    given source code files.");
		println("    Value: 'java' (for Java) or 'py' (for Python).");

	}

	private static void println(String s) {
		System.out.println(s);
	}

}
