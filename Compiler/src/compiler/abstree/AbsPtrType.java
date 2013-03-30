package compiler.abstree;

import java.io.*;

/** Opis kazalcev.  */
public class AbsPtrType extends AbsType {

	/** Osnovni tip.  */
	public final AbsType type;
	
	public AbsPtrType(AbsType type) {
		this.type = type;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"PtrType\">");
		if (getPosition() != null) getPosition().toXML(xml);
		type.toXML(xml);
		xml.println("</absnode>");
	}

}
