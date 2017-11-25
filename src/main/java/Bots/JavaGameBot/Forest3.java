package Bots.JavaGameBot;

public class Forest3 extends Location {

	public Forest3() {
		super("Forest3", new Enemy[] {new Goblin()}, new double[] {1.0}, new Location[] {new Forest2()}, new double[] {0.34}, 0, new Gold(0));
	}

}