package compiler.semanal;

import java.util.*;

import compiler.*;
import compiler.abstree.*;

public class TypeResolver implements Visitor {

	private static final HashMap<AbsTree, SemType> types = new HashMap<AbsTree, SemType>();

	/**
	 * Doloci tip vozlisca abstraktnega sintaksnega drevesa.
	 * 
	 * @param absNode
	 *            Vozlisce abstraktnega sintaksnega drevesa.
	 * @param semType
	 *            Tip vozlisca abstraktnega sintaksnega drevesa.
	 */
	public static void setType(AbsTree absNode, SemType semType) {
		if (types.get(absNode) != null)
			Report.error("Internal error. Node already in types.", 1);
		types.put(absNode, semType);
	}

	/**
	 * Vrne tip vozlisca abstraktnega sintaksnega drevesa.
	 * 
	 * @param absNode
	 *            Vozlisce abstraktnega sintaksnega drevesa.
	 * @return Tip vozlisca abstraktnega sintaksnega drevesa ali
	 *         <code>null</code>, ce tip se ni dolocen.
	 */
	public static SemType getType(AbsTree absNode) {
		return types.get(absNode);
	}

	private static boolean structEqual(HashMap<SemType, HashSet<SemType>> eqs,
			SemType type1, SemType type2) {
		type1 = type1.actualType();
		type2 = type2.actualType();

		if ((type1 instanceof SemAtomType) || (type2 instanceof SemAtomType)) {
			SemAtomType atomType1 = (SemAtomType) type1;
			SemAtomType atomType2 = (SemAtomType) type2;
			return (atomType1.typ == atomType2.typ);
		}

		if ((type1 instanceof SemArrType) || (type2 instanceof SemArrType)) {
			SemArrType arrType1 = (SemArrType) type1;
			SemArrType arrType2 = (SemArrType) type2;
			if (eqs.get(arrType1) == null)
				eqs.put(arrType1, new HashSet<SemType>());
			if (eqs.get(arrType2) == null)
				eqs.put(arrType2, new HashSet<SemType>());
			if (eqs.get(arrType1).contains(arrType2)
					&& eqs.get(arrType1).contains(arrType2))
				return true;
			if (eqs.get(arrType1).contains(arrType2)
					|| eqs.get(arrType1).contains(arrType2))
				Report.error("Internal error.", 1);
			if (arrType1.arity != arrType2.arity)
				return false;
			eqs.get(arrType1).add(arrType2);
			eqs.get(arrType2).add(arrType1);
			return structEqual(eqs, arrType1.type, arrType2.type);
		}

		if ((type1 instanceof SemPtrType) || (type2 instanceof SemPtrType)) {
			SemPtrType ptrType1 = (SemPtrType) type1;
			SemPtrType ptrType2 = (SemPtrType) type2;
			if (eqs.get(ptrType1) == null)
				eqs.put(ptrType1, new HashSet<SemType>());
			if (eqs.get(ptrType2) == null)
				eqs.put(ptrType2, new HashSet<SemType>());
			if (eqs.get(ptrType1).contains(ptrType2)
					&& eqs.get(ptrType1).contains(ptrType2))
				return true;
			if (eqs.get(ptrType1).contains(ptrType2)
					|| eqs.get(ptrType1).contains(ptrType2))
				Report.error("Internal error.", 1);
			eqs.get(ptrType1).add(ptrType2);
			eqs.get(ptrType2).add(ptrType1);
			return structEqual(eqs, ptrType1.type, ptrType2.type);
		}

		if ((type1 instanceof SemRecType) || (type2 instanceof SemRecType)) {
			SemRecType recType1 = (SemRecType) type1;
			SemRecType recType2 = (SemRecType) type2;
			if (eqs.get(recType1) == null)
				eqs.put(recType1, new HashSet<SemType>());
			if (eqs.get(recType2) == null)
				eqs.put(recType2, new HashSet<SemType>());
			if (eqs.get(recType1).contains(recType2)
					&& eqs.get(recType1).contains(recType2))
				return true;
			if (eqs.get(recType1).contains(recType2)
					|| eqs.get(recType1).contains(recType2))
				Report.error("Internal error.", 1);
			if (recType1.compNames.size() != recType2.compNames.size())
				return false;
			for (int i = 0; i < recType1.compNames.size(); i++) {
				if (!(structEqual(eqs, recType1.compTypes.get(i),
						recType2.compTypes.get(i))))
					return false;
			}
			return true;
		}

		return false;
	}

