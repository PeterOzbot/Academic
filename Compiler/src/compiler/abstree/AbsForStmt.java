package compiler.abstree;

import java.io.*;

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

	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"ForStmt\">");
		if (getPosition() != null) getPosition().toXML(xml);
		name.toXML(xml);
		loBound.toXML(xml);
		hiBound.toXML(xml);
		loopExpr.toXML(xml);
		xml.println("</absnode>");
	}

}
