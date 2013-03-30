package compiler.abstree;

import compiler.*;
import compiler.lexanal.*;

/** Abstraktno sintaksno drevo.  */
public abstract class AbsTree implements XMLable {

	private Position position = null;

	public Position getPosition() {
		return position;
	}
	
	private void setMin(Position position) {
		if (this.position == null) this.position = position.clone(); else this.position.setMin(position); 
	}
	
	public void setMin(Symbol symbol) {
		this.setMin(symbol.getPosition());
	}
	
	public void setMin(AbsTree tree) {
		this.setMin(tree.getPosition());
	}
	
	private void setMax(Position position) {
		if (this.position == null) this.position = position.clone(); else this.position.setMax(position); 
	}
	
	public void setMax(Symbol symbol) {
		this.setMax(symbol.getPosition());
	}
	
	public void setMax(AbsTree tree) {
		this.setMax(tree.getPosition());
	}
	
}
