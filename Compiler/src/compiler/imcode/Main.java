package compiler.imcode;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.synanal.*;
import compiler.abstree.*;

/** Izvede prevajanje do faze generiranja kode.  */
public class Main {

	public static void main(String programName) {
		try {
			PrintStream xml = XML.open("imcode");
			{
				LexAnal lexer = new LexAnal();
				lexer.openSourceFile(programName);
				
				SynAnal parser = new SynAnal(lexer, null);
				AbsTree absTree = parser.parse();
				absTree.accept(new compiler.semanal.ConstExprEvaluator());
				absTree.accept(new compiler.semanal.DeclarationResolver());
				absTree.accept(new compiler.semanal.TypeResolver());
				absTree.accept(new compiler.frames.FrameResolver());
				absTree.accept(new compiler.imcode.CodeGenerator());
				absTree.accept(new compiler.imcode.PrintXML(xml));
				lexer.closeSourceFile();
			}
			XML.close("imcode", xml);
		} catch (IOException exception) {
			Report.error("Cannot compute.", 1);
		}
	}

}
