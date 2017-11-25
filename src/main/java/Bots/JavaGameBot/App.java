package Bots.JavaGameBot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class App extends ListenerAdapter {

	public String prefix = "~";			//How to get bot attention
	public int availableLineNum = 0;	//Where to put in the next save file

	public static void main( String[] args ) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		JDA bot = new JDABuilder(AccountType.BOT).setToken("MzgxNTg0OTE1NDg3MjYwNjcy.DPJSRg.x_5cG6Ks2C0r2b5BZ6oCQSco6zw").buildBlocking();	//How the bot logs in
		bot.addEventListener(new App());																									//The bot is able to react to things
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		Message msg = e.getMessage();					//Message
		MessageChannel msgChannel = e.getChannel();		//Channel Message was sent in
		User msgAuthor = e.getAuthor();					//Author of the message

		if (msg.getContent().length() >= 12 && msg.getContent().substring(0, 10).equalsIgnoreCase(prefix + "setPrefix")) {				//You are able to change the prefix
			prefix = msg.getContent().substring(11);
			msgChannel.sendMessage("The prefix has been changed to " + prefix).queue();
		} 
		else if (msg.getContent().length() >= 15 && msg.getContent().substring(0, 15).equalsIgnoreCase(prefix + "startAdventure")) {	//Starting a new game
			try {
				msgChannel.sendMessage("Creating Save File...").queue();
				createNewSave(msgAuthor);
				msgChannel.sendMessage("New Save File Created.").queue();
			} catch (IOException e1) {
				msgChannel.sendMessage("Please tell Noah that this happened, would be nice if you @ him now so he knows where it happened").queue();
				e1.printStackTrace();
			}
		}
		else if (msg.getContent().length() <= 6 && msg.getContent().substring(0, 6).equalsIgnoreCase(prefix + "fight")) {				//Engaging in Combat
			Player player = getPlayerFromSave(msgAuthor, msgChannel);
			String message = "```diff\n";
			int enemyAttack = player.returnEnemy().attack();
			int playerAttack = player.attack();
			player.takeDamage(enemyAttack);
			message += "- You took " + enemyAttack + " damage.\n";
			if (player.isDead()) {
				player.death();
				player.setEnemy(player.returnEnemy());
				player.returnEnemy().setHealth(-1);
				message += "- You have died. You have lost 50% of your gold and 50% of your experience.";
				saveGame(msgAuthor, player, msgChannel);
			} else {
				player.returnEnemy().takeDamage(playerAttack);
				message += "+ You dealt " + playerAttack + " damage.\n";
				if (player.returnEnemy().isDead()) {
					Loot[] drops = player.returnEnemy().drop();
					player.get(drops);
					player.get(player.returnEnemy().returnExperience());
					message += "+ You have killed the " + player.returnEnemy().returnName() + ", and recieved " + lootArrayToString(drops) + player.returnEnemy().returnExperience();
					message += "+ You have " + player.returnCurrentHealth() + "/" + player.returnMaxHealth() + " health.\n";
					if (player.checkIfLevelUp()) {
						message += "You have leveled up! Your max health has been increased by 50 and you have been healed to full";
					}
					player.setEnemy(player.returnCurrentLocation().findEnemy());
					saveGame(msgAuthor, player, msgChannel);
				} else {
					message += "- The " + player.returnEnemy().returnName() + " has " + player.returnEnemy().returnCurrentHealth() + "/" + player.returnEnemy().returnMaxHealth() + " health.\n";
					message += "+ You have " + player.returnCurrentHealth() + "/" + player.returnMaxHealth() + " health.\n";
					saveGame(msgAuthor, player, msgChannel);
				}
			}
			message += "```";
			msgChannel.sendMessage(message).queue();
		}
	}

	public Player getPlayerFromSave(User msgAuthor, MessageChannel msgChannel) {
		String saveFile;
		Player player = null;
		try {
			saveFile = getSave(msgAuthor.getId());
			if (saveFile.equals("error")) {
				msgChannel.sendMessage("Your save could not be found, make sure that you have started your adventure by sending '" + prefix + "sendAdventure'.");
			} else {
				player = new Player(getCertainPartOfSave(saveFile, 2), Integer.parseInt(getCertainPartOfSave(saveFile, 3)), getInventory(getCertainPartOfSave(saveFile, 4)), getWeapon(getCertainPartOfSave(saveFile, 5)), Integer.parseInt(getCertainPartOfSave(saveFile, 6)), getEnemy(getCertainPartOfSave(saveFile, 7), Integer.parseInt(getCertainPartOfSave(saveFile, 8))), getCurrentLocation(getCertainPartOfSave(saveFile, 9)), getKnownLocations(getCertainPartOfSave(saveFile, 10)));										
			}
		} catch (IOException e1) {
			msgChannel.sendMessage("Please tell Noah that this happened, would be nice if you @ him now so he knows where it happened").queue();
			e1.printStackTrace();
		}
		return player;
	}

	public void createNewSave(User author) throws IOException {	
		File file = new File("C:\\SaveFiles.txt");								//Where the save files are kept
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String input = "";														//Will be used to store the entire file, and then to output it
		String line;															//Used to store a single line
		boolean newPlayer = true;
		for (int i = 0; i < availableLineNum; i++) {
			line = reader.readLine();
			if (!line.substring(0, 18).equals(author.getId())) {
				input += line + '\n';
			} else {
				newPlayer = false;
			}
		}
		input += author.getId(); 			//Discord ID
		input += "--";
		input += author.getName(); 			//Player Name
		input += "--";
		input += 1; 						//Level
		input += "--";
		input += "Shortsword-1";			//Inventory
		input += "--";
		input += "Shortsword";				//Weapon Name
		input += "--";
		input += 0;							//Experience
		input += "--";
		input += "Goblin";					//Enemy Currently Fighting Name
		input += "--";
		input += "10";						//Enemy Currently Fighting Current Health
		input += "--";
		input += "Forest1";					//Current Location
		input += "--";
		input += "Forest1";					//Known Locations
		input += '\n';
		if (newPlayer) {
			availableLineNum++;
		}
		FileOutputStream out = new FileOutputStream("C:\\SaveFiles.txt");		//Where the file is outputted
		out.write(input.getBytes());											//File is outputted
	}

	public void saveGame(User author, Player player, MessageChannel msgChannel) {
		File file = new File("C:\\SaveFiles.txt");								//Where the save files are kept
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			msgChannel.sendMessage("Please tell Noah that this happened, would be nice if you @ him now so he knows where it happened").queue();
			e.printStackTrace();
		}
		String input = "";														//Will be used to store the entire file, and then to output it
		String line;															//Used to store a single line
		boolean newPlayer = true;
		for (int i = 0; i < availableLineNum; i++) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				msgChannel.sendMessage("Please tell Noah that this happened, would be nice if you @ him now so he knows where it happened").queue();
				e.printStackTrace();
			}
			if (!line.substring(0, 18).equals(author.getId())) {
				input += line + '\n';
			} else {
				newPlayer = false;
			}
		}
		input += author.getId(); 								//Discord ID
		input += "--";
		input += author.getName(); 								//Player Name
		input += "--";
		input += player.returnLevel(); 							//Level
		input += "--";
		input += player.InventoryToString();				//Inventory
		input += "--";
		input += player.returnWeaponAsString();					//Weapon Name
		input += "--";
		input += 0;												//Experience
		input += "--";
		input += player.returnEnemy().returnName();				//Enemy Currently Fighting Name
		input += "--";
		input += player.returnEnemy().returnCurrentHealth();	//Enemy Currently Fighting Current Health
		input += "--";		
		input += player.returnCurrentLocation().returnName();	//Current Location
		input += "--";
		input += player.knownLocationsToString();			//Known Locations
		input += '\n';
		if (newPlayer) {
			availableLineNum++;
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream("C:\\SaveFiles.txt");		//Where the file is outputted
		} catch (FileNotFoundException e) {
			msgChannel.sendMessage("Please tell Noah that this happened, would be nice if you @ him now so he knows where it happened").queue();
			e.printStackTrace();
		}
		try {
			out.write(input.getBytes());							//File is outputted
		} catch (IOException e) {
			msgChannel.sendMessage("Please tell Noah that this happened, would be nice if you @ him now so he knows where it happened").queue();
			e.printStackTrace();
		}
	}

	public String getSave(String discordId) throws IOException {
		File file = new File("C:\\SaveFiles.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		for (int i = 0; i < availableLineNum; i++) {
			line = reader.readLine();
			if (line.substring(0, 18).equals(discordId)) {
				return line;
			}
		}
		return "error";
	}

	public String getCertainPartOfSave(String saveFile, int part) {
		return saveFile.split("--")[part - 1];
	}

	public Weapon getWeapon(String name) {
		if (name.equals("Dagger")) {
			return new Dagger(1);
		} else {
			return new Shortsword(1);
		}
	}

	public Enemy getEnemy(String name, int health) {
		Enemy enemy;
		if (name.equals("Goblin")) {
			enemy = new Goblin();
		} else {
			enemy = new Wolf();
		}
		if (health == -1) {
			return enemy;
		}
		enemy.setHealth(health);
		return enemy;
	}

	public Loot[] getInventory(String itemList) {
		String[] items = itemList.split("-");
		Loot[] inv = new Loot[200];
		for (int i = 0; i < items.length - 1; i += 2) {
			if (items[i].equals("Dagger")) {
				inv[i] = new Dagger(Integer.parseInt(items[i + 1]));
			} else if (items[i].equals("Dagger")) {
				inv[i] = new Shortsword(Integer.parseInt(items[i + 1]));
			} else if (items[i].equals("Wolf Fur")) {
				inv[i] = new WolfFur(Integer.parseInt(items[i + 1]));
			} else if (items[i].equals("Gold")){
				inv[i] = new Gold(Integer.parseInt(items[i + 1]));
			}
		}
		return inv;
	}

	public Location getCurrentLocation(String location) {
		if (location.equals("Forest2")) {
			return new Forest2();
		} else if (location.equals("Forest3")) {
			return new Forest3();
		} else {
			return new Forest1();
		}
	}

	public Location[] getKnownLocations(String locationList) {
		String[] locations = locationList.split("-");
		Location[] knownLocations = new Location[3];
		for (int i = 0; i < locations.length; i++) {
			if (locations.equals("Forest1")) {
				knownLocations[i] = new Forest1();
			} else if (locations[i].equals("Forest2")) {
				knownLocations[i] = new Forest2();
			} else if (locations[i].equals("Forest3")) {
				knownLocations[i] = new Forest3();
			}
		}
		return knownLocations;
	}

	public String lootArrayToString(Loot[] loot) {
		String str = "";
		if (loot[0] == null) {
			str = "no items, ";
		} else {
			for (int i = 0; i < loot.length; i++) {
				if (loot[i].returnAmount() == 1) {
					str += "1 " + loot[i].returnName() + ", ";
				} else {
					str += loot[i].returnAmount() + " " + loot[i].returnPluralName() + ", ";
				}
				if (loot[i + 1] == null) {
					break;
				}
			}
		}
		str += "and ";
		return str;
	}
}