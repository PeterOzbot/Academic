package compiler.abstree;

import java.io.*;

/** Izraz z unarnim operatorjem.  */
public class AbsUnExpr extends AbsExpr {

	public static final int ADD = 0;
	public static final int SUB = 1;
	public static final int MUL = 2;
	public static final int AND = 3;
	public static final int NOT = 4;
	
	/** Unarni operator.  */
	public final int oper;
	
	/** Podizraz.  */
	public final AbsExpr subExpr;
	
	public AbsUnExpr(int oper, AbsExpr subExpr) {
		this.oper = oper;
		this.subExpr = subExpr;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		String operName;
		switch (oper) {
		case ADD: operName = "ADD"  ; break;
		case SUB: operName = "SUB"  ; break;
		case MUL: operName = "MUL"  ; break;
		case AND: operName = "AND"  ; break;
		case NOT: operName = "NOT"  ; break;
		default : operName = "ERROR"; break;
		}
		xml.println("<absnode node=\"UnExpr\" value=\"" + operName + "\">");
		if (getPosition() != null) getPosition().toXML(xml);	
		subExpr.toXML(xml);
		xml.println("</absnode>");
	}
	
}
