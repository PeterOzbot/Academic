package compiler.abstree;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;

/** Atomarni izraz (konstanta osnovnega podatkovnega tipa). */
public class AbsAtomExpr extends AbsExpr {

	/** Konstanta. */
	public final Symbol expr;
	
	public AbsAtomExpr(Symbol expr) {
		if (expr == null) this.expr = null; else
		switch (expr.getToken()) {
		case Symbol.INTCONST   :
		case Symbol.REALCONST  :
		case Symbol.BOOLCONST  :
		case Symbol.STRINGCONST:
			this.expr = expr;
			break;
		default:
			this.expr = null;
			Report.error("Internal error.", 1);
		}
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"AtomExpr\">");
		if (getPosition() != null) getPosition().toXML(xml);
		if (expr != null) expr.toXML(xml);
		xml.println("</absnode>");
	}

}
