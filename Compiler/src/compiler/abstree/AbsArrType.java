package compiler.abstree;

import java.io.*;

/** Tabelarni tip.  */
public class AbsArrType extends AbsType {

	/** Osnovni tip.  */
	public final AbsType type;
	
	/** Velikost tabele.  */
	public final AbsExpr size;
	
	public AbsArrType(AbsType type, AbsExpr size) {
		this.type = type;
		this.size = size;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"ArrType\">");
		if (getPosition() != null) getPosition().toXML(xml);
		type.toXML(xml);
		size.toXML(xml);
		xml.println("</absnode>");
	}

}
