package compiler.imcode;

import compiler.*;
import compiler.frames.Temp;

public abstract class ImCode implements XMLable {
	public ImSEQ linearCode = null;

	public Temp linearCodeResult = null;

	public abstract void linearCode();
}
