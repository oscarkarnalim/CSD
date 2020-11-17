package support;

import java.util.HashSet;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianStemmer;

public class NaturalLanguageProcesser {
	public static IndonesianStemmer indonesianStemmer = new IndonesianStemmer();
	public static HashSet<String> indonesianStopWords = new HashSet<>();
	public static HashSet<String> englishStopWords = new HashSet<>();

	static {
		fillStopWords(IndonesianAnalyzer.getDefaultStopSet().toString(),
				indonesianStopWords);
		fillStopWords(EnglishAnalyzer.getDefaultStopSet().toString(),
				englishStopWords);
	}

	// get stop words from a string resulted from Lucene analyser
	private static void fillStopWords(String rawSW,
			HashSet<String> out) {
		rawSW = rawSW.substring(1, rawSW.length() - 1);
		String[] SWList = rawSW.split(",");
		for (String s : SWList) {
			out.add(s.trim());
		}
	}

	// public static PorterStemmer ps;

	private static String getIndonesianStem(String s) {
		char[] sarr = s.toCharArray();
		int newLength = indonesianStemmer.stem(sarr, sarr.length, true);
		String ns = "";
		for (int k = 0; k < newLength; k++) {
			ns += sarr[k];
		}
		// IndonesianAnalyzer.getDefaultStopSet();
		return ns;
	}

	private static String getEnglishStem(String s) {
		OwnPorterStemmer englishStemmer = new OwnPorterStemmer();
		char[] sarr = s.toCharArray();
		englishStemmer.add(sarr, sarr.length);
		englishStemmer.stem();
		sarr = englishStemmer.getResultBuffer();
		int newLength = englishStemmer.getResultLength();
		String ns = "";
		for (int k = 0; k < newLength; k++) {
			ns += sarr[k];
		}
		return ns;
	}

	public static String getStem(String s, String languageCode) {
		if (languageCode.equals("en")) {
			return getEnglishStem(s);
		} else if (languageCode.equals("id")) {
			return getIndonesianStem(s);
		}else{
			return null;
		}
	}

	public static boolean isStopWord(String s, String languageCode) {
		if (languageCode.equals("en")) {
			return englishStopWords.contains(s);
		} else if (languageCode.equals("id")){
			return indonesianStopWords.contains(s);
		}else{
			return false;
		}
	}
}
