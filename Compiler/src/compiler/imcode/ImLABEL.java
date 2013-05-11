package compiler.imcode;

import java.io.*;

import compiler.frames.*;

/** Labela; ne vrne rezultata.  */
public class ImLABEL extends ImCode {

	public final Label label;
		
	public ImLABEL(Label label) {
		this.label = label;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"LABEL\" value=\"" + label.label + "\"/>");
	}
	
}
