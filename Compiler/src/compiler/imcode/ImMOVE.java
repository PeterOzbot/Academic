package compiler.imcode;

import java.io.*;

import compiler.frames.Temp;

/** Prenos vrednosti:
 * 
 * Levi podizraz mora biti bodisi MEM bodisi TEMP.
 * (*) Ce je levi podizraz MEM, prenese vrednost desnega podizraza
 * v pomnilnisko celico, ki jo doloca vrednost podizraza pod MEM.
 * (*) Ce je levi podizraz TEMP, prenese vrednost desnega podizraza
 * v zacasno spremenljivko.
 * V obeh primerih vrne vrednost desnega podizraza.
 */
public class ImMOVE extends ImCode {

	public final ImCode dst;
	
	public final ImCode src;
	
	public ImMOVE(ImCode dst, ImCode src) {
		this.dst = dst;
		this.src = src;
	}
	
	@Override
	public void toXML(PrintStream xml) {
		xml.println("<iminstruction instr=\"MOVE\">");
		dst.toXML(xml);
		src.toXML(xml);
		xml.println("</iminstruction>");
	}
	
	@Override
	public void linearCode() {
		if (linearCode != null) return;
		if (dst instanceof ImMEM) {
			linearCode = new ImSEQ();
			
			dst.linearCode();
			linearCode.codes.addAll(((ImMEM) dst).expr.linearCode.codes);

			src.linearCode();
			linearCode.codes.addAll(src.linearCode.codes);
			linearCode.codes.add(new ImMOVE(new ImMEM(new ImTEMP(((ImMEM) dst).expr.linearCodeResult)), new ImTEMP(src.linearCodeResult)));
			
			linearCodeResult = src.linearCodeResult;
		}
		if (dst instanceof ImTEMP) {
			linearCode = new ImSEQ();
			
			src.linearCode();
			Temp srcTemp = new Temp();
			linearCode.codes.addAll(src.linearCode.codes);
			linearCode.codes.add(new ImMOVE(dst, new ImTEMP(src.linearCodeResult)));
			
			linearCodeResult = srcTemp;			
		}
	}
}
