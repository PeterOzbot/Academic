package compiler.abstree;

/** Deklaracija tipa. */
public class AbsTypDecl extends AbsDecl {

	/** Ime tipa.  */
	public final AbsTypeName name;
	
	/** Tip tipa.  */
	public final AbsType type;
	
	public AbsTypDecl(AbsTypeName name, AbsType type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
