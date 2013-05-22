package compiler.imcode;

import java.util.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.abstree.*;
import compiler.semanal.*;
import compiler.frames.*;

public class CodeGenerator implements Visitor {

	private static HashMap<AbsTree, ImCode> imcodes = new HashMap<AbsTree, ImCode>();

	private static int _level = 0;

	public static void setCode(AbsTree node, ImCode code) {
		imcodes.put(node, code);
	}

	public static ImCode getCode(AbsTree node) {
		return imcodes.get(node);
	}

	public void visit(AbsArrType acceptor) {
		// DONE
	}

	public void visit(AbsAssignStmt acceptor) {
		// gremo v levi podizraz
		acceptor.fstSubExpr.accept(this);
		// gremo v desni podizraz
		acceptor.sndSubExpr.accept(this);

		// pridobimo ImCode od obeh
		ImCode fstSubExprImCode = getCode(acceptor.fstSubExpr);
		ImCode sndSubExprImCode = getCode(acceptor.sndSubExpr);

		// nastavimo assign statement
		setCode(acceptor, new ImMOVE(fstSubExprImCode, sndSubExprImCode));
	}

	public void visit(AbsAtomExpr acceptor) {
		// vrednost lexema
		String lexeme = acceptor.expr.getLexeme();

		// za vsak specificen tip dobimo vrednost
		switch (acceptor.GetType()) {
		case AbsAtomExpr.INT:
			Integer intValue = new Integer(lexeme);
			setCode(acceptor, new ImCONSTi(intValue));
			break;
		case AbsAtomExpr.REAL:
			float floatValue = Float.parseFloat(lexeme);
			setCode(acceptor, new ImCONSTr(floatValue));
			break;
		case AbsAtomExpr.BOOL:
			// ce je true - 1
			if (lexeme.equals(LexAnal.True)) {
				setCode(acceptor, new ImCONSTi(1));
				break;
			}
			// ce je false - 0
			if (lexeme.equals(LexAnal.False)) {
				setCode(acceptor, new ImCONSTi(0));
				break;
			}
			// ce ni nic napaka
			Report.error("Internal error.", 1);
		case AbsAtomExpr.STRING:
			setCode(acceptor, new ImCONSTs(lexeme));
			break;
		default:
			Report.error("Internal error. No match for AbsAtomExpr type.", 1);
		}
	}

	public void visit(AbsAtomType acceptor) {
		// DONE
	}

