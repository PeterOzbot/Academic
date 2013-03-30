package compiler.abstree;

import java.io.*;

/** Deklaracija spremenljivke. */
public class AbsVarDecl extends AbsDecl {

	/** Ime spremelnjivke.  */
	public final AbsExprName name;
	
	/** Tip spremenljivke.  */
	public final AbsType type;
	
	public AbsVarDecl(AbsExprName name, AbsType type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"VarDecl\">");
		if (getPosition() != null) getPosition().toXML(xml);
		name.toXML(xml);
		type.toXML(xml);
		xml.println("</absnode>");
	}
	
}
