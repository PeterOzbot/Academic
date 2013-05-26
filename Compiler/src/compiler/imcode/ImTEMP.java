package compiler.imcode;

import java.io.*;

import compiler.frames.*;

/** Zacasna spremenljivka.  */
public class ImTEMP extends ImCode {

	public final Temp temp;
	
	public ImTEMP(Temp temp) {
		this.temp = temp;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"TEMP\" value=\"" + temp.temp + "\"/>");
	}
	
	@Override
	public void linearCode() {
		if (linearCode != null) return;
		linearCodeResult = new Temp();
		linearCode = new ImSEQ();
		linearCode.codes.add(new ImMOVE(new ImTEMP(linearCodeResult), this));
	}
}
