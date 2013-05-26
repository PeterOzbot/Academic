package compiler.imcode;

import java.io.*;

import compiler.frames.Temp;

/**
 * Vrednost registra: dovoljeni vrednosti sta "FP" in "SP"; vrne vrednost
 * imenovanega registra.
 */
public class ImNAME extends ImCode {

	public final String name;
	
	public ImNAME(String name) {
		this.name = name;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"NAME\" value=\"" + name + "\"/>");
	}

	@Override
	public void linearCode() {
		if (linearCode != null) return;
		linearCodeResult = new Temp();
		linearCode = new ImSEQ();
		linearCode.codes.add(new ImMOVE(new ImTEMP(linearCodeResult), this));
	}
	
}
