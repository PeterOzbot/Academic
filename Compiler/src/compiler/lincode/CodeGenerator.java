package compiler.lincode;

import java.util.*;

import compiler.abstree.*;
import compiler.frames.*;
import compiler.imcode.*;

public class CodeGenerator implements Visitor {
	
	public static HashMap<Label, Frame> framesByLabel = new HashMap<Label, Frame>();
	
	public static HashMap<Label, ImCode> codesByLabel = new HashMap<Label, ImCode>();

	public void visit(AbsArrType acceptor) {
		// DONE
	}

	public void visit(AbsAssignStmt acceptor) {
		acceptor.fstSubExpr.accept(this);
		acceptor.sndSubExpr.accept(this);		
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsAtomExpr acceptor) {
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsAtomType acceptor) {
		// DONE
	}

	public void visit(AbsBinExpr acceptor) {
		acceptor.fstSubExpr.accept(this);
		if (acceptor.oper != AbsBinExpr.REC) acceptor.sndSubExpr.accept(this);		
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsDecls acceptor) {
		for (AbsDecl decl : acceptor.decls)
			decl.accept(this);
	}

	public void visit(AbsExprName acceptor) {
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsExprs acceptor) {
		for (AbsExpr expr : acceptor.exprs) expr.accept(this);
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsForStmt acceptor) {
		acceptor.loBound.accept(this);
		acceptor.hiBound.accept(this);
		acceptor.loopExpr.accept(this);
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsFunCall acceptor) {
		for (AbsExpr arg: acceptor.args.exprs) arg.accept(this);
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsFunDecl acceptor) {
		acceptor.expr.accept(this);
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor.expr);
		code.linearCode();
		
		Frame frame = FrameResolver.getFrame(acceptor);
		ImCode linCode = code.linearCode;
		framesByLabel.put(frame.label, frame);
		codesByLabel.put(frame.label, linCode);
	}

	public void visit(AbsIfStmt acceptor) {
		acceptor.condExpr.accept(this);
		acceptor.thenExprs.accept(this);
		if (acceptor.elseExprs != null) acceptor.elseExprs.accept(this);
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
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
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsVarDecl acceptor) {
		// DONE
	}

	public void visit(AbsWhereExpr acceptor) {
		acceptor.subExpr.accept(this);
		acceptor.decls.accept(this);
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

	public void visit(AbsWhileStmt acceptor) {
		acceptor.condExpr.accept(this);
		acceptor.loopExpr.accept(this);
		ImCode code = compiler.imcode.CodeGenerator.getCode(acceptor);
		code.linearCode();
	}

}
