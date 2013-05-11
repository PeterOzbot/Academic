package compiler.imcode;

import java.io.*;
import java.util.*;

import compiler.frames.*;

/**
 * Klic podprograma: klice podprogram s podano staticno povezavo in argumenti;
 * ne vrne rezultata, ker mora biti rezultat vrnjen preko klicnega zapisa.
 */
public class ImCALL extends ImCode {

	public final Label fun;
	
	public final ImCode sl;
	
	public final Vector<ImCode> args;
	
	public ImCALL(Label fun, ImCode sl) {
		this.fun = fun;
		this.sl = sl;
		args = new Vector<ImCode>();
	}

	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"CALL\" value=\"" + fun.label + "\">");
		sl.toXML(xml);
		for (ImCode code : args) code.toXML(xml);
		xml.println("</iminstruction>");
	}

}
