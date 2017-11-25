package Bots.JavaGameBot;

public abstract class Character {

	String name;
	int maxHealth;
	int currentHealth;
	Weapon weapon;
	
	public Character(String name, int maxHealth, Weapon weapon) {
		this.name = name;
		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;
		this.weapon = weapon;
	}
	
	public int attack() {
		return weapon.genDamageDealt();
	}
	
	public boolean isDead() {
		if (currentHealth <= 0) {
			return true;
		}
		return false;
	}
	
	public void takeDamage(int damageTaken) {
		currentHealth -= damageTaken;
	}
	
	public String returnName() {
		return name;
	}
	
	public int returnCurrentHealth() {
		return currentHealth;
	}
	
	public int returnMaxHealth() {
		return maxHealth;
	}
}
