package compiler.semanal;

import java.io.*;

import compiler.*;

/** Atomarni tip.  */
public class SemAtomType extends SemType {
	
	public static final int INT    = 0;
	public static final int REAL   = 1;
	public static final int BOOL   = 2;
	public static final int STRING = 3;
	public static final int VOID   = 4;

	/** Atomarni tip. */
	public final int typ;
	
	public SemAtomType(int typ) {
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
	public void toXML(PrintStream xml) {
		String type;
		switch (typ) {
		case INT   : type = "INT"   ; break;
		case REAL  : type = "REAL"  ; break;
		case BOOL  : type = "BOOL"  ; break;
		case STRING: type = "STRING"; break;
		case VOID  : type = "VOID"  ; break;
		default:
			type = "";
			Report.error("Internal error.", 1);
		}
		xml.println("<semtype type=\"" + type + "\"/>");
	}
	
	@Override
	public SemType actualType() {
		return this;
	}

	@Override
	public int size() {
		switch (typ) {
		case INT   : return 4;
		case REAL  : return 8;
		case BOOL  : return 1;
		case STRING: return 8;
		case VOID  : return 0;
		default:
			Report.error("Internal error.", 1);
			return -1;
		}
	}
	
}
