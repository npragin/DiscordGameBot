package Bots.JavaGameBot;

import net.dv8tion.jda.core.entities.MessageChannel;

public class Player extends Character {
	
	int level;
	Loot[] inventory;
	Enemy enemyFighting;
	int experience;
	Location currentLocation;
	Location[] knownLocations;
	int[] experienceCaps = new int[] {200, 500, 1200, 2100, 4500};
	int[] maxHealths = new int[] {100, 150, 200, 250, 300};	
	
	public Player(String name, int level, Loot[] inventory, Weapon weapon, int experience, Enemy enemyFighting, Location currentLocation, Location[] knownLocations) {
		super(name, new int[] {100, 150, 200, 250, 300}[level - 1], weapon);
		this.level = level;
		this.inventory = inventory;
		this.enemyFighting = enemyFighting;
		this.experience = experience;
		this.currentLocation = currentLocation;
		this.knownLocations = knownLocations;
	}
	
	public Enemy returnEnemy() {
		return enemyFighting;
	}
	
	public void get(int experience) {
		this.experience += experience;
	}
	
	public boolean checkIfLevelUp() {
		if (experience >= experienceCaps[level - 1]) {
			level += 1;
			currentHealth = maxHealths[level - 1];
			return true;
		}
		return false;
	}
	
	public void travel(Location destination, MessageChannel msgChannel) {
			if (destination.equals(knownLocations) && destination.isConnected(currentLocation) && isLocationKnown(destination)) {
				currentLocation = destination;
				msgChannel.sendMessage("You have arrived.").queue();
				return;
			}
		msgChannel.sendMessage("That location is not here");
	}
	
	private void addLocationToKnownLocations(Location location) {
		for (int i = 0; i < knownLocations.length; i++) {
			if (knownLocations[i].equals(location)) {
				return;
			}
		}
		for (int i = 0; i < knownLocations.length; i++) {
			if (knownLocations[i] == null) {
				knownLocations[i] = location;
			}
		}
	}

	public void get(Loot[] items) {
		boolean condition = false;
		for (int i = 0; i < items.length; i++) {
			condition = false;
			for (int j = 0; j < inventory.length; j++) {
				if (inventory[j].returnName().equals(items[i].returnName())) {
					inventory[j].increaseAmount(items[i].returnAmount());
					condition = true;
					break;
				}
			}
			if (condition) {
				continue;
			}
			for (int j = 0; j < inventory.length; j++) {
				if (inventory[j] == null) {
					inventory[j] = items[i];
				}
			}
		}
	}
	
	public boolean isLocationKnown(Location location) {
		for (int i = 0; i < knownLocations.length; i++) {
			if (knownLocations[i].equals(location)) {
				return true;
			}
		}
		return false;
	}
	
	public void checkInvForEmpty() {
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i].returnAmount() <= 0) {
				inventory[i] = null;
			}
		}
	}
	
	public void death() {
		if (level == 1) {
			experience /= 2;
		} else {
			experience = ((experience - experienceCaps[level - 2]) / 2) + experienceCaps[level - 2];
		}
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i].returnName().equals("Gold")) {
				inventory[i].setAmount(inventory[i].returnAmount() / 2);
			}
		}
		currentHealth = maxHealth;
	}
	
	public void setEnemy(Enemy enemy) {
		enemyFighting = enemy;
	}
	
	public int returnLevel() {
		return level;
	}
	
	public String returnWeaponAsString() {
		return weapon.returnName();
	}
	
	public Location returnCurrentLocation() {
		return currentLocation;
	}

	public String InventoryToString() {
		String str = "";
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i + 1] == null || i == inventory.length - 1) {
				str += inventory[i].returnName() + "-" + inventory[i].returnAmount();
				break;
			} else {
				str += inventory[i].returnName() + "-" + inventory[i].returnAmount() + "-";
			}
		}
		return str;
	}
	
	public String knownLocationsToString() {
		String str = "";
		for (int i = 0; i < knownLocations.length; i++) {
			if (i == knownLocations.length - 1 || knownLocations[i + 1] == null) {
				str += knownLocations[i].returnName();
				break;
			}
			str += knownLocations[i].returnName() + "-";
		}
		return str;
	}
}