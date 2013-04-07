package compiler.semanal;

import java.util.*;

import compiler.*;
import compiler.abstree.*;
import compiler.lexanal.*;

/** Simbolna tabela.  */
public class SymbolTable {

	/** Simbolna tabela.  */
	private HashMap<String, Stack<AbsDecl>> namesByDecls;
	
	/**	Globina gnezdenja.  */
	@SuppressWarnings("unused")
	private int scope;
	
	/** Imena po nivojih gnezdenja.  */
	private Stack<HashSet<String>> namesByScope;
	
	/** Ustvari novo prazno simbolno tabelo.  */
	public SymbolTable() {
		namesByDecls = new HashMap<String, Stack<AbsDecl>>();
		namesByScope = new Stack<HashSet<String>>();
		scope = 0;
		newScope();
		
		// Standardna knjiznica.
		{
			try {
				Symbol identifier = new Symbol(Symbol.IDENTIFIER, "putInt", new Position("", 0, 0, 0, 0));
				AbsFunDecl fun = new AbsFunDecl(new AbsExprName(identifier), null, null, null);
				fun.setMin(identifier); fun.setMax(identifier);
				ins("putInt", fun);
			} catch (DeclarationAlreadyExistsException _) {
				Report.error("Internal error.", 1);
			}
		}
		{
			try {
				Symbol identifier = new Symbol(Symbol.IDENTIFIER, "getInt", new Position("", 0, 0, 0, 0));
				AbsFunDecl fun = new AbsFunDecl(new AbsExprName(identifier), null, null, null);
				fun.setMin(identifier); fun.setMax(identifier);
				ins("getInt", fun);
			} catch (DeclarationAlreadyExistsException _) {
				Report.error("Internal error.", 1);
			}
		}
		
		newScope();
	}
	
	/** Ustvari nov nivo gnezdenja.  */
	public void newScope() {
		scope++;
		namesByScope.push(new HashSet<String>());
	}
	
	/** Razveljavi trenutni nivo gnezdenja.  */
	public void oldScope() {
		try {
			for (String name: namesByScope.pop()) {
				Stack<AbsDecl> decls = namesByDecls.get(name);
				decls.pop();
				if (decls.isEmpty()) namesByDecls.remove(name);
			}
			scope--;
		} catch (EmptyStackException _) {
			Report.error("Internal error.", 1);
		}
	}
	
	/** Vstavi novo deklaracijo imena v simbolno tabelo.
	 * 
	 * @param name Ime, za katerega bo vstavljena deklaracija.
	 * @param decl Nova deklaracija imena na trenutnem nivoju gnezdenja.
	 * @throws DeclarationAlreadyExistsException Ce deklaracija tega imena na trenutnem nivoju gnezdenja ze obstaja.
	 */
	public void ins(String name, AbsDecl decl) throws DeclarationAlreadyExistsException {
		if (namesByScope.peek().contains(name)) throw new DeclarationAlreadyExistsException();
		Stack<AbsDecl> decls = namesByDecls.get(name);
		if (decls == null) {
			decls = new Stack<AbsDecl>();
			namesByDecls.put(name, decls);
		}
		decls.push(decl);
		namesByScope.peek().add(name);
	}
	
	/** Vrne deklaracijo imena iz simbolne tabele.
	 * 
	 * @param name Ime, za katerega bo vrnjena deklaracija.
	 * @return Deklaracija imena.
	 * @throws DeclarationDoesNotExistsException Ce deklaracija tega imena ne obstaja.
	 */
	public AbsDecl fnd(String name) throws DeclarationDoesNotExistException {
		Stack<AbsDecl> decls = namesByDecls.get(name);
		if (decls == null) throw new DeclarationDoesNotExistException();
		return decls.peek();
	}

}
