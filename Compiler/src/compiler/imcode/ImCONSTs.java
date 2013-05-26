package compiler.imcode;

import java.io.*;

import compiler.frames.Temp;

/** Konstanta tipa string. */
public class ImCONSTs extends ImCode {

	public final String stringValue;

	public ImCONSTs(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"CONSTs\" value=\"" + stringValue
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
