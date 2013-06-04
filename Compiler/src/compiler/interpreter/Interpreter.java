package compiler.interpreter;

import java.util.*;

import compiler.*;
import compiler.frames.*;
import compiler.imcode.*;

public class Interpreter {

	public static boolean debug = false;

	/*--- staticni del navideznega stroja ---*/
	
	/** Pomnilnik navideznega stroja. */
	public static HashMap<Integer, Object> mems = new HashMap<Integer, Object>();
	
	public static void stM(Integer address, Object value) {
		if (debug) System.out.println(" [" + address + "] <= " + value);
		mems.put(address, value);
	}

	public static Object ldM(Integer address) {
		Object value = mems.get(address);
		if (debug) System.out.println(" [" + address + "] => " + value);
		return value;
	}
	
	/** Kazalec na vrh klicnega zapisa. */
	private static int fp = 1000;

	/** Kazalec na dno klicnega zapisa. */
	private static int sp = 1000;
	
	/*--- dinamicni del navideznega stroja ---*/
	
	/** Zacasne spremenljivke (`registri') navideznega stroja. */
	public HashMap<Temp, Object> temps = new HashMap<Temp, Object>();
		
	public void stT(Temp temp, Object value) {
		if (debug) System.out.println(" " + temp.temp + " <= " + value);
		temps.put(temp, value);
	}

	public Object ldT(Temp temp) {
		Object value = temps.get(temp);
		if (debug) System.out.println(" " + temp.temp + " => " + value);
		return value;
	}
	
	/*--- Izvajanje navideznega stroja. ---*/
	
	public Interpreter(Frame frame, ImSEQ code) {
		if (debug) {
			System.out.println("[START OF " + frame.label.label + "]");
		}
	
		stM(sp + frame.oldFPoffset, fp);
		fp = sp;
		sp = sp - frame.getSize() -4;//TODO: -4 da je vkljuèen SL = ocitno prej nesme bit?
		if (debug) {
			System.out.println("[FP=" + fp + "]");
			System.out.println("[SP=" + sp + "]");
		}

		int pc = 0;
		Object result = null;
		while (pc < code.codes.size()) {
			if (debug) System.out.println("pc=" + pc);
			ImCode instruction = code.codes.elementAt(pc);
			result = execute(instruction);
			if (result instanceof Label) {
				for (pc = 0; pc < code.codes.size(); pc++) {
					instruction = code.codes.elementAt(pc);
					if ((instruction instanceof ImLABEL) && (((ImLABEL) instruction).label.label.equals(((Label) result).label)))
						break;
				}
			}
			else
				pc++;
		}
		
		fp = (Integer) ldM(fp + frame.oldFPoffset);
		sp = sp + frame.getSize() +4;//TODO: +4 da je vkljuèen SL = ocitno prej nesme bit?
		if (debug) {
			System.out.println("[FP=" + fp + "]");
			System.out.println("[SP=" + sp + "]");
		}
		
		stM(sp, result);
		if (debug) {
			System.out.println("[RV=" + result + "]");
		}

		if (debug) {
			System.out.println("[END OF " + frame.label.label + "]");
		}
	}
	
