package compiler.frames;

import java.util.*;

import compiler.Report;
import compiler.abstree.*;
import compiler.semanal.*;

public class FrameResolver implements Visitor {

	private static HashMap<AbsVarDecl, Access> accesses = new HashMap<AbsVarDecl, Access>();

	public static void setAccess(AbsVarDecl varDecl, Access access) {
		accesses.put(varDecl, access);
	}

	public static Access getAccess(AbsVarDecl varDecl) {
		return accesses.get(varDecl);
	}

	private static HashMap<AbsFunDecl, Frame> frames = new HashMap<AbsFunDecl, Frame>();

	public static void setFrame(AbsFunDecl funDecl, Frame frame) {
		frames.put(funDecl, frame);
	}

	public static Frame getFrame(AbsFunDecl funDecl) {
		return frames.get(funDecl);
	}

	private Stack<Frame> frms;

	public Frame mainFrame;

	public FrameResolver() {
		mainFrame = new Frame("", 0);

		frms = new Stack<Frame>();
		frms.push(mainFrame);
	}

	public void visit(AbsArrType acceptor) {
		acceptor.type.accept(this);
	}

	public void visit(AbsAssignStmt acceptor) {
		acceptor.fstSubExpr.accept(this);
		acceptor.sndSubExpr.accept(this);
	}

	public void visit(AbsAtomExpr acceptor) {
	}

	public void visit(AbsAtomType acceptor) {
	}

	public void visit(AbsBinExpr acceptor) {
		acceptor.fstSubExpr.accept(this);
		acceptor.sndSubExpr.accept(this);
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
		acceptor.loBound.accept(this);
		acceptor.hiBound.accept(this);
		acceptor.loopExpr.accept(this);
	}

	public void visit(AbsFunCall acceptor) {
		Frame frame = frms.peek();
		// dobimo deklaracijo funkcije
		int argsSize = 0;
		AbsDecl absDecl = DeclarationResolver.getDecl(acceptor.name);

		if (absDecl instanceof AbsFunDecl) {
			AbsFunDecl absFunDecl = (AbsFunDecl) absDecl;

			// ce je sistemska ne naredimo nic
			if (LibSys.IsSystem(absFunDecl.name.identifier.getLexeme())) {
				return;
			}

			// gremo cez vse vhodne parametre
			for (AbsDecl absParamDecl : absFunDecl.pars.decls) {
				if (absParamDecl instanceof AbsVarDecl) {

					// dobimo tip
					AbsVarDecl absVarDecl = (AbsVarDecl) absParamDecl;
					SemType semType = TypeResolver.getType(absVarDecl);
					// velikost tipa
					argsSize = argsSize + semType.actualType().size();

				} else {
					Report.warning("Function parameter not absVarDecl.",
							absDecl.getPosition());
				}
			}
		} else {
			Report.warning(
					"Function call's function is not in declaracion resolver",
					absDecl.getPosition());
		}
		// po instanci
		frame.ensureOutArgsSize(argsSize + 4);
		// po tabeli
		// frame.ensureOutArgsSize(acceptor.args.exprs.size() * 4 + 4);

		for (AbsExpr arg : acceptor.args.exprs)
			arg.accept(this);
	}

	public void visit(AbsFunDecl acceptor) {
		Frame frame = frms.peek();
		Frame newFrame = new Frame(acceptor.name.identifier.getLexeme(),
				frame.getLevel() + 1);
		setFrame(acceptor, newFrame);
		frms.push(newFrame);
		int offset = 4;// SL + 4 prejsnjega zapisa
		for (AbsDecl decl : acceptor.pars.decls) {
			AbsVarDecl varDecl = (AbsVarDecl) decl;
			SemType semType = TypeResolver.getType(varDecl);
			Access access = new Access(frame.getLevel() + 1, offset);
			offset += semType.actualType().size();
			setAccess(varDecl, access);
		}
		acceptor.type.accept(this);
		acceptor.expr.accept(this);
		frms.pop();
	}

	public void visit(AbsIfStmt acceptor) {
		acceptor.condExpr.accept(this);
		acceptor.thenExprs.accept(this);
		acceptor.elseExprs.accept(this);
	}

	public void visit(AbsPtrType acceptor) {
		acceptor.type.accept(this);
	}

	public void visit(AbsRecType acceptor) {
		int offset = 0;
		for (AbsDecl decl : acceptor.comps.decls) {
			AbsVarDecl varDecl = (AbsVarDecl) decl;
			Access varAccess = new Access(-1, offset);
			setAccess(varDecl, varAccess);
			SemType type = TypeResolver.getType(varDecl);
			offset += type.size();
			varDecl.type.accept(this);
		}
	}

	public void visit(AbsTypDecl acceptor) {
		acceptor.type.accept(this);
	}

	public void visit(AbsTypeName acceptor) {
	}

	public void visit(AbsUnExpr acceptor) {
		acceptor.subExpr.accept(this);
	}

	public void visit(AbsVarDecl acceptor) {
		// Lokalna spremenljivka.
		// Parametre obdelamo pri AbsFunDecl.
		// Komponente obdelamo pri AbsRecType.
		Frame frame = frms.peek();
		SemType type = TypeResolver.getType(acceptor);
		int offset = frame.addVariable(type.size());
		Access access = new Access(frame.getLevel(), offset);
		setAccess(acceptor, access);
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
