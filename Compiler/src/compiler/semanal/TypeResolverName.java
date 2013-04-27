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

public class TypeResolverName implements Visitor {

	@Override
	public void visit(AbsAtomExpr acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsBinExpr acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsExprName acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsExprs acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsUnExpr acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsWhereExpr acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsFunCall acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsIfStmt acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsWhileStmt acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsForStmt acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsAssignStmt acceptor) {
		// ne naredimo nic

	}

	@Override
	public void visit(AbsTypeName acceptor) {
		// naredimo SemTypeName
		SemTypeName semTypeName = new SemTypeName(
				acceptor.identifier.getLexeme());

		// dodamo
		TypeResolver.setType(acceptor, semTypeName);
	}

	@Override
	public void visit(AbsAtomType acceptor) {
		// povezemo celoten atom type
		TypeResolver.setType(acceptor, new SemAtomType(acceptor.typ));
	}

	@Override
	public void visit(AbsPtrType acceptor) {
		// pregledamo tip
		acceptor.type.accept(this);

		// pridobimo sem type
		SemType semType = TypeResolver.getType(acceptor.type);

		// dodamo ptr type
		TypeResolver.setType(acceptor, new SemPtrType(semType));
	}

	@Override
	public void visit(AbsArrType acceptor) {
		// gremo cez tip
		acceptor.type.accept(this);

		// pridobimo sem type, poberemo ven kar se naredi
		SemType semType = TypeResolver.getType(acceptor.type);

		// pridobimo konstanto
		int size = ConstExprEvaluator.getValue(acceptor.size);

		TypeResolver.setType(acceptor, new SemArrType(semType, size));
	}

	@Override
	public void visit(AbsRecType acceptor) {
		// samo dodamo tip
		SemRecType semRecType = new SemRecType();
		TypeResolver.setType(acceptor, semRecType);
	}

	@Override
	public void visit(AbsDecls acceptor) {
		for (AbsDecl decl : acceptor.decls)
			decl.accept(this);
	}

	@Override
	public void visit(AbsVarDecl acceptor) {
		// obhodimo tip
		acceptor.type.accept(this);

		// dobimo tip
		SemType semType = TypeResolver.getType(acceptor.type);

		TypeResolver.setType(acceptor, semType);
	}

	@Override
	public void visit(AbsFunDecl acceptor) {
		// obhodimo tip
		acceptor.type.accept(this);

		// dobimo tip
		SemType semAcceptorType = TypeResolver.getType(acceptor.type);

		// dodamo tip funkcije, preden gremo cez deklaracijo in expression
		TypeResolver.setType(acceptor, semAcceptorType);
	}

	@Override
	public void visit(AbsTypDecl acceptor) {
		// preverimo tip
		acceptor.type.accept(this);

		// pridobimo tip
		SemType semType = TypeResolver.getType(acceptor.type);

		TypeResolver.setType(acceptor, semType);
	}

}
