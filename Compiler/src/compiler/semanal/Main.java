package compiler.semanal;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.synanal.*;
import compiler.abstree.*;

/** Izvede prevajanje do faze semanticne analize.  */
public class Main {

	public static void main(String programName) {
		try {
			PrintStream xml = XML.open("semanal");
			{
				LexAnal lexer = new LexAnal();
				lexer.openSourceFile(programName);
				
				SynAnal parser = new SynAnal(lexer, null);
				AbsTree absTree = parser.parse();
				absTree.accept(new compiler.semanal.ConstExprEvaluator());
				absTree.accept(new compiler.semanal.DeclarationResolver());
				absTree.accept(new compiler.semanal.PrintXML(xml));
					
				lexer.closeSourceFile();
			}
			XML.close("semanal", xml);
		} catch (IOException exception) {
			Report.error("Cannot perform semantic analysis.", 1);
		}
	}

}
