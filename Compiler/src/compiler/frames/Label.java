package compiler.frames;

import java.io.*;

import compiler.*;

/** Labela funkcije. */
public class Label implements XMLable {

	private static int labelCounter = 0;

	/** Ime labele. */
	public String label;

	public Label(String name) {
		label = "L" + (labelCounter++) + "::" + name;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<frmlabel label=\"" + label + "\"/>");
	}

}
