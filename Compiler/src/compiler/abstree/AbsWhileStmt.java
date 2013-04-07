package compiler.abstree;

/** Opis <code>while</code> stavka.  */
public class AbsWhileStmt extends AbsStmt {

	/** Pogoj.  */
	public final AbsExpr condExpr;
	
	/** Telo zanke.  */
	public final AbsExpr loopExpr;

	public AbsWhileStmt(AbsExpr condExpr, AbsExpr loopExpr) {
		this.condExpr = condExpr;
		this.loopExpr = loopExpr;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
