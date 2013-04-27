package compiler.frames;

import java.io.*;

import compiler.*;

/** Opis klicnega zapisa.  */
public class Frame implements XMLable {
	
	/** Labela funkcije. */
	public Label label;
	
	/** Staticni nivo funkcije tega zapisa.  */
	public int level;
	
	/** Odmik za shranjevanje FPja. */
	public int oldFPoffset;
	
	/** Odmik za shranjevanje povratnega naslova. */
	public int oldRAoffset;
	
	/** Velikost izhodnih argumentov. */
	private int outArgsSize;
	
	/** Velikost klicnega zapisa. */
	public int size;
	
	public Frame(String name, int level) {
		this.label = new Label(name);
		this.level = level;
		oldFPoffset = -4;
		oldRAoffset = -8;
		outArgsSize = 0;
		size = 8;
	}
	
	/** Doda novo spremenljivko v klicni zapis in vrne njen odmik.
	 * 
	 * @param size Velikost nove spremenljivke v bytih.
	 * @return Odmik nove spremenljivke v bytih.
	 */
	public int addVariable(int size) {
		int offset = this.size - 8;
		oldFPoffset -= size;
		oldRAoffset -= size;
		this.size += size;
		return offset;
	}
	
	public void ensureOutArgsSize(int outArgsSize) {
		if (outArgsSize <= this.outArgsSize) return;
		int diff = outArgsSize - this.outArgsSize;
		this.outArgsSize += diff;
		this.size += diff;
	}
	
	public void toXML(PrintStream xml) {
		xml.print("<frmframe label=\"" + label.label + "\" level=\"" + level + "\" fp=\"" + oldFPoffset + "\" ra=\"" + oldRAoffset + "\" size=\"" + size + "\"/>");
	}

}
