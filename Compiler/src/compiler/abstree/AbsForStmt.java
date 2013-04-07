package compiler.abstree;

/** Opis <code>for</code> stavka.  */
public class AbsForStmt extends AbsStmt {

	/** Zancna spremenljivka.  */
	public final AbsExprName name;
	
	/** Spodnja meja.  */
	public final AbsExpr loBound;
	
	/** Zgornja meja.  */
	public final AbsExpr hiBound;
	
	/** Telo zanke.  */
	public final AbsExpr loopExpr;
	
	public AbsForStmt(AbsExprName name, AbsExpr loBound, AbsExpr hiBound, AbsExpr loopExpr) {
		this.name = name;
		this.loBound = loBound;
		this.hiBound = hiBound;
		this.loopExpr = loopExpr;
	}

	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
