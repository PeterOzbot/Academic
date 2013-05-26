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
	
	@Override
	public void linearCode() {
		if (linearCode != null) return;
		linearCode = new ImSEQ();
		for (ImCode code : codes) {
			code.linearCode();
			if (code.linearCode instanceof ImSEQ) {
				linearCode.codes.addAll(((ImSEQ) code.linearCode).codes);
			} else
				linearCode.codes.add(code.linearCode);
		}
		linearCodeResult = codes.lastElement().linearCodeResult;
	}
}
