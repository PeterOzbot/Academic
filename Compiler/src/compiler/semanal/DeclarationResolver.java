package compiler.semanal;

import java.util.*;

import compiler.*;
import compiler.abstree.*;

public class DeclarationResolver implements Visitor {
	// nastavitve
	private static final Boolean PRINT_EX_STACK = false;

	private static final SymbolTable names = new SymbolTable();

	private static final HashMap<AbsTree, AbsDecl> decls = new HashMap<AbsTree, AbsDecl>();

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
	public static void InsertName(String name, AbsDecl declaration) {
		try {
			// vstavi v simbolno tabelo
			names.ins(name, declaration);

		} catch (DeclarationAlreadyExistsException e) {
			if (PRINT_EX_STACK) {
				e.printStackTrace();
			}
			Report.error("Can't be resolved to variable - " + name, 1);
		}
	}

	// Poskusa in obravnava iskanje imena
	public static void FindName(String name) {
		try {
			// preveri èe je med imeni
			names.fnd(name);

		} catch (DeclarationDoesNotExistException e) {
			if (PRINT_EX_STACK) {
				e.printStackTrace();
			}
			Report.error("Can't be resolved to variable - " + name, 1);
		}
	}

	@Override
	public void visit(AbsArrType acceptor) {
		// preveri notranji expression
		acceptor.size.accept(this);
		// TODO: je se kaj?

	}

	@Override
	public void visit(AbsAssignStmt acceptor) {

		// preveri èe je med imeni
		acceptor.fstSubExpr.accept(this);

		// prveri expression ki je drugi del
		acceptor.sndSubExpr.accept(this);
	}

	@Override
	public void visit(AbsAtomExpr acceptor) {
		// TODO: tukaj ne naredimo nic?
	}

	@Override
	public void visit(AbsAtomType acceptor) {
		// TODO: tukaj ne naredimo nic?
	}

	@Override
	public void visit(AbsBinExpr acceptor) {
		// ce je tipa rec je potrebno pogledat ce je spremenjivka vidna
		// TODO: tukaj potrebno pogledat v postfix_expression ce je vidna ali v
		// globalnem trenutnem?
		if (acceptor.oper == AbsBinExpr.REC) {

			// preveri èe obstaja tip
			acceptor.fstSubExpr.accept(this);

			// preveri drugi del
			acceptor.sndSubExpr.accept(this);
		} else {
			// preveri oba dela posebej
			acceptor.fstSubExpr.accept(this);
			acceptor.sndSubExpr.accept(this);
		}
	}

	@Override
	public void visit(AbsDecls acceptor) {
		// pregleda vse expressin-e
		for (AbsDecl decl : acceptor.decls) {
			decl.accept(this);
		}

	}

	@Override
	public void visit(AbsExprName acceptor) {
		// tukaj samo preverimo -> ker je potrebno dajat ime in deklaracijo ->
		// to je samo ime
		// potrebno je pazit da se to ne klice ko je potreba po vstavljanju

		// preveri èe je med imeni
		FindName(acceptor.identifier.getLexeme());
	}

	@Override
	public void visit(AbsExprs acceptor) {
		// pregleda vse expressin-e
		for (AbsExpr expr : acceptor.exprs) {
			expr.accept(this);
		}
	}

	@Override
	public void visit(AbsForStmt acceptor) {
		// TODO: ali je potrebno ugotovit ali je bil identifier deklariran do
		// zdaj, ali je za naprej -> glede tega da ni tipa bi reku da za nazaj

		// preveri èe je med imeni
		FindName(acceptor.name.identifier.getLexeme());

		// preveri se ostale dele for zanke
		acceptor.hiBound.accept(this);
		acceptor.loBound.accept(this);
		acceptor.loopExpr.accept(this);
	}

	@Override
	public void visit(AbsFunCall acceptor) {

		// preveri èe je med imeni
		FindName(acceptor.name.identifier.getLexeme());

		// preveri se ostal del klica funkcije
		acceptor.args.accept(this);
	}

	@Override
	public void visit(AbsFunDecl acceptor) {
		// potrebno dati ime funkcije v ta scope
		InsertName(acceptor.name.identifier.getLexeme(), acceptor);

		// narediti nov scope
		names.newScope();

		// dodati funkcijske parametre med imena
		acceptor.pars.accept(this);

		// preveriti expression
		acceptor.expr.accept(this);

		// zapreti scope
		names.oldScope();
	}

	@Override
	public void visit(AbsIfStmt acceptor) {
		// preveri if
		acceptor.condExpr.accept(this);

		// preveri then
		acceptor.thenExprs.accept(this);

		// preveri else
		acceptor.elseExprs.accept(this);
	}

	@Override
	public void visit(AbsPtrType acceptor) {
		// preveri tip globlje
		acceptor.type.accept(this);

	}

	@Override
	public void visit(AbsRecType acceptor) {
		// preveri posamezne komponente
		acceptor.comps.accept(this);

	}

	@Override
	public void visit(AbsTypDecl acceptor) {
		// doda med imena
		InsertName(acceptor.name.identifier.getLexeme(), acceptor);

		// preveri ostali del tipa
		acceptor.type.accept(this);

	}

	@Override
	public void visit(AbsTypeName acceptor) {
		// tukaj ne naredimo nic -> ker je potrebno dajat ime in deklaracijo ->
		// to je samo ime
		// pri preverjanju ce obstaja je dovolj preprosto da se kar direktno
		// klice kjer se rabi
	}

	@Override
	public void visit(AbsUnExpr acceptor) {
		// preveri drugi del(prvi je del predznak)
		acceptor.subExpr.accept(this);
	}

	@Override
	public void visit(AbsVarDecl acceptor) {
		// preveri ce obstaja v scope-u deklaracija spremneljivke
		FindName(acceptor.name.identifier.getLexeme());
	}

	@Override
	public void visit(AbsWhereExpr acceptor) {// tukaj je posebnost da je prvo
												// drugi del(deklaracije), potem
												// prvi(izrazi)
		// naredimo nov scope
		names.newScope();

		// dodamo vse deklaracije
		acceptor.decls.accept(this);

		// preverimo vse expression-e
		acceptor.subExpr.accept(this);

		// zapremo scope
		names.oldScope();

	}

	@Override
	public void visit(AbsWhileStmt acceptor) {
		// preverit posamezne expression-e
		acceptor.condExpr.accept(this);
		acceptor.loopExpr.accept(this);

	}
}
