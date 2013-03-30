package compiler.abstree;

import java.io.*;

/** Opis zapisov.  */
public class AbsRecType extends AbsType {

	/** Komponente.  */
	public final AbsDecls comps;
	
	public AbsRecType(AbsDecls comps) {
		this.comps = comps;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"RecType\">");
		if (getPosition() != null) getPosition().toXML(xml);
		comps.toXML(xml);
		xml.println("</absnode>");
	}

}
