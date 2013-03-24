package compiler.synanal;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;

/** Izvede prevajanje do faze sintaksne analize. */
public class Main {

	public static void main(String programName) {
		try {
			PrintStream xml = XML.open("synanal");
			{
				LexAnal lexer = new LexAnal();
				lexer.openSourceFile(programName);
				
				SynAnal parser = new SynAnal(lexer, xml);
				parser.parse();
					
				lexer.closeSourceFile();
			}
			XML.close("synanal", xml);
		} catch (IOException exception) {
			Report.error("Cannot perform syntax analysis.", 1);
		}
	}

}