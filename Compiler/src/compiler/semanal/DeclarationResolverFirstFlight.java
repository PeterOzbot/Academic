package compiler.semanal;

import compiler.abstree.AbsArrType;
import compiler.abstree.AbsAssignStmt;
import compiler.abstree.AbsAtomExpr;
import compiler.abstree.AbsAtomType;
import compiler.abstree.AbsBinExpr;
import compiler.abstree.AbsDecl;
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

public class DeclarationResolverFirstFlight implements Visitor {

	// /
	// / Declaration
	// /

	@Override
	public void visit(AbsDecls acceptor) {
		// pregleda vse expressin-e
		for (AbsDecl decl : acceptor.decls) {
			decl.accept(this);
		}
	}

	@Override
	public void visit(AbsVarDecl acceptor) {
		// doda med imena
		DeclarationResolver.InsertName(acceptor,
				acceptor.name.identifier.getLexeme());

	}

	@Override
	public void visit(AbsTypDecl acceptor) {
		// doda med imena
		DeclarationResolver.InsertName(acceptor,
				acceptor.name.identifier.getLexeme());

	}

	@Override
	public void visit(AbsFunDecl acceptor) {
		// potrebno dati ime funkcije v ta scope
		DeclarationResolver.InsertName(acceptor,
				acceptor.name.identifier.getLexeme());
	}

	@Override
	public void visit(AbsArrType acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsAssignStmt acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsAtomExpr acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsAtomType acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsBinExpr acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsExprName acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsExprs acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsForStmt acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsFunCall acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsIfStmt acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsPtrType acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsRecType acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsTypeName acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsUnExpr acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsWhereExpr acceptor) {
		// nothing to do here
	}

	@Override
	public void visit(AbsWhileStmt acceptor) {
		// nothing to do here
	}
}
