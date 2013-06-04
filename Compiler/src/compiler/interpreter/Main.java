package compiler.interpreter;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;
import compiler.synanal.*;
import compiler.abstree.*;
import compiler.frames.FrameResolver;

/** Izvede prevajanje in izvede vmesno kodo. */
public class Main {

	public static void main(String programName) {
		try {
			AbsTree absTree;
			compiler.frames.FrameResolver frameResolver = null;
			PrintStream xml = XML.open("lincode");
			{
				LexAnal lexer = new LexAnal();
				lexer.openSourceFile(programName);
				SynAnal parser = new SynAnal(lexer, null);
				absTree = parser.parse();
				absTree.accept(new compiler.semanal.ConstExprEvaluator());
				absTree.accept(new compiler.semanal.DeclarationResolver());
				absTree.accept(new compiler.semanal.TypeResolver());
				frameResolver = new compiler.frames.FrameResolver();
				absTree.accept(frameResolver);
				absTree.accept(new compiler.imcode.CodeGenerator());
				absTree.accept(new compiler.lincode.CodeGenerator());
				absTree.accept(new compiler.lincode.PrintXML(xml));
				lexer.closeSourceFile();
			}
			XML.close("lincode", xml);

			new Interpreter(frameResolver.mainFrame,
					compiler.imcode.CodeGenerator.getCode(absTree).linearCode);
		} catch (IOException exception) {
			Report.error("Cannot compute.", 1);
		}
	}

}
