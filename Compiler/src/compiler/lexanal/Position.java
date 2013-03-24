package compiler.lexanal;

import java.io.*;

import compiler.*;

public class Position implements XMLable {

	private String filename;

	private int begLine;

	private int begColumn;

	private int endLine;

	private int endColumn;

	public Position(String filename, int begLine, int begColumn, int endLine,
			int endColumn) {
		this.filename = filename;
		this.begLine = begLine;
		this.begColumn = begColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
	}

	public Position(String filename) {
		this.filename = filename;
	}

	public void SetStart(int begLine, int begColumn) {
		this.begLine = begLine;
		this.begColumn = begColumn;
	}

	public void SetEnd(String simbol) {
		this.endColumn = this.begColumn + simbol.length() - 1;
		this.endLine = this.begLine;
	}
	
	@Override
	public boolean equals(Object obj) {
		Position objPosition = (Position)obj;
		if(objPosition != null)
		{
			if(begColumn == objPosition.begColumn && begLine == objPosition.begLine && endLine == objPosition.endLine && endColumn == objPosition.endColumn)
				return true;
			else return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public void toXML(PrintStream xml) {
		xml.println("<position filename=\"" + filename + "\" begLine=\""
				+ begLine + "\" begColumn=\"" + begColumn + "\" endLine=\""
				+ endLine + "\" endColumn=\"" + endColumn + "\"/>");
	}

	@Override
	public String toString() {
		return "BegLine=" + begLine + " BegColumn=" + begColumn + " EndLine ="
				+ endLine + " EndColumn=" + endColumn;
	}
}
