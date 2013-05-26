package compiler.imcode;

import java.io.*;

import compiler.frames.*;

/**
 * Pogojni skok: ce je podizraz resnicen, skoci na prvo, sicer na drugo labelo;
 * ne vrne rezultata.
 */
public class ImCJUMP extends ImCode {

	public final ImCode cond;
	
	public final Label thenLabel;
	
	public final Label elseLabel;
		
	public ImCJUMP(ImCode cond, Label thenLabel, Label elseLabel) {
		this.cond = cond;
		this.thenLabel = thenLabel;
		this.elseLabel = elseLabel;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"CJUMP\" value=\"" + thenLabel.label + "," + elseLabel.label + "\">");
		cond.toXML(xml);
		xml.println("</iminstruction>");
	}
	
	@Override
	public void linearCode() {
		if (linearCode != null) return;
		cond.linearCode();
		linearCode = new ImSEQ();
		linearCode.codes.addAll(cond.linearCode.codes);
		linearCode.codes.add(new ImCJUMP(new ImTEMP(cond.linearCodeResult), thenLabel, elseLabel));
		linearCodeResult = new Temp();
	}
}
