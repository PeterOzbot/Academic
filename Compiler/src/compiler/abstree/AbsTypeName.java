package compiler.abstree;

import compiler.*;
import compiler.lexanal.*;

/** Ime tipa.  */
public class AbsTypeName extends AbsType {
	
	/** Ime.  */
	public final Symbol identifier;
	
	public AbsTypeName(Symbol identifier) {
		if (identifier == null) Report.error("Internal error.", 1);
		if (identifier.getToken() != Symbol.IDENTIFIER) Report.error("Internal error.", 1);
		this.identifier = identifier;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
