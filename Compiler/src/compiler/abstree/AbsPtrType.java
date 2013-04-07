package compiler.abstree;

/** Opis kazalcev.  */
public class AbsPtrType extends AbsType {

	/** Osnovni tip.  */
	public final AbsType type;
	
	public AbsPtrType(AbsType type) {
		this.type = type;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
