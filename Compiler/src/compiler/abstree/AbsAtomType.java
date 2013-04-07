package compiler.abstree;

import compiler.*;

/** Atomarni tip.  */
public class AbsAtomType extends AbsType {
	
	public static final int INT    = 0;
	public static final int REAL   = 1;
	public static final int BOOL   = 2;
	public static final int STRING = 3;
	public static final int VOID   = 4;

	/** Atomarni tip. */
	public final int typ;
	
	public AbsAtomType(int typ) {
		switch (typ) {
		case INT   :
		case REAL  :
		case BOOL  :
		case STRING:
		case VOID  :
			this.typ = typ;
			break;
		default    :
			this.typ = -1;
			Report.error("Internal error.", 1);
		}
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }

}
