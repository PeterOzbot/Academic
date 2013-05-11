package compiler.imcode;

import java.io.*;

/**
 * Unarni operator: izvede unarni operator na podizrazu in vrne rezultat.
 */
public class ImUNOP extends ImCode {
	
	public static final int ADDi = 1;
	public static final int SUBi = 2;
	public static final int ADDr = 3;
	public static final int SUBr = 4;
	public static final int NOT  = 5;
	
	public final int oper;
		
	public final ImCode subExpr;
	
	public ImUNOP(int oper, ImCode subExpr) {
		this.oper = oper;
		this.subExpr = subExpr;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		String operName = "";
		switch (oper) {
		case ADDi: operName = "ADDi"; break;
		case SUBi: operName = "SUBi"; break;
		case ADDr: operName = "ADDr"; break;
		case SUBr: operName = "SUBr"; break;
		case NOT : operName = "NOT";  break;
		}
		xml.println("<iminstruction instr=\"UNOP\" value=\"" + operName + "\">");
		subExpr.toXML(xml);
		xml.println("</iminstruction>");
	}

}
