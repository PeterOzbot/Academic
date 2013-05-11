package compiler.imcode;

import java.io.*;

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

}
