package compiler.synanal;

import java.io.*;

import compiler.*;
import compiler.lexanal.*;

/** Sintaksni analizator. */
public class SynAnal {
	// nastavitve
	private final Boolean REPORT_SKIP = false;
	
	private LexAnal lexer;

	private Symbol symbol;

	/** Ustvari nov sintaksni analizator. */
	public SynAnal(LexAnal lexer, PrintStream xml) {
		this.lexer = lexer;
		this.xml = xml;
	}

	/**
	 * Opravi sintaksno analizo.
	 * 
	 * @throws IOException
	 *             Ce je prislo do napake pri branju vhodne datoteke.
	 */
	public void parse() throws IOException {
		symbol = lexer.getNextSymbol();

		try {
			parseSource();
			if (symbol != null)
				throw new ParseException();
		} catch (ParseException exception) {
			Report.error("Syntax error.", -1);
		}
	}

	private void parseSource() throws IOException, ParseException {
		debug("source");
		parseExpressions();
		debug();
	}

	private void parseExpressions() throws IOException, ParseException {
		debug("parseExpressions");

		parseExpression();

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.COMMA:
			skip(Symbol.COMMA);
			parseExpressions();
			break;
		case -1:
			break;// konec
		default:
			throw new ParseException();
		}
		debug();
	}

	private void parseExpression() throws IOException, ParseException {
		debug("parseExpression");

		parseOrExpression();

		debug();
	}

	private void parseOrExpression() throws IOException, ParseException {
		debug("parseOrExpression");

		parseAndExpression();

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.OR:
			skip(Symbol.OR);
			parseOrExpression();
			break;
		}

		debug();
	}

	private void parseAndExpression() throws IOException, ParseException {
		debug("parseAndExpression");

		parseRelationalExpression();

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.AND:
			skip(Symbol.AND);
			parseAndExpression();
			break;
		}

		debug();
	}

	private void parseRelationalExpression() throws IOException, ParseException {
		debug("parseRelationalExpression");

		parseAdditiveExpression();

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.EQU:
			skip(Symbol.EQU);
			parseRelationalExpression();
			break;
		case Symbol.NEQ:
			skip(Symbol.NEQ);
			parseRelationalExpression();
			break;
		case Symbol.LTH:
			skip(Symbol.LTH);
			parseRelationalExpression();
			break;

		case Symbol.GTH:
			skip(Symbol.GTH);
			parseRelationalExpression();
			break;

		case Symbol.LEQ:
			skip(Symbol.LEQ);
			parseRelationalExpression();
			break;

		case Symbol.GEQ:
			skip(Symbol.GEQ);
			parseRelationalExpression();
			break;
		}

		debug();
	}

	private void parseAdditiveExpression() throws IOException, ParseException {
		debug("additive_expression");
		parseMultiplicativeExpression();
		parseAdditiveExpressionRest();
		debug();
	}

	private void parseAdditiveExpressionRest() throws IOException,
			ParseException {
		debug("additive_expression'");
		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.ADD:
			skip(Symbol.ADD);
			parseMultiplicativeExpression();
			parseAdditiveExpressionRest();
			break;
		case Symbol.SUB:
			skip(Symbol.SUB);
			parseMultiplicativeExpression();
			parseAdditiveExpressionRest();
			break;
		default:
			break;
		}
		debug();
	}

	private void parseMultiplicativeExpression() throws IOException,
			ParseException {
		debug("parseMultiplicativeExpression");

		parsePrefixExpression();

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.MUL:
			skip(Symbol.MUL);
			parseMultiplicativeExpression();
			break;
		case Symbol.DIV:
			skip(Symbol.DIV);
			parseMultiplicativeExpression();
			break;
		case Symbol.MOD:
			skip(Symbol.MOD);
			parseMultiplicativeExpression();
			break;
		}
		debug();
	}

	private void parsePrefixExpression() throws IOException, ParseException {
		debug("parsePrefixExpression");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.ADD:
			skip(Symbol.ADD);
			parsePrefixExpression();
			break;
		case Symbol.SUB:
			skip(Symbol.SUB);
			parsePrefixExpression();
			break;
		case Symbol.MUL:
			skip(Symbol.MUL);
			parsePrefixExpression();
			break;
		case Symbol.NOT:
			skip(Symbol.NOT);
			parsePrefixExpression();
			break;
		case Symbol.AND:
			skip(Symbol.AND);
			parsePrefixExpression();
			break;
		default:
			parsePostfixExpression();
			break;
		}

		debug();
	}

	private void parsePostfixExpression() throws IOException, ParseException {
		debug("parsePostfixExpression");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.INTCONST:
			skip(Symbol.INTCONST);
			break;
		case Symbol.REALCONST:
			skip(Symbol.REALCONST);
			break;
		case Symbol.BOOLCONST:
			skip(Symbol.BOOLCONST);
			break;
		case Symbol.STRINGCONST:
			skip(Symbol.STRINGCONST);
			break;
		case Symbol.LBRACE:
			skip(Symbol.LBRACE);
			parsePostfixExpressionBrace();
			skip(Symbol.RBRACE);
			break;
		case Symbol.IDENTIFIER:
			skip(Symbol.IDENTIFIER);
			if (Symbol.LPARENT == (symbol != null ? symbol.getToken() : -1)) {
				skip(Symbol.LPARENT);
				parseExpression();
				skip(Symbol.RPARENT);
			}
			break;
		case Symbol.LPARENT:
			skip(Symbol.LPARENT);
			parseExpression();
			skip(Symbol.RPARENT);
			break;
		}
		
		parsePostfixExpressionAddon();
		
		debug();
	}

	private void parsePostfixExpressionAddon() throws IOException,
			ParseException {
		debug("parsePostfixExpression");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.DOT:
			skip(Symbol.DOT);
			skip(Symbol.IDENTIFIER);
			break;
		case Symbol.LBRACKET:
			skip(Symbol.LBRACKET);
			parseExpression();
			skip(Symbol.RBRACKET);
			break;
		case Symbol.WHERE:
			skip(Symbol.WHERE);
			parseDeclarations();
			break;
		}

		debug();
	}

	private void parsePostfixExpressionBrace() throws IOException,
			ParseException {
		debug("parsePostfixExpressionBrace");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.IDENTIFIER:
			skip(Symbol.IDENTIFIER);
			skip(Symbol.ASSIGN);
			parseExpression();
			break;
		case Symbol.IF:
			skip(Symbol.IF);
			parseExpression();
			skip(Symbol.THEN);
			parseExpression();
			if (Symbol.ELSE == (symbol != null ? symbol.getToken() : -1)) {
				skip(Symbol.ELSE);
				parseExpression();
			}
			break;
		case Symbol.FOR:
			skip(Symbol.FOR);
			skip(Symbol.IDENTIFIER);
			skip(Symbol.ASSIGN);
			parseExpression();
			skip(Symbol.COMMA);
			parseExpression();
			skip(Symbol.COLON);
			parseExpression();
			break;
		case Symbol.WHILE:
			skip(Symbol.WHILE);
			parseExpression();
			skip(Symbol.COLON);
			parseExpression();
			break;
		}

		debug();
	}

	private void parseDeclarations() throws IOException, ParseException {
		debug("parseDeclarations");

		parseDeclaration();

		switch (symbol != null ? symbol.getToken() : -1) {
		case -1:
			break;
		case Symbol.VAR:
		case Symbol.TYP:
		case Symbol.FUN:
			parseDeclarations();
			break;
		}

		debug();
	}

	private void parseDeclaration() throws IOException, ParseException {
		debug("Declaration");
		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.VAR:
			parseVariableDeclaration();
			break;
		case Symbol.TYP:
			parseTypeDeclaration();
			break;
		case Symbol.FUN:
			parseFunctionDeclaration();
			break;
		default:
			throw new ParseException();
		}
		debug();
	}

	private void parseFunctionDeclaration() throws IOException, ParseException {
		debug("FunctionDeclaration");
		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.FUN:
			skip(Symbol.FUN);
			skip(Symbol.IDENTIFIER);
			skip(Symbol.LPARENT);
			parseFunctionParameters();
			skip(Symbol.RPARENT);
			skip(Symbol.COLON);
			parseType();
			skip(Symbol.ASSIGN);
			parseExpression();
			skip(Symbol.SEMIC);
			break;
		default:
			throw new ParseException();
		}
		debug();
	}
	
	private void parseFunctionParameters() throws IOException, ParseException {
		debug("FunctionParameters");

		parseFunctionParameter();

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.COMMA:
			skip(Symbol.COMMA);
			parseRecordComponents();
			break;
		}
		debug();
	}

	private void parseFunctionParameter() throws IOException, ParseException {
		debug("FunctionParameter");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.IDENTIFIER:
			skip(Symbol.IDENTIFIER);
			skip(Symbol.COLON);
			parseType();
			break;
		default:
			throw new ParseException();
		}
		debug();
	}
	
	private void parseTypeDeclaration() throws IOException, ParseException {
		debug("TypeDeclaration");
		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.TYP:
			skip(Symbol.TYP);
			skip(Symbol.IDENTIFIER);
			skip(Symbol.COLON);
			parseType();
			skip(Symbol.SEMIC);
			break;
		default:
			throw new ParseException();
		}
		debug();
	}
	
	private void parseVariableDeclaration() throws IOException, ParseException {
		debug("variable_declaration");
		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.VAR:
			skip(Symbol.VAR);
			skip(Symbol.IDENTIFIER);
			skip(Symbol.COLON);
			parseType();
			skip(Symbol.SEMIC);
			break;
		default:
			throw new ParseException();
		}
		debug();
	}

	private void parseType() throws IOException, ParseException {
		debug("type");
		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.INT:
			skip(Symbol.INT);
			break;
		case Symbol.REAL:
			skip(Symbol.REAL);
			break;
		case Symbol.BOOL:
			skip(Symbol.BOOL);
			break;
		case Symbol.STRING:
			skip(Symbol.STRING);
			break;
		case Symbol.LBRACE:
			skip(Symbol.LBRACE);
			skip(Symbol.RBRACE);
			break;
		case Symbol.IDENTIFIER:
			skip(Symbol.IDENTIFIER);
			break;
		case Symbol.MUL:
			skip(Symbol.MUL);
			parseType();
			break;
		case Symbol.ARR:
			skip(Symbol.ARR);
			skip(Symbol.LBRACKET);
			parseExpression();
			skip(Symbol.RBRACKET);
			parseType();
			break;
		case Symbol.REC:
			skip(Symbol.REC);
			skip(Symbol.LPARENT);
			parseRecordComponents();
			skip(Symbol.RPARENT);
			break;
		case Symbol.LPARENT:
			skip(Symbol.LPARENT);
			parseType();
			skip(Symbol.RPARENT);
			break;
		default:
			throw new ParseException();
		}
		debug();
	}

	private void parseRecordComponents() throws IOException, ParseException {
		debug("RecordComponents");

		parseRecordComponent();

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.COMMA:
			skip(Symbol.COMMA);
			parseRecordComponents();
			break;
		}
		debug();
	}

	private void parseRecordComponent() throws IOException, ParseException {
		debug("RecordComponent");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.IDENTIFIER:
			skip(Symbol.IDENTIFIER);
			skip(Symbol.COLON);
			parseType();
			break;
		default:
			throw new ParseException();
		}
		debug();
	}

	private Symbol skip(int token) throws IOException, ParseException {
		if (symbol == null)
			throw new ParseException();
		if (symbol.getToken() != token)
			throw new ParseException();
		
		// sporoci skip
		if (REPORT_SKIP)
			Report.information("Preverjamo simbol: " + symbol.getLexeme(), symbol.getPosition());
		
		// za debugat - lazje dobit kjer prid do napake
		//if(symbol.getPosition().equals(new Position("",6,17,6,17)))
		//{
		//	int a = 2;
		//}
		
		Symbol skippedSymbol = symbol;
		if (xml != null)
			skippedSymbol.toXML(xml);
		symbol = lexer.getNextSymbol();
		return skippedSymbol;
	}

	private PrintStream xml;

	private void debug(String nonterminal) {
		if (xml != null) {
			xml.println("<production>");
			xml.println("<leftside nonterminal=\"" + nonterminal + "\"/>");
			xml.println("<rightside>");
		}
	}

	private void debug() {
		if (xml != null) {
			xml.println("</rightside>");
			xml.println("</production>");
		}
	}
}
