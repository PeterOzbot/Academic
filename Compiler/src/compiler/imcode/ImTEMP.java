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

}
