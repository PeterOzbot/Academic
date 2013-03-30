package compiler;

public class Main {
	
	public static void main(String[] args) {
		
		System.out.println("This is Proteus compiler:");
		
		switch (args.length) {
		case 0:
			Report.error("No file name specified.", 1);
			break;
		case 1:
			compiler.abstree.Main.main(args[0]);
			break;
		case 2:
			if (args[1].equals("lexanal")) compiler.lexanal.Main.main(args[0]); else
			if (args[1].equals("synanal")) compiler.synanal.Main.main(args[0]); else
			if (args[1].equals("abstree")) compiler.abstree.Main.main(args[0]); else
			{
				Report.warning("Illegal compiler phase specified.");
				compiler.abstree.Main.main(args[0]);
			}
			break;
		default:
			Report.warning("Too many command line arguments.");
			break;
		}
		
		System.out.println(":-)");
		System.exit(0);
	}

}