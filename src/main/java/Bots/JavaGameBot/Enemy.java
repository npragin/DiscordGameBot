package Bots.JavaGameBot;

public abstract class Enemy extends Character {

	Loot[] lootTable;
	int experienceDrop;

	public Enemy(String name, int maxHealth, Weapon weapon, Loot[] lootTable, int experienceDrop) {
		super(name, maxHealth, weapon);
		this.lootTable = lootTable;
		this.experienceDrop = experienceDrop;
	}

	public void setHealth(int health) {
		if (health == -1) {
			currentHealth = maxHealth;
		} else {
			currentHealth = health;
		}
	}

	public Loot[] drop() {
		Loot[] drops = new Loot[lootTable.length];
		for (int i = 0; i < drops.length; i++) {
			if (lootTable[i].drop()) {
				drops[i] = lootTable[i];
			}
		}
		return drops;
	}

	public int returnExperience() {
		return experienceDrop;
	}
}