package compiler.imcode;

import java.io.*;

/** Konstanta tipa int. */
public class ImCONSTi extends ImCode {

	public final int intValue;
	
	public ImCONSTi(int intValue) {
		this.intValue = intValue;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"CONSTi\" value=\"" + intValue + "\"/>");
	}
	
}
