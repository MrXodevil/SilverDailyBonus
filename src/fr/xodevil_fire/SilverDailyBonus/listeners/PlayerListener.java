package fr.xodevil_fire.SilverDailyBonus.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.xodevil_fire.SilverDailyBonus.Core;

public class PlayerListener implements Listener {

	private Core plugin;
	private HashMap<Player, Boolean> playerBonus = new HashMap<Player, Boolean>();
	public PlayerListener(Core sdbCore) {
		this.plugin = sdbCore;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
			String date = new SimpleDateFormat("yy/MM/dd").format(Calendar.getInstance().getTime());
			if (!Core.playersBonus.contains(event.getPlayer() + date)) {
				if (plugin.getConfig().getBoolean("Bonus.ParGroupe.Activer") == true) {
					if (plugin.getGroupConfig().getConfigurationSection("Groupes") != null) {
						Set<String> groupes = plugin.getGroupConfig().getConfigurationSection("Groupes").getKeys(false);
						Integer nbFound = 0;
						for (String groupe : groupes) {
							ConfigurationSection cs = plugin.getGroupConfig().getConfigurationSection("Groupes." + groupe);
							if (event.getPlayer().hasPermission("silverdailybonus.getbonus.groups." + groupe)) {
								if ((cs.getBoolean("Bonus.Items.Activer") == true) || (cs.getBoolean("Bonus.Monnaie.Activer") == true)) {
									if ((cs.getBoolean("Bonus.Items.Activer") == true) && (cs.getBoolean("Bonus.Monnaie.Activer") == false)) {
										if (this.playerBonus.containsKey(event.getPlayer())) {
											return;
										}
										Integer nbFoundB = 0;
										Integer i = 0;
										StringBuffer blocks = new StringBuffer();
										for (String block : plugin.getGroupItems(groupe)) {
											Integer b = plugin.getItemCs(block).getInt("ID");
											if (Material.getMaterial(b) != null) {
												nbFoundB++;
												blocks.append(Integer.parseInt(plugin.getGroupNumber(groupe)[i]) + " de " + plugin.getItemCs(block).getString("DisplayName").toLowerCase().replaceAll("&", "§") + ChatColor.AQUA + ", ");
												ItemStack is = new ItemStack(Material.getMaterial(b), Integer.parseInt(plugin.getGroupNumber(groupe)[i]), (byte) plugin.getItemCs(block).getInt("Metadata"));
												ItemMeta meta = (ItemMeta) is.getItemMeta();
												if (!plugin.getItemCs(block).getString("DisplayName").equals("false")) {
													meta.setDisplayName(plugin.getItemCs(block).getString("DisplayName").replaceAll("&", "§"));
												}
												if (!plugin.getItemCs(block).getString("Lore").equals("false")) {
													List<String> description = new ArrayList<String>();
													for (String lore : plugin.getItemCs(block).getStringList("Lore")) {
														description.add(lore.replaceAll("&", "§"));
													}
													meta.setLore(description);
												}
												if (!plugin.getItemCs(block).getString("Enchantements").equals("false")) {
													for (String enchantment : plugin.getEnchantments(block)) {
														meta.addEnchant(Enchantment.getByName(enchantment.split(":")[0]), Integer.parseInt(enchantment.split(":")[1]), true);
													}
												}
												is.setItemMeta(meta);
												event.getPlayer().getInventory().addItem(is);
												event.getPlayer().updateInventory();
											}
											i++;
										}
										if (nbFoundB >= 1) {
											event.getPlayer().sendMessage(ChatColor.GRAY + "[Bonus] " + ChatColor.AQUA + "Vous venez de recevoir " + blocks.substring(0, blocks.length() - 2).toString() + " pour avoir joué aujourd'hui !");
											this.playerBonus.put(event.getPlayer(), true);
										}
									} else if ((cs.getBoolean("Bonus.Items.Activer") == true) && (cs.getBoolean("Bonus.Monnaie.Activer") == true)) {
										if (this.playerBonus.containsKey(event.getPlayer())) {
											return;
										}
										Integer nbFoundB = 0;
										Integer i = 0;
										StringBuffer blocks = new StringBuffer();
										for (String block : plugin.getGroupItems(groupe)) {
											Integer b = plugin.getItemCs(block).getInt("ID");
											if (Material.getMaterial(b) != null) {
												nbFoundB++;
												blocks.append(Integer.parseInt(plugin.getGroupNumber(groupe)[i]) + " de " + plugin.getItemCs(block).getString("DisplayName").toLowerCase().replaceAll("&", "§") + ChatColor.AQUA + ", ");
												ItemStack is = new ItemStack(Material.getMaterial(b), Integer.parseInt(plugin.getGroupNumber(groupe)[i]), (byte) plugin.getItemCs(block).getInt("Metadata"));
												ItemMeta meta = (ItemMeta) is.getItemMeta();
												if (!plugin.getItemCs(block).getString("DisplayName").equals("false")) {
													meta.setDisplayName(plugin.getItemCs(block).getString("DisplayName").replaceAll("&", "§"));
												}
												if (!plugin.getItemCs(block).getString("Lore").equals("false")) {
													List<String> description = new ArrayList<String>();
													for (String lore : plugin.getItemCs(block).getStringList("Lore")) {
														description.add(lore.replaceAll("&", "§"));
													}
													meta.setLore(description);
												}
												if (!plugin.getItemCs(block).getString("Enchantements").equals("false")) {
													for (String enchantment : plugin.getEnchantments(block)) {
														meta.addEnchant(Enchantment.getByName(enchantment.split(":")[0]), Integer.parseInt(enchantment.split(":")[1]), true);
													}
												}
												is.setItemMeta(meta);
												event.getPlayer().getInventory().addItem(is);
												event.getPlayer().updateInventory();
											}
											i++;
										}
										if (nbFoundB >= 1) {
											Core.economy.depositPlayer(event.getPlayer().getName(), cs.getDouble("Bonus.Monnaie.Montant"));
											event.getPlayer().sendMessage(ChatColor.GRAY + "[Bonus] " + ChatColor.AQUA + "Vous venez de recevoir " + blocks.substring(0, blocks.length() - 2).toString() + " ainsi que " + Core.economy.format(cs.getDouble("Bonus.Monnaie.Montant"))  + " pour avoir joué aujourd'hui !");
											this.playerBonus.put(event.getPlayer(), true);
										}
									} else {
										if (this.playerBonus.containsKey(event.getPlayer())) {
											return;
										}
										Core.economy.depositPlayer(event.getPlayer().getName(), cs.getDouble("Bonus.Monnaie.Montant"));
										event.getPlayer().sendMessage(ChatColor.GRAY + "[Bonus] " + ChatColor.AQUA + "Vous venez de recevoir " + Core.economy.format(cs.getDouble("Bonus.Monnaie.Montant")) + " pour avoir joué aujourd'hui !");
										this.playerBonus.put(event.getPlayer(), true);
									}
								}
								nbFound++;
								Core.playersBonus.add(event.getPlayer() + date);
								plugin.saveAll();
							}
						}
						/*if (nbFound == 0) {
							if (event.getPlayer().hasPermission("silverdailybonus.getbonus")) {
								if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) || (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == true)) {
									if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == false)) {
										if (this.playerBonus.containsKey(event.getPlayer())) {
											return;
										}
										Integer nbFoundB = 0;
										Integer i = 0;
										StringBuffer blocks = new StringBuffer();
										for (String block : plugin.getItems()) {
											Integer b = Integer.parseInt(block);
											if (Material.getMaterial(b) != null) {
												nbFoundB++;
												blocks.append(Integer.parseInt(plugin.getNumber()[i]) + " de " + Material.getMaterial(b).name().toLowerCase() +", ");
												event.getPlayer().getInventory().addItem(new ItemStack(b, Integer.parseInt(plugin.getNumber()[i])));
											}
											i++;
										}
										if (nbFoundB >= 1) {
											event.getPlayer().sendMessage(ChatColor.GRAY + "[Bonus] " + ChatColor.AQUA + "Vous venez de recevoir " + blocks.substring(0, blocks.length() - 2).toString() + " pour avoir joué aujourd'hui !");
											this.playerBonus.put(event.getPlayer(), true);
										}
									} else if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == true)) {
										if (this.playerBonus.containsKey(event.getPlayer())) {
											return;
										}
										Integer nbFoundB = 0;
										StringBuffer blocks = new StringBuffer();
										Integer i = 0;
										for (String block : plugin.getItems()) {
											Integer b = Integer.parseInt(block);
											if (Material.getMaterial(b) != null) {
												nbFoundB++;
												blocks.append(Integer.parseInt(plugin.getNumber()[i]) + " de " + Material.getMaterial(b).name().toLowerCase() +", ");
												event.getPlayer().getInventory().addItem(new ItemStack(b, Integer.parseInt(plugin.getNumber()[i])));
											}
											i++;
										}
										Core.economy.depositPlayer(event.getPlayer().getName(), plugin.getConfig().getDouble("Bonus.Monnaie.Montant"));
										if (nbFoundB >= 1) {
											event.getPlayer().sendMessage(ChatColor.GRAY + "[Bonus] " + ChatColor.AQUA + "Vous venez de recevoir " + " " + blocks.substring(0, blocks.length() - 2).toString() + " ainsi que " + Core.economy.format(plugin.getConfig().getDouble("Bonus.Monnaie.Montant")) + " pour avoir joué aujourd'hui !");
											this.playerBonus.put(event.getPlayer(), true);
										}
									} else {
										if (this.playerBonus.containsKey(event.getPlayer())) {
											return;
										}
										Core.economy.depositPlayer(event.getPlayer().getName(), plugin.getConfig().getDouble("Bonus.Monnaie.Montant"));
										event.getPlayer().sendMessage(ChatColor.GRAY + "[Bonus] " + ChatColor.AQUA + "Vous venez de recevoir " + Core.economy.format(plugin.getConfig().getDouble("Bonus.Monnaie.Montant")) + " pour avoir joué aujourd'hui !");
										this.playerBonus.put(event.getPlayer(), true);
									}
									Core.playersBonus.add(event.getPlayer() + date);
									plugin.saveAll();
								}
							}
						}*/
					}
				} else {
					if (event.getPlayer().hasPermission("silverdailybonus.getbonus")) {
						if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) || (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == true)) {
							if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == false)) {
								if (this.playerBonus.containsKey(event.getPlayer())) {
									return;
								}
								Integer nbFoundB = 0;
								Integer i = 0;
								StringBuffer blocks = new StringBuffer();
								for (String block : plugin.getItems()) {
									Integer b = plugin.getItemCs(block).getInt("ID");
									if (Material.getMaterial(b) != null) {
										nbFoundB++;
										blocks.append(Integer.parseInt(plugin.getNumber()[i]) + " de " + plugin.getItemCs(block).getString("DisplayName").toLowerCase().replaceAll("&", "§") + ChatColor.AQUA + ", ");
										ItemStack is = new ItemStack(Material.getMaterial(b), Integer.parseInt(plugin.getNumber()[i]), (byte) plugin.getItemCs(block).getInt("Metadata"));
										ItemMeta meta = (ItemMeta) is.getItemMeta();
										if (!plugin.getItemCs(block).getString("DisplayName").equals("false")) {
											meta.setDisplayName(plugin.getItemCs(block).getString("DisplayName").replaceAll("&", "§"));
										}
										if (!plugin.getItemCs(block).getString("Lore").equals("false")) {
											List<String> description = new ArrayList<String>();
											for (String lore : plugin.getItemCs(block).getStringList("Lore")) {
												description.add(lore.replaceAll("&", "§"));
											}
											meta.setLore(description);
										}
										if (!plugin.getItemCs(block).getString("Enchantements").equals("false")) {
											for (String enchantment : plugin.getEnchantments(block)) {
												meta.addEnchant(Enchantment.getByName(enchantment.split(":")[0]), Integer.parseInt(enchantment.split(":")[1]), true);
											}
										}
										is.setItemMeta(meta);
										event.getPlayer().getInventory().addItem(is);
										event.getPlayer().updateInventory();
									}
									i++;
								}
								if (nbFoundB >= 1) {
									event.getPlayer().sendMessage(ChatColor.GRAY + "[Bonus] " + ChatColor.AQUA + "Vous venez de recevoir " + blocks.substring(0, blocks.length() - 2).toString() + " pour avoir joué aujourd'hui !");
									this.playerBonus.put(event.getPlayer(), true);
								}
							} else if ((plugin.getConfig().getBoolean("Bonus.Items.Activer") == true) && (plugin.getConfig().getBoolean("Bonus.Monnaie.Activer") == true)) {
								if (this.playerBonus.containsKey(event.getPlayer())) {
									return;
								}
								Integer nbFoundB = 0;
								StringBuffer blocks = new StringBuffer();
								Integer i = 0;
								for (String block : plugin.getItems()) {
									Integer b = plugin.getItemCs(block).getInt("ID");
									if (Material.getMaterial(b) != null) {
										nbFoundB++;
										blocks.append(Integer.parseInt(plugin.getNumber()[i]) + " de " + plugin.getItemCs(block).getString("DisplayName").toLowerCase().replaceAll("&", "§") + ChatColor.AQUA + ", ");
										ItemStack is = new ItemStack(Material.getMaterial(b), Integer.parseInt(plugin.getNumber()[i]), (byte) plugin.getItemCs(block).getInt("Metadata"));
										ItemMeta meta = (ItemMeta) is.getItemMeta();
										if (!plugin.getItemCs(block).getString("DisplayName").equals("false")) {
											meta.setDisplayName(plugin.getItemCs(block).getString("DisplayName").replaceAll("&", "§"));
										}
										if (!plugin.getItemCs(block).getString("Lore").equals("false")) {
											List<String> description = new ArrayList<String>();
											for (String lore : plugin.getItemCs(block).getStringList("Lore")) {
												description.add(lore.replaceAll("&", "§"));
											}
											meta.setLore(description);
										}
										if (!plugin.getItemCs(block).getString("Enchantements").equals("false")) {
											for (String enchantment : plugin.getEnchantments(block)) {
												meta.addEnchant(Enchantment.getByName(enchantment.split(":")[0]), Integer.parseInt(enchantment.split(":")[1]), true);
											}
										}
										is.setItemMeta(meta);
										event.getPlayer().getInventory().addItem(is);
										event.getPlayer().updateInventory();
									}
									i++;
								}
								Core.economy.depositPlayer(event.getPlayer().getName(), plugin.getConfig().getDouble("Bonus.Monnaie.Montant"));
								if (nbFoundB >= 1) {
									event.getPlayer().sendMessage(ChatColor.GRAY + "[Bonus] " + ChatColor.AQUA + "Vous venez de recevoir " + blocks.substring(0, blocks.length() - 2).toString() + " ainsi que " + Core.economy.format(plugin.getConfig().getDouble("Bonus.Monnaie.Montant")) + " pour avoir joué aujourd'hui !");
									this.playerBonus.put(event.getPlayer(), true);
								}
							} else {
								if (this.playerBonus.containsKey(event.getPlayer())) {
									return;
								}
								Core.economy.depositPlayer(event.getPlayer().getName(), plugin.getConfig().getDouble("Bonus.Monnaie.Montant"));
								event.getPlayer().sendMessage(ChatColor.AQUA + "Vous venez de recevoir " + Core.economy.format(plugin.getConfig().getDouble("Bonus.Monnaie.Montant")) + " pour avoir joué aujourd'hui !");
								this.playerBonus.put(event.getPlayer(), true);
							}
							Core.playersBonus.add(event.getPlayer() + date);
							plugin.saveAll();
						}
					}
				}
			}
	}

}
