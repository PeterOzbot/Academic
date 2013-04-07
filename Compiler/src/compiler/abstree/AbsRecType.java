package compiler.abstree;

/** Opis zapisov.  */
public class AbsRecType extends AbsType {

	/** Komponente.  */
	public final AbsDecls comps;
	
	public AbsRecType(AbsDecls comps) {
		this.comps = comps;
	}
	
	@Override
	public void accept(Visitor visitor) { visitor.visit(this); }
	
}
