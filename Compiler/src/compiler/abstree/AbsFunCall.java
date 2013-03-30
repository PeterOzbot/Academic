package compiler.abstree;

import java.io.*;

/** Klic funkcije.  */
public class AbsFunCall extends AbsExpr {
	
	/** Ime funkcije.  */
	public final AbsExprName name;
	
	/** Argumenti funkcije.  */
	public final AbsExprs args;

	public AbsFunCall(AbsExprName name, AbsExprs args) {
		this.name = name;
		this.args = args;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"FunCall\">");
		if (getPosition() != null) getPosition().toXML(xml);
		name.toXML(xml);
		args.toXML(xml);
		xml.println("</absnode>");
	}
	
}
