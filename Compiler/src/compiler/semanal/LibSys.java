package compiler.semanal;

import compiler.Report;
import compiler.abstree.AbsDecl;
import compiler.abstree.AbsExprName;
import compiler.abstree.AbsFunDecl;
import compiler.lexanal.Position;
import compiler.lexanal.Symbol;

//fun putInt(i:int):void
//fun getInt(i:^int):void
//fun putString(s:string):void
//fun getString(s:^string):void

public class LibSys {
	public static AbsFunDecl PutString;
	public static AbsFunDecl GetString;
	public static AbsFunDecl PutInt;
	public static AbsFunDecl GetInt;

	static {
		{
			// PutInt
			Symbol identifier = new Symbol(Symbol.IDENTIFIER, "putInt",
					new Position("", 0, 0, 0, 0));
			AbsFunDecl fun = new AbsFunDecl(new AbsExprName(identifier), null,
					null,null);
			fun.setMin(identifier);
			fun.setMax(identifier);
			PutInt = fun;
		}
		{
			// GetInt
			Symbol identifier = new Symbol(Symbol.IDENTIFIER, "getInt",
					new Position("", 0, 0, 0, 0));
			AbsFunDecl fun = new AbsFunDecl(new AbsExprName(identifier), null,
					null, null);
			fun.setMin(identifier);
			fun.setMax(identifier);
			GetInt = fun;
		}
		{
			// GetString
			Symbol identifier = new Symbol(Symbol.IDENTIFIER, "getString",
					new Position("", 0, 0, 0, 0));
			AbsFunDecl fun = new AbsFunDecl(new AbsExprName(identifier), null,
					null, null);
			fun.setMin(identifier);
			fun.setMax(identifier);
			GetString = fun;
		}

		{
			// PutString
			Symbol identifier = new Symbol(Symbol.IDENTIFIER, "putString",
					new Position("", 0, 0, 0, 0));
			AbsFunDecl fun = new AbsFunDecl(new AbsExprName(identifier), null,
					null, null);
			fun.setMin(identifier);
			fun.setMax(identifier);
			PutString = fun;
		}
	}

	public static Boolean IsSystem(String name) {
		if (PutString.name.identifier.getLexeme().equals(name))
			return true;
		else if (GetString.name.identifier.getLexeme().equals(name))
			return true;
		else if (GetInt.name.identifier.getLexeme().equals(name))
			return true;
		else if (PutInt.name.identifier.getLexeme().equals(name))
			return true;
		else
			return false;
	}
	
	public static Boolean IsSystem(AbsDecl absDecl) {
		// pricakujemo deklaracijo funkcije
		AbsFunDecl absFunDecl = null;
		if (absDecl instanceof AbsFunDecl) {
			absFunDecl = (AbsFunDecl) absDecl;
		} else {
			Report.error(
					"TypeResolverSys: Expected function declaration not of type AbsFunDecl.",
					absDecl.getPosition(), 1);
		}
		// preverimo ce je ena izmed sistemskih
		String functionName = absFunDecl.name.identifier.getLexeme();
		return LibSys.IsSystem(functionName);
	}
}
