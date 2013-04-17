package compiler.semanal;

import compiler.*;

/** Tip.  */
public abstract class SemType implements XMLable {
	
	/** Vrne konkreten (nepoimenovani) tip.
	 * 
	 * @return Konkreten (nepoimenovani) tip.
	 */
	public abstract SemType actualType();
	
	/** Vrne velikost tega podatkovnega tipa v bytih.
	 * 
	 * @return Velikost tega podatkovnega tipa.
	 */
	public abstract int size();
	
}
