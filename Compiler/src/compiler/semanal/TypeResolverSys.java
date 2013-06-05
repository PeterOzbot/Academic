package compiler.semanal;

import compiler.Report;
import compiler.abstree.AbsArrType;
import compiler.abstree.AbsAssignStmt;
import compiler.abstree.AbsAtomExpr;
import compiler.abstree.AbsAtomType;
import compiler.abstree.AbsBinExpr;
import compiler.abstree.AbsDecls;
import compiler.abstree.AbsExprName;
import compiler.abstree.AbsExprs;
import compiler.abstree.AbsForStmt;
import compiler.abstree.AbsFunCall;
import compiler.abstree.AbsFunDecl;
import compiler.abstree.AbsIfStmt;
import compiler.abstree.AbsPtrType;
import compiler.abstree.AbsRecType;
import compiler.abstree.AbsTypDecl;
import compiler.abstree.AbsTypeName;
import compiler.abstree.AbsUnExpr;
import compiler.abstree.AbsVarDecl;
import compiler.abstree.AbsWhereExpr;
import compiler.abstree.AbsWhileStmt;
import compiler.abstree.Visitor;

public class TypeResolverSys implements Visitor {

	@Override
	public void visit(AbsAtomExpr acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsBinExpr acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsExprName acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsExprs acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsUnExpr acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsWhereExpr acceptor) {
		// nothing todo here

	}

	// TODO: tole malo bolj genericno, zdaj je hardcodano za tiste 4
	@Override
	public void visit(AbsFunCall acceptor) {
		// preverimo ce je samo en argument argumente
		if (acceptor.args.exprs.size() != 1) {
			Report.error("System function too many parameters.",
					acceptor.getPosition(), 1);
		}
		// preverimo argument za string
		if (acceptor.name.identifier.getLexeme().equals(
				LibSys.GetString.name.identifier.getLexeme())) {
			// TODO:preverit èe je pointer na spremenljivko -
			// tipa string

		} else if (acceptor.name.identifier.getLexeme().equals(
				LibSys.PutString.name.identifier.getLexeme())) {
			// TODO: preverit ce je tipa string, lahko je kar koli, samo da je
			// rezultat tipa string
		}
		// preverimo argument za int
		else if (acceptor.name.identifier.getLexeme().equals(
				LibSys.GetInt.name.identifier.getLexeme())) {
			// TODO:preverit èe je pointer  tipa int
		} else if (acceptor.name.identifier.getLexeme().equals(
				LibSys.PutInt.name.identifier.getLexeme())) {
			// TODO: preverit ce je tipa int, lahko je kar koli, samo da je
			// rezultat tipa int
		} else {
			Report.error("TypeResolverSys: System function not found!.",
					acceptor.getPosition(), 1);
		}

		// dolocimo tip klica funkcije
		TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.VOID));

	}

	@Override
	public void visit(AbsIfStmt acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsWhileStmt acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsForStmt acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsAssignStmt acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsTypeName acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsAtomType acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsPtrType acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsArrType acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsRecType acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsDecls acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsVarDecl acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsFunDecl acceptor) {
		// nothing todo here

	}

	@Override
	public void visit(AbsTypDecl acceptor) {
		// nothing todo here

	}

}
