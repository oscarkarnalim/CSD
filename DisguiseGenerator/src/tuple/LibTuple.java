package tuple;

public class LibTuple implements Comparable<LibTuple> {
	private String text, type;
	private int line;
	
	// store the text prior generalisation
	private String rawText;

	// only used for python
	private int column;

	public LibTuple(String text, String type, int line) {
		this(text, type, line, -1);
	}

	public LibTuple(String text, String type, int line, int column) {
		super();
		this.text = text;
		this.rawText = text;
		this.type = type;
		this.line = line;
		this.column = column;
	}

	public void incrementLine() {
		this.line++;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getRawText() {
		return rawText;
	}

	public void setRawText(String rawText) {
		this.rawText = rawText;
	}

	@Override
	public int compareTo(LibTuple arg0) {
		// TODO Auto-generated method stub
		if (this.getLine() != arg0.getLine())
			return this.getLine() - arg0.getLine();
		else{
			return this.getColumn() - arg0.getColumn();
		}
	}
	
	public String toString(){
		return this.getText();
	}

}
