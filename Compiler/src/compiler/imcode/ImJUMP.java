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

}
