package compiler.imcode;

import java.io.*;

import compiler.frames.*;

/** Brezpogojni skok na labelo; ne vrne rezultata.  */
public class ImJUMP extends ImCode {

	public final Label label;
		
	public ImJUMP(Label label) {
		this.label = label;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"JUMP\" value=\"" + label.label + "\"/>");
	}
	
	@Override
	public void linearCode() {
		if (linearCode != null) return;
		linearCodeResult = new Temp();
		linearCode = new ImSEQ();
		linearCode.codes.add(this);
	}
}
