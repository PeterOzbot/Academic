package compiler.frames;

import java.io.*;

import compiler.*;

/** Opis klicnega zapisa. */
public class Frame implements XMLable {

	/** Labela funkcije. */
	public Label label;

	/** Staticni nivo funkcije tega zapisa. */
	private int _level;

	public void setLevel(int level) {
		_level = level;
	}

	public int getLevel() {
		return _level;
	}

	/** Odmik za shranjevanje FPja. */
	public int oldFPoffset;

	/** Odmik za shranjevanje povratnega naslova. */
	public int oldRAoffset;

	/** Velikost izhodnih argumentov. */
	private int outArgsSize;

	/** Velikost klicnega zapisa. */
	private int _size;

	// pozicija zadnje lokalne spremenljivke
	private int _localVariableSize;

	public void setSize(int size) {
		_size = size;
	}

	public int getSize() {
		return _size;
	}

	public Frame(String name, int level) {
		this.label = new Label(name);
		this._level = level;
		oldFPoffset = -4;
		oldRAoffset = -8;
		outArgsSize = 0;
		_size = 8;
		_localVariableSize = 0;
	}

	/**
	 * Doda novo spremenljivko v klicni zapis in vrne njen odmik.
	 * 
	 * @param size
	 *            Velikost nove spremenljivke v bytih.
	 * @return Odmik nove spremenljivke v bytih.
	 */
	public int addVariable(int size) {
		int offset = _localVariableSize = _localVariableSize - size;
		oldFPoffset -= size;
		oldRAoffset -= size;
		this._size += size;
		return offset;
	}

	public void ensureOutArgsSize(int outArgsSize) {
		if (outArgsSize <= this.outArgsSize)
			return;
		int diff = outArgsSize - this.outArgsSize;
		this.outArgsSize += diff;
		this._size += diff;
	}

	public void toXML(PrintStream xml) {
		xml.print("<frmframe label=\"" + label.label + "\" level=\"" + _level
				+ "\" fp=\"" + oldFPoffset + "\" ra=\"" + oldRAoffset
				+ "\" size=\"" + _size + "\"/>");
	}

}
