package compiler.abstree;

import java.io.*;
import java.util.*;

/** Seznam izrazov.  */
public class AbsExprs extends AbsExpr {

	public final Vector<AbsExpr> exprs;
	
	public AbsExprs(Vector<AbsExpr> exprs) {
		this.exprs = exprs;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"Exprs\">");
		if (getPosition() != null) getPosition().toXML(xml);
		for (AbsExpr expr: exprs) expr.toXML(xml);
		xml.println("</absnode>");
	}
	
}
