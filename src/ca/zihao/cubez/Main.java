package ca.zihao.cubez;

/**
 * Created by Zihao on 5/22/2014.
 */

public class Main {
	public static void main(String[] args) {
		//	new ConsoleMain(args).run(); System.exit(0);
		if(args.length > 1 && args[1].equals("-console")) {
			new ConsoleMain(args).run();
		} else {
			new WindowMain(args).run();
		}
	}
}
