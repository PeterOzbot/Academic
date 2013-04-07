package compiler.abstree;

import java.util.*;

/** Seznam deklaracij.  */
public class AbsDecls extends AbsDecl {

	public final Vector<AbsDecl> decls;
	
	public AbsDecls(Vector<AbsDecl> decls) {
		this.decls = decls;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
