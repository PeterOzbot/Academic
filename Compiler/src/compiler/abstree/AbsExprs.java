package compiler.abstree;

import java.util.*;

/** Seznam izrazov. */
public class AbsExprs extends AbsExpr {

	public final Vector<AbsExpr> exprs;

	public AbsExprs(Vector<AbsExpr> exprs) {
		this.exprs = exprs;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
