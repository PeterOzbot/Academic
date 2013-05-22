package compiler.frames;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.semanal.ConstExprEvaluator;
import compiler.synanal.*;
import compiler.abstree.*;

/** Izvede prevajanje do faze izracuna klicnih zapisov.  */
public class Main {

	public static void main(String programName) {
		try {
			PrintStream xml = XML.open("frames");
			{
				LexAnal lexer = new LexAnal();
				lexer.openSourceFile(programName);
				
				SynAnal parser = new SynAnal(lexer, null);
				AbsTree absTree = parser.parse();
				absTree.accept(new ConstExprEvaluator());
				absTree.accept(new compiler.semanal.DeclarationResolver());
				absTree.accept(new compiler.semanal.TypeResolver());
				absTree.accept(new compiler.frames.FrameResolver());
				absTree.accept(new compiler.frames.PrintXML(xml));
				lexer.closeSourceFile();
			}
			XML.close("frames", xml);
		} catch (IOException exception) {
			Report.error("Cannot compute.", 1);
		}
	}

}
