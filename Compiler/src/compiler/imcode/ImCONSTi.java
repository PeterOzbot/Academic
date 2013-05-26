package compiler.imcode;

import java.io.*;

import compiler.frames.Temp;

/** Konstanta tipa int. */
public class ImCONSTi extends ImCode {

	public final int intValue;

	public ImCONSTi(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"CONSTi\" value=\"" + intValue
				+ "\"/>");
	}

	public void linearCode() {
		if (linearCode != null)
			return;
		linearCodeResult = new Temp();
		linearCode = new ImSEQ();
		linearCode.codes.add(new ImMOVE(new ImTEMP(linearCodeResult), this));
	}
}
