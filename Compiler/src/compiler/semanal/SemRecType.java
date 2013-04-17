package compiler.semanal;

import java.io.*;
import java.util.*;

/** Opis zapisov.  */
public class SemRecType extends SemType {

	/** Imena posameznih komponent.  */
	public final Vector<String> compNames;
	
	/** Tipi posameznih komponent.  */
	public final Vector<SemType> compTypes;
	
	public SemRecType() {
		compNames = new Vector<String>();
		compTypes = new Vector<SemType>();
	}

	@Override
	public void toXML(PrintStream xml) {
		xml.println("<semtype type=\"REC\">");
		for (SemType type: compTypes) type.toXML(xml);
		xml.println("</semtype>");
	}
	
	@Override
	public SemType actualType() {
		return this;
	}

	@Override
	public int size() {
		int size = 0;
		for (SemType type: compTypes) size = size + type.size();
		return size;
	}

}
