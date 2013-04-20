package compiler.abstree;

public interface Visitor {
	// expressions
	public void visit(AbsAtomExpr acceptor);

	public void visit(AbsBinExpr acceptor);

	public void visit(AbsExprName acceptor);

	public void visit(AbsExprs acceptor);

	public void visit(AbsUnExpr acceptor);

	public void visit(AbsWhereExpr acceptor);

	public void visit(AbsFunCall acceptor);

	// statement
	public void visit(AbsIfStmt acceptor);

	public void visit(AbsWhileStmt acceptor);

	public void visit(AbsForStmt acceptor);

	public void visit(AbsAssignStmt acceptor);

	// tipi
	public void visit(AbsTypeName acceptor);

	public void visit(AbsAtomType acceptor);

	public void visit(AbsPtrType acceptor);

	public void visit(AbsArrType acceptor);

	public void visit(AbsRecType acceptor);

	// deklaracije
	public void visit(AbsDecls acceptor);

	public void visit(AbsVarDecl acceptor);

	public void visit(AbsFunDecl acceptor);

	public void visit(AbsTypDecl acceptor);
}
