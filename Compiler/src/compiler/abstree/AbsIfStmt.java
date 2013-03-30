package compiler.abstree;

import java.io.*;

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

	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"IfStmt\">");
		if (getPosition() != null) getPosition().toXML(xml);
		condExpr.toXML(xml);
		thenExprs.toXML(xml);
		if (elseExprs != null) elseExprs.toXML(xml);
		xml.println("</absnode>");
	}

}
