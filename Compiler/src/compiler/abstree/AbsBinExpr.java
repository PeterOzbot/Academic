package compiler.abstree;

import java.io.*;

/** Izraz z binarnim operatorjem.  */
public class AbsBinExpr extends AbsExpr {

	public static final int OR  = 0;
	public static final int AND = 1;
	public static final int EQU = 2;
	public static final int NEQ = 3;
	public static final int LTH = 4;
	public static final int GTH = 5;
	public static final int LEQ = 6;
	public static final int GEQ = 7;
	public static final int ADD = 8;
	public static final int SUB = 9;
	public static final int MUL = 10;
	public static final int DIV = 11;
	public static final int MOD = 12;
	public static final int ARR = 13;
	public static final int REC = 14;
	
	/** Binarni operator.  */
	public final int oper;
	
	/** Prvi (levi) podizraz.  */
	public final AbsExpr fstSubExpr;
	
	/** Drugi (desni) podizraz.  */
	public final AbsExpr sndSubExpr;
	
	public AbsBinExpr(int oper, AbsExpr fstSubExpr, AbsExpr sndSubExpr) {
		this.oper = oper;
		this.fstSubExpr = fstSubExpr;
		this.sndSubExpr = sndSubExpr;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		String operName;
		switch (oper) {
		case OR : operName = "OR"   ; break;
		case AND: operName = "AND"  ; break;
		case EQU: operName = "EQU"  ; break;
		case NEQ: operName = "NEQ"  ; break;
		case LTH: operName = "LTH"  ; break;
		case GTH: operName = "GTH"  ; break;
		case LEQ: operName = "LEQ"  ; break;
		case GEQ: operName = "GEQ"  ; break;
		case ADD: operName = "ADD"  ; break;
		case SUB: operName = "SUB"  ; break;
		case MUL: operName = "MUL"  ; break;
		case DIV: operName = "DIV"  ; break;
		case MOD: operName = "MOD"  ; break;
		case ARR: operName = "ARR"  ; break;
		case REC: operName = "REC"  ; break;
		default : operName = "ERROR"; break;
		}
		xml.println("<absnode node=\"BinExpr\" value=\"" + operName + "\">");
		if (getPosition() != null) getPosition().toXML(xml);
		fstSubExpr.toXML(xml);
		sndSubExpr.toXML(xml);
		xml.println("</absnode>");
	}
	
}
