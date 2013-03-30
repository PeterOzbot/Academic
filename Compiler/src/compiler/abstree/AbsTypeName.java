package compiler.abstree;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;

/** Ime tipa.  */
public class AbsTypeName extends AbsType {
	
	/** Ime.  */
	public final Symbol identifier;
	
	public AbsTypeName(Symbol identifier) {
		if (identifier == null) Report.error("Internal error.", -1);
		if (identifier.getToken() != Symbol.IDENTIFIER) Report.error("Internal error.", -1);
		this.identifier = identifier;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"TypeName\">");
		if (getPosition() != null) getPosition().toXML(xml);
		identifier.toXML(xml);
		xml.println("</absnode>");
	}

}
