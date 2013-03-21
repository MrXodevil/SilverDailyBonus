package fr.xodevil_fire.SilverDailyBonus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xodevil_fire.SilverDailyBonus.API.logger.DebugManager;
import fr.xodevil_fire.SilverDailyBonus.listeners.PlayerListener;

public class Core extends JavaPlugin {
	
	private PlayerListener playerListener = new PlayerListener(this);
	public static ArrayList<String> playersBonus = new ArrayList<String>();
    private FileConfiguration conf = null;
    private File groupsConfig = null;
    private FileConfiguration itemsConf = null;
    private File itemsConfig = null;
	public static Economy economy = null;
	public static Permission perms = null;
	Boolean vault = true;
	
	@Override
	public void onDisable() {
		this.saveAll();
		if (this.getConfig().getBoolean("Bonus.ParGroupe.Activer") == true) {
			this.saveGroupConfig();
		}
	}
	
	@Override
	public void onEnable() {
	    if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
	        vault = false;
	    }
		this.getServer().getPluginManager().registerEvents(playerListener, this);
		this.loadAll();
		this.loadConfig();
		if ((this.getConfig().getBoolean("Bonus.Monnaie.Activer") == true) && (vault == false)) {
			this.getConfig().set("Bonus.Monnaie.Activer", false);
			this.saveConfig();
			this.getLogger().warning("Vault est requis afin d'activer les bonus de monnaie !");
		}
		if (vault == true) {
		this.setupEconomy();
		this.setupPermissions(); }
		this.loadItemsConfig();
		if (this.getItemsConfig().getConfigurationSection("Items") == null) {
			String[] lore = {"&eJe peut etre croquee","&eJ'aime a etre verte"};
			this.getItemsConfig().createSection("Items");
			this.getItemsConfig().createSection("Items.Test");
			this.getItemsConfig().addDefault("Items.Test.ID", 260);
			this.getItemsConfig().addDefault("Items.Test.Metadata", 0);
			this.getItemsConfig().addDefault("Items.Test.DisplayName", "&aPomme");
			this.getItemsConfig().addDefault("Items.Test.Lore", lore);
			this.getItemsConfig().addDefault("Items.Test.Enchantements", "DAMAGE_ALL:5,FIRE_ASPECT:1");
			this.saveItemsConfig();
		}
		if (this.getConfig().getBoolean("Bonus.ParGroupe.Activer") == true) {
			this.loadGroupConfig();
			if (this.getGroupConfig().getConfigurationSection("Groupes") == null) {
				this.getGroupConfig().createSection("Groupes");
				this.getGroupConfig().createSection("Groupes.Test");
				this.getGroupConfig().createSection("Groupes.Test.Bonus");
				this.getGroupConfig().createSection("Groupes.Test.Bonus.Items");
				this.getGroupConfig().createSection("Groupes.Test.Bonus.Monnaie");
				this.getGroupConfig().addDefault("Groupes.Test.Bonus.Items.Activer", true);
				this.getGroupConfig().addDefault("Groupes.Test.Bonus.Items.ID", 261);
				this.getGroupConfig().addDefault("Groupes.Test.Bonus.Items.Nombre", 2);
				this.getGroupConfig().addDefault("Groupes.Test.Bonus.Monnaie.Activer", false);
				this.getGroupConfig().addDefault("Groupes.Test.Bonus.Monnaie.Montant", 261);
				this.saveGroupConfig();
			} else {
				Set<String> groupes = this.getGroupConfig().getConfigurationSection("Groupes").getKeys(false);
				for (String groupe : groupes) {
					ConfigurationSection cs = this.getGroupConfig().getConfigurationSection("Groupes." + groupe);
					if ((cs.getBoolean("Bonus.Monnaie.Activer") == true) && (vault == false)) {
						cs.set("Bonus.Monnaie.Activer", false);
						this.saveGroupConfig();
						this.getLogger().warning("Vault est requis afin d'activer les bonus de monnaie !");
					}
				}
			}
		}
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
		config.addDefault("Bonus.Items.ID", "260,261");
		config.addDefault("Bonus.Items.Nombre", "5,1");
		config.addDefault("Bonus.Monnaie.Activer", false);
		config.addDefault("Bonus.Monnaie.Montant", 260);
		config.addDefault("Bonus.ParGroupe.Activer", false);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public ConfigurationSection getItemCs(String name) {
		return this.getItemsConfig().getConfigurationSection("Items." + name);
	}
	
