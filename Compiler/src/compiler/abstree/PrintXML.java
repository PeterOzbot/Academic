package compiler.abstree;

import java.io.*;

/** Izpis abstraktnega sintaksnega drevesa.  */
public class PrintXML implements Visitor {
	
	private PrintStream xml;
	
	public PrintXML(PrintStream xml) {
		this.xml = xml;
	}
	
	public void visit(AbsArrType acceptor) {
		xml.println("<absnode node=\"ArrType\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.type.accept(this);
		acceptor.size.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsAssignStmt acceptor) {
		xml.println("<absnode node=\"AssignStmt\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.fstSubExpr.accept(this);
		acceptor.sndSubExpr.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsAtomExpr acceptor) {
		xml.println("<absnode node=\"AtomExpr\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		if (acceptor.expr != null) acceptor.expr.toXML(xml);
		xml.println("</absnode>");
	}
	
	public void visit(AbsAtomType acceptor) {
		String typName;
		switch (acceptor.typ) {
		case AbsAtomType.INT   : typName = "INT"   ; break;
		case AbsAtomType.REAL  : typName = "REAL"  ; break;
		case AbsAtomType.BOOL  : typName = "BOOL"  ; break;
		case AbsAtomType.STRING: typName = "STRING"; break;
		case AbsAtomType.VOID  : typName = "VOID"  ; break;
		default                : typName = "ERROR" ; break;
		}
		xml.println("<absnode node=\"AtomType\" value=\"" + typName + "\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		xml.println("</absnode>");
	}
	
	public void visit(AbsBinExpr acceptor) {
		String operName;
		switch (acceptor.oper) {
		case AbsBinExpr.OR : operName = "OR"   ; break;
		case AbsBinExpr.AND: operName = "AND"  ; break;
		case AbsBinExpr.EQU: operName = "EQU"  ; break;
		case AbsBinExpr.NEQ: operName = "NEQ"  ; break;
		case AbsBinExpr.LTH: operName = "LTH"  ; break;
		case AbsBinExpr.GTH: operName = "GTH"  ; break;
		case AbsBinExpr.LEQ: operName = "LEQ"  ; break;
		case AbsBinExpr.GEQ: operName = "GEQ"  ; break;
		case AbsBinExpr.ADD: operName = "ADD"  ; break;
		case AbsBinExpr.SUB: operName = "SUB"  ; break;
		case AbsBinExpr.MUL: operName = "MUL"  ; break;
		case AbsBinExpr.DIV: operName = "DIV"  ; break;
		case AbsBinExpr.MOD: operName = "MOD"  ; break;
		case AbsBinExpr.ARR: operName = "ARR"  ; break;
		case AbsBinExpr.REC: operName = "REC"  ; break;
		default            : operName = "ERROR"; break;
		}
		xml.println("<absnode node=\"BinExpr\" value=\"" + operName + "\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.fstSubExpr.accept(this);
		acceptor.sndSubExpr.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsDecls acceptor) {
		xml.println("<absnode node=\"Decls\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		for (AbsDecl decl: acceptor.decls) decl.accept(this);
		xml.println("</absnode>");	
	}
	
	public void visit(AbsExprName acceptor) {
		xml.println("<absnode node=\"ExprName\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.identifier.toXML(xml);
		xml.println("</absnode>");
	}
	
	public void visit(AbsExprs acceptor) {
		xml.println("<absnode node=\"Exprs\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		for (AbsExpr expr: acceptor.exprs) expr.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsForStmt acceptor) {
		xml.println("<absnode node=\"ForStmt\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.name.accept(this);
		acceptor.loBound.accept(this);
		acceptor.hiBound.accept(this);
		acceptor.loopExpr.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsFunCall acceptor) {
		xml.println("<absnode node=\"FunCall\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.name.accept(this);
		acceptor.args.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsFunDecl acceptor) {
		xml.println("<absnode node=\"FunDecl\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.name.accept(this);
		acceptor.pars.accept(this);
		acceptor.type.accept(this);
		acceptor.expr.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsIfStmt acceptor) {
		xml.println("<absnode node=\"IfStmt\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.condExpr.accept(this);
		acceptor.thenExprs.accept(this);
		if (acceptor.elseExprs != null) acceptor.elseExprs.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsPtrType acceptor) {
		xml.println("<absnode node=\"PtrType\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.type.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsRecType acceptor) {
		xml.println("<absnode node=\"RecType\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.comps.accept(this);
		xml.println("</absnode>");		
	}
	
	public void visit(AbsTypDecl acceptor) {
		xml.println("<absnode node=\"TypDecl\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.name.accept(this);
		acceptor.type.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsTypeName acceptor) {
		xml.println("<absnode node=\"TypeName\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.identifier.toXML(xml);
		xml.println("</absnode>");
	}
	
	public void visit(AbsUnExpr acceptor) {
		String operName;
		switch (acceptor.oper) {
		case AbsUnExpr.ADD: operName = "ADD"  ; break;
		case AbsUnExpr.SUB: operName = "SUB"  ; break;
		case AbsUnExpr.MUL: operName = "MUL"  ; break;
		case AbsUnExpr.AND: operName = "AND"  ; break;
		case AbsUnExpr.NOT: operName = "NOT"  ; break;
		default : operName = "ERROR"; break;
		}
		xml.println("<absnode node=\"UnExpr\" value=\"" + operName + "\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.subExpr.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsVarDecl acceptor) {
		xml.println("<absnode node=\"VarDecl\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.name.accept(this);
		acceptor.type.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsWhereExpr acceptor) {
		xml.println("<absnode node=\"WhereExpr\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.subExpr.accept(this);
		acceptor.decls.accept(this);
		xml.println("</absnode>");
	}
	
	public void visit(AbsWhileStmt acceptor) {
		xml.println("<absnode node=\"WhileStmt\">");
		if (acceptor.getPosition() != null) acceptor.getPosition().toXML(xml);
		acceptor.condExpr.accept(this);
		acceptor.loopExpr.accept(this);
		xml.println("</absnode>");
	}

}
