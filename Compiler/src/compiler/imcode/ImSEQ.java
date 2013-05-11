package compiler.imcode;

import java.io.*;
import java.util.*;

/** Zaporedje izrazov: vrne vrednost zadnjega izraza.  */
public class ImSEQ extends ImCode {
	
	public final Vector<ImCode> codes;
	
	public ImSEQ() {
		codes = new Vector<ImCode>();
	}

	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"SEQ\">");
		for (ImCode code : codes) code.toXML(xml);
		xml.println("</iminstruction>");
	}

}
