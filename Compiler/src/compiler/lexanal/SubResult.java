package compiler.lexanal;

public class SubResult {
	public Symbol Symbol = null;
	public Boolean PreskociZnak = false;

	public SubResult(Symbol symbol, Boolean preskociZnak) {
		Symbol = symbol;
		PreskociZnak = preskociZnak;
	}
}
