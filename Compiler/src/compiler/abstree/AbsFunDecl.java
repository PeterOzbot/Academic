package compiler.abstree;

import java.io.*;

/** Deklaracija funkcije. */
public class AbsFunDecl extends AbsDecl {

	/** Ime funckije.  */
	public final AbsExprName name;
	
	/** Parametri funkcije.  */
	public final AbsDecls pars;
	
	/** Tip funkcije.  */
	public final AbsType type;

	/** Izraz funckije.  */
	public final AbsExpr expr;
	
	public AbsFunDecl(AbsExprName name, AbsDecls pars, AbsType type, AbsExpr expr) {
		this.name = name;
		this.pars = pars;
		this.type = type;
		this.expr = expr;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"FunDecl\">");
		if (getPosition() != null) getPosition().toXML(xml);
		name.toXML(xml);
		pars.toXML(xml);
		type.toXML(xml);
		expr.toXML(xml);
		xml.println("</absnode>");
	}
	
}
