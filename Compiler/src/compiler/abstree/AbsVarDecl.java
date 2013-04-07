package compiler.abstree;

/** Deklaracija spremenljivke. */
public class AbsVarDecl extends AbsDecl {

	/** Ime spremelnjivke.  */
	public final AbsExprName name;
	
	/** Tip spremenljivke.  */
	public final AbsType type;
	
	public AbsVarDecl(AbsExprName name, AbsType type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
