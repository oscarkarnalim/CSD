package disguisegenerator;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import tuple.LibTuple;

public class CodeObfuscatorCore {

	public static void printTokenString(
			ArrayList<LibTuple> tokenString) {
		// this method print the token string
		for (int i = 0; i < tokenString.size() - 1; i++) {
			LibTuple token = tokenString.get(i);
			System.out.println(token.getLine() + ":" + token.getText() + " "
					+ token.getType());
		}
		if (tokenString.size() > 0)
			System.out.println(tokenString.get(tokenString.size() - 1)
					.getText());
	}

	public static void writeCode(String filePath,
			ArrayList<LibTuple> tokenString) {
		// this method write the token string to a file
		try {
			FileWriter fw = new FileWriter(filePath);
			for (LibTuple t : tokenString) {
				fw.write(t.getText());
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Random r = new Random();
}
