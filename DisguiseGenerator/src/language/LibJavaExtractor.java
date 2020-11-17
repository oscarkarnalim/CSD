package language;

import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import support.AdditionalKeywordsManager;
import support.javaantlr.Java8Lexer;
import tuple.LibTuple;

public class LibJavaExtractor {
	
	public static ArrayList<LibTuple> getDefaultTokenString(
			String filePath, String additionalKeywordsPath) {
		// take all tokens including comments and whitespaces and keep some
		// tokens as keywords
		try {
			ArrayList<LibTuple> result = new ArrayList<>();
			// build the lexer
			Lexer lexer = new Java8Lexer(new ANTLRFileStream(filePath));
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// only till size-1 as the last one is EOF token
			for (int index = 0; index < tokens.size() - 1; index++) {
				Token token = tokens.get(index);
				// take all tokens including whitespaces
				result.add(new LibTuple(token
						.getText(), Java8Lexer.VOCABULARY.getDisplayName(token
						.getType()), token.getLine()));
			}

			if (additionalKeywordsPath != null && additionalKeywordsPath != "") {
				// read the additional keywords
				ArrayList<ArrayList<String>> additionalKeywords = AdditionalKeywordsManager
						.readAdditionalKeywords(additionalKeywordsPath);
				for (int i = 0; i < result.size(); i++) {
					// skip if whitespaces or comments
					if(result.get(i).getType().equals("WS")
							|| result.get(i).getType().endsWith("COMMENT")) 
						continue;
					
					// if it is the beginning of keywords
					int pos = AdditionalKeywordsManager.p4IndexOf(i, result,
							additionalKeywords);
					if (pos != -1) {
						// number of left matches
						int matchedLeft = additionalKeywords.get(pos).size();
						// check the existence of that keyword in token string
						int j = i;
						for(;j<result.size();j++) {
							LibTuple token = result.get(j);
							
							// skip if whitespaces or comments
							if(token.getType().equals("WS")
									|| token.getType().endsWith("COMMENT")) 
								continue;
							else {
								// set the type to additional keyword
								token.setType("additional_keyword");
								// reduce matched left by one
								matchedLeft--;
								// stop if no matches left
								if(matchedLeft == 0)
									break;
							}
						}

						// skip the position
						i = j;
					}
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
			Lexer lexer = new Java8Lexer(new ANTLRFileStream(filePath));
			// extract the tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			// only till size-1 as the last one is EOF token
			for (int index = 0; index < tokens.size() - 1; index++) {
				Token token = tokens.get(index);
				// take all tokens including whitespaces
				result.add(new LibTuple(token.getText(),
						Java8Lexer.VOCABULARY.getDisplayName(token.getType()), token.getLine()));
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
						// loop behaviour
						i += (additionalKeywords.get(pos).size() - 1);
					}
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