	public void visit(AbsBinExpr acceptor) {
		// gremo cez levi del
		acceptor.fstSubExpr.accept(this);
		// gremo cez desni del
		acceptor.sndSubExpr.accept(this);

		// dobimo ImCode za oba
		ImCode fstSubExprImCode = getCode(acceptor.fstSubExpr);
		ImCode sndSubExprImCode = getCode(acceptor.sndSubExpr);

		// dobimo tip za obe strani + preverimo èe sta enaka
		SemType fstSubExprSemType = TypeResolver.getType(acceptor.fstSubExpr);
		SemType sndSubExprSemType = TypeResolver.getType(acceptor.sndSubExpr);
		if (!TypeResolver.equal(fstSubExprSemType, sndSubExprSemType)) {
			Report.error(
					"Internal error. AbsBinExpr.fstSubExpr and AbsBinExpr.sndSubExpr type does not match.",
					1);
		}
		// nastavimo zastavice kakšen tip sta(gledamo samo prvi del - morata
		// biti ista)
		Boolean isBool = TypeResolver.equal(fstSubExprSemType, new SemAtomType(
				SemAtomType.BOOL));
		Boolean isInt = TypeResolver.equal(fstSubExprSemType, new SemAtomType(
				SemAtomType.INT));
		Boolean isReal = TypeResolver.equal(fstSubExprSemType, new SemAtomType(
				SemAtomType.REAL));
		Boolean isString = TypeResolver.equal(fstSubExprSemType,
				new SemAtomType(SemAtomType.STRING));

		// nastavimo kodo glede na operacijo
		switch (acceptor.GetOperand()) {
		case ImBINOP.OR:
			setCode(acceptor, new ImBINOP(ImBINOP.OR, fstSubExprImCode,
					sndSubExprImCode));
			break;
		case ImBINOP.AND:
			setCode(acceptor, new ImBINOP(ImBINOP.AND, fstSubExprImCode,
					sndSubExprImCode));
			break;
		case ImBINOP.EQU:
			// ce je tip na obeh straneh int ali bool -> EUQi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.EQUi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> EUQr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.EQUr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> EUQs
			if (isString) {
				setCode(acceptor, new ImBINOP(ImBINOP.EQUs, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
		case ImBINOP.NEQ:
			// ce je tip na obeh straneh int ali bool -> NEQi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.NEQi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> NEQr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.NEQr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> NEQs
			if (isString) {
				setCode(acceptor, new ImBINOP(ImBINOP.NEQs, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
		case ImBINOP.LTH:
			// ce je tip na obeh straneh int ali bool -> LTHi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.LTHi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> LTHr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.LTHr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> LTHs
			if (isString) {
				setCode(acceptor, new ImBINOP(ImBINOP.LTHs, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
		case ImBINOP.GTH:
			// ce je tip na obeh straneh int ali bool -> GTHi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.GTHi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> GTHr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.GTHr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> GTHs
			if (isString) {
				setCode(acceptor, new ImBINOP(ImBINOP.GTHs, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
		case ImBINOP.LEQ:
			// ce je tip na obeh straneh int ali bool -> LEQi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.LEQi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> NEQr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.NEQr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> NEQs
			if (isString) {
				setCode(acceptor, new ImBINOP(ImBINOP.NEQs, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
		case ImBINOP.GEQ:
			// ce je tip na obeh straneh int ali bool -> NEQi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.NEQi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> NEQr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.NEQr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> NEQs
			if (isString) {
				setCode(acceptor, new ImBINOP(ImBINOP.NEQs, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
		case ImBINOP.ADD:
			// ce je tip na obeh straneh int ali bool -> ADDi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.ADDi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> ADDr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.ADDr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> ADDs
			if (isString) {
				Report.error("Internal error.Can not ADD string.", 1);
			}
		case ImBINOP.SUB:
			// ce je tip na obeh straneh int ali bool -> SUBi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.SUBi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> SUBr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.SUBr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> SUBs
			if (isString) {
				Report.error("Internal error.Can not SUB string.", 1);
			}
		case ImBINOP.MUL:
			// ce je tip na obeh straneh int ali bool -> MULi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.MULi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> MULr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.MULr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> MULs
			if (isString) {
				Report.error("Internal error.Can not MUL string.", 1);
			}
		case ImBINOP.DIV:
			// ce je tip na obeh straneh int ali bool -> DIVi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.DIVi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> DIVr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.DIVr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> DIVs
			if (isString) {
				Report.error("Internal error.Can not DIV string.", 1);
			}
		case ImBINOP.MOD:
			// ce je tip na obeh straneh int ali bool -> MODi
			if (isBool || isInt) {
				setCode(acceptor, new ImBINOP(ImBINOP.MODi, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh real -> MODr
			if (isReal) {
				setCode(acceptor, new ImBINOP(ImBINOP.MODr, fstSubExprImCode,
						sndSubExprImCode));
				break;
			}
			// ce je tip na obeh straneh string -> MODs
			if (isString) {
				Report.error("Internal error.Can not MOD string.", 1);
			}
		case ImBINOP.ARR:
			// ImCode za prvi del arraya more bit MEM
			if (!(fstSubExprImCode instanceof ImMEM)) {
				Report.error(
						"Internal error. AbsBinExpr.fstSubExpr  ImCode not ImMEM.",
						1);
			}
			ImMEM fstSubExprArrImMEM = (ImMEM) fstSubExprImCode;

			// zgradimo drugi del za array
			ImBINOP sndSubExprImBINOP = new ImBINOP(ImBINOP.MULi, new ImCONSTi(
					sndSubExprSemType.actualType().size()), sndSubExprImCode);

			// zgradimo izraz in nastavimo acceptorju, posebnost da za
			// fstSubExprImMEM vzamemo zadnji MEM proc
			ImCode arrCode = new ImMEM(new ImBINOP(ImBINOP.ADDi,
					fstSubExprArrImMEM.expr, sndSubExprImBINOP));
			CodeGenerator.setCode(acceptor, arrCode);
			break;
		case ImBINOP.REC:
			// ImCode za prvi del records more bit MEM
			if (!(fstSubExprImCode instanceof ImMEM)) {
				Report.error(
						"Internal error. AbsBinExpr.fstSubExpr  ImCode not ImMEM.",
						1);
			}
			ImMEM fstSubExprRecImMEM = (ImMEM) fstSubExprImCode;

			// pridobimo Access za drugi del recorda - po piki
			AbsVarDecl varDecl = (AbsVarDecl) DeclarationResolver
					.getDecl(acceptor.sndSubExpr);
			Access componentAccess = FrameResolver.getAccess(varDecl);

			// zgradimo izraz za record, drugi del je constanta odmika tipa
			// drugega dela
			ImCode recCode = new ImMEM(new ImBINOP(ImBINOP.ADDi,
					fstSubExprRecImMEM.expr, new ImCONSTi(
							componentAccess.offset)));
			CodeGenerator.setCode(acceptor, recCode);
		default:
			Report.error("Internal error. No match for AbsBinExpr operand.", 1);
		}
	}

	public void visit(AbsDecls acceptor) {
		for (AbsDecl decl : acceptor.decls)
			decl.accept(this);
	}

	public void visit(AbsExprName acceptor) {// TODO - preglej ce je vredu
		AbsVarDecl varDecl = (AbsVarDecl) DeclarationResolver.getDecl(acceptor);
		Access varAccess = FrameResolver.getAccess(varDecl);
		ImCode exprCode = new ImNAME("FP");
		for (int i = 0; i < (_level - varAccess.level); i++)
			exprCode = new ImMEM(exprCode);
		exprCode = new ImMEM(new ImBINOP(ImBINOP.ADDi, exprCode, new ImCONSTi(
				varAccess.offset)));
		CodeGenerator.setCode(acceptor, exprCode);
	}

	public void visit(AbsExprs acceptor) {
		for (AbsExpr expr : acceptor.exprs)
			expr.accept(this);
		ImSEQ exprsCode = new ImSEQ();
		for (AbsExpr expr : acceptor.exprs) {
			ImCode exprCode = CodeGenerator.getCode(expr);
			exprsCode.codes.add(exprCode);
		}
		CodeGenerator.setCode(acceptor, exprsCode);
	}

	public void visit(AbsForStmt acceptor) {
		// TODO napak èe gre èez int loop,in se zacikla
		// gremo cez vse dele
		acceptor.loBound.accept(this);
		acceptor.hiBound.accept(this);
		acceptor.loopExpr.accept(this);
		// pridobimo ImCode za vse dele
		ImCode loBoundImCode = getCode(acceptor.loBound);
		ImCode hiBoundImCode = getCode(acceptor.hiBound);
		ImCode loopExprImCode = getCode(acceptor.loopExpr);

		// nova sekvenca
		ImSEQ forStmtImSEQ = new ImSEQ();

		// nova temp za var for-a
		ImTEMP forVarImTEMP = new ImTEMP(new Temp());

		// prvo izracunamo lower bound in nastavimo spremenjivki
		forStmtImSEQ.codes.add(new ImMOVE(forVarImTEMP, loBoundImCode));

		// naredimo label0 in dodamo
		Label label0 = new Label("0", "Start");
		ImLABEL label0ImLABEL = new ImLABEL(label0);
		forStmtImSEQ.codes.add(label0ImLABEL);

		// naredimo label2
		Label label2 = new Label("2", "End");

		// naredimo pogoj
		ImBINOP conditionImBINOP = new ImBINOP(ImBINOP.LEQi, forVarImTEMP,
				hiBoundImCode);
		// naredimo CJUMP in dodamo
		ImCJUMP imCJUMP = new ImCJUMP(conditionImBINOP, label0, label2);
		forStmtImSEQ.codes.add(imCJUMP);

		// naredimo label1 in dodamo
		Label label1 = new Label("1", "Middle");
		ImLABEL label1ImLABEL = new ImLABEL(label1);
		forStmtImSEQ.codes.add(label1ImLABEL);

		// dodamo glavni expression
		forStmtImSEQ.codes.add(loopExprImCode);

		// naredimo increase števca - temp
		ImBINOP incCOunterImBINOP = new ImBINOP(ImBINOP.ADDi, forVarImTEMP,
				new ImCONSTi(1));
		// naredimo MOVE števca poveèanega za ena v temp in dodamo
		ImMOVE incCounterImMOVE = new ImMOVE(forVarImTEMP, incCOunterImBINOP);
		forStmtImSEQ.codes.add(incCounterImMOVE);

		// naredimo JUMP ter dodamo
		ImJUMP jumpToMiddleImJUMP = new ImJUMP(label0);
		forStmtImSEQ.codes.add(jumpToMiddleImJUMP);

		// naredimo label2ImLABEL in dodamo
		forStmtImSEQ.codes.add(new ImLABEL(label2));
	}

	public void visit(AbsFunCall acceptor) {// TODO - preglje ce je vredu
		Label funLabel;
		int funLevel;
		if (acceptor.name.identifier.getLexeme().equals("putInt")
				|| acceptor.name.identifier.getLexeme().equals("getInt")) {
			funLabel = new Label("sys", acceptor.name.identifier.getLexeme());
			funLevel = 0;
		} else {
			AbsFunDecl funDecl = (AbsFunDecl) DeclarationResolver
					.getDecl(acceptor.name);
			Frame frame = FrameResolver.getFrame(funDecl);
			funLabel = frame.label;
			funLevel = frame.getLevel();
		}
		ImCode sl = new ImNAME("FP");
		for (int i = 0; i <= _level - funLevel; i++)
			sl = new ImMEM(sl);
		ImCALL callCode = new ImCALL(funLabel, sl);
		for (AbsExpr arg : acceptor.args.exprs) {
			arg.accept(this);
			ImCode argCode = CodeGenerator.getCode(arg);
			callCode.args.add(argCode);
		}
		CodeGenerator.setCode(acceptor, callCode);
	}

	public void visit(AbsFunDecl acceptor) {
		_level++;
		acceptor.expr.accept(this);
		_level--;
	}

	public void visit(AbsIfStmt acceptor) {
		// gremo cez vse dele
		acceptor.condExpr.accept(this);
		acceptor.thenExprs.accept(this);
		if (acceptor.elseExprs != null) {
			acceptor.elseExprs.accept(this);
		}
		// pridobimo ImCode za vse dele
		ImCode condExprImCode = getCode(acceptor.condExpr);
		ImCode thenExprsImCode = getCode(acceptor.thenExprs);
		ImCode elseExprsImCode = null;
		if (acceptor.elseExprs != null) {
			elseExprsImCode = getCode(acceptor.elseExprs);
		}

		// nova sekvenca
		ImSEQ forStmtImSEQ = new ImSEQ();

		// naredimo label0-then
		Label label0 = new Label("0", "Then");
		// naredimo label1-else
		Label label1 = new Label("1", "Else");

		// naredimo CJUMP za condition in dodamo
		ImCJUMP conditionImCJUMP = new ImCJUMP(condExprImCode, label0, label1);
		forStmtImSEQ.codes.add(conditionImCJUMP);

		// naredimo ImLABEL in dodamo
		forStmtImSEQ.codes.add(new ImLABEL(label0));

		// naredimo temp spremenjivko za rezultat
		ImTEMP resultImTEMP = new ImTEMP(new Temp());
		// premaknemo thenExpr v temp in dodamo
		ImMOVE thenImMOVE = new ImMOVE(resultImTEMP, thenExprsImCode);
		forStmtImSEQ.codes.add(thenImMOVE);

		// naredimo temp2 - konec
		Label label2 = new Label("2", "End");
		// dodamo jump na label2
		forStmtImSEQ.codes.add(new ImJUMP(label2));

		// naredimo ImLABEL za else in dodamo
		forStmtImSEQ.codes.add(new ImLABEL(label1));

		// ce ni else-a ne anredimo nic
		if (acceptor.elseExprs != null) {
			// premaknemo elseExpr v temp in dodamo
			ImMOVE elseImMOVE = new ImMOVE(resultImTEMP, elseExprsImCode);
			forStmtImSEQ.codes.add(elseImMOVE);
		}

		// naredimo in dodamo label za konec
		forStmtImSEQ.codes.add(new ImLABEL(label2));

		// dodamo temp kot rezultat
		forStmtImSEQ.codes.add(resultImTEMP);
	}

	public void visit(AbsPtrType acceptor) {
		// DONE
	}

	public void visit(AbsRecType acceptor) {
		// DONE
	}

	public void visit(AbsTypDecl acceptor) {
		// DONE
	}

	public void visit(AbsTypeName acceptor) {
		// DONE
	}

	public void visit(AbsUnExpr acceptor) {
		acceptor.subExpr.accept(this);
		SemType type = TypeResolver.getType(acceptor);
		ImCode subCode = CodeGenerator.getCode(acceptor.subExpr);

		switch (acceptor.oper) {
		case AbsBinExpr.ADD:
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.INT)))
				CodeGenerator.setCode(acceptor,
						new ImUNOP(ImUNOP.ADDi, subCode));
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.REAL)))
				CodeGenerator.setCode(acceptor,
						new ImUNOP(ImUNOP.ADDr, subCode));
			break;
		case AbsBinExpr.SUB:
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.INT)))
				CodeGenerator.setCode(acceptor,
						new ImUNOP(ImUNOP.SUBi, subCode));
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.REAL)))
				CodeGenerator.setCode(acceptor,
						new ImUNOP(ImUNOP.SUBr, subCode));
			break;

		case AbsUnExpr.NOT:
			CodeGenerator.setCode(acceptor, new ImUNOP(ImUNOP.NOT, subCode));
			break;
		}

	}

	public void visit(AbsVarDecl acceptor) {
		// DONE
	}

	public void visit(AbsWhereExpr acceptor) {
		acceptor.subExpr.accept(this);
		ImCode subExprImCode = getCode(acceptor.subExpr);
		setCode(acceptor, subExprImCode);
	}

	public void visit(AbsWhileStmt acceptor) {
		// gremo cez oba dela
		acceptor.condExpr.accept(this);
		acceptor.loopExpr.accept(this);

		// pridobimo ImCode za oba
		ImCode condExprImCode = getCode(acceptor.condExpr);
		ImCode loopExprImCode = getCode(acceptor.loopExpr);

		// nova sekvenca
		ImSEQ forStmtImSEQ = new ImSEQ();

		// naredimo label0 in ImLABEL ter dodamo
		Label label0 = new Label("0", "Condition");
		ImLABEL label0ImLABEL = new ImLABEL(label0);
		forStmtImSEQ.codes.add(label0ImLABEL);

		// naredimo label1 - condition true
		Label label1 = new Label("1", "Condition_True");
		// naredimo label2 - condition false
		Label label2 = new Label("2", "Condition_False");
		// naredimo CJUMP in dodamo
		ImCJUMP conditionImCJUMP = new ImCJUMP(condExprImCode, label1, label2);
		forStmtImSEQ.codes.add(conditionImCJUMP);

		// naredimo ImLABEL za label1 in dodamo
		ImLABEL label1ImLABEL = new ImLABEL(label1);
		forStmtImSEQ.codes.add(label1ImLABEL);

		// dodamo loopExpr
		forStmtImSEQ.codes.add(loopExprImCode);

		// dodamo ImJUMP na label0 - zacetek
		forStmtImSEQ.codes.add(new ImJUMP(label0));

		// naredimo ImLABEL za label2 in dodamo
		ImLABEL label2ImLABEL = new ImLABEL(label2);
		forStmtImSEQ.codes.add(label2ImLABEL);
	}

}
