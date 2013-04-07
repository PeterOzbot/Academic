package compiler.abstree;

/** Tabelarni tip.  */
public class AbsArrType extends AbsType {

	/** Osnovni tip.  */
	public final AbsType type;
	
	/** Velikost tabele.  */
	public final AbsExpr size;
	
	public AbsArrType(AbsType type, AbsExpr size) {
		this.type = type;
		this.size = size;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
