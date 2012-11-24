package fr.xodevil_fire.SilverDailyBonus.listeners;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import fr.xodevil_fire.SilverDailyBonus.SDBCore;

public class SDBPlayerListener implements Listener {

	private SDBCore plugin;
	public SDBPlayerListener(SDBCore sdbCore) {
		this.plugin = sdbCore;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("silverdailybonus.getbonus")) {
			String date = new SimpleDateFormat("yy/MM/dd").format(Calendar.getInstance().getTime());
			if (!SDBCore.playersBonus.contains(event.getPlayer() + date)) {
				if (plugin.getConfig().getBoolean("Bonus.ParGroupe.Activer") == true) {
					if (plugin.getGroupConfig().getConfigurationSection("Groupes") != null) {
						Set<String> groupes = plugin.getGroupConfig().getConfigurationSection("Groupes").getKeys(false);
						Integer nbFound = 0;
						for (Object groupe : groupes) {
							if (event.getPlayer().hasPermission("silverdailybonus.getbonus.groups." + groupe)) {
								if ((plugin.getGroupConfig().getBoolean("Groupes." + groupe + ".Bonus.Items.Activer") == true) || (plugin.getGroupConfig().getBoolean("Groupes." + groupe + ".Bonus.Monnaie.Activer") == true)) {
									if ((plugin.getGroupConfig().getBoolean("Groupes." + groupe + ".Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Groupes." + groupe + ".Bonus.Monnaie.Activer") == false)) {
										if (Material.getMaterial((plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.ID"))) != null) {
											event.getPlayer().getInventory().addItem(new ItemStack(plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.ID"), plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.Nombre")));
											event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.Nombre") + " de " + Material.getMaterial(plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.ID")).name().toLowerCase() + " pour avoir joué aujourd'hui !");
										}
									} else if ((plugin.getGroupConfig().getBoolean("Groupes." + groupe + ".Bonus.Items.Activer") == true) && (plugin.getGroupConfig().getBoolean("Groupes." + groupe + ".Bonus.Monnaie.Activer") == true)) {
										event.getPlayer().getInventory().addItem(new ItemStack(plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.ID"), plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.Nombre")));
										SDBCore.economy.depositPlayer(event.getPlayer().getName(), plugin.getGroupConfig().getDouble("Groupes." + groupe + ".Bonus.Monnaie.Montant"));
										event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.Nombre") + " de " + Material.getMaterial(plugin.getGroupConfig().getInt("Groupes." + groupe + ".Bonus.Items.ID")).name().toLowerCase() + " ainsi que " + SDBCore.economy.format(plugin.getGroupConfig().getDouble("Groupes." + groupe + ".Bonus.Monnaie.Montant"))  + " pour avoir joué aujourd'hui !");
										SDBCore.playersBonus.add(event.getPlayer() + date);
									} else {
										SDBCore.economy.depositPlayer(event.getPlayer().getName(), plugin.getGroupConfig().getDouble("Groupes." + groupe + ".Bonus.Monnaie.Montant"));
										event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + SDBCore.economy.format(plugin.getGroupConfig().getDouble("Groupes." + groupe + ".Bonus.Monnaie.Montant")) + " pour avoir joué aujourd'hui !");
									}
								}
								nbFound++;
								SDBCore.playersBonus.add(event.getPlayer() + date);
								plugin.saveAll();
							}
						}
						if (nbFound == 0) {
							if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) || (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == true)) {
								if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == false)) {
									if (Material.getMaterial((plugin.getConfig().getInt("Bonus.Items.ID"))) != null) {
										event.getPlayer().getInventory().addItem(new ItemStack(plugin.getConfig().getInt("Bonus.Items.ID"), plugin.getConfig().getInt("Bonus.Items.Nombre")));
										event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + plugin.getConfig().getInt("Bonus.Items.Nombre") + " de " + Material.getMaterial(plugin.getConfig().getInt("Bonus.Items.ID")).name().toLowerCase() + " pour avoir joué aujourd'hui !");
									}
								} else if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == true)) {
									event.getPlayer().getInventory().addItem(new ItemStack(plugin.getConfig().getInt("Bonus.Items.ID"), plugin.getConfig().getInt("Bonus.Items.Nombre")));
									SDBCore.economy.depositPlayer(event.getPlayer().getName(), plugin.getConfig().getDouble("Bonus.Monnaie.Montant"));
									event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + plugin.getConfig().getInt("Bonus.Items.Nombre") + " de " + Material.getMaterial(plugin.getConfig().getInt("Bonus.Items.ID")).name().toLowerCase() + " ainsi que " + SDBCore.economy.format(plugin.getConfig().getDouble("Bonus.Monnaie.Montant"))  + " pour avoir joué aujourd'hui !");
									SDBCore.playersBonus.add(event.getPlayer() + date);
								} else {
									SDBCore.economy.depositPlayer(event.getPlayer().getName(), plugin.getConfig().getDouble("Bonus.Monnaie.Montant"));
									event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + SDBCore.economy.format(plugin.getConfig().getDouble("Bonus.Monnaie.Montant")) + " pour avoir joué aujourd'hui !");
								}
								SDBCore.playersBonus.add(event.getPlayer() + date);
								plugin.saveAll();
							}
						}
					} 
				} else if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) || (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == true)) {
					if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == false)) {
						if (Material.getMaterial((plugin.getConfig().getInt("Bonus.Items.ID"))) != null) {
							event.getPlayer().getInventory().addItem(new ItemStack(plugin.getConfig().getInt("Bonus.Items.ID"), plugin.getConfig().getInt("Bonus.Items.Nombre")));
							event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + plugin.getConfig().getInt("Bonus.Items.Nombre") + " de " + Material.getMaterial(plugin.getConfig().getInt("Bonus.Items.ID")).name().toLowerCase() + " pour avoir joué aujourd'hui !");
						}
					} else if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == true)) {
						event.getPlayer().getInventory().addItem(new ItemStack(plugin.getConfig().getInt("Bonus.Items.ID"), plugin.getConfig().getInt("Bonus.Items.Nombre")));
						SDBCore.economy.depositPlayer(event.getPlayer().getName(), plugin.getConfig().getDouble("Bonus.Monnaie.Montant"));
						event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + plugin.getConfig().getInt("Bonus.Items.Nombre") + " de " + Material.getMaterial(plugin.getConfig().getInt("Bonus.Items.ID")).name().toLowerCase() + " ainsi que " + SDBCore.economy.format(plugin.getConfig().getDouble("Bonus.Monnaie.Montant"))  + " pour avoir joué aujourd'hui !");
						SDBCore.playersBonus.add(event.getPlayer() + date);
					} else {
						SDBCore.economy.depositPlayer(event.getPlayer().getName(), plugin.getConfig().getDouble("Bonus.Monnaie.Montant"));
						event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + SDBCore.economy.format(plugin.getConfig().getDouble("Bonus.Monnaie.Montant")) + " pour avoir joué aujourd'hui !");
					}
					SDBCore.playersBonus.add(event.getPlayer() + date);
					plugin.saveAll();
				}
					
			}
		}
	}

}
