package compiler.abstree;

import compiler.Report;
import compiler.imcode.ImBINOP;

/** Izraz z binarnim operatorjem. */
public class AbsBinExpr extends AbsExpr {

	public static final int OR = 0;
	public static final int AND = 1;
	public static final int EQU = 2;
	public static final int NEQ = 3;
	public static final int LTH = 4;
	public static final int GTH = 5;
	public static final int LEQ = 6;
	public static final int GEQ = 7;
	public static final int ADD = 8;
	public static final int SUB = 9;
	public static final int MUL = 10;
	public static final int DIV = 11;
	public static final int MOD = 12;
	public static final int ARR = 13;
	public static final int REC = 14;

	/** Binarni operator. */
	public final int oper;

	/** Prvi (levi) podizraz. */
	public final AbsExpr fstSubExpr;

	/** Drugi (desni) podizraz. */
	public final AbsExpr sndSubExpr;

	public AbsBinExpr(int oper, AbsExpr fstSubExpr, AbsExpr sndSubExpr) {
		this.oper = oper;
		this.fstSubExpr = fstSubExpr;
		this.sndSubExpr = sndSubExpr;
	}

	/**
	 * Pretvori operand tega razreda(staticno enumeracijo) v operand ImBINOP
	 * (staticno enumeracijo)
	 */
	public int GetOperand() {

		switch (oper) {
		case OR:
			return ImBINOP.OR;
		case AND:
			return ImBINOP.AND;
		case EQU:
			return ImBINOP.EQU;
		case NEQ:
			return ImBINOP.NEQ;
		case LTH:
			return ImBINOP.LTH;
		case GTH:
			return ImBINOP.GTH;
		case LEQ:
			return ImBINOP.LEQ;
		case GEQ:
			return ImBINOP.GEQ;
		case ADD:
			return ImBINOP.ADD;
		case SUB:
			return ImBINOP.SUB;
		case MUL:
			return ImBINOP.MUL;
		case DIV:
			return ImBINOP.DIV;
		case MOD:
			return ImBINOP.MOD;
		case ARR:
			return ImBINOP.ARR;
		case REC:
			return ImBINOP.REC;
		default:
			Report.error(
					"Internal error. AbsBinExpr.GetType() no match for operand.",
					1);
			return 0;
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