	public String[] getEnchantments(String itemName) {
		String e = this.getItemCs(itemName).getString("Enchantements");
		String[] enchantments = e.split(",\\s*");
		return enchantments;
	}
	
	public String[] getItems() {
		String i = this.getConfig().getString("Bonus.Items.ID");
		String[] items = {i};
		if (i.contains(",")) {
			items = i.split(",\\s*");
		}
	    return items;
	}
	
	public String[] getNumber() {
		String i = this.getConfig().getString("Bonus.Items.Nombre");
		String[] items = {i};
		if (i.contains(",")) {
			items = i.split(",\\s*");
		}
	    return items;
	}
	
    public void loadGroupConfig() {
        if (groupsConfig == null) {
        groupsConfig = new File(getDataFolder(), "groups.yml");
        }
        if (!groupsConfig.exists()) {
			try {
				groupsConfig.createNewFile();
			} catch (Exception e) {
				DebugManager.debug(this, e.toString(), e.getClass().toString());
			}
		}
        conf = YamlConfiguration.loadConfiguration(groupsConfig);
        InputStream defConfigStream = this.getResource("groups.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            conf.setDefaults(defConfig);
        }
        conf.options().copyDefaults(true);
    }
    
    public void saveGroupConfig() {
        if (conf == null || groupsConfig == null) {
        return;
        }
        try {
            getGroupConfig().save(groupsConfig);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + groupsConfig, e);
            DebugManager.debug(this, e.toString(), e.getClass().toString());
        }
    }
    
    public FileConfiguration getGroupConfig() {
        if (groupsConfig == null) {
            this.loadGroupConfig();
        }
        return conf;
    }
    
    public void loadItemsConfig() {
        if (itemsConfig == null) {
        itemsConfig = new File(getDataFolder(), "items.yml");
        }
        if (!itemsConfig.exists()) {
			try {
				itemsConfig.createNewFile();
			} catch (Exception e) {
				DebugManager.debug(this, e.toString(), e.getClass().toString());
			}
		}
        itemsConf = YamlConfiguration.loadConfiguration(itemsConfig);
        InputStream defConfigStream = this.getResource("items.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            itemsConf.setDefaults(defConfig);
        }
        itemsConf.options().copyDefaults(true);
    }
    
    public void saveItemsConfig() {
        if (itemsConf == null || itemsConfig == null) {
        return;
        }
        try {
            getItemsConfig().save(itemsConfig);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + itemsConfig, e);
            DebugManager.debug(this, e.toString(), e.getClass().toString());
        }
    }
    
    public FileConfiguration getItemsConfig() {
        if (itemsConfig == null) {
            this.loadItemsConfig();
        }
        return itemsConf;
    }
    
	public String[] getGroupItems(String group) {
		String i = this.getGroupConfig().getConfigurationSection("Groupes." + group).getString("Bonus.Items.ID");
		String[] items = i.split(",\\s*");
		return items;
	}
	
	public String[] getGroupNumber(String group) {
		String n = this.getGroupConfig().getConfigurationSection("Groupes." + group).getString("Bonus.Items.Nombre");
		String[] number = n.split(",\\s*");
		return number;
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
				DebugManager.debug(this, e.toString(), e.getClass().toString());
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
					DebugManager.debug(this, e.toString(), e.getClass().toString());
				}
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			DebugManager.debug(this, e.toString(), e.getClass().toString());
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
				DebugManager.debug(this, e.getCause().toString(), e.getCause().getClass().toString());
			}
		} else {
			try {
				datafile.createNewFile();
			} catch (Exception e) {
				DebugManager.debug(this, e.toString(), e.getClass().toString());
			}
		}
	}

}
