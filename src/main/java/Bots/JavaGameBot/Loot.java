package Bots.JavaGameBot;

public abstract class Loot {
	
	String name;
	String pluralName;
	int value;
	double percentDrop;
	int amount;
	
	public Loot(String name, String pluralName, int value, double percentDrop, int amount) {
		this.name = name;
		this.pluralName = pluralName;
		this.value = value;
		this.percentDrop = percentDrop;
		this.amount = amount;
	}
	
	public int sell(int amount) {
		this.amount -= amount;
		return value * amount;
	}
	
	public boolean drop() {
		if (Math.random() <= percentDrop) {
			return true;
		}
		return false;
	}
	
	public String returnName() {
		return name;
	}
	
	public String returnPluralName() {	
		return pluralName;
	}
	
	public int returnAmount() {
		return amount;
	}
	
	public void increaseAmount(int num) {
		amount += num;
	}
	
	public void setAmount(int num) {
		amount = num;
	}
}