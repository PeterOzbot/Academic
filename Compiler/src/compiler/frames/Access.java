package compiler.frames;

import java.io.*;
import compiler.*;

/** Opis dostopa do spremenljivke, parametra ali komponente.  */
public class Access implements XMLable {

	/** Staticni nivo spremenljivke, parametra ali komponente. */
	public final int level;
	
	/** Odmik spremenljivke, parametra ali komponente.  */
	public final int offset;
	
	public Access(int level, int offset) {
		this.level = level;
		this.offset = offset;
	}
	
	public void toXML(PrintStream xml) {
		xml.print("<frmaccess level=\"" + level + "\" offset=\"" + offset + "\"/>");
	}
	
}
