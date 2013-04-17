package compiler.semanal;

import java.io.*;
import java.util.*;

import compiler.*;

/** Ime tipa.  */
public class SemTypeName extends SemType {
	
	/** Ime, ki opisuje tip.  */
	public final String name;
	
	/** Vozlisce razreda <code>SemType</code>,
	 *  ki ustreza definiciji imena <code>identifier</code>.
	 */
	public SemType namedType = null;
	
	public SemTypeName(String identifier) {
		this.name = identifier;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<semtype type=\"NAME\" value=\"" + name + "\"/>");
	}

	@Override
	public SemType actualType() {
		HashSet<SemType> types = new HashSet<SemType>();
		SemType type = this;
		do {
			if (type == null) Report.error("Internal error.", 1);
			if (type instanceof SemTypeName) {
				if (types.contains(type)) Report.error("Circular type definition.", 1);
				types.add(type);
				type = ((SemTypeName)type).namedType;
			}
		} while (type instanceof SemTypeName);
		return type;
	}

	@Override
	public int size() {
		return this.actualType().size();
	}

}
