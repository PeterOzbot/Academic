package compiler;

import compiler.lexanal.*;

public class Report {

	/** Izpise opozorilo.  */
	public static void warning(String msg) {
		System.err.println(":-o " + msg);
	}

	/** Izpise opozorilo, ki je vezano na del vhodne datoteke.  */
	public static void warning(String msg, Position position) {
		System.err.println(":-o " + position + " " + msg);
	}

	/** Izpise obvestilo o napaki in konca izvajanje programa.  */
	public static void error(String msg, int exitCode) {
		System.err.println(":-( " + msg);
		Thread.dumpStack();
		System.exit(exitCode);
	}
	
	/** Izpise obvestilo o napaki, ki je vezano na del vhodne datoteke, in konca izvajanje programa.  */
	public static void error(String msg, Position position, int exitCode) {
		System.err.println(":-( " + position + " " + msg);
		Thread.dumpStack();
		System.exit(exitCode);
	}
	
	/** Izpise informacijo o dogajanju */
	public static void information(String msg, Position position) {
		System.out.println(":-| " + position + " " + msg );
	}
}
