package compiler.abstree;

import java.io.*;

/** Deklaracija tipa. */
public class AbsTypDecl extends AbsDecl {

	/** Ime tipa.  */
	public final AbsTypeName name;
	
	/** Tip tipa.  */
	public final AbsType type;
	
	public AbsTypDecl(AbsTypeName name, AbsType type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"TypDecl\">");
		if (getPosition() != null) getPosition().toXML(xml);
		name.toXML(xml);
		type.toXML(xml);
		xml.println("</absnode>");
	}
	
}
