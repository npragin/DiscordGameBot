package Bots.JavaGameBot;

public class Forest1 extends Location {

	public Forest1() {
		super("Forest1", new Enemy[] {new Wolf()}, new double[] {1.0}, new Location[] {new Forest2()}, new double[] {0.34}, 0, new Gold(0));
	}

}