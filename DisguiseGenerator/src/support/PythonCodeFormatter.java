package support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class PythonCodeFormatter {

	public static String pythonCompilerPath = "C:\\Python27\\python.exe";
	public static String pythonFormatterPath = "yapfLib\\main.py";
	public static String pythonStyleSettingPath = "yapfLib\\style.yapf";

	public static boolean formatCode(String filepath) throws Exception {
		/*
		 * Convert the code to its beautiful format with the help of YAPF from
		 * Google VERSION 0.28.0. The method returns true if the formatting is
		 * successful. 
		 */
		
		/*
		 * create the copy of the file
		 */
		String line;
		BufferedReader br = new BufferedReader(new FileReader(
				new File(filepath)));
		FileWriter fw = new FileWriter(new File(filepath + ".formatted"));
		while ((line = br.readLine()) != null) {
			fw.write(line + System.lineSeparator());
		}
		fw.close();
		br.close();
		
		// start formatting the copied code
		boolean isFormatted = true;
		try {
			// execute the formatter
			Process p = Runtime.getRuntime().exec(
					new String[] { pythonCompilerPath, pythonFormatterPath,
							filepath + ".formatted", pythonStyleSettingPath});

			/*
			 * check whether such execution produces error. If it is, mark
			 * formatting as unsuccessful.
			 */
			br = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					isFormatted = false;
					break;
				}
			}
			br.close();

			// wait the process till finished
			p.waitFor();
		} catch (Exception e) {
			// if error, mark also as unsuccessfull
			isFormatted = false;
		}

		return isFormatted;
	}
}
