package compiler.abstree;

/** Deklaracija funkcije. */
public class AbsFunDecl extends AbsDecl {

	/** Ime funkcije.  */
	public final AbsExprName name;
	
	/** Parametri funkcije.  */
	public final AbsDecls pars;
	
	/** Tip funkcije.  */
	public final AbsType type;

	/** Izraz funckije.  */
	public final AbsExpr expr;
	
	public AbsFunDecl(AbsExprName name, AbsDecls pars, AbsType type, AbsExpr expr) {
		this.name = name;
		this.pars = pars;
		this.type = type;
		this.expr = expr;
	}

	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
