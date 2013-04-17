package compiler.semanal;

import java.io.*;

/** Opis kazalcev.  */
public class SemPtrType extends SemType {

	/** Osnovni tip.  */
	public final SemType type;
	
	public SemPtrType(SemType type) {
		this.type = type;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<semtype type=\"PTR\">");
		type.toXML(xml);
		xml.println("</semtype>");
	}

	@Override
	public SemType actualType() {
		return this;
	}

	@Override
	public int size() {
		return 4;
	}

}
