package compiler.abstree;

import compiler.lexanal.*;

/** Abstraktno sintaksno drevo.  */
public abstract class AbsTree {

	private Position position = null;

	public Position getPosition() {
		return position;
	}
	
	/** Doloci polozaj zacetka stavcne oblike tega drevesa.
	 * 
	 * @param position Polozaj zacetka.
	 */
	private void setMin(Position position) {
		if (this.position == null) this.position = position.clone(); else this.position.setMin(position); 
	}
	
	/** Doloci polozaj zacetka stavcne oblike tega drevesa.
	 * 
	 * @param symbol Symbol, ki zacne stavcno obliko tega drevesa.
	 */
	public void setMin(Symbol symbol) {
		this.setMin(symbol.getPosition());
	}
	
	/** Doloci polozaj zacetka stavcne oblike tega drevesa.
	 * 
	 * @param tree Drevo, ki opisuje zacetek stavcne oblike tega drevesa.
	 */
	public void setMin(AbsTree tree) {
		this.setMin(tree.getPosition());
	}
	
	/** Doloci polozaj konca stavcne oblike tega drevesa.
	 * 
	 * @param position Polozaj konca.
	 */
	private void setMax(Position position) {
		if (this.position == null) this.position = position.clone(); else this.position.setMax(position); 
	}
	
	/** Doloci polozaj konca stavcne oblike tega drevesa.
	 * 
	 * @param symbol Symbol, ki konca stavcno obliko tega drevesa.
	 */
	public void setMax(Symbol symbol) {
		this.setMax(symbol.getPosition());
	}
	
	/** Doloci polozaj konca stavcne oblike tega drevesa.
	 * 
	 * @param tree Drevo, ki opisuje konec stavcne oblike tega drevesa.
	 */
	public void setMax(AbsTree tree) {
		this.setMax(tree.getPosition());
	}
	
	/** Sprejme obiskovalca.
	 * 
	 * @param visitor Obiskovalec.
	 */
	public abstract void accept(Visitor visitor);
	
}