	public Object execute(ImCode instruction) {
		
		if (instruction instanceof ImBINOP) {
			ImBINOP instr = (ImBINOP) instruction;
			Object fstSubValue = execute(instr.fstSubExpr);
			Object sndSubValue = execute(instr.sndSubExpr);
			switch (instr.oper) {
			case ImBINOP.OR:
				return ((((Integer) fstSubValue).intValue() != 0) || (((Integer) sndSubValue).intValue() != 0) ? 1 : 0);
			case ImBINOP.AND:
				return ((((Integer) fstSubValue).intValue() != 0) && (((Integer) sndSubValue).intValue() != 0) ? 1 : 0);
			case ImBINOP.EQUi:
				return (((Integer) fstSubValue).intValue() == ((Integer) sndSubValue).intValue() ? 1 : 0);
			case ImBINOP.NEQi:
				return (((Integer) fstSubValue).intValue() != ((Integer) sndSubValue).intValue() ? 1 : 0);
			case ImBINOP.LTHi:
				return (((Integer) fstSubValue).intValue() < ((Integer) sndSubValue).intValue() ? 1 : 0);
			case ImBINOP.GTHi:
				return (((Integer) fstSubValue).intValue() > ((Integer) sndSubValue).intValue() ? 1 : 0);
			case ImBINOP.LEQi:
				return (((Integer) fstSubValue).intValue() <= ((Integer) sndSubValue).intValue() ? 1 : 0);
			case ImBINOP.GEQi:
				return (((Integer) fstSubValue).intValue() >= ((Integer) sndSubValue).intValue() ? 1 : 0);
			case ImBINOP.ADDi:
				return (((Integer) fstSubValue).intValue() + ((Integer) sndSubValue).intValue());
			case ImBINOP.SUBi:
				return (((Integer) fstSubValue).intValue() - ((Integer) sndSubValue).intValue());
			case ImBINOP.MULi:
				return (((Integer) fstSubValue).intValue() * ((Integer) sndSubValue).intValue());
			case ImBINOP.DIVi:
				return (((Integer) fstSubValue).intValue() / ((Integer) sndSubValue).intValue());
			case ImBINOP.MODi:
				return (((Integer) fstSubValue).intValue() % ((Integer) sndSubValue).intValue());
			case ImBINOP.EQUr:
				return (((Float) fstSubValue).floatValue() == ((Float) sndSubValue).floatValue() ? 1 : 0);
			case ImBINOP.NEQr:
				return (((Float) fstSubValue).floatValue() != ((Float) sndSubValue).floatValue() ? 1 : 0);
			case ImBINOP.LTHr:
				return (((Float) fstSubValue).floatValue() < ((Float) sndSubValue).floatValue() ? 1 : 0);
			case ImBINOP.GTHr:
				return (((Float) fstSubValue).floatValue() > ((Float) sndSubValue).floatValue() ? 1 : 0);
			case ImBINOP.LEQr:
				return (((Float) fstSubValue).floatValue() <= ((Float) sndSubValue).floatValue() ? 1 : 0);
			case ImBINOP.GEQr:
				return (((Float) fstSubValue).floatValue() >= ((Float) sndSubValue).floatValue() ? 1 : 0);
			case ImBINOP.ADDr:
				return (((Float) fstSubValue).floatValue() + ((Float) sndSubValue).floatValue());
			case ImBINOP.SUBr:
				return (((Float) fstSubValue).floatValue() - ((Float) sndSubValue).floatValue());
			case ImBINOP.MULr:
				return (((Float) fstSubValue).floatValue() * ((Float) sndSubValue).floatValue());
			case ImBINOP.DIVr:
				return (((Float) fstSubValue).floatValue() / ((Float) sndSubValue).floatValue());
			case ImBINOP.MODr:
				return (((Float) fstSubValue).floatValue() % ((Float) sndSubValue).floatValue());
			case ImBINOP.EQUs:
				return (((String) fstSubValue).compareTo((String) sndSubValue)) == 0 ? 1 : 0;
			case ImBINOP.NEQs:
				return (((String) fstSubValue).compareTo((String) sndSubValue)) != 0 ? 1 : 0;
			case ImBINOP.LTHs:
				return (((String) fstSubValue).compareTo((String) sndSubValue)) < 0 ? 1 : 0;
			case ImBINOP.GTHs:
				return (((String) fstSubValue).compareTo((String) sndSubValue)) > 0 ? 1 : 0;
			case ImBINOP.LEQs:
				return (((String) fstSubValue).compareTo((String) sndSubValue)) <= 0 ? 1 : 0;
			case ImBINOP.GEQs:
				return (((String) fstSubValue).compareTo((String) sndSubValue)) >= 0 ? 1 : 0;
			}
			Report.error("Internal error.", 1);
			return null;
		}
		
		if (instruction instanceof ImCALL) {
			ImCALL instr = (ImCALL) instruction;
			
			if(!instr.fun.label.equals("L1::f"))
			{
				int offset = 0;
				stM(sp + offset, execute(instr.sl)); 
				offset += 4;
				for (ImCode arg : instr.args) {
					stM(sp + offset, execute(arg));
					offset += 4;
				}
			}

			
			if (instr.fun.label.equals("Lsys::putInt")) {
				System.out.println((Integer) ldM(sp + 4));
				return null;
			}
			if (instr.fun.label.equals("Lsys::getInt")) {
				Scanner scanner = new Scanner(System.in);
				stM((Integer) ldM(sp + 4),scanner.nextInt());
				return null;
			}
			if (instr.fun.label.equals("Lsys::putString")) {
				Object value =  ldM(sp + 4);
				System.out.println((String)value);
				return null;
			}
			if (instr.fun.label.equals("Lsys::getString")) {
				Scanner scanner = new Scanner(System.in);
				stM((Integer) ldM (sp + 4),scanner.next());
				return null;
			}

			
			new Interpreter(compiler.lincode.CodeGenerator.framesByLabel.get(instr.fun), (ImSEQ) compiler.lincode.CodeGenerator.codesByLabel.get(instr.fun));
			
			return null;
		}
		
		if (instruction instanceof ImCJUMP) {
			ImCJUMP instr = (ImCJUMP) instruction;
			Object cond = execute(instr.cond);
			if (cond instanceof Integer) {
				if (((Integer) cond).intValue() != 0)
					return instr.thenLabel;
				else
					return instr.elseLabel;
			}
			else Report.error("CJUMP: illegal condition type.", 1);
		}
		
		if (instruction instanceof ImCONSTi) {
			ImCONSTi instr = (ImCONSTi) instruction;
			return new Integer(instr.intValue);
		}
		
		if (instruction instanceof ImCONSTr) {
			ImCONSTr instr = (ImCONSTr) instruction;
			return new Float(instr.realValue);
		}
		
		if (instruction instanceof ImCONSTs) {
			ImCONSTs instr = (ImCONSTs) instruction;
			return new String(instr.stringValue);
		}
		
		if (instruction instanceof ImJUMP) {
			ImJUMP instr = (ImJUMP) instruction;
			return instr.label;
		}
		
		if (instruction instanceof ImLABEL) {
			return null;
		}
		
		if (instruction instanceof ImMEM) {
			ImMEM instr = (ImMEM) instruction;
			return ldM((Integer) execute(instr.expr));
		}
		
		if (instruction instanceof ImMOVE) {
			ImMOVE instr = (ImMOVE) instruction;
			if (instr.dst instanceof ImTEMP) {
				Temp temp = ((ImTEMP) instr.dst).temp;
				Object srcValue = execute(instr.src);
				stT(temp, srcValue);
				return srcValue;
			}
			if (instr.dst instanceof ImMEM) {
				Object dstValue = execute(((ImMEM) instr.dst).expr);
				Object srcValue = execute(instr.src);
				stM((Integer) dstValue, srcValue);
				return srcValue;
			}
		}
		
		if (instruction instanceof ImNAME) {
			ImNAME instr = (ImNAME) instruction;
			if (instr.name.equals("FP")) return fp;
			if (instr.name.equals("SP")) return sp;
		}
		
		if (instruction instanceof ImTEMP) {
			ImTEMP instr = (ImTEMP) instruction;
			return ldT(instr.temp);
		}
	
		if (instruction instanceof ImUNOP) {
			ImUNOP instr = (ImUNOP) instruction;
			Object subValue = execute(instr.subExpr);
			switch (instr.oper) {
			case ImUNOP.ADDi:
				return +(((Integer) subValue).intValue());
			case ImUNOP.SUBi:
				return -(((Integer) subValue).intValue());
			case ImUNOP.ADDr:
				return +(((Float) subValue).floatValue());
			case ImUNOP.SUBr:
				return +(((Float) subValue).floatValue());
			case ImUNOP.NOT:
				return (((Integer) subValue).intValue() == 0 ? 1 : 0);
			}
			Report.error("Internal error.", 1);
			return null;
		}
		
		return null;
	}
	
}
