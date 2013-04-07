package compiler.abstree;

public class AbsAssignStmt extends AbsStmt {

	/** Prvi (levi) podizraz. */
	public final AbsExpr fstSubExpr;

	/** Drugi (desni) podizraz. */
	public final AbsExpr sndSubExpr;

	public AbsAssignStmt(AbsExpr fstSubExpr, AbsExpr sndSubExpr) {
		this.fstSubExpr = fstSubExpr;
		this.sndSubExpr = sndSubExpr;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
