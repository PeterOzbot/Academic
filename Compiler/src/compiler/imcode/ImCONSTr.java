package compiler.imcode;

import java.io.*;

import compiler.frames.Temp;

/** Konstanta tipa real.  */
public class ImCONSTr extends ImCode {

	public final float realValue;
	
	public ImCONSTr(float realValue) {
		this.realValue = realValue;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"CONSTr\" value=\"" + realValue + "\"/>");
	}
	
	public void linearCode() {
		if (linearCode != null) return;
		linearCodeResult = new Temp();
		linearCode = new ImSEQ();
		linearCode.codes.add(new ImMOVE(new ImTEMP(linearCodeResult), this));
	}
}