	/**
	 * Ugotovi enakost tipov.
	 * 
	 * @param type1
	 *            Prvi tip.
	 * @param type2
	 *            Drugi tip.
	 * @return <code>true</code>, ce sta tipa enaka, <code>false</code> sicer.
	 */
	public static boolean equal(SemType type1, SemType type2) {
		return structEqual(new HashMap<SemType, HashSet<SemType>>(), type1,
				type2);
	}

	public void visit(AbsUnExpr acceptor) {
		acceptor.subExpr.accept(this);
		switch (acceptor.oper) {
		case AbsUnExpr.ADD:
		case AbsUnExpr.SUB: {
			SemType type = TypeResolver.getType(acceptor.subExpr);
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.INT))) {
				TypeResolver
						.setType(acceptor, new SemAtomType(SemAtomType.INT));
				return;
			}
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.REAL))) {
				TypeResolver.setType(acceptor,
						new SemAtomType(SemAtomType.REAL));
				return;
			}
			Report.error("Illegal operand type.", acceptor.getPosition(), 1);
		}
		case AbsUnExpr.NOT: {
			SemType type = TypeResolver.getType(acceptor.subExpr);
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.BOOL))) {
				TypeResolver.setType(acceptor,
						new SemAtomType(SemAtomType.BOOL));
				return;
			}
			Report.error("Illegal operand type.", acceptor.getPosition(), 1);
		}
		case AbsUnExpr.MUL: {
			SemType type = TypeResolver.getType(acceptor.subExpr).actualType();
			if (type instanceof SemPtrType) {
				SemPtrType ptrType = (SemPtrType) type;
				TypeResolver.setType(acceptor, ptrType.type);
				return;
			}

			Report.error("Illegal operand type.", acceptor.getPosition(), 1);
		}
		case AbsUnExpr.AND: {
			SemType type = TypeResolver.getType(acceptor.subExpr);
			TypeResolver.setType(acceptor, new SemPtrType(type));
			return;
		}
		default:
			Report.error("Internal error.", acceptor.getPosition(), 1);
		}
	}

	@Override
	public void visit(AbsAtomExpr acceptor) {
		switch (acceptor.GetType()) {
		case AbsAtomExpr.INT:
			TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.INT));
			break;
		case AbsAtomExpr.STRING:
			TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.STRING));
			break;
		case AbsAtomExpr.BOOL:
			TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.BOOL));
			break;
		case AbsAtomExpr.REAL:
			TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.REAL));
			break;
		case AbsAtomExpr.VOID:
			TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.VOID));
			break;
		default:
			Report.error("Internal error.", acceptor.getPosition(), 1);
			break;
		}
	}

	@Override
	public void visit(AbsBinExpr acceptor) {
		// preverimo levi del in dobimo tip
		acceptor.fstSubExpr.accept(this);
		SemType fstSubExprSemType = TypeResolver.getType(acceptor.fstSubExpr);

		// preverimo desni del in dobimo tip
		acceptor.sndSubExpr.accept(this);
		SemType sndSubExprSemType = TypeResolver.getType(acceptor.sndSubExpr);

		// preverimo ce so tipi vredu
		switch (acceptor.oper) {
		case AbsBinExpr.AND:
		case AbsBinExpr.OR:
			// oba morata bit bool, vse skupaj je bool
			if (!equal(fstSubExprSemType, new SemAtomType(SemAtomType.BOOL))) {
				Report.error("Illegal operand type. Should be bool.",
						acceptor.fstSubExpr.getPosition(), 1);
			}
			if (!equal(sndSubExprSemType, new SemAtomType(SemAtomType.BOOL))) {
				Report.error("Illegal operand type. Should be bool.",
						acceptor.sndSubExpr.getPosition(), 1);
			}

			// celoten izraz je bool
			TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.BOOL));
			break;
		case AbsBinExpr.EQU:
		case AbsBinExpr.NEQ:
		case AbsBinExpr.LTH:
		case AbsBinExpr.GTH:
		case AbsBinExpr.LEQ:
		case AbsBinExpr.GEQ:
			// oba moreta bit isti tip
			if (!equal(fstSubExprSemType, sndSubExprSemType)) {
				Report.error(
						"Illegal operand type. Statements are not equal type.",
						acceptor.getPosition(), 1);
			}

			// celoten izraz je bool
			TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.BOOL));
			break;
		case AbsBinExpr.ADD:
		case AbsBinExpr.SUB:
		case AbsBinExpr.MUL:
		case AbsBinExpr.DIV:
		case AbsBinExpr.MOD:
			// oba tipa ista
			if (!equal(fstSubExprSemType, sndSubExprSemType)) {
				Report.error(
						"Illegal operand type. Statements are not equal type.",
						acceptor.getPosition(), 1);
			}

			// skupen tip je lahko int
			SemType commonSemType = new SemAtomType(SemAtomType.INT);
			if (!equal(fstSubExprSemType, commonSemType)) {
				// skupen tip je lahko real
				commonSemType = new SemAtomType(SemAtomType.REAL);
				if (!equal(fstSubExprSemType, commonSemType)) {
					Report.error(
							"Illegal operand type. Statements are not type INT or REAL.",
							acceptor.getPosition(), 1);
				}

			}

			// celoten izraz je skupnega tipa
			TypeResolver.setType(acceptor, commonSemType);
			break;
		case AbsBinExpr.REC:
			// dobimo definicijo rec komponente
			SemType semType = fstSubExprSemType.actualType();
			if (!(semType instanceof SemRecType)) {
				Report.error(
						"Illegal operand type. First operand in AbsBinExpr.REC must be type of SemRecType",
						acceptor.getPosition(), 1);
			}
			SemRecType semRecType = (SemRecType) semType;

			// pridobimo in preverimo èe so res konkretna imena x1.x2
			AbsExprName absExprNameSnd = (AbsExprName) acceptor.sndSubExpr;
			if (absExprNameSnd == null) {
				Report.error(
						"Rec call does not have AbsExprName as second statement.",
						1);
			}
			// preverimo ce je druga komponenta med komponentami
			if (!semRecType.compNames.contains(absExprNameSnd.identifier
					.getLexeme())) {
				Report.error(
						"Illegal operand type. Record does not have component - "
								+ absExprNameSnd.identifier.getLexeme(),
						acceptor.fstSubExpr.getPosition(), 1);
			}
			// pridobimo tip druge komponente
			int compIndex = semRecType.compNames
					.indexOf(absExprNameSnd.identifier.getLexeme());
			SemType compType = semRecType.compTypes.elementAt(compIndex);

			// celoten izraz je tipa drugega dela po operatorju("x1.x2")
			TypeResolver.setType(acceptor, compType);
			break;
		case AbsBinExpr.ARR:
			// preverit da je notranji statement int
			if (!equal(sndSubExprSemType, new SemAtomType(SemAtomType.INT))) {
				Report.error("Illegal operand type. Should be int.",
						acceptor.fstSubExpr.getPosition(), 1);
			}

			// dobit tip arraya
			SemArrType semArrType = (SemArrType) fstSubExprSemType;

			// vse skupej je tipa arraya
			TypeResolver.setType(acceptor, semArrType.type);
			break;
		default:
			Report.error("Internal error.", acceptor.getPosition(), 1);
			break;
		}
	}

	@Override
	public void visit(AbsExprName acceptor) {
		// pridobimo tip
		AbsDecl absDecl = DeclarationResolver.getDecl(acceptor);
		if (absDecl instanceof AbsVarDecl) {
			AbsVarDecl absVarDecl = (AbsVarDecl) absDecl;

			SemType semType = null;
			// ce je tip osnoven potem ne pridobivamo
			if (absVarDecl.type instanceof AbsAtomType) {
				AbsAtomType atomType = (AbsAtomType) absVarDecl.type;
				semType = new SemAtomType(atomType.typ);
			} else {
				// pridobimo tip
				semType = TypeResolver.getType(absVarDecl.type);
			}

			// nastavimo trenutnemu
			TypeResolver.setType(acceptor, semType.actualType());

		} else { /* TODO: verjetno nikoli ne pride sem ? */
		}
	}

	@Override
	public void visit(AbsExprs acceptor) {
		for (AbsExpr expr : acceptor.exprs)
			expr.accept(this);

		// dobimo tip zadnjega expressiona
		SemType semType = TypeResolver.getType(acceptor.exprs.lastElement());

		// dolocimo tip where expressiona
		TypeResolver.setType(acceptor, semType);
	}

	@Override
	public void visit(AbsWhereExpr acceptor) {
		// gremo cez deklaracije za pridobit imena
		acceptor.decls.accept(new TypeResolverName());
		// povezeno custom tipe
		acceptor.decls.accept(new TypeResolverNameNamed());
		// preverimo s tem deklaracije
		acceptor.decls.accept(this);

		// cez expressione
		acceptor.subExpr.accept(this);

		// potem dobi tip expressiona
		SemType semType = TypeResolver.getType(acceptor.subExpr);

		// dolocimo tip where expressiona
		TypeResolver.setType(acceptor, semType);
	}

	@Override
	public void visit(AbsFunCall acceptor) {
		// gremo cez exprs
		acceptor.args.accept(this);

		// dobimo tip funkcije
		AbsDecl absDecl = DeclarationResolver.getDecl(acceptor.name);

		// dobimo tip
		SemType semType = TypeResolver.getType(absDecl);

		// preverimo ce je stevilo ergumentov isto
		int callNumberArgs = acceptor.args.exprs.size();
		int declNumberArgs = ((AbsFunDecl) absDecl).pars.decls.size();
		if (callNumberArgs != declNumberArgs) {
			Report.error(
					"Illegal operand type. Number of arguments missmatch.",
					acceptor.getPosition(), 1);
		}

		// preverimo tip argumentov
		for (int i = 0; i < declNumberArgs; i++) {
			SemType semTypeCall = TypeResolver.getType(acceptor.args.exprs
					.elementAt(i));
			SemType semTypeDecl = TypeResolver
					.getType(((AbsFunDecl) absDecl).pars.decls.elementAt(i));

			// preveri enakost
			if (!equal(semTypeDecl, semTypeCall)) {
				Report.error(
						"Illegal operand type. Function call argument wrong type.",
						acceptor.args.exprs.elementAt(i).getPosition(), 1);
			}
		}

		// dolocimo tip klica funkcije
		TypeResolver.setType(acceptor, semType);
	}

	// STATEMENT

	@Override
	public void visit(AbsIfStmt acceptor) {
		// naredimo obhod po pogoju
		acceptor.condExpr.accept(this);

		// pridobimo generiran tip
		SemType semTypCondition = TypeResolver.getType(acceptor.condExpr);

		// condition more biti tipa BOOL
		if (!equal(semTypCondition, new SemAtomType(SemAtomType.BOOL))) {
			Report.error(
					"Illegal operand type. Condition should be type of Bool.",
					acceptor.condExpr.getPosition(), 1);
		}

		// gremo cez oba expressiona ce sta
		acceptor.thenExprs.accept(this);
		// pridobimo tip
		SemType semTypeThenExpr = TypeResolver.getType(acceptor.thenExprs);

		// gremo cez ce obstaja else expr in pridobimo tip
		SemType semTypeElseExpr = null;
		if (acceptor.elseExprs != null) {
			acceptor.elseExprs.accept(this);
			semTypeElseExpr = TypeResolver.getType(acceptor.elseExprs);
		}

		// ce je tip elseExpr null - ker elseExpr ne obstaja potem je tip klica
		// void, cene je tip klica funkcije tip elseExpr
		if (semTypeElseExpr != null) {

			// preverimo še da je tip else in then expr enak
			if (!equal(semTypeThenExpr, semTypeElseExpr)) {
				Report.error(
						"Illegal operand type. Then and Else Expr should be the same type.",
						acceptor.condExpr.getPosition(), 1);
			}
			// dodamo
			TypeResolver.setType(acceptor, semTypeElseExpr);
		} else {
			TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.VOID));
		}
	}

	@Override
	public void visit(AbsWhileStmt acceptor) {
		// gremo cez pogoj in pridobimo tip
		acceptor.condExpr.accept(this);

		// tip pogoja more bit BOOL
		SemType semTypeCondExpr = TypeResolver.getType(acceptor.condExpr);
		if (!equal(semTypeCondExpr, new SemAtomType(SemAtomType.BOOL))) {
			Report.error(
					"Illegal operand type. Condition expr for while must be bool.",
					acceptor.condExpr.getPosition(), 1);
		}

		// gremo cez tel zanke
		acceptor.loopExpr.accept(this);

		// celotna zanka while je void
		TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.VOID));
	}

	@Override
	public void visit(AbsForStmt acceptor) {
		// dobimo tip identifikatorja
		acceptor.name.accept(this);
		SemType semTypeName = TypeResolver.getType(acceptor.name);

		// dobimo tip spodnje meje in zgornje
		acceptor.loBound.accept(this);
		SemType semTypeLoBound = TypeResolver.getType(acceptor.loBound);
		acceptor.hiBound.accept(this);
		SemType semTypeHiBound = TypeResolver.getType(acceptor.hiBound);

		// vsi morejo bit int
		if (!equal(semTypeName, new SemAtomType(SemAtomType.INT))) {
			Report.error("Illegal operand type. Loop variable not type Int.",
					acceptor.name.getPosition(), 1);
		}
		if (!equal(semTypeLoBound, new SemAtomType(SemAtomType.INT))) {
			Report.error("Illegal operand type. LoBound not type Int.",
					acceptor.name.getPosition(), 1);
		}
		if (!equal(semTypeHiBound, new SemAtomType(SemAtomType.INT))) {
			Report.error("Illegal operand type. HiBound not type Int.",
					acceptor.name.getPosition(), 1);
		}

		// celotna zanka for je void
		TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.VOID));
	}

	@Override
	public void visit(AbsAssignStmt acceptor) {
		// gremo cez desni in levi podizraz
		acceptor.fstSubExpr.accept(this);
		acceptor.sndSubExpr.accept(this);
		// pridobimo oba tipa
		SemType semTypeFstSubExpr = TypeResolver.getType(acceptor.fstSubExpr);
		SemType semTypeSndSubExpr = TypeResolver.getType(acceptor.sndSubExpr);

		// oba tipa moreta bit ista
		if (!equal(semTypeFstSubExpr, semTypeSndSubExpr)) {
			Report.error(
					"Illegal operand type. Both expressions must be the same type.",
					acceptor.getPosition(), 1);
		}

		// nastavimo tip assign stmt
		TypeResolver.setType(acceptor, semTypeFstSubExpr);
	}

	// TIPI

	@Override
	public void visit(AbsTypeName acceptor) {
		// ne naredimo nic
	}

	@Override
	public void visit(AbsAtomType acceptor) {
		// ne naredimo nic
	}

	@Override
	public void visit(AbsPtrType acceptor) {
		// ne naredimo nic
	}

	@Override
	public void visit(AbsArrType acceptor) {

		// ne naredimo nic
	}

	@Override
	public void visit(AbsRecType acceptor) {
		// gremo cez tipe
		acceptor.comps.accept(new TypeResolverName());
		acceptor.comps.accept(new TypeResolverNameNamed());
		// zgraditi je potrebno seznama komponent(ime/tip)
		SemRecType recType = (SemRecType) TypeResolver.getType(acceptor);
		for (AbsDecl absDecl : acceptor.comps.decls) {

			// dodamo koponenti ime
			AbsVarDecl absVarDecl = (AbsVarDecl) absDecl;
			recType.compNames.add(absVarDecl.name.identifier.getLexeme());

			// pridobimo tip in dodamo
			SemType semType = TypeResolver.getType(absVarDecl.type);
			recType.compTypes.add(semType);
		}
	}

	// DEKLARACIJE
	public void visit(AbsDecls acceptor) {
		for (AbsDecl decl : acceptor.decls)
			decl.accept(this);
	}

	@Override
	public void visit(AbsVarDecl acceptor) {
		acceptor.type.accept(this);
	}

	@Override
	public void visit(AbsFunDecl acceptor) {
		// dobimo tip
		SemType semAcceptorType = TypeResolver.getType(acceptor.type);

		// obhodimo deklracije
		acceptor.pars.accept(new TypeResolverName());

		// obhodimo da povezemo povezane tipe
		acceptor.pars.accept(new TypeResolverNameNamed());

		// obhodimo deklracije
		acceptor.pars.accept(this);

		// obhodimo expression
		acceptor.expr.accept(this);

		// pridobimo tip expressiona
		SemType semFstExprType = TypeResolver.getType(acceptor.expr);
		// preverimo tip expressiona in tip funkcije
		if (!TypeResolver.equal(semAcceptorType, semFstExprType)) {
			Report.error(
					"Illegal type. Function type not the same as its expression.",
					acceptor.getPosition(), 1);
		}
	}

	@Override
	public void visit(AbsTypDecl acceptor) {
		acceptor.type.accept(this);
	}
}
