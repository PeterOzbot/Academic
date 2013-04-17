package compiler.semanal;

import java.util.*;

import compiler.*;
import compiler.abstree.*;
import compiler.lexanal.*;

public class TypeResolver implements Visitor {

	private static final HashMap<AbsTree, SemType> types = new HashMap<AbsTree, SemType>();
	
	/** Doloci tip vozlisca abstraktnega sintaksnega drevesa.
	 * 
	 * @param absNode Vozlisce abstraktnega sintaksnega drevesa.
	 * @param semType Tip vozlisca abstraktnega sintaksnega drevesa.
	 */
	public static void setType(AbsTree absNode, SemType semType) {
		if (types.get(absNode) != null) Report.error("Internal error.", 1);
		types.put(absNode, semType);
	}
	
	/** Vrne tip vozlisca abstraktnega sintaksnega drevesa.
	 * 
	 * @param absNode Vozlisce abstraktnega sintaksnega drevesa.
	 * @return Tip vozlisca abstraktnega sintaksnega drevesa ali <code>null</code>, ce tip se ni dolocen.
	 */
	public static SemType getType(AbsTree absNode) {
		return types.get(absNode);
	}

	private static boolean structEqual(HashMap<SemType, HashSet<SemType>> eqs, SemType type1, SemType type2) {
		type1 = type1.actualType();
		type2 = type2.actualType();
		
		if ((type1 instanceof SemAtomType) || (type2 instanceof SemAtomType)) {
			SemAtomType atomType1 = (SemAtomType)type1;
			SemAtomType atomType2 = (SemAtomType)type2;
			return (atomType1.typ == atomType2.typ);
		}

		if ((type1 instanceof SemArrType) || (type2 instanceof SemArrType)) {
			SemArrType arrType1 = (SemArrType)type1;
			SemArrType arrType2 = (SemArrType)type2;
			if (eqs.get(arrType1) == null) eqs.put(arrType1, new HashSet<SemType>());
			if (eqs.get(arrType2) == null) eqs.put(arrType2, new HashSet<SemType>());
			if (eqs.get(arrType1).contains(arrType2) && eqs.get(arrType1).contains(arrType2)) return true;
			if (eqs.get(arrType1).contains(arrType2) || eqs.get(arrType1).contains(arrType2))
				Report.error("Internal error.", 1);
			if (arrType1.arity != arrType2.arity) return false;
			eqs.get(arrType1).add(arrType2);
			eqs.get(arrType2).add(arrType1);
			return structEqual(eqs, arrType1.type, arrType2.type);
		}
		
		if ((type1 instanceof SemPtrType) || (type2 instanceof SemPtrType)) {
			SemPtrType ptrType1 = (SemPtrType)type1;
			SemPtrType ptrType2 = (SemPtrType)type2;
			if (eqs.get(ptrType1) == null) eqs.put(ptrType1, new HashSet<SemType>());
			if (eqs.get(ptrType2) == null) eqs.put(ptrType2, new HashSet<SemType>());
			if (eqs.get(ptrType1).contains(ptrType2) && eqs.get(ptrType1).contains(ptrType2)) return true;
			if (eqs.get(ptrType1).contains(ptrType2) || eqs.get(ptrType1).contains(ptrType2))
				Report.error("Internal error.", 1);
			eqs.get(ptrType1).add(ptrType2);
			eqs.get(ptrType2).add(ptrType1);
			return structEqual(eqs, ptrType1.type, ptrType2.type);
		}
		
		if ((type1 instanceof SemRecType) || (type2 instanceof SemRecType)) {
			SemRecType recType1 = (SemRecType)type1;
			SemRecType recType2 = (SemRecType)type2;
			if (eqs.get(recType1) == null) eqs.put(recType1, new HashSet<SemType>());
			if (eqs.get(recType2) == null) eqs.put(recType2, new HashSet<SemType>());
			if (eqs.get(recType1).contains(recType2) && eqs.get(recType1).contains(recType2)) return true;
			if (eqs.get(recType1).contains(recType2) || eqs.get(recType1).contains(recType2))
				Report.error("Internal error.", 1);
			if (recType1.compNames.size() != recType2.compNames.size()) return false;
			for (int i = 0; i < recType1.compNames.size(); i++) {
				if (! (structEqual(eqs, recType1.compTypes.get(i), recType2.compTypes.get(i)))) return false;
			}
			return true;
		}
		
		return false;
	}
	
	/** Ugotovi enakost tipov.
	 * 
	 * @param type1 Prvi tip.
	 * @param type2 Drugi tip.
	 * @return <code>true</code>, ce sta tipa enaka, <code>false</code> sicer.
	 */
	public static boolean equal(SemType type1, SemType type2) {
		return structEqual(new HashMap<SemType, HashSet<SemType>>(), type1, type2);
	}

	// TODO
	
	public void visit(AbsDecls acceptor) {
		for (AbsDecl decl: acceptor.decls)
			if (decl instanceof AbsTypDecl) {
				AbsTypDecl typDecl = (AbsTypDecl)decl;
				TypeResolver.setType(typDecl.name, new SemTypeName(typDecl.name.identifier.getLexeme()));
			}
		for (AbsDecl decl: acceptor.decls) decl.accept(this);
		TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.VOID));
	}
	
	// TODO

	public void visit(AbsUnExpr acceptor) {
		acceptor.subExpr.accept(this);
		switch (acceptor.oper) {
		case AbsUnExpr.ADD:
		case AbsUnExpr.SUB:
		{
			SemType type = TypeResolver.getType(acceptor.subExpr);
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.INT))) {
				TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.INT));
				return;
			}
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.REAL))) {
				TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.REAL));
				return;
			}
			Report.error("Illegal operand type.", acceptor.getPosition(), 1);
		}
		case AbsUnExpr.NOT:
		{
			SemType type = TypeResolver.getType(acceptor.subExpr);
			if (TypeResolver.equal(type, new SemAtomType(SemAtomType.BOOL))) {
				TypeResolver.setType(acceptor, new SemAtomType(SemAtomType.BOOL));
				return;
			}
			Report.error("Illegal operand type.", acceptor.getPosition(), 1);
		}
		case AbsUnExpr.MUL:
		{
			SemType type = TypeResolver.getType(acceptor.subExpr).actualType();
			if (type instanceof SemPtrType) {
				SemPtrType ptrType = (SemPtrType)type;
				TypeResolver.setType(acceptor, ptrType.type);
				return;
			}
			Report.error("Illegal operand type.", acceptor.getPosition(), 1);
		}
		case AbsUnExpr.AND:
		{
			SemType type = TypeResolver.getType(acceptor.subExpr);
			TypeResolver.setType(acceptor, new SemPtrType(type));
			return;
		}
		default           :
			Report.error("Internal error.", acceptor.getPosition(), 1);
		}
	}

	@Override
	public void visit(AbsArrType acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsAssignStmt acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsAtomExpr acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsAtomType acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsBinExpr acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsExprName acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsExprs acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsForStmt acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsFunCall acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsFunDecl acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsIfStmt acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsPtrType acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsRecType acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsTypDecl acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsTypeName acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsVarDecl acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsWhereExpr acceptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AbsWhileStmt acceptor) {
		// TODO Auto-generated method stub
		
	}
}
