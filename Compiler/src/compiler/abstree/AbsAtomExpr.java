package compiler.abstree;

import compiler.*;
import compiler.lexanal.*;

/** Atomarni izraz (konstanta osnovnega podatkovnega tipa). */
public class AbsAtomExpr extends AbsExpr {

	public static final int INT    = 0;
	public static final int REAL   = 1;
	public static final int BOOL   = 2;
	public static final int STRING = 3;
	public static final int VOID   = 4;

	/** Konstanta. */
	public final Symbol expr;

	public AbsAtomExpr(Symbol expr) {
		if (expr == null)
			this.expr = null;
		else
			switch (expr.getToken()) {
			case Symbol.INTCONST:
			case Symbol.REALCONST:
			case Symbol.BOOLCONST:
			case Symbol.STRINGCONST:
				this.expr = expr;
				break;
			default:
				this.expr = null;
				Report.error("Internal error.", 1);
			}
	}

	public int GetType() {
		if (expr == null)
			return VOID;
		else {
			switch (expr.getToken()) {
			case Symbol.INTCONST:
				return INT;
			case Symbol.REALCONST:
				return REAL;
			case Symbol.BOOLCONST:
				return BOOL;
			case Symbol.STRINGCONST:
				return STRING;
			default:
				Report.error("Internal error.", 1);
				return -1;
			}
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
