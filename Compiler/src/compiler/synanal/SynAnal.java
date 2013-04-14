package compiler.synanal;

import java.io.*;
import java.util.Vector;

import compiler.*;
import compiler.abstree.AbsArrType;
import compiler.abstree.AbsAssignStmt;
import compiler.abstree.AbsAtomExpr;
import compiler.abstree.AbsAtomType;
import compiler.abstree.AbsBinExpr;
import compiler.abstree.AbsDecl;
import compiler.abstree.AbsDecls;
import compiler.abstree.AbsExpr;
import compiler.abstree.AbsExprName;
import compiler.abstree.AbsExprs;
import compiler.abstree.AbsForStmt;
import compiler.abstree.AbsFunCall;
import compiler.abstree.AbsFunDecl;
import compiler.abstree.AbsIfStmt;
import compiler.abstree.AbsPtrType;
import compiler.abstree.AbsRecType;
import compiler.abstree.AbsTree;
import compiler.abstree.AbsTypDecl;
import compiler.abstree.AbsType;
import compiler.abstree.AbsTypeName;
import compiler.abstree.AbsUnExpr;
import compiler.abstree.AbsVarDecl;
import compiler.abstree.AbsWhereExpr;
import compiler.abstree.AbsWhileStmt;
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
	public AbsTree parse() throws IOException {
		symbol = lexer.getNextSymbol();
		AbsTree abstraktnoDrevo = null;
		try {
			// zacne parsat vse source
			abstraktnoDrevo = parseSource();
			if (symbol != null)
				throw new ParseException();
		} catch (ParseException exception) {
			Report.error("Syntax error.", -1);
		}
		return abstraktnoDrevo;
	}

	private AbsTree parseSource() throws IOException, ParseException {
		AbsTree abstraktnoDrevo = null;

		debug("source");

		// zacne parsat celoten source kot Expressions
		abstraktnoDrevo = parseExpressions();

		debug();

		return abstraktnoDrevo;
	}

	private AbsExprs parseExpressions() throws IOException, ParseException {
		debug("parse_Expressions");

		AbsExprs seznamIzrazov = null;

		Vector<AbsExpr> vectorIzrazov = new Vector<AbsExpr>();

		// parsa prvi Expression
		vectorIzrazov.add(parseExpression());

		// parsa ostale Expressions ce obstajajo
		vectorIzrazov = parseExpressionsRest(vectorIzrazov);

		seznamIzrazov = new AbsExprs(vectorIzrazov);

		// set min set max
		seznamIzrazov.setMax(vectorIzrazov.firstElement());
		seznamIzrazov.setMin(vectorIzrazov.lastElement());

		debug();

		return seznamIzrazov;
	}

	private Vector<AbsExpr> parseExpressionsRest(Vector<AbsExpr> vectorIzrazov)
			throws IOException, ParseException {
		debug("parse_Expressions'");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.COMMA:
			skip(Symbol.COMMA);
			// v primeru vejice parsa naslednji Expression
			vectorIzrazov.add(parseExpression());
			// zacne parsat ostale Expressions
			// ce jih ni nebo nic naredila ta metoda in se bo koncalo
			vectorIzrazov = parseExpressionsRest(vectorIzrazov);
			break;
		}

		debug();

		return vectorIzrazov;
	}

	private AbsExpr parseExpression() throws IOException, ParseException {
		debug("parse_Expression");

		AbsExpr izraz = null;
		// parsa OrExpression
		izraz = parseOrExpression();

		debug();

		return izraz;
	}

	private AbsExpr parseOrExpression() throws IOException, ParseException {
		debug("parse_OrExpression");

		AbsExpr izrazBinarniOperatorOr = null;

		// parsa AndExpression
		izrazBinarniOperatorOr = parseAndExpression();

		// parsa ostali del OrExpression
		izrazBinarniOperatorOr = parseOrExpressionRest(izrazBinarniOperatorOr);

		debug();

		return izrazBinarniOperatorOr;
	}

	private AbsExpr parseOrExpressionRest(AbsExpr izrazBinarniOperatorOr)
			throws IOException, ParseException {
		debug("parse_OrExpression'");

		AbsExpr lastIzrazBinarniOperatorOr = izrazBinarniOperatorOr;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.OR:
			skip(Symbol.OR);
			// ce je | parsa drugi cel AndExpression
			AbsExpr izrazBinarniOperatorAnd = parseAndExpression();

			// zdruzi prvi del in parsan drugi del
			izrazBinarniOperatorOr = new AbsBinExpr(AbsBinExpr.OR,
					izrazBinarniOperatorOr, izrazBinarniOperatorAnd);

			// set min/max
			izrazBinarniOperatorOr.setMin(lastIzrazBinarniOperatorOr);
			izrazBinarniOperatorOr.setMax(izrazBinarniOperatorAnd);

			// parsa ostali del OrExpressio
			izrazBinarniOperatorOr = parseOrExpressionRest(izrazBinarniOperatorOr);
			break;
		}

		debug();

		return izrazBinarniOperatorOr;
	}

	private AbsExpr parseAndExpression() throws IOException, ParseException {
		debug("parse_AndExpression");

		AbsExpr izrazBinarniOperatorAnd = null;
		// parsa RelationalExpression
		izrazBinarniOperatorAnd = parseRelationalExpression();
		// ostal del RelationalExpression
		izrazBinarniOperatorAnd = parseAndExpressionRest(izrazBinarniOperatorAnd);

		debug();

		return izrazBinarniOperatorAnd;
	}

	private AbsExpr parseAndExpressionRest(AbsExpr izrazBinarniOperatorAnd)
			throws IOException, ParseException {
		debug("parse_AndExpression'");

		AbsExpr lastIzrazBinarniOperatorAnd = izrazBinarniOperatorAnd;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.AND:
			skip(Symbol.AND);
			// ce je znak & parsa drugi del RelationalExpression
			AbsExpr izrazBinarniRealational = parseRelationalExpression();
			// zdruzi
			izrazBinarniOperatorAnd = new AbsBinExpr(AbsBinExpr.AND,
					izrazBinarniOperatorAnd, izrazBinarniRealational);

			// set min/max
			izrazBinarniOperatorAnd.setMin(lastIzrazBinarniOperatorAnd);
			izrazBinarniOperatorAnd.setMax(izrazBinarniRealational);

			izrazBinarniOperatorAnd = parseAndExpressionRest(izrazBinarniOperatorAnd);
			break;
		}

		debug();

		return izrazBinarniOperatorAnd;
	}

	private AbsExpr parseRelationalExpression() throws IOException,
			ParseException {
		debug("parse_RelationalExpression");

		AbsExpr izrazBinarniRelational = null;
		// parsa AdditiveExpression
		izrazBinarniRelational = parseAdditiveExpression();
		// ostali del AdditiveExpression
		izrazBinarniRelational = parseRelationalExpressionRest(izrazBinarniRelational);

		debug();

		return izrazBinarniRelational;
	}

	private AbsExpr parseRelationalExpressionRest(AbsExpr izrazBinarniRelational)
			throws IOException, ParseException {
		debug("parse_RelationalExpression'");

		AbsExpr izrazBinarniAdditive = null;

		AbsExpr lastIzrazBinarniRelational = izrazBinarniRelational;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.EQU:
			skip(Symbol.EQU);
			izrazBinarniAdditive = parseAdditiveExpression();

			izrazBinarniRelational = new AbsBinExpr(AbsBinExpr.EQU,
					izrazBinarniRelational, izrazBinarniAdditive);

			// set min/max
			izrazBinarniRelational.setMin(lastIzrazBinarniRelational);
			izrazBinarniRelational.setMax(izrazBinarniAdditive);

			izrazBinarniRelational = parseRelationalExpressionRest(izrazBinarniRelational);
			break;
		case Symbol.NEQ:
			skip(Symbol.NEQ);
			izrazBinarniAdditive = parseAdditiveExpression();

			izrazBinarniRelational = new AbsBinExpr(AbsBinExpr.NEQ,
					izrazBinarniRelational, izrazBinarniAdditive);

			// set min/max
			izrazBinarniRelational.setMin(lastIzrazBinarniRelational);
			izrazBinarniRelational.setMax(izrazBinarniAdditive);

			izrazBinarniRelational = parseRelationalExpressionRest(izrazBinarniRelational);
			break;
		case Symbol.LTH:
			skip(Symbol.LTH);
			izrazBinarniAdditive = parseAdditiveExpression();

			izrazBinarniRelational = new AbsBinExpr(AbsBinExpr.LTH,
					izrazBinarniRelational, izrazBinarniAdditive);

			// set min/max
			izrazBinarniRelational.setMin(lastIzrazBinarniRelational);
			izrazBinarniRelational.setMax(izrazBinarniAdditive);

			izrazBinarniRelational = parseRelationalExpressionRest(izrazBinarniRelational);
			break;

		case Symbol.GTH:
			skip(Symbol.GTH);
			izrazBinarniAdditive = parseAdditiveExpression();

			izrazBinarniRelational = new AbsBinExpr(AbsBinExpr.GTH,
					izrazBinarniRelational, izrazBinarniAdditive);

			// set min/max
			izrazBinarniRelational.setMin(lastIzrazBinarniRelational);
			izrazBinarniRelational.setMax(izrazBinarniAdditive);

			izrazBinarniRelational = parseRelationalExpressionRest(izrazBinarniRelational);
			break;

		case Symbol.LEQ:
			skip(Symbol.LEQ);
			izrazBinarniAdditive = parseAdditiveExpression();

			izrazBinarniRelational = new AbsBinExpr(AbsBinExpr.LEQ,
					izrazBinarniRelational, izrazBinarniAdditive);

			// set min/max
			izrazBinarniRelational.setMin(lastIzrazBinarniRelational);
			izrazBinarniRelational.setMax(izrazBinarniAdditive);

			izrazBinarniRelational = parseRelationalExpressionRest(izrazBinarniRelational);
			break;

		case Symbol.GEQ:
			skip(Symbol.GEQ);
			izrazBinarniAdditive = parseAdditiveExpression();

			izrazBinarniRelational = new AbsBinExpr(AbsBinExpr.GEQ,
					izrazBinarniRelational, izrazBinarniAdditive);

			// set min/max
			izrazBinarniRelational.setMin(lastIzrazBinarniRelational);
			izrazBinarniRelational.setMax(izrazBinarniAdditive);

			izrazBinarniRelational = parseRelationalExpressionRest(izrazBinarniRelational);
			break;
		}

		debug();

		return izrazBinarniRelational;
	}

	private AbsExpr parseAdditiveExpression() throws IOException,
			ParseException {
		debug("additive_expression");

		AbsExpr izrazBinarniAdditive = null;

		izrazBinarniAdditive = parseMultiplicativeExpression();

		izrazBinarniAdditive = parseAdditiveExpressionRest(izrazBinarniAdditive);

		debug();

		return izrazBinarniAdditive;
	}

	private AbsExpr parseAdditiveExpressionRest(AbsExpr izrazBinarniAdditive)
			throws IOException, ParseException {
		debug("additive_expression'");

		AbsExpr izrazBinarniMulti = null;
		AbsExpr lastIzrazBinarniAdditive = izrazBinarniAdditive;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.ADD:
			skip(Symbol.ADD);
			izrazBinarniMulti = parseMultiplicativeExpression();

			izrazBinarniAdditive = new AbsBinExpr(AbsBinExpr.ADD,
					izrazBinarniAdditive, izrazBinarniMulti);

			// set min/max
			izrazBinarniAdditive.setMin(lastIzrazBinarniAdditive);
			izrazBinarniAdditive.setMax(izrazBinarniMulti);

			izrazBinarniAdditive = parseAdditiveExpressionRest(izrazBinarniAdditive);
			break;
		case Symbol.SUB:
			skip(Symbol.SUB);
			izrazBinarniMulti = parseMultiplicativeExpression();

			izrazBinarniAdditive = new AbsBinExpr(AbsBinExpr.SUB,
					izrazBinarniAdditive, izrazBinarniMulti);

			// set min/max
			izrazBinarniAdditive.setMin(lastIzrazBinarniAdditive);
			izrazBinarniAdditive.setMax(izrazBinarniMulti);

			izrazBinarniAdditive = parseAdditiveExpressionRest(izrazBinarniAdditive);
			break;
		default:
			break;
		}

		debug();

		return izrazBinarniAdditive;
	}

	private AbsExpr parseMultiplicativeExpression() throws IOException,
			ParseException {
		debug("parse_MultiplicativeExpression");

		AbsExpr izrazBinarniMulti = null;

		izrazBinarniMulti = parsePrefixExpression();

		izrazBinarniMulti = parseMultiplicativeExpressionRest(izrazBinarniMulti);

		debug();

		return izrazBinarniMulti;
	}

	private AbsExpr parseMultiplicativeExpressionRest(AbsExpr izrazBinarniMulti)
			throws IOException, ParseException {
		debug("parse_MultiplicativeExpression'");

		AbsExpr izrazUnarniPrefix = null;
		AbsExpr lastIzrazBinarniMulti = izrazBinarniMulti;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.MUL:
			skip(Symbol.MUL);

			izrazUnarniPrefix = parsePrefixExpression();

			izrazBinarniMulti = new AbsBinExpr(AbsBinExpr.MUL,
					izrazBinarniMulti, izrazUnarniPrefix);

			// set min/max
			izrazBinarniMulti.setMin(lastIzrazBinarniMulti);
			izrazBinarniMulti.setMax(izrazUnarniPrefix);

			izrazBinarniMulti = parseMultiplicativeExpressionRest(izrazBinarniMulti);
			break;
		case Symbol.DIV:
			skip(Symbol.DIV);

			izrazUnarniPrefix = parsePrefixExpression();

			izrazBinarniMulti = new AbsBinExpr(AbsBinExpr.DIV,
					izrazBinarniMulti, izrazUnarniPrefix);

			// set min/max
			izrazBinarniMulti.setMin(lastIzrazBinarniMulti);
			izrazBinarniMulti.setMax(izrazUnarniPrefix);

			izrazBinarniMulti = parseMultiplicativeExpressionRest(izrazBinarniMulti);
			break;
		case Symbol.MOD:
			skip(Symbol.MOD);

			izrazUnarniPrefix = parsePrefixExpression();

			izrazBinarniMulti = new AbsBinExpr(AbsBinExpr.MOD,
					izrazBinarniMulti, izrazUnarniPrefix);

			// set min/max
			izrazBinarniMulti.setMin(lastIzrazBinarniMulti);
			izrazBinarniMulti.setMax(izrazUnarniPrefix);

			izrazBinarniMulti = parseMultiplicativeExpressionRest(izrazBinarniMulti);
			break;
		}

		debug();

		return izrazBinarniMulti;
	}

	private AbsExpr parsePrefixExpression() throws IOException, ParseException {
		debug("parse_PrefixExpression");

		AbsExpr izrazUnarniPrefix = null;
		AbsExpr prefixExpression = null;
		Symbol starterSymbol = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.ADD:
			starterSymbol = skip(Symbol.ADD);

			prefixExpression = parsePrefixExpression();

			izrazUnarniPrefix = new AbsUnExpr(AbsUnExpr.ADD, prefixExpression);

			// set min/max
			izrazUnarniPrefix.setMin(starterSymbol);
			izrazUnarniPrefix.setMax(prefixExpression);

			break;
		case Symbol.SUB:
			starterSymbol = skip(Symbol.SUB);

			prefixExpression = parsePrefixExpression();

			izrazUnarniPrefix = new AbsUnExpr(AbsUnExpr.SUB, prefixExpression);

			// set min/max
			izrazUnarniPrefix.setMin(starterSymbol);
			izrazUnarniPrefix.setMax(prefixExpression);

			break;
		case Symbol.MUL:
			starterSymbol = skip(Symbol.MUL);

			prefixExpression = parsePrefixExpression();

			izrazUnarniPrefix = new AbsUnExpr(AbsUnExpr.MUL, prefixExpression);

			// set min/max
			izrazUnarniPrefix.setMin(starterSymbol);
			izrazUnarniPrefix.setMax(prefixExpression);

			break;
		case Symbol.NOT:
			starterSymbol = skip(Symbol.NOT);

			prefixExpression = parsePrefixExpression();

			izrazUnarniPrefix = new AbsUnExpr(AbsUnExpr.NOT, prefixExpression);

			// set min/max
			izrazUnarniPrefix.setMin(starterSymbol);
			izrazUnarniPrefix.setMax(prefixExpression);

			break;
		case Symbol.AND:
			starterSymbol = skip(Symbol.AND);

			prefixExpression = parsePrefixExpression();

			izrazUnarniPrefix = new AbsUnExpr(AbsUnExpr.AND, prefixExpression);

			// set min/max
			izrazUnarniPrefix.setMin(starterSymbol);
			izrazUnarniPrefix.setMax(prefixExpression);

			break;
		default:
			izrazUnarniPrefix = parsePostfixExpression();
			break;
		}

		debug();

		return izrazUnarniPrefix;
	}

	private AbsExpr parsePostfixExpression() throws IOException, ParseException {
		debug("parsePostfixExpression");

		AbsExpr izrazPostfix = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.INTCONST:

			izrazPostfix = new AbsAtomExpr(symbol);

			// set min/max
			izrazPostfix.setMin(symbol);
			izrazPostfix.setMax(symbol);

			skip(Symbol.INTCONST);
			break;
		case Symbol.REALCONST:

			izrazPostfix = new AbsAtomExpr(symbol);

			// set min/max
			izrazPostfix.setMin(symbol);
			izrazPostfix.setMax(symbol);

			skip(Symbol.REALCONST);
			break;
		case Symbol.BOOLCONST:

			izrazPostfix = new AbsAtomExpr(symbol);

			// set min/max
			izrazPostfix.setMin(symbol);
			izrazPostfix.setMax(symbol);

			skip(Symbol.BOOLCONST);
			break;
		case Symbol.STRINGCONST:

			izrazPostfix = new AbsAtomExpr(symbol);

			// set min/max
			izrazPostfix.setMin(symbol);
			izrazPostfix.setMax(symbol);

			skip(Symbol.STRINGCONST);

			break;
		case Symbol.LBRACE:

			skip(Symbol.LBRACE);

			izrazPostfix = parsePostfixExpressionBrace();

			skip(Symbol.RBRACE);
			break;
		case Symbol.IDENTIFIER:
			Symbol identifier = skip(Symbol.IDENTIFIER);

			if (Symbol.LPARENT == (symbol != null ? symbol.getToken() : -1)) {
				skip(Symbol.LPARENT);

				AbsExprs expressions = parseExpressions();

				AbsExprName exprName = new AbsExprName(identifier);

				// set min/max
				exprName.setMin(identifier);
				exprName.setMax(identifier);

				izrazPostfix = new AbsFunCall(exprName, expressions);

				// set min/max
				izrazPostfix.setMin(exprName);
				izrazPostfix.setMax(expressions);

				skip(Symbol.RPARENT);
			} else {
				izrazPostfix = new AbsExprName(identifier);
				// set min/max
				izrazPostfix.setMin(identifier);
				izrazPostfix.setMax(identifier);
			}

			break;
		case Symbol.LPARENT:
			skip(Symbol.LPARENT);

			izrazPostfix = parseExpressions();

			skip(Symbol.RPARENT);
			break;
		}

		izrazPostfix = parsePostfixExpressionAddon(izrazPostfix);

		debug();

		return izrazPostfix;
	}

	private AbsExpr parsePostfixExpressionAddon(AbsExpr izrazPostfix)
			throws IOException, ParseException {
		debug("parsePostfixExpression");

		AbsExpr lastIzrazPostfix= izrazPostfix;
		
		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.DOT:
			skip(Symbol.DOT);

			Symbol identifier = skip(Symbol.IDENTIFIER);

			AbsExprName exprName = new AbsExprName(identifier);

			// set min/max
			izrazPostfix.setMin(identifier);
			izrazPostfix.setMax(identifier);

			izrazPostfix = new AbsBinExpr(AbsBinExpr.REC, izrazPostfix,
					exprName);

			// set min/max
			izrazPostfix.setMin(lastIzrazPostfix);
			izrazPostfix.setMax(exprName);

			break;
		case Symbol.LBRACKET:
			skip(Symbol.LBRACKET);

			AbsExpr izraz = parseExpression();

			izrazPostfix = new AbsBinExpr(AbsBinExpr.ARR, izrazPostfix, izraz);

			Symbol simbol = skip(Symbol.RBRACKET);

			// set min/max
			izrazPostfix.setMin(lastIzrazPostfix);
			izrazPostfix.setMax(simbol);

			break;
		case Symbol.WHERE:
			skip(Symbol.WHERE);

			AbsDecls izrazDeklaracij = parseDeclarations();

			izrazPostfix = new AbsWhereExpr(izrazPostfix, izrazDeklaracij);

			// set min/max
			izrazPostfix.setMin(lastIzrazPostfix);
			izrazPostfix.setMax(izrazDeklaracij);

			break;
		}

		debug();

		return izrazPostfix;
	}

	private AbsExpr parsePostfixExpressionBrace() throws IOException,
			ParseException {
		debug("parse_PostfixExpressionBrace");

		// posebnost prazen brace, ce je prazen se tukej nastavi kaj bi bil
		// prazen, ker potem gre ven in zunaj preveri se drugi }, tukaj pa ne
		// naredi nic in ostane default
		AbsExpr izrazPostfixBrace = new AbsAtomExpr(null);
		Symbol identifier = null;
		AbsExprs loopExprs = null;
		AbsExpr condExpr = null;
		AbsExprName exprName = null;
		Symbol simbol = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.IDENTIFIER:
			identifier = skip(Symbol.IDENTIFIER);
			skip(Symbol.ASSIGN);

			AbsExpr izraz = parseExpression();

			exprName = new AbsExprName(identifier);

			// set min/max
			exprName.setMin(identifier);
			exprName.setMax(identifier);

			izrazPostfixBrace = new AbsAssignStmt(exprName, izraz);

			// set min/max
			izrazPostfixBrace.setMin(exprName);
			izrazPostfixBrace.setMax(izraz);

			break;
		case Symbol.IF:
			simbol = skip(Symbol.IF);

			condExpr = parseExpression();

			skip(Symbol.THEN);

			AbsExprs thenExprs = parseExpressions();

			AbsExprs elseExprs = null;
			if (Symbol.ELSE == (symbol != null ? symbol.getToken() : -1)) {
				skip(Symbol.ELSE);
				elseExprs = parseExpressions();
			}

			izrazPostfixBrace = new AbsIfStmt(condExpr, thenExprs, elseExprs);

			// set min/max
			izrazPostfixBrace.setMin(simbol);
			if (elseExprs != null) {
				izrazPostfixBrace.setMax(elseExprs);
			} else {
				izrazPostfixBrace.setMax(thenExprs);
			}

			break;
		case Symbol.FOR:
			simbol = skip(Symbol.FOR);

			identifier = skip(Symbol.IDENTIFIER);

			skip(Symbol.ASSIGN);

			AbsExpr loBound = parseExpression();

			skip(Symbol.COMMA);

			AbsExpr hiBound = parseExpression();

			skip(Symbol.COLON);

			loopExprs = parseExpressions();

			exprName = new AbsExprName(identifier);

			// set min/max
			exprName.setMin(identifier);
			exprName.setMax(identifier);

			izrazPostfixBrace = new AbsForStmt(exprName, loBound, hiBound,
					loopExprs);

			// set min/max
			izrazPostfixBrace.setMin(exprName);
			izrazPostfixBrace.setMax(loopExprs);

			break;
		case Symbol.WHILE:

			simbol = skip(Symbol.WHILE);

			condExpr = parseExpression();

			skip(Symbol.COLON);

			loopExprs = parseExpressions();

			izrazPostfixBrace = new AbsWhileStmt(condExpr, loopExprs);

			// set min/max
			izrazPostfixBrace.setMin(simbol);
			izrazPostfixBrace.setMax(loopExprs);

			break;
		}

		debug();

		return izrazPostfixBrace;
	}

	private AbsDecls parseDeclarations() throws IOException, ParseException {
		debug("parse_Declarations");

		AbsDecls izrazDeklaracij = null;

		Vector<AbsDecl> vectorDecklaracij = new Vector<AbsDecl>();

		vectorDecklaracij.add(parseDeclaration());

		vectorDecklaracij = parseDeclarationsRest(vectorDecklaracij);

		izrazDeklaracij = new AbsDecls(vectorDecklaracij);

		// set min/max
		izrazDeklaracij.setMin(vectorDecklaracij.firstElement());
		izrazDeklaracij.setMax(vectorDecklaracij.lastElement());

		debug();

		return izrazDeklaracij;
	}

	private Vector<AbsDecl> parseDeclarationsRest(
			Vector<AbsDecl> vectorDecklaracij) throws IOException,
			ParseException {
		debug("parse_Declarations'");

		switch (symbol != null ? symbol.getToken() : -1) {
		case -1:
			break;
		case Symbol.VAR:
		case Symbol.TYP:
		case Symbol.FUN:
			vectorDecklaracij.add(parseDeclaration());
			vectorDecklaracij = parseDeclarationsRest(vectorDecklaracij);
			break;
		}

		debug();

		return vectorDecklaracij;
	}

	private AbsDecl parseDeclaration() throws IOException, ParseException {
		debug("Declaration");

		AbsDecl deklaracija = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.VAR:
			deklaracija = parseVariableDeclaration();
			break;
		case Symbol.TYP:
			deklaracija = parseTypeDeclaration();
			break;
		case Symbol.FUN:
			deklaracija = parseFunctionDeclaration();
			break;
		default:
			throw new ParseException();
		}

		debug();

		return deklaracija;
	}

	private AbsDecl parseFunctionDeclaration() throws IOException,
			ParseException {
		debug("FunctionDeclaration");

		AbsFunDecl deklaracijaFunkcije = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.FUN:
			Symbol firstSimbol = skip(Symbol.FUN);
			Symbol identifier = skip(Symbol.IDENTIFIER);
			skip(Symbol.LPARENT);
			AbsDecls parametriFunkcije = parseFunctionParameters();
			skip(Symbol.RPARENT);
			skip(Symbol.COLON);
			AbsType tip = parseType();
			skip(Symbol.ASSIGN);
			AbsExpr izraz = parseExpression();
			Symbol lastSimbol = skip(Symbol.SEMIC);

			AbsExprName exprName = new AbsExprName(identifier);

			// set min/max
			exprName.setMin(identifier);
			exprName.setMax(identifier);

			deklaracijaFunkcije = new AbsFunDecl(exprName, parametriFunkcije,
					tip, izraz);

			// set min/max
			deklaracijaFunkcije.setMin(firstSimbol);
			deklaracijaFunkcije.setMax(lastSimbol);

			break;
		default:
			throw new ParseException();
		}

		debug();

		return deklaracijaFunkcije;
	}

	private AbsDecls parseFunctionParameters() throws IOException,
			ParseException {
		debug("FunctionParameters");

		AbsDecls parametriFuncije = null;

		Vector<AbsDecl> vektorParametrovFunkcije = new Vector<AbsDecl>();

		vektorParametrovFunkcije.add(parseFunctionParameter());

		vektorParametrovFunkcije = parseFunctionParametersRest(vektorParametrovFunkcije);

		parametriFuncije = new AbsDecls(vektorParametrovFunkcije);

		// set min/max
		parametriFuncije.setMin(vektorParametrovFunkcije.firstElement());
		parametriFuncije.setMax(vektorParametrovFunkcije.lastElement());

		debug();

		return parametriFuncije;
	}

	private Vector<AbsDecl> parseFunctionParametersRest(
			Vector<AbsDecl> vektorParametrovFunkcije) throws IOException,
			ParseException {
		debug("FunctionParameters");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.COMMA:
			skip(Symbol.COMMA);

			vektorParametrovFunkcije.add(parseFunctionParameter());

			vektorParametrovFunkcije = parseFunctionParametersRest(vektorParametrovFunkcije);
			break;
		}

		debug();

		return vektorParametrovFunkcije;
	}

	private AbsDecl parseFunctionParameter() throws IOException, ParseException {
		debug("FunctionParameter");

		AbsDecl parameterFunkcije = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.IDENTIFIER:
			Symbol identifier = skip(Symbol.IDENTIFIER);

			skip(Symbol.COLON);

			AbsType tip = parseType();

			AbsExprName exprName = new AbsExprName(identifier);

			// set min/max
			exprName.setMin(identifier);
			exprName.setMax(identifier);

			parameterFunkcije = new AbsVarDecl(exprName, tip);

			// set min/max
			parameterFunkcije.setMin(exprName);
			parameterFunkcije.setMax(tip);

			break;
		default:
			throw new ParseException();
		}
		debug();

		return parameterFunkcije;
	}

	private AbsDecl parseTypeDeclaration() throws IOException, ParseException {
		debug("TypeDeclaration");

		AbsDecl deklaracijaTipa = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.TYP:
			Symbol starterSimbol = skip(Symbol.TYP);

			Symbol identifier = skip(Symbol.IDENTIFIER);

			skip(Symbol.COLON);

			AbsType tip = parseType();

			skip(Symbol.SEMIC);

			AbsTypeName typeName = new AbsTypeName(identifier);
			// set min/max
			typeName.setMin(identifier);
			typeName.setMax(identifier);

			deklaracijaTipa = new AbsTypDecl(typeName, tip);

			// set min/max
			deklaracijaTipa.setMin(starterSimbol);
			deklaracijaTipa.setMax(tip);
			break;
		default:
			throw new ParseException();
		}

		debug();

		return deklaracijaTipa;
	}

	private AbsVarDecl parseVariableDeclaration() throws IOException,
			ParseException {
		debug("variable_declaration");

		AbsVarDecl deklaracijaSpremenljivke = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.VAR:
			Symbol starterSimbol = skip(Symbol.VAR);

			Symbol identifier = skip(Symbol.IDENTIFIER);

			skip(Symbol.COLON);

			AbsType tip = parseType();

			skip(Symbol.SEMIC);

			AbsExprName exprName = new AbsExprName(identifier);
			// set min/max
			exprName.setMin(identifier);
			exprName.setMax(identifier);

			deklaracijaSpremenljivke = new AbsVarDecl(exprName, tip);

			// set min/max
			deklaracijaSpremenljivke.setMin(starterSimbol);
			deklaracijaSpremenljivke.setMax(tip);
			break;
		default:
			throw new ParseException();
		}

		debug();

		return deklaracijaSpremenljivke;
	}

	private AbsType parseType() throws IOException, ParseException {
		debug("type");

		AbsType tip = null;
		AbsType tempTip = null;
		Symbol starterSimbol = null;
		Symbol endSimbol = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.INT:
			starterSimbol = skip(Symbol.INT);

			tip = new AbsAtomType(AbsAtomType.INT);

			// set min/max
			tip.setMin(starterSimbol);
			tip.setMax(starterSimbol);

			break;
		case Symbol.REAL:
			starterSimbol = skip(Symbol.REAL);

			tip = new AbsAtomType(AbsAtomType.REAL);

			// set min/max
			tip.setMin(starterSimbol);
			tip.setMax(starterSimbol);

			break;
		case Symbol.BOOL:
			starterSimbol = skip(Symbol.BOOL);

			tip = new AbsAtomType(AbsAtomType.BOOL);

			// set min/max
			tip.setMin(starterSimbol);
			tip.setMax(starterSimbol);

			break;
		case Symbol.STRING:
			starterSimbol = skip(Symbol.STRING);

			tip = new AbsAtomType(AbsAtomType.STRING);

			// set min/max
			tip.setMin(starterSimbol);
			tip.setMax(starterSimbol);

			break;
		case Symbol.LBRACE:
			starterSimbol = skip(Symbol.LBRACE);
			endSimbol = skip(Symbol.RBRACE);

			tip = new AbsAtomType(AbsAtomType.VOID);

			// set min/max
			tip.setMin(starterSimbol);
			tip.setMax(endSimbol);

			break;
		case Symbol.IDENTIFIER:
			Symbol identifier = skip(Symbol.IDENTIFIER);

			tip = new AbsTypeName(identifier);

			// set min/max
			tip.setMin(identifier);
			tip.setMax(identifier);

			break;
		case Symbol.MUL:
			starterSimbol = skip(Symbol.MUL);
			tempTip = parseType();

			tip = new AbsPtrType(tempTip);

			// set min/max
			tip.setMin(starterSimbol);
			tip.setMax(tempTip);
			break;
		case Symbol.ARR:
			starterSimbol = skip(Symbol.ARR);
			skip(Symbol.LBRACKET);

			AbsExpr izraz = parseExpression();

			endSimbol = skip(Symbol.RBRACKET);

			tempTip = parseType();

			tip = new AbsArrType(tempTip, izraz);

			// set min/max
			tip.setMin(starterSimbol);
			tip.setMax(tempTip);

			break;
		case Symbol.REC:
			starterSimbol = skip(Symbol.REC);
			skip(Symbol.LPARENT);

			AbsDecls recordComponents = parseRecordComponents();

			endSimbol = skip(Symbol.RPARENT);

			tip = new AbsRecType(recordComponents);

			// set min/max
			tip.setMin(starterSimbol);
			tip.setMax(endSimbol);

			break;
		case Symbol.LPARENT:
			skip(Symbol.LPARENT);
			// tukaj nisem siguren, verjetno je pravilno tako
			// ( type ) -> lahko generira ( ( ( ( type ) ) ) ), drugega itak
			// nemore
			tip = parseType();

			skip(Symbol.RPARENT);
			break;
		default:
			throw new ParseException();
		}

		debug();

		return tip;
	}

	private AbsDecls parseRecordComponents() throws IOException, ParseException {
		debug("RecordComponents");

		AbsDecls seznamDeklaracij = null;

		Vector<AbsDecl> vektorDeklaracij = new Vector<AbsDecl>();

		vektorDeklaracij.add(parseRecordComponent());

		vektorDeklaracij = parseRecordComponentsRest(vektorDeklaracij);

		seznamDeklaracij = new AbsDecls(vektorDeklaracij);

		// set min/max
		seznamDeklaracij.setMin(vektorDeklaracij.firstElement());
		seznamDeklaracij.setMax(vektorDeklaracij.lastElement());

		debug();

		return seznamDeklaracij;
	}

	private Vector<AbsDecl> parseRecordComponentsRest(
			Vector<AbsDecl> vektorDeklaracij) throws IOException,
			ParseException {
		debug("RecordComponents'");

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.COMMA:
			skip(Symbol.COMMA);
			vektorDeklaracij.add(parseRecordComponent());
			vektorDeklaracij = parseRecordComponentsRest(vektorDeklaracij);
			break;
		}

		debug();

		return vektorDeklaracij;
	}

	private AbsDecl parseRecordComponent() throws IOException, ParseException {
		debug("RecordComponent");

		AbsDecl deklaracija = null;

		switch (symbol != null ? symbol.getToken() : -1) {
		case Symbol.IDENTIFIER:
			Symbol identifier = skip(Symbol.IDENTIFIER);

			skip(Symbol.COLON);

			AbsType tip = parseType();

			deklaracija = new AbsVarDecl(new AbsExprName(identifier), tip);

			// set min/max
			deklaracija.setMin(identifier);
			deklaracija.setMax(tip);

			break;
		default:
			throw new ParseException();
		}
		debug();

		return deklaracija;
	}

	// podporne metode

	private Symbol skip(int token) throws IOException, ParseException {
		if (symbol == null)
			throw new ParseException();
		if (symbol.getToken() != token)
			throw new ParseException();

		// sporoci skip
		if (REPORT_SKIP)
			Report.information(
					"SynAnal = Preverjamo simbol: " + symbol.getLexeme(),
					symbol.getPosition());

		// za debugat - lazje dobit kjer prid do napake
		// napise se position zadnjega znaka ki uspe
		// if(symbol.getPosition().equals(new Position("",1,18,1,22)))
		// {
		//
		// int a = 2;
		// }

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
		//Report.information(nonterminal, new Position("",0,0,0,0));
	}

	private void debug() {
		if (xml != null) {
			xml.println("</rightside>");
			xml.println("</production>");
		}
		//Report.information("end", new Position("",0,0,0,0));
	}
}
