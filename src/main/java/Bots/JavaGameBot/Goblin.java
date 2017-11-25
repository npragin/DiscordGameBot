package Bots.JavaGameBot;

public class Goblin extends Enemy{
	
	public Goblin() {
		super("Goblin", 10, new Dagger(1), new Loot[] {new Gold(15), new Dagger(1)}, 50);
	}

}