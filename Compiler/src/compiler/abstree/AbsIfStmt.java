package compiler.abstree;

/** Opis <code>if</code> stavka.  */
public class AbsIfStmt extends AbsStmt {
	
	/** Pogoj.  */
	public final AbsExpr condExpr;
	
	/** Pozitivna veja.  */
	public final AbsExpr thenExprs;
	
	/** Negativna veja.  */
	public final AbsExpr elseExprs;
	
	public AbsIfStmt(AbsExpr condExpr, AbsExpr thenExprs, AbsExpr elseExprs) {
		this.condExpr = condExpr;
		this.thenExprs = thenExprs;
		this.elseExprs = elseExprs;
	}

	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
