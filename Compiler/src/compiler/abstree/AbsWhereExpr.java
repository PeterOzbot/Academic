package compiler.abstree;

/** Opis <code>where</code> izraza.  */
public class AbsWhereExpr extends AbsExpr {

	/** Izraz.  */
	public final AbsExpr subExpr;
	
	/** Deklaracije.  */
	public final AbsDecls decls;
	
	public AbsWhereExpr(AbsExpr subExpr, AbsDecls decls) {
		this.subExpr = subExpr;
		this.decls = decls;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
