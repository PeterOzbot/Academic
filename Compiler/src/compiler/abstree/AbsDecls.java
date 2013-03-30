package compiler.abstree;

import java.io.*;
import java.util.*;

/** Seznam deklaracij.  */
public class AbsDecls extends AbsDecl {

	public final Vector<AbsDecl> decls;
	
	public AbsDecls(Vector<AbsDecl> decls) {
		this.decls = decls;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<absnode node=\"Decls\">");
		if (getPosition() != null) getPosition().toXML(xml);
		for (AbsDecl decl: decls) decl.toXML(xml);
		xml.println("</absnode>");
	}
	
}
