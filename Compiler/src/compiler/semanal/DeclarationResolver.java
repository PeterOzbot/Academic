package compiler.semanal;

import java.util.*;

import compiler.*;
import compiler.abstree.*;

public class DeclarationResolver implements Visitor {
	// nastavitve
	private static final Boolean PRINT_EX_STACK = false;

	private static final SymbolTable names = new SymbolTable();

	private static final HashMap<AbsTree, AbsDecl> decls = new HashMap<AbsTree, AbsDecl>();

	private final DeclarationResolverMainTour declarationResolverMainTour = new DeclarationResolverMainTour();

	/**
	 * Doloci deklaracijo imena.
	 * 
	 * @param absNode
	 *            Ime (mora biti {@link AbsExprName} ali {@link AbsTypeName}.).
	 * @param absDecl
	 *            Deklaracija imena.
	 */
	public static void setDecl(AbsTree absNode, AbsDecl absDecl) {
		if (!((absNode instanceof AbsExprName) || (absNode instanceof AbsTypeName)))
			Report.error("Internal error.", 1);
		if (decls.get(absNode) != null)
			Report.error("Internal error.", 1);
		decls.put(absNode, absDecl);
	}

	/**
	 * Vrne deklaracijo imena.
	 * 
	 * @param absNode
	 *            Ime (mora biti {@link AbsExprName} ali {@link AbsTypeName}.).
	 * @return Deklaracija imena ali <code>null</code>, ce deklaracija se ni
	 *         dolocena.
	 */
	public static AbsDecl getDecl(AbsTree absNode) {
		return decls.get(absNode);
	}

	// Poskusa in obravnava vstavljanje imena
	public static void InsertName(AbsDecl absDecl, String name) {

		try {
			// vstavi v simbolno tabelo
			names.ins(name, absDecl);

		} catch (DeclarationAlreadyExistsException e) {
			if (PRINT_EX_STACK) {
				e.printStackTrace();
			}
			Report.warning("Declaration already exists for variable - " + name,
					absDecl.getPosition());
		}
	}

	// Poskusa in obravnava iskanje imena
	public static void FindName(AbsTree absNode, String name) {
		try {
			// preveri èe je med imeni
			AbsDecl absDecl = names.fnd(name);

			// ce je naredimo povezavo - dodamo v decls
			setDecl(absNode, absDecl);
			
		} catch (DeclarationDoesNotExistException e) {
			if (PRINT_EX_STACK) {
				e.printStackTrace();
			}
			Report.error("Can't be resolved to variable - " + name, 1);
		}
	}

	public static void newScope() {
		names.newScope();
	}

	public static void oldScope() {
		names.oldScope();
	}

	@Override
	public void visit(AbsAssignStmt acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsAtomExpr acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsBinExpr acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsDecls acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsExprName acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsExprs acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsForStmt acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsFunCall acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsIfStmt acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsWhereExpr acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsWhileStmt acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsUnExpr acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsTypeName acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsAtomType acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsPtrType acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsArrType acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsRecType acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsTypDecl acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsVarDecl acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}

	@Override
	public void visit(AbsFunDecl acceptor) {
		acceptor.accept(declarationResolverMainTour);
	}
}
