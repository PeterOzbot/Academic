package compiler.abstree;

import compiler.*;
import compiler.lexanal.*;

/** Ime funckije ali spremenljivke. */
public class AbsExprName extends AbsExpr {

	/** Ime. */
	public final Symbol identifier;

	public AbsExprName(Symbol identifier) {
		if (identifier == null)
			Report.error("Internal error.", 1);
		if (identifier.getToken() != Symbol.IDENTIFIER)
			Report.error("Internal error.", 1);
		this.identifier = identifier;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
