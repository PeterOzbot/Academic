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

	public Vector<Integer> sizes;

	public ImCALL(Label fun, ImCode sl) {
		this.fun = fun;
		this.sl = sl;
		args = new Vector<ImCode>();
		sizes = new Vector<Integer>();
	}

	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"CALL\" value=\"" + fun.label
				+ "\">");
		sl.toXML(xml);
		for (ImCode code : args)
			code.toXML(xml);
		xml.println("</iminstruction>");
	}
	
	@Override
	public void linearCode() {
		if (linearCode != null) return;
		linearCode = new ImSEQ();
		sl.linearCode();
		linearCode.codes.addAll(sl.linearCode.codes);
		ImCALL call = new ImCALL(fun, new ImTEMP(sl.linearCodeResult));
		for (ImCode arg : args) {
			arg.linearCode();
			linearCode.codes.addAll(arg.linearCode.codes);
			call.args.add(new ImTEMP(arg.linearCodeResult));
		}
		linearCode.codes.add(call);
		linearCodeResult = new Temp();
	}
}
