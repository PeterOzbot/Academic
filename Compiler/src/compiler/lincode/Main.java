package compiler.lincode;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.synanal.*;
import compiler.abstree.*;

/** Izvede prevajanje do faze izracuna linearizirane kode.  */
public class Main {

	public static void main(String programName) {
		try {
			PrintStream xml = XML.open("lincode");
			{
				LexAnal lexer = new LexAnal();
				lexer.openSourceFile(programName);
				
				SynAnal parser = new SynAnal(lexer, null);
				AbsTree absTree = parser.parse();
				absTree.accept(new compiler.semanal.DeclarationResolver());
				absTree.accept(new compiler.semanal.TypeResolver());
				absTree.accept(new compiler.frames.FrameResolver());
				absTree.accept(new compiler.imcode.CodeGenerator());
				absTree.accept(new compiler.lincode.CodeGenerator());
				absTree.accept(new compiler.lincode.PrintXML(xml));
				lexer.closeSourceFile();
			}
			XML.close("lincode", xml);
		} catch (IOException exception) {
			Report.error("Cannot compute.", 1);
		}
	}

}
