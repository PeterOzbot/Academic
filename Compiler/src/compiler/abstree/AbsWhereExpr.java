package compiler.abstree;

import java.io.*;

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
	
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"WhereExpr\">");
		if (getPosition() != null) getPosition().toXML(xml);
		subExpr.toXML(xml);
		decls.toXML(xml);
		xml.println("</absnode>");
	}
	
}
