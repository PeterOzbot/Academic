package compiler.imcode;

import java.io.*;
import compiler.frames.Temp;

/**
 * Dostop do pomnilnika: vrne vrednost na naslovu, ki ga doloca vrednost
 * podizraza (a glej tudi ukaz MOVE).
 */
public class ImMEM extends ImCode {

	public final ImCode expr;
	
	public ImMEM(ImCode expr) {
		this.expr = expr;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"MEM\">");
		expr.toXML(xml);
		xml.println("</iminstruction>");
	}
	
	@Override
	public void linearCode() {
		if (linearCode != null) return;
		expr.linearCode();
		linearCodeResult = new Temp();
		linearCode = new ImSEQ();
		linearCode.codes.addAll(expr.linearCode.codes);
		linearCode.codes.add(new ImMOVE(new ImTEMP(linearCodeResult), new ImMEM(new ImTEMP(expr.linearCodeResult))));
	}
}
