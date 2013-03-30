package compiler.abstree;

import java.io.*;

public class AbsAssignStmt extends AbsStmt {

	/** Prvi (levi) podizraz.  */
	public final AbsExpr fstSubExpr;
	
	/** Drugi (desni) podizraz.  */
	public final AbsExpr sndSubExpr;
	
	public AbsAssignStmt(AbsExpr fstSubExpr, AbsExpr sndSubExpr) {
		this.fstSubExpr = fstSubExpr;
		this.sndSubExpr = sndSubExpr;
	}
	
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"AssignStmt\">");
		if (getPosition() != null) getPosition().toXML(xml);
		fstSubExpr.toXML(xml);
		sndSubExpr.toXML(xml);
		xml.println("</absnode>");
	}

}
