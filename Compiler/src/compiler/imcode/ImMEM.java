package compiler.imcode;

import java.io.*;

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
	
}
