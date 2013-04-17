package compiler.semanal;

import java.io.*;

/** Tabelarni tip.  */
public class SemArrType extends SemType {

	/** Osnovni tip.  */
	public final SemType type;
	
	/** Velikost tabele.  */
	public final int arity;
	
	public SemArrType(SemType type, int size) {
		this.type = type;
		this.arity = size;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<semtype type=\"ARR\" value=\"" + arity + "\">");
		type.toXML(xml);
		xml.println("</semtype>");
	}
	
	@Override
	public SemType actualType() {
		return this;
	}

	@Override
	public int size() {
		return arity * type.size();
	}

}
