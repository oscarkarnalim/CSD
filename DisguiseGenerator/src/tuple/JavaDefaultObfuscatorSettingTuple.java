package tuple;

public class JavaDefaultObfuscatorSettingTuple extends ObfuscatorSettingTuple{
	public JavaDefaultObfuscatorSettingTuple(String humanLanguage) {
		super(80, 80, 2,"//", "/*", "*/", humanLanguage,
				true, false, true, 0.5, 0.5, 0.5, 2, 2, 2, 2);
	}
}
