package compiler.abstree;

import java.io.*;

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
	
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"WhileStmt\">");
		if (getPosition() != null) getPosition().toXML(xml);
		condExpr.toXML(xml);
		loopExpr.toXML(xml);
		xml.println("</absnode>");
	}

}
