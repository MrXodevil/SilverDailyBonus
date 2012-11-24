package fr.xodevil_fire.SilverDailyBonus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xodevil_fire.SilverDailyBonus.listeners.SDBPlayerListener;

public class SDBCore extends JavaPlugin {
	
	private SDBPlayerListener playerListener = new SDBPlayerListener(this);
	public static ArrayList<String> playersBonus = new ArrayList<String>();
    private FileConfiguration conf = null;
    private File groupsConfig = null;
	public static Economy economy = null;
	public static Permission perms = null;
	
	@Override
	public void onDisable() {
		String date = new SimpleDateFormat("yy/MM/dd").format(Calendar.getInstance().getTime());
		for (String player : playersBonus) {
			if (!player.contains(date)) {
				playersBonus.remove(player);
			}
		}
		this.saveAll();
	}
	
	@Override
	public void onEnable() {
		String date = new SimpleDateFormat("yy/MM/dd").format(Calendar.getInstance().getTime());
		for (String player : playersBonus) {
			if (!player.contains(date)) {
				playersBonus.remove(player);
			}
		}
		this.getServer().getPluginManager().registerEvents(playerListener, this);
		this.loadAll();
		this.loadConfig();
		this.setupEconomy();
		this.setupPermissions();
		this.loadGroupConfig();
	}
	
	private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
	}
	
	private boolean setupPermissions() {
		@SuppressWarnings("rawtypes")
        RegisteredServiceProvider rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = (Permission)rsp.getProvider();
        return perms != null;
	}
	
	public void loadConfig() {
		FileConfiguration config = this.getConfig();
		config.addDefault("Bonus.Items.Activer", true);
		config.addDefault("Bonus.Items.ID", 260);
		config.addDefault("Bonus.Items.Nombre", 1);
		config.addDefault("Bonus.Monnaie.Activer", false);
		config.addDefault("Bonus.Monnaie.Montant", 260);
		config.addDefault("Bonus.ParGroupe.Activer", false);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
    public void loadGroupConfig() {
        if (groupsConfig == null) {
        groupsConfig = new File(getDataFolder(), "groups.yml");
        }
        if (!groupsConfig.exists()) {
			try {
				groupsConfig.createNewFile();
			} catch (Exception e) {
				System.out.println("----------------------------------------");
				System.out.println("|   SilverDailyBonus by xodevil_fire   |");
				System.out.println("|       Erreur ! Voir ci dessous :     |");
				System.out.println("----------------------------------------");
				System.out.println(e.toString());
				System.out.println("----------------------------------------");
			}
		}
        conf = YamlConfiguration.loadConfiguration(groupsConfig);
        InputStream defConfigStream = this.getResource("groups.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            conf.setDefaults(defConfig);
        }
    }
    
    public void saveGroupConfig() {
        if (conf == null || groupsConfig == null) {
        return;
        }
        try {
            getGroupConfig().save(groupsConfig);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + groupsConfig, ex);
        }
    }
    
    public FileConfiguration getGroupConfig() {
        if (groupsConfig == null) {
            this.loadGroupConfig();
        }
        return conf;
    }
	
	public void saveAll() {
		File folder = new File("plugins/SilverDailyBonus");
		if (!folder.exists()) {
			folder.mkdir();
		}
		File datafile = new File("plugins/SilverDailyBonus/players.dat");
		if (!datafile.exists()) {
			try {
				datafile.createNewFile();
			} catch (Exception e) {
				System.out.println("----------------------------------------");
				System.out.println("|   SilverDailyBonus by xodevil_fire   |");
				System.out.println("|       Erreur ! Voir ci dessous :     |");
				System.out.println("----------------------------------------");
				System.out.println(e.toString());
				System.out.println("----------------------------------------");
			}
		}
		try {
			FileWriter fw = new FileWriter(datafile.getAbsoluteFile(), false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(playersBonus.size());
			bw.newLine();
			for (int i = 0; i < playersBonus.size(); i++) 
			{
				try {
					bw.write(playersBonus.get(i));
					bw.newLine();
				} catch (Exception e) {
					System.out.println("----------------------------------------");
					System.out.println("|   SilverDailyBonus by xodevil_fire   |");
					System.out.println("|       Erreur ! Voir ci dessous :     |");
					System.out.println("----------------------------------------");
					System.out.println(e.toString());
					System.out.println("----------------------------------------");
				}
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			System.out.println("----------------------------------------");
			System.out.println("|   SilverDailyBonus by xodevil_fire   |");
			System.out.println("|       Erreur ! Voir ci dessous :     |");
			System.out.println("----------------------------------------");
			System.out.println(e.toString());
			System.out.println("----------------------------------------");
		}
	}

	public void loadAll() {
		File folder = new File("plugins/SilverDailyBonus");
		if (!folder.exists()) {
			folder.mkdir();
		}
		File datafile = new File("plugins/SilverDailyBonus/players.dat");
		if (datafile.exists()) {
			try {
				FileReader fr = new FileReader(datafile.getAbsoluteFile());
				BufferedReader br = new BufferedReader(fr);
				int size = br.read();
				br.readLine();
				for (int i = 0; i < size; i++) {
					playersBonus.add(br.readLine());
				}
				br.close();
				fr.close();
			} catch (IOException e) {
				System.out.println("----------------------------------------");
				System.out.println("|   SilverDailyBonus by xodevil_fire   |");
				System.out.println("|       Erreur ! Voir ci dessous :     |");
				System.out.println("----------------------------------------");
				System.out.println(e.getCause());
				System.out.println("----------------------------------------");
			}
		} else {
			try {
				datafile.createNewFile();
			} catch (Exception e) {
				System.out.println("----------------------------------------");
				System.out.println("|   SilverDailyBonus by xodevil_fire   |");
				System.out.println("|       Erreur ! Voir ci dessous :     |");
				System.out.println("----------------------------------------");
				System.out.println(e.toString());
				System.out.println("----------------------------------------");
			}
		}
	}

}
