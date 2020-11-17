package disguisegenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import support.NaturalLanguageProcesser;
import tuple.LibTuple;

public class CodeObfuscatorIdentifier {

	public static void applyIdentiifierObfuscation(ArrayList<LibTuple> tokenString, String languageCode,
			HashMap<String, String> namePairs, String filename, ArrayList<Integer> selectedDisguises) {

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			String newText = in;
			for (Integer i : selectedDisguises) {
				// update the string till its final form

				// identifier 35-44

				if (i == 35) {
					// 35 removing all stop words from the identifiers' sub-words if
					// these sub-words are separated by underscore or next character
					// capitalisation.
					newText = CodeObfuscatorIdentifier._i01RemovingStopWords(newText, languageCode);
				} else if (i == 36) {
					// 36 removing all underscores from the identifiers.
					newText = newText.replaceAll("_", "");
				} else if (i == 37) {
					// 37 removing all numbers from the identifiers.
					newText = newText.replaceAll("0", "").replaceAll("1", "").replaceAll("2", "").replaceAll("3", "")
							.replaceAll("4", "").replaceAll("5", "").replaceAll("6", "").replaceAll("7", "")
							.replaceAll("8", "").replaceAll("9", "");
				} else if (i == 38) {
					// 38 capitalising all identifier's characters.
					newText = newText.toUpperCase();
				} else if (i == 39) {
					// 39 decapitalising all identifier's characters.
					newText = newText.toLowerCase();
				} else if (i == 40) {
					// 40 replacing all identifiers' sub-word transitions from
					// underscore to next character capitalisation (e.g., 'this_is_var'
					// to 'thisIsVar').
					newText = _i06replacingSubWordTransitionsfromthis_is_vartothisIsVar(newText);
				} else if (i == 41) {
					// 41 replacing all identifiers' sub-word transitions from next
					// character capitalisation to underscore (e.g., 'thisIsVar' to
					// 'this_is_var').
					newText = _i07replacingSubWordTransitionsfromthisIsVartothis_is_var(newText);
				} else if (i == 42) {
					// 42 renaming all identifiers by keeping only the first character
					// each.
					newText = (newText.charAt(0) + "");
				} else if (i == 43) {
					// 43 renaming all identifiers by keeping their acronyms (generated
					// by removing all vocals except the first char).
					String newText2 = "";
					for (int k = 0; k < newText.length(); k++) {
						char c = newText.charAt(k);

						// if it is vocal in non-first pos, skip
						if ((c == 'a' || c == 'A' || c == 'i' || c == 'I' || c == 'u' || c == 'U' || c == 'e'
								|| c == 'E' || c == 'o' || c == 'O') && k != 0)
							continue;

						newText2 += c;
					}

					newText = newText2;
				} else if (i == 44) {
					// 44 anonymising all identifiers by renaming them as
					// 'anonymisedIdent'.
					String newText2 = "anonymisedIdent";
					if (languageCode.equals("id"))
						newText2 = "identifierAnonim";

					newText = newText2;
				}
			}

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		// update the identifier names
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}

	}

	public static String _i01RemovingStopWords(String in, String languageCode) {
		/*
		 * remove all stop words from identifiers. 'en' is the language code for english
		 * while 'id' is for indonesian. There is a mechanism to deal with conflicting
		 * names.
		 */

		// tokenise the text
		ArrayList<String> subwords = tokenizeIdentifier(in);

		// per word, check whether it is a stop word. If true, remove
		for (int j = 0; j < subwords.size(); j++) {
			String c = subwords.get(j);
			if (NaturalLanguageProcesser.isStopWord(c.toLowerCase(), languageCode)) {
				subwords.remove(j);
				j--;
			}
		}

		// create the new identifier
		String newText = "";
		for (int j = 0; j < subwords.size(); j++) {
			newText += subwords.get(j);
		}

		return newText;

	}

	public static String _i06replacingSubWordTransitionsfromthis_is_vartothisIsVar(String in) {
		/*
		 * replacing all identifiers' sub-word transitions from underscore to next
		 * character capitalisation (e.g., 'this_is_var' to 'thisIsVar'). There is a
		 * mechanism to deal with conflicting names.
		 */

		// tokenise the text
		ArrayList<String> subwords = tokenizeIdentifier(in);

		// remove each _ and capitalise the next token
		// exclude the last token from consideration
		for (int j = 0; j < subwords.size() - 1; j++) {
			String c = subwords.get(j);
			if (c.equals("_")) {
				// remove the token
				subwords.remove(j);

				// capitalise the first char of the next sub word
				String nextSubWord = subwords.get(j);
				String firstchar = (nextSubWord.charAt(0) + "");
				// capitalise if it is not the first word
				if (j != 0)
					firstchar = firstchar.toUpperCase();
				// merge to remaining characters
				nextSubWord = firstchar + nextSubWord.substring(1);
				subwords.set(j, nextSubWord);
			}
		}

		// create the new identifier
		String newText = "";
		for (int j = 0; j < subwords.size(); j++) {
			newText += subwords.get(j);
		}

		return newText;
	}

	public static String _i07replacingSubWordTransitionsfromthisIsVartothis_is_var(String in) {
		/*
		 * replacing all identifiers' sub-word transitions from next character
		 * capitalisation to underscore (e.g., 'thisIsVar' to 'this_is_var'). There is a
		 * mechanism to deal with conflicting names.
		 */

		// tokenise the text
		ArrayList<String> subwords = tokenizeIdentifier(in);

		// for each two adjacent non underscore words, put underscore
		// between them
		for (int j = 0; j < subwords.size(); j++) {
			String c = subwords.get(j);
			if (j > 0) {
				String prev = subwords.get(j - 1);
				if (!c.equals("_") && !prev.equals("_")) {
					// add an underscore
					subwords.add(j, "_");
					j++;
					// lowercase the c word
					subwords.set(j, c.toLowerCase());
				}
			}
		}

		// create the new identifier
		String newText = "";
		for (int j = 0; j < subwords.size(); j++) {
			newText += subwords.get(j);
		}

		return newText;
	}

	public static void i01RemovingStopWords(ArrayList<LibTuple> tokenString, String languageCode,
			HashMap<String, String> namePairs, String filename) {
		/*
		 * remove all stop words from identifiers. 'en' is the language code for english
		 * while 'id' is for indonesian. There is a mechanism to deal with conflicting
		 * names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// tokenise the text
			ArrayList<String> subwords = tokenizeIdentifier(in);

			// per word, check whether it is a stop word. If true, remove
			for (int j = 0; j < subwords.size(); j++) {
				String c = subwords.get(j);
				if (NaturalLanguageProcesser.isStopWord(c.toLowerCase(), languageCode)) {
					subwords.remove(j);
					j--;
				}
			}

			// create the new identifier
			String newText = "";
			for (int j = 0; j < subwords.size(); j++) {
				newText += subwords.get(j);
			}

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		// update the identifier names
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}

	}

	private static ArrayList<String> _getAllIdentifierNames(ArrayList<LibTuple> tokenString) {
		// get all identifier names
		ArrayList<String> identifiers = new ArrayList<>();
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				// add to identifier if unique
				if (!identifiers.contains(t.getText()))
					identifiers.add(t.getText());
			}
		}

		return identifiers;
	}

	private static String _getNonConflictingIdentName(String name, HashMap<String, String> namePairs) {
		// rename the ident if redundant. Adding a single number as the prefix.

		// if the name is empty, add dummy name
		if (name.length() == 0)
			name = "a";

		int counter = 1;
		String newname = name;
		while (namePairs.values().contains(newname)) {
			newname = name + counter;
			counter++;
		}
		return newname;
	}

	public static ArrayList<String> tokenizeIdentifier(String ident) {
		/*
		 * This method tokenise the ident to several subwords based on capital
		 * transition or underscore. Adapted from source code authorship project,
		 * IdentDataHandler class
		 */
		ArrayList<String> output = new ArrayList<String>();
		String tempTerm = "";
		/*
		 * lastType merupakan tipe karakter sebelumnya. 0 merupakan karakter biasa, 1
		 * kapital, 2 angka
		 */
		int lastType = -1;
		for (int i = 0; i < ident.length(); i++) {
			char c = ident.charAt(i);
			if (c >= 'a' && c <= 'z') {
				/*
				 * Jika berbeda tipe dan jumlah karakter lebih besar dari 1, lakukan proses
				 * pemotongan.
				 */
				if (lastType != 0) {
					if (lastType == 1) {
						/*
						 * Jika karakter sebelumnya kapital, tambahkan substring dari tempterm tampa
						 * melibatkan karakter terakhir, set tempterm dengan karakter terakhir yang
						 * dilowercase
						 */
						// ambil semua karakter awal
						String tempTerm2 = tempTerm.substring(0, tempTerm.length() - 1);
						// tambahkan dalam list
						if (tempTerm2.length() > 0)
							output.add(tempTerm2);
						// set dengan karakter pertama
						tempTerm = tempTerm.charAt(tempTerm.length() - 1) + "";
					} else {
						if (tempTerm.length() > 0)
							output.add(tempTerm);
						tempTerm = "";
					}
				}
				tempTerm += c;
				lastType = 0;
			} else if (c >= '0' && c <= '9') {
				if (lastType != 2) {
					if (tempTerm.length() > 0)
						output.add(tempTerm);
					tempTerm = "";
				}
				tempTerm += c;
				lastType = 2;
			} else if (c >= 'A' && c <= 'Z') {
				/*
				 * jika karakter sebelumnya bukan kapital, tambahkan dulu string tersebut dalam
				 * termList. kasus osCar jadi os dan car
				 */
				if (lastType != 1) {
					if (tempTerm.length() > 0)
						output.add(tempTerm);
					tempTerm = "";
				}
				tempTerm += c;
				lastType = 1;
			} else {
				if (tempTerm.length() > 0)
					output.add(tempTerm);
				tempTerm = "";
				lastType = -1;
				// so that the underscore is still stored in the result
				output.add("_");
			}
		}
		if (tempTerm.length() > 0)
			output.add(tempTerm);

		return output;
	}

	public static void i02Removing_(ArrayList<LibTuple> tokenString, HashMap<String, String> namePairs,
			String filename) {
		/*
		 * remove all underscores from identifiers. There is a mechanism to deal with
		 * conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// remove all underscores
			String newText = in.replaceAll("_", "");

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

	public static void i03RemovingNumbers(ArrayList<LibTuple> tokenString, HashMap<String, String> namePairs,
			String filename) {
		/*
		 * remove all numbers from identifiers. There is a mechanism to deal with
		 * conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// remove all numbers
			String newText = in.replaceAll("0", "").replaceAll("1", "").replaceAll("2", "").replaceAll("3", "")
					.replaceAll("4", "").replaceAll("5", "").replaceAll("6", "").replaceAll("7", "").replaceAll("8", "")
					.replaceAll("9", "");

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

	public static void i04CapitalisingAllCharacters(ArrayList<LibTuple> tokenString, HashMap<String, String> namePairs,
			String filename) {
		/*
		 * capitalise all chars in identifiers. There is a mechanism to deal with
		 * conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// capitalise
			String newText = in.toUpperCase();

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

	public static void i05DecapitalisingAllCharacters(ArrayList<LibTuple> tokenString,
			HashMap<String, String> namePairs, String filename) {
		/*
		 * decapitalise all chars in identifiers. There is a mechanism to deal with
		 * conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// decapitalise
			String newText = in.toLowerCase();

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

	public static void i06replacingSubWordTransitionsfromthis_is_vartothisIsVar(ArrayList<LibTuple> tokenString,
			HashMap<String, String> namePairs, String filename) {
		/*
		 * replacing all identifiers' sub-word transitions from underscore to next
		 * character capitalisation (e.g., 'this_is_var' to 'thisIsVar'). There is a
		 * mechanism to deal with conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// tokenise the text
			ArrayList<String> subwords = tokenizeIdentifier(in);

			// remove each _ and capitalise the next token
			// exclude the last token from consideration
			for (int j = 0; j < subwords.size() - 1; j++) {
				String c = subwords.get(j);
				if (c.equals("_")) {
					// remove the token
					subwords.remove(j);

					// capitalise the first char of the next sub word
					String nextSubWord = subwords.get(j);
					String firstchar = (nextSubWord.charAt(0) + "");
					// capitalise if it is not the first word
					if (j != 0)
						firstchar = firstchar.toUpperCase();
					// merge to remaining characters
					nextSubWord = firstchar + nextSubWord.substring(1);
					subwords.set(j, nextSubWord);
				}
			}

			// create the new identifier
			String newText = "";
			for (int j = 0; j < subwords.size(); j++) {
				newText += subwords.get(j);
			}

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		// update the identifier names
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

	public static void i07replacingSubWordTransitionsfromthisIsVartothis_is_var(ArrayList<LibTuple> tokenString,
			HashMap<String, String> namePairs, String filename) {
		/*
		 * replacing all identifiers' sub-word transitions from next character
		 * capitalisation to underscore (e.g., 'thisIsVar' to 'this_is_var'). There is a
		 * mechanism to deal with conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// tokenise the text
			ArrayList<String> subwords = tokenizeIdentifier(in);

			// for each two adjacent non underscore words, put underscore
			// between them
			for (int j = 0; j < subwords.size(); j++) {
				String c = subwords.get(j);
				if (j > 0) {
					String prev = subwords.get(j - 1);
					if (!c.equals("_") && !prev.equals("_")) {
						// add an underscore
						subwords.add(j, "_");
						j++;
						// lowercase the c word
						subwords.set(j, c.toLowerCase());
					}
				}
			}

			// create the new identifier
			String newText = "";
			for (int j = 0; j < subwords.size(); j++) {
				newText += subwords.get(j);
			}

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		// update the identifier names
		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

	public static void i08KeepingOnlyTheFirstCharacter(ArrayList<LibTuple> tokenString,
			HashMap<String, String> namePairs, String filename) {
		/*
		 * shorten the identifiers by only keeping the first char There is a mechanism
		 * to deal with conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// get the first char only
			String newText = (in.charAt(0) + "");

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

	public static void i09KeepingOnlyTheConsonants(ArrayList<LibTuple> tokenString, HashMap<String, String> namePairs,
			String filename) {
		/*
		 * shorten the identifiers by only keeping the consonants as acronyms. Per
		 * subword, the vocal is only stored if it is the first char. There is a
		 * mechanism to deal with conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// tokenise the text
			ArrayList<String> subwords = tokenizeIdentifier(in);

			// for each subword
			for (int j = 0; j < subwords.size(); j++) {
				String s = subwords.get(j);
				// remove all vocals
				String newText = "";
				for (int k = 0; k < s.length(); k++) {
					char c = s.charAt(k);

					// if it is vocal in non-first pos, skip
					if ((c == 'a' || c == 'A' || c == 'i' || c == 'I' || c == 'u' || c == 'U' || c == 'e' || c == 'E'
							|| c == 'o' || c == 'O') && k != 0)
						continue;

					newText += c;
				}

				// store the text as the updated sub word
				subwords.set(j, newText);
			}

			// create the new identifier
			String newText = "";
			for (int j = 0; j < subwords.size(); j++) {
				newText += subwords.get(j);
			}

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

	public static void i10AnonymisingAllIdentifiers(ArrayList<LibTuple> tokenString, String language,
			HashMap<String, String> namePairs, String filename) {
		/*
		 * anonymise all identifiers by renaming them as anonymisedIdent. The vocal is
		 * only stored if it is the first char. There is a mechanism to deal with
		 * conflicting names.
		 */

		// get the identifiers
		ArrayList<String> identifiers = _getAllIdentifierNames(tokenString);
		identifiers.add(filename);
		Iterator<String> it = identifiers.iterator();
		while (it.hasNext()) {
			String in = it.next();

			// if the old identifier name has already listed
			if (namePairs.get(in) != null)
				continue;

			// get the new name
			String newText = "anonymisedIdent";
			if (language.equals("id"))
				newText = "identifierAnonim";

			// generate the non conflicting name
			newText = _getNonConflictingIdentName(newText, namePairs);

			// put as the new pair
			namePairs.put(in, newText);
		}

		for (int i = 0; i < tokenString.size(); i++) {
			LibTuple t = tokenString.get(i);
			// check whether t is identifier
			if (t.getType().equals("Identifier")) {
				String text = t.getText();
				// update the name
				t.setText(namePairs.get(text));
			}
		}
	}

}
