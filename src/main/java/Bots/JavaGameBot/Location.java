package Bots.JavaGameBot;

public abstract class Location {
	
	String name;
	Enemy[] availableEnemies;
	double[] chancesOfFindingEnemies;
	Location[] connectedLocations;
	double[] chancesOfFindingLocations;
	int levelRequirement;
	Loot itemRequirement;
	
	public Location(String name, Enemy[] availableEnemies, double[] chancesOfFindingEnemies, Location[] connectedLocations, double[] chancesOfFindingLocations, int levelRequirement, Loot itemRequirement) {
		this.name = name;
		this.availableEnemies = availableEnemies;
		this.chancesOfFindingEnemies = chancesOfFindingEnemies;
		this.connectedLocations = connectedLocations;
		this.chancesOfFindingLocations = chancesOfFindingLocations;
		this.levelRequirement = levelRequirement;
		this.itemRequirement = itemRequirement;
	}

	public Enemy findEnemy() {
		double random = Math.random();
		for (int i = 0; i < availableEnemies.length; i++) {
			if (random <= chancesOfFindingEnemies[i]) {
				return availableEnemies[i];
			}
		}
		return availableEnemies[0];
	}
	
	public Location findLocation(int level, Loot[] inv) {
		double random;
		for (int i = 0; i < connectedLocations.length; i++) {
			random = Math.random();
			if (random <= chancesOfFindingLocations[i] && level > levelRequirement && isItemRequirementMet(inv)) {
				return connectedLocations[i];
			}
		}
		return null;
	}
	
	private boolean isItemRequirementMet(Loot[] inv) {
		if (itemRequirement.returnAmount() <= 0) {
			return true;
		}
		for (int i = 0; i < inv.length; i++) {
			if (inv[i].returnName().equals(itemRequirement.returnName()) && inv[i].returnAmount() >= itemRequirement.returnAmount()) {
				return true;
			}
		}
		return false;
	}

	public String returnName() {
		return name;
	}
	
	public boolean isConnected(Location location) {
		for (int i = 0; i < connectedLocations.length; i++) {
			if (connectedLocations[i].equals(location)) {
				return true;
			}
		}
		return false;
	}
}
