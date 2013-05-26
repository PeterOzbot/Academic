package compiler.imcode;

import java.io.*;

import compiler.frames.Temp;

/**
 * Binarni operator: izvede binarni operator nad podizrazoma in vrne rezultat.
 */
public class ImBINOP extends ImCode {
	
	public static final int OR   = 1;
	public static final int AND  = 2;

	public static final int EQUi = 3;
	public static final int NEQi = 4;
	public static final int LTHi = 5;
	public static final int GTHi = 6;
	public static final int LEQi = 7;
	public static final int GEQi = 8;

	public static final int ADDi = 9;
	public static final int SUBi = 10;
	public static final int MULi = 11;
	public static final int DIVi = 12;
	public static final int MODi = 13;
	
	public static final int EQUr = 14;
	public static final int NEQr = 15;
	public static final int LTHr = 16;
	public static final int GTHr = 17;
	public static final int LEQr = 18;
	public static final int GEQr = 20;

	public static final int ADDr = 21;
	public static final int SUBr = 22;
	public static final int MULr = 23;
	public static final int DIVr = 24;
	public static final int MODr = 25;
	
	public static final int EQUs = 26;
	public static final int NEQs = 27;
	public static final int LTHs = 28;
	public static final int GTHs = 29;
	public static final int LEQs = 30;
	public static final int GEQs = 31;
	
	public static final int EQU = 32;
	public static final int NEQ = 33;
	public static final int LTH = 34;
	public static final int GTH = 35;
	public static final int LEQ = 36;
	public static final int GEQ = 37;
	public static final int ADD = 38;
	public static final int SUB = 39;
	public static final int MUL = 40;
	public static final int DIV = 41;
	public static final int MOD = 42;
	
	public static final int ARR = 43;
	public static final int REC = 44;
	
	public final int oper;
	
	public final ImCode fstSubExpr;
	
	public final ImCode sndSubExpr;
	
	public ImBINOP(int oper, ImCode fstSubExpr, ImCode sndSubExpr) {
		this.oper = oper;
		this.fstSubExpr = fstSubExpr;
		this.sndSubExpr = sndSubExpr;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		String operName = "";
		switch (oper) {
		case OR: operName = "OR"; break;
		case AND: operName = "AND"; break;

		case EQUi: operName = "EQUi"; break;
		case NEQi: operName = "NEQi"; break;
		case LTHi: operName = "LTHi"; break;
		case GTHi: operName = "GTHi"; break;
		case LEQi: operName = "LEQi"; break;
		case GEQi: operName = "GEQi"; break;

		case ADDi: operName = "ADDi"; break;
		case SUBi: operName = "SUBi"; break;
		case MULi: operName = "MULi"; break;
		case DIVi: operName = "DIVi"; break;
		case MODi: operName = "MODi"; break;
		
		case EQUr: operName = "EQUr"; break;
		case NEQr: operName = "NEQr"; break;
		case LTHr: operName = "LTHr"; break;
		case GTHr: operName = "GTHr"; break;
		case LEQr: operName = "LEQr"; break;
		case GEQr: operName = "GEQr"; break;

		case ADDr: operName = "ADDr"; break;
		case SUBr: operName = "SUBr"; break;
		case MULr: operName = "MULr"; break;
		case DIVr: operName = "DIVr"; break;
		case MODr: operName = "MODr"; break;
		
		case EQUs: operName = "EQUs"; break;
		case NEQs: operName = "NEQs"; break;
		case LTHs: operName = "LTHs"; break;
		case GTHs: operName = "GTHs"; break;
		case LEQs: operName = "LEQs"; break;
		case GEQs: operName = "GEQs"; break;
		
		}
		xml.println("<iminstruction instr=\"BINOP\" value=\"" + operName + "\">");
		fstSubExpr.toXML(xml);
		sndSubExpr.toXML(xml);
		xml.println("</iminstruction>");
	}
	
	@Override
	public void linearCode() {
		if (linearCode != null) return;
		fstSubExpr.linearCode();
		sndSubExpr.linearCode();
		linearCodeResult = new Temp();
		linearCode = new ImSEQ();
		linearCode.codes.addAll(fstSubExpr.linearCode.codes);
		linearCode.codes.addAll(sndSubExpr.linearCode.codes);
		linearCode.codes.add(new ImMOVE(new ImTEMP(linearCodeResult), new ImBINOP(oper, new ImTEMP(fstSubExpr.linearCodeResult), new ImTEMP(sndSubExpr.linearCodeResult))));
	}
	
}
