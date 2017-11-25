package Bots.JavaGameBot;

public abstract class Weapon extends Loot {
	
	int maxDamage;
	int minDamage;
	
	public Weapon(String name, String pluralName, int value, double percentDrop, int amount, int maxDamage, int minDamage) {
		super(name, pluralName, value, percentDrop, amount);
		this.maxDamage = maxDamage;
		this.minDamage = minDamage;
	}
	
	public int genDamageDealt() {
		double random = Math.random();
		if (random == 1) {
			random = 0.9999999999999999;
		}
		return (int) (random * maxDamage) + minDamage;
	}
}