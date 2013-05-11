package compiler.imcode;

import java.util.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.abstree.*;
import compiler.semanal.*;
import compiler.frames.*;

public class CodeGenerator implements Visitor {

	private static HashMap<AbsTree, ImCode> imcodes = new HashMap<AbsTree, ImCode>();

	private static int level = 0;

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
		// TODO
	}

	public void visit(AbsAtomExpr acceptor) {
		// TODO
	}

	public void visit(AbsAtomType acceptor) {
		// DONE
	}

	public void visit(AbsBinExpr acceptor) {
		// TODO
	}

	public void visit(AbsDecls acceptor) {
		for (AbsDecl decl : acceptor.decls)
			decl.accept(this);
	}

	public void visit(AbsExprName acceptor) {
		AbsVarDecl varDecl = (AbsVarDecl) DeclarationResolver.getDecl(acceptor);
		Access varAccess = FrameResolver.getAccess(varDecl);
		ImCode exprCode = new ImNAME("FP");
		for (int i = 0; i < (level - varAccess.level); i++)
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
		// TODO
	}

	public void visit(AbsFunCall acceptor) {
		Label funLabel;
		int funLevel;
		if (acceptor.name.identifier.getLexeme().equals("putInt") ||
				acceptor.name.identifier.getLexeme().equals("getInt")) {
			funLabel = new Label("sys", acceptor.name.identifier.getLexeme());
			funLevel = 0;
		}
		else {
			AbsFunDecl funDecl = (AbsFunDecl) DeclarationResolver
					.getDecl(acceptor.name);
			Frame frame = FrameResolver.getFrame(funDecl);
			funLabel = frame.label;
			funLevel = frame.getLevel();
		}
		ImCode sl = new ImNAME("FP");
		for (int i = 0; i <= level - funLevel; i++)
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
		level++;
		acceptor.expr.accept(this);
		level--;
	}

	public void visit(AbsIfStmt acceptor) {
		// TODO
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
		// TODO
	}

	public void visit(AbsWhileStmt acceptor) {
		// TODO
	}

}
