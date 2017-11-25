package Bots.JavaGameBot;

public class Wolf extends Enemy {

	public Wolf() {
		super("Wolf", 8, new WolfTeeth(), new Loot[] {new WolfFur(1)}, 25);
	}
}
