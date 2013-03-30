package compiler.abstree;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.synanal.*;

/** Izvede prevajanje do faze izgradnje sintaksnega drevesa.  */
public class Main {

	public static void main(String programName) {
		try {
			PrintStream xml = XML.open("abstree");
			{
				LexAnal lexer = new LexAnal();
				lexer.openSourceFile(programName);
				
				SynAnal parser = new SynAnal(lexer, null);
				AbsTree absTree = parser.parse();
				absTree.toXML(xml);
					
				lexer.closeSourceFile();
			}
			XML.close("abstree", xml);
		} catch (IOException exception) {
			Report.error("Cannot perform syntax analysis.", 1);
		}
	}

}