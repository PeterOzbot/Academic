package compiler.semanal;

import compiler.abstree.AbsArrType;
import compiler.abstree.AbsAssignStmt;
import compiler.abstree.AbsAtomExpr;
import compiler.abstree.AbsAtomType;
import compiler.abstree.AbsBinExpr;
import compiler.abstree.AbsDecl;
import compiler.abstree.AbsDecls;
import compiler.abstree.AbsExpr;
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

public class DeclarationResolverMainTour implements Visitor {

	private final DeclarationResolverFirstFlight declarationResolverFirstFlight = new DeclarationResolverFirstFlight();

	@Override
	public void visit(AbsAssignStmt acceptor) {

		// preveri èe je med imeni
		acceptor.fstSubExpr.accept(this);

		// prveri expression ki je drugi del
		acceptor.sndSubExpr.accept(this);
	}

	@Override
	public void visit(AbsAtomExpr acceptor) {
		// ne naredimo nic tukaj
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
		// lahko pride iz : i + 2
		// preveri èe je med imeni
		DeclarationResolver.FindName(acceptor, acceptor.identifier.getLexeme());
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
		DeclarationResolver.FindName(acceptor.name, acceptor.name.identifier.getLexeme());

		// preveri se ostale dele for zanke
		acceptor.hiBound.accept(this);
		acceptor.loBound.accept(this);
		acceptor.loopExpr.accept(this);
	}

	@Override
	public void visit(AbsFunCall acceptor) {

		// preveri èe je med imeni
		DeclarationResolver.FindName(acceptor.name, acceptor.name.identifier.getLexeme());

		// preveri se ostal del klica funkcije
		acceptor.args.accept(this);
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

	// tukaj je posebnost da je prvo
	// drugi del(deklaracije), potem
	// prvi(izrazi) - z drugim resolverjem
	@Override
	public void visit(AbsWhereExpr acceptor) {

		// naredimo nov scope
		DeclarationResolver.newScope();

		// dodamo vse deklaracije
		acceptor.decls.accept(declarationResolverFirstFlight);

		// preverimo vse expression-e
		acceptor.subExpr.accept(this);

		// pregledamo deklaracije še s tem resolverjem
		acceptor.decls.accept(this);

		// zapremo scope
		DeclarationResolver.oldScope();

	}

	@Override
	public void visit(AbsWhileStmt acceptor) {
		// preverit posamezne expression-e
		acceptor.condExpr.accept(this);
		acceptor.loopExpr.accept(this);

	}

	@Override
	public void visit(AbsUnExpr acceptor) {
		// preveri drugi del(prvi je del predznak)
		acceptor.subExpr.accept(this);
	}

	//
	// Tipi
	//

	@Override
	public void visit(AbsTypeName acceptor) {
		// tukaj ne naredimo nic -> IDENTIFIER - se prebere zunaj
	}

	@Override
	public void visit(AbsAtomType acceptor) {
		// ne naredimo nic -> INT, REAL, BOOL, STRING
	}

	@Override
	public void visit(AbsPtrType acceptor) {
		// preveri tip globlje, to je *
		acceptor.type.accept(this);
	}

	@Override
	public void visit(AbsArrType acceptor) {
		// preveri notranji expression
		acceptor.size.accept(this);

		// preveri tip globlje
		acceptor.type.accept(this);
	}

	@Override
	public void visit(AbsRecType acceptor) {
		// preveri posamezne komponente
		acceptor.comps.accept(this);
	}

	//
	// Deklaracije
	//

	@Override
	public void visit(AbsTypDecl acceptor) {
		// ime se doda pri prvem preletu - DeclarationResolverFirstFlight

		// preveri ostali del tipa
		acceptor.type.accept(this);
	}

	@Override
	public void visit(AbsVarDecl acceptor) {
		// ime se doda pri prvem preletu - DeclarationResolverFirstFlight
		// v tem obhodu tukaj povezemo deklaracijo tipa in izrabo le tega
		// ampak samo v primeru ce je custom tip
		if (acceptor.type instanceof AbsTypeName) {
			DeclarationResolver.FindName(acceptor.type, ((AbsTypeName) acceptor.type).identifier.getLexeme());
		}
	}

	@Override
	public void visit(AbsFunDecl acceptor) {
		// ime se doda pri prvem preletu - DeclarationResolverFirstFlight

		// narediti nov scope
		DeclarationResolver.newScope();

		// dodati funkcijske parametre med imena - naj to doda
		// DeclarationResolverFirstFlight
		acceptor.pars.accept(declarationResolverFirstFlight);
		
		// preveri custom tipe deklaracij
		acceptor.pars.accept(this);

		// preveriti expression
		acceptor.expr.accept(this);

		// zapreti scope
		DeclarationResolver.oldScope();
	}
}
