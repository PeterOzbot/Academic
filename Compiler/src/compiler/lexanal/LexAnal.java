package compiler.lexanal;

import java.io.*;

/** Leksikalni analizator.  */
public class LexAnal {

	/** Ustvari nov leksikalni analizator.  */
	public LexAnal() {
		// TODO
	}

	/** Odpre datoteko z izvorno kodo programa.
	 * 
	 * @param programName ime datoteke z izvorno kodo programa.
	 * @throws IOException Ce datoteke z izvorno kodo programa ni mogoce odpreti.
	 */
	public void openSourceFile(String programName) throws IOException {
		// TODO
	}
	
	/** Zapre datoteko z izvorno kodo programa.
	 * 
	 * @throws IOException Ce datoteke z izvorno kodo programa ni mogoce zapreti.
	 */
	public void closeSourceFile() throws IOException {
		// TODO
	}
	
	/** Vrne naslednji osnovni simbol.
	 * 
	 * @return Naslednji osnovni simbol ali <code>null</code> ob koncu datoteke.
	 * @throws IOException Ce je prislo do napake pri branju vhodne datoteke.
	 */
	public Symbol getNextSymbol() throws IOException {
		// TODO
		return null;
	}
	
}
