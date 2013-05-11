package compiler.imcode;

import java.io.*;

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

}
