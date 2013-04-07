package compiler.abstree;

/** Izraz z binarnim operatorjem.  */
public class AbsBinExpr extends AbsExpr {

	public static final int OR  = 0;
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
	
	/** Binarni operator.  */
	public final int oper;
	
	/** Prvi (levi) podizraz.  */
	public final AbsExpr fstSubExpr;
	
	/** Drugi (desni) podizraz.  */
	public final AbsExpr sndSubExpr;
	
	public AbsBinExpr(int oper, AbsExpr fstSubExpr, AbsExpr sndSubExpr) {
		this.oper = oper;
		this.fstSubExpr = fstSubExpr;
		this.sndSubExpr = sndSubExpr;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
