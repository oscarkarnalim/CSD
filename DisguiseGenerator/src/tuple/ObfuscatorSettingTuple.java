package tuple;

public class ObfuscatorSettingTuple {

	protected int nForMaxCharsInSingleLineComment;
	protected int nForMaxCharsInMultiLineComment, nForSpacesReplacedByTab;
	protected String prefixSingleLineComment, prefixMultiLineComment;
	protected String postfixMultiLineComment, humanLanguage;
	protected boolean isDataTypeSensitive;
	protected boolean isSingleQuoteAlsoStringSeparator;
	protected boolean isWhitespacesIgnorable;

	// for applied disguises
	protected double singleLineCommentRemovalProb, multiLineCommentRemovalProb,
			allCommentRemovalProb;
	protected int numReplacingSpaces, numReplacingTabs, numReplacingNewlines,
			numReplacingSpacesForTabs;

	public ObfuscatorSettingTuple(int nForMaxCharsInSingleLineComment,
			int nForMaxCharsInMultiLineComment, int nForSpacesReplacedByTab,
			String prefixSingleLineComment, String prefixMultiLineComment,
			String postfixMultiLineComment, String humanLanguage,
			boolean isDataTypeSensitive,
			boolean isSingleQuoteAlsoStringSeparator,
			boolean isWhitespacesIgnorable,
			double singleLineCommentRemovalProb,
			double multiLineCommentRemovalProb, double allCommentRemovalProb,
			int numReplacingSpaces, int numReplacingTabs,
			int numReplacingNewlines, int numReplacingSpacesForTabs) {
		super();
		this.nForMaxCharsInSingleLineComment = nForMaxCharsInSingleLineComment;
		this.nForMaxCharsInMultiLineComment = nForMaxCharsInMultiLineComment;
		this.nForSpacesReplacedByTab = nForSpacesReplacedByTab;
		this.prefixSingleLineComment = prefixSingleLineComment;
		this.prefixMultiLineComment = prefixMultiLineComment;
		this.postfixMultiLineComment = postfixMultiLineComment;
		this.humanLanguage = humanLanguage;
		this.isDataTypeSensitive = isDataTypeSensitive;
		this.isSingleQuoteAlsoStringSeparator = isSingleQuoteAlsoStringSeparator;
		this.isWhitespacesIgnorable = isWhitespacesIgnorable;
		this.singleLineCommentRemovalProb = singleLineCommentRemovalProb;
		this.multiLineCommentRemovalProb = multiLineCommentRemovalProb;
		this.allCommentRemovalProb = allCommentRemovalProb;
		this.numReplacingSpaces = numReplacingSpaces;
		this.numReplacingTabs = numReplacingTabs;
		this.numReplacingNewlines = numReplacingNewlines;
		this.numReplacingSpacesForTabs = numReplacingSpacesForTabs;
	}

	// to check whether the language has multi line comment
	public boolean isFacilitatingMultiLineComment() {
		if (getPrefixMultiLineComment().length() > 0)
			return true;
		else
			return false;
	}

	public int getnForMaxCharsInSingleLineComment() {
		return nForMaxCharsInSingleLineComment;
	}

	public void setnForMaxCharsInSingleLineComment(
			int nForMaxCharsInSingleLineComment) {
		this.nForMaxCharsInSingleLineComment = nForMaxCharsInSingleLineComment;
	}

	public int getnForMaxCharsInMultiLineComment() {
		return nForMaxCharsInMultiLineComment;
	}

	public void setnForMaxCharsInMultiLineComment(
			int nForMaxCharsInMultiLineComment) {
		this.nForMaxCharsInMultiLineComment = nForMaxCharsInMultiLineComment;
	}

	public int getnForSpacesReplacedByTab() {
		return nForSpacesReplacedByTab;
	}

	public void setnForSpacesReplacedByTab(int nForSpacesReplacedByTab) {
		this.nForSpacesReplacedByTab = nForSpacesReplacedByTab;
	}

	public String getPrefixSingleLineComment() {
		return prefixSingleLineComment;
	}

	public void setPrefixSingleLineComment(String prefixSingleLineComment) {
		this.prefixSingleLineComment = prefixSingleLineComment;
	}

	public String getPrefixMultiLineComment() {
		return prefixMultiLineComment;
	}

	public void setPrefixMultiLineComment(String prefixMultiLineComment) {
		this.prefixMultiLineComment = prefixMultiLineComment;
	}

	public String getPostfixMultiLineComment() {
		return postfixMultiLineComment;
	}

	public void setPostfixMultiLineComment(String postfixMultiLineComment) {
		this.postfixMultiLineComment = postfixMultiLineComment;
	}

	public String getHumanLanguage() {
		return humanLanguage;
	}

	public void setHumanLanguage(String humanLanguage) {
		this.humanLanguage = humanLanguage;
	}

	public boolean isDataTypeSensitive() {
		return isDataTypeSensitive;
	}

	public void setDataTypeSensitive(boolean isDataTypeSensitive) {
		this.isDataTypeSensitive = isDataTypeSensitive;
	}

	public boolean isSingleQuoteAlsoStringSeparator() {
		return isSingleQuoteAlsoStringSeparator;
	}

	public void setSingleQuoteAlsoStringSeparator(
			boolean isSingleQuoteAlsoStringSeparator) {
		this.isSingleQuoteAlsoStringSeparator = isSingleQuoteAlsoStringSeparator;
	}

	public boolean isWhitespacesIgnorable() {
		return isWhitespacesIgnorable;
	}

	public void setWhitespacesIgnorable(boolean isWhitespacesIgnorable) {
		this.isWhitespacesIgnorable = isWhitespacesIgnorable;
	}

	public double getSingleLineCommentRemovalProb() {
		return singleLineCommentRemovalProb;
	}

	public void setSingleLineCommentRemovalProb(double singleLineCommentRemovalProb) {
		this.singleLineCommentRemovalProb = singleLineCommentRemovalProb;
	}

	public double getMultiLineCommentRemovalProb() {
		return multiLineCommentRemovalProb;
	}

	public void setMultiLineCommentRemovalProb(double multiLineCommentRemovalProb) {
		this.multiLineCommentRemovalProb = multiLineCommentRemovalProb;
	}

	public double getAllCommentRemovalProb() {
		return allCommentRemovalProb;
	}

	public void setAllCommentRemovalProb(double allCommentRemovalProb) {
		this.allCommentRemovalProb = allCommentRemovalProb;
	}

	public int getNumReplacingSpaces() {
		return numReplacingSpaces;
	}

	public void setNumReplacingSpaces(int numReplacingSpaces) {
		this.numReplacingSpaces = numReplacingSpaces;
	}

	public int getNumReplacingTabs() {
		return numReplacingTabs;
	}

	public void setNumReplacingTabs(int numReplacingTabs) {
		this.numReplacingTabs = numReplacingTabs;
	}

	public int getNumReplacingNewlines() {
		return numReplacingNewlines;
	}

	public void setNumReplacingNewlines(int numReplacingNewlines) {
		this.numReplacingNewlines = numReplacingNewlines;
	}

	public int getNumReplacingSpacesForTabs() {
		return numReplacingSpacesForTabs;
	}

	public void setNumReplacingSpacesForTabs(int numReplacingSpacesForTabs) {
		this.numReplacingSpacesForTabs = numReplacingSpacesForTabs;
	}

}
