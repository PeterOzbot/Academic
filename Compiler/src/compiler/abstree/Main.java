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
				PrintXML printXML = new PrintXML(xml);
				absTree.accept(printXML);
					
				lexer.closeSourceFile();
			}
			XML.close("abstree", xml);
		} catch (IOException exception) {
			Report.error("Cannot construct abstract syntax tree.", 1);
		}
	}

}