package compiler.abstree;

/** Klic funkcije.  */
public class AbsFunCall extends AbsExpr {
	
	/** Ime funkcije.  */
	public final AbsExprName name;
	
	/** Argumenti funkcije.  */
	public final AbsExprs args;

	public AbsFunCall(AbsExprName name, AbsExprs args) {
		this.name = name;
		this.args = args;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
