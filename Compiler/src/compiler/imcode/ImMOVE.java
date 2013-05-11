package compiler.imcode;

import java.io.*;

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
	
}
