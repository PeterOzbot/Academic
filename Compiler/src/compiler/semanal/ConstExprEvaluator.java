package compiler.semanal;

import java.util.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.abstree.*;

public class ConstExprEvaluator implements Visitor {

	private static final HashMap<AbsTree, Integer> values = new HashMap<AbsTree, Integer>();

	/**
	 * Doloci vrednost konstantnega celostevilcnega izraza.
	 * 
	 * @param absNode
	 *            Vozlisce abstraktnega sintaksnega drevesa.
	 * @param value
	 *            Vrednost konstantnega celostevilcnega izraza.
	 */
	public static void setValue(AbsTree absNode, Integer value) {
		if (values.get(absNode) != null)
			Report.error("Internal error.", 1);
		values.put(absNode, value);
	}

	/**
	 * Vrne vrednost konstantnega celostevilcnega izraza.
	 * 
	 * @param absNode
	 *            Vozlisce abstraktnega sintaksnega drevesa.
	 * @return Vrednost konstantnega celostevilcnega izraza ali
	 *         <code>null</code>, ce vrednost ne more biti dolocena.
	 */
	public static Integer getValue(AbsTree absNode) {
		return values.get(absNode);
	}

	public void visit(AbsArrType acceptor) {
		acceptor.type.accept(this);
		acceptor.size.accept(this);
	}

	public void visit(AbsAssignStmt acceptor) {
		acceptor.fstSubExpr.accept(this);
		acceptor.sndSubExpr.accept(this);
	}

	public void visit(AbsAtomExpr acceptor) {
		if (acceptor.expr != null) {//TODO: {} tezava, za prazen brace dajem expr null,
			switch (acceptor.expr.getToken()) {
			case Symbol.INTCONST:
				try {
					setValue(acceptor, new Integer(acceptor.expr.getLexeme()));
				} catch (NumberFormatException _) {
					Report.error("Illegal int constant.",
							acceptor.getPosition(), 1);
				}
			default:
			}
		}
	}

	public void visit(AbsAtomType acceptor) {
	}

	public void visit(AbsBinExpr acceptor) {
		acceptor.fstSubExpr.accept(this);
		acceptor.sndSubExpr.accept(this);
		Integer fstValue = getValue(acceptor.fstSubExpr);
		Integer sndValue = getValue(acceptor.sndSubExpr);
		if ((fstValue != null) && (sndValue != null)) {
			switch (acceptor.oper) {
			case AbsBinExpr.ADD:
				setValue(acceptor, fstValue + sndValue);
				break;
			case AbsBinExpr.SUB:
				setValue(acceptor, fstValue - sndValue);
				break;
			case AbsBinExpr.MUL:
				setValue(acceptor, fstValue * sndValue);
				break;
			case AbsBinExpr.DIV:
				if (sndValue == 0)
					Report.warning("Cannot divide with 0.",
							acceptor.getPosition());
				else
					setValue(acceptor, fstValue / sndValue);
				break;
			case AbsBinExpr.MOD:
				if (sndValue == 0)
					Report.warning("Cannot divide with 0.",
							acceptor.getPosition());
				else
					setValue(acceptor, fstValue % sndValue);
				break;
			}
		}
	}

	public void visit(AbsDecls acceptor) {
		for (AbsDecl decl : acceptor.decls)
			decl.accept(this);
	}

	public void visit(AbsExprName acceptor) {
	}

	public void visit(AbsExprs acceptor) {
		for (AbsExpr expr : acceptor.exprs)
			expr.accept(this);
	}

	public void visit(AbsForStmt acceptor) {
		acceptor.name.accept(this);
		acceptor.loBound.accept(this);
		acceptor.hiBound.accept(this);
		acceptor.loopExpr.accept(this);
	}

	public void visit(AbsFunCall acceptor) {
		acceptor.name.accept(this);
		acceptor.args.accept(this);
	}

	public void visit(AbsFunDecl acceptor) {
		acceptor.name.accept(this);
		acceptor.pars.accept(this);
		acceptor.type.accept(this);
		acceptor.expr.accept(this);
	}

	public void visit(AbsIfStmt acceptor) {
		acceptor.condExpr.accept(this);
		acceptor.thenExprs.accept(this);
		if (acceptor.elseExprs != null)
			acceptor.elseExprs.accept(this);
	}

	public void visit(AbsPtrType acceptor) {
		acceptor.type.accept(this);
	}

	public void visit(AbsRecType acceptor) {
		acceptor.comps.accept(this);
	}

	public void visit(AbsTypDecl acceptor) {
		acceptor.name.accept(this);
		acceptor.type.accept(this);
	}

	public void visit(AbsTypeName acceptor) {
	}

	public void visit(AbsUnExpr acceptor) {
		acceptor.subExpr.accept(this);
		Integer value = getValue(acceptor.subExpr);
		if (value != null) {
			switch (acceptor.oper) {
			case AbsUnExpr.ADD:
				setValue(acceptor, +value);
				break;
			case AbsUnExpr.SUB:
				setValue(acceptor, -value);
				break;
			}
		}
	}

	public void visit(AbsVarDecl acceptor) {
		acceptor.name.accept(this);
		acceptor.type.accept(this);
	}

	public void visit(AbsWhereExpr acceptor) {
		acceptor.decls.accept(this);
		acceptor.subExpr.accept(this);
	}

	public void visit(AbsWhileStmt acceptor) {
		acceptor.condExpr.accept(this);
		acceptor.loopExpr.accept(this);
	}

}
