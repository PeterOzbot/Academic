package compiler.imcode;

import java.io.*;

/** Konstanta tipa string.  */
public class ImCONSTs extends ImCode {

	public final String stringValue;
	
	public ImCONSTs(String stringValue) {
		this.stringValue = stringValue;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"CONSTs\" value=\"" + stringValue + "\"/>");
	}
	
}
