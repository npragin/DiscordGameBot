package Bots.JavaGameBot;

public class Forest2 extends Location {

	public Forest2() {
		super("Forest2", new Enemy[] {new Goblin(), new Wolf()}, new double[] {0.5, 1.0}, new Location[] {new Forest3()}, new double[] {0.34}, 0, new Gold(0));
	}

}