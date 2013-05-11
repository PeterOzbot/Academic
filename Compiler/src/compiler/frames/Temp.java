package compiler.frames;

import java.io.*;

import compiler.*;

/** Zacasna spremenljivka. */
public class Temp implements XMLable {

	private static int tempCounter = 0;

	/** Ime zacasne spremenljivke. */
	public String temp;

	public Temp() {
		temp = "T" + (tempCounter++);
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<frmtemp temp=\"" + temp + "\"/>");
	}

}
