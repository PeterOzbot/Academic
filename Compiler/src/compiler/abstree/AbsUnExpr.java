package compiler.abstree;

/** Izraz z unarnim operatorjem.  */
public class AbsUnExpr extends AbsExpr {

	public static final int ADD = 0;
	public static final int SUB = 1;
	public static final int MUL = 2;
	public static final int AND = 3;
	public static final int NOT = 4;
	
	/** Unarni operator.  */
	public final int oper;
	
	/** Podizraz.  */
	public final AbsExpr subExpr;
	
	public AbsUnExpr(int oper, AbsExpr subExpr) {
		this.oper = oper;
		this.subExpr = subExpr;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
