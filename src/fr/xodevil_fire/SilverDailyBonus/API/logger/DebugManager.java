package fr.xodevil_fire.SilverDailyBonus.API.logger;

import static java.lang.System.getProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class DebugManager {
	
	public static boolean debug(Plugin plugin, String log, String className) {
        try {
            StringBuffer debug = new StringBuffer();
            debug.append("\n=============").append(plugin.getName()).append(" has encountered an error!=============")
                    .append("\nStacktrace:\n").append(log).append("\n")
                    .append(plugin.getName()).append(" version: ").append(plugin.getDescription().getVersion())
                    .append("\nPlugins loaded: ").append(Arrays.asList(Bukkit.getPluginManager().getPlugins()))
                    .append("\nCraftBukkit version: ").append(Bukkit.getServer().getBukkitVersion())
                    .append("\nJava info: ").append(getProperty("java.version"))
                    .append("\nOS info: ").append(getProperty("os.arch")).append(" ").append(getProperty("os.name")).append(", ").append(getProperty("os.version"))
                    .append("\nPlease report this error to ").append(plugin.getName()).append(" the Bukkit forums!");
            try {
                //One-liner beauty.
                String FILE_NAME = String.format("%s_%s_%s.error.log", plugin.getName(), className, new BigInteger(1,
                        Arrays.copyOfRange(MessageDigest.getInstance("MD5").digest(debug.toString().getBytes()), 0, 6)).toString().substring(0, 6));
                File root = new File(plugin.getDataFolder(), "errors");
                if (root.exists() || root.mkdir()) {
                    File dump = new File(root.getAbsoluteFile(), FILE_NAME);
                    if (!dump.exists() && dump.createNewFile()) {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
                        writer.write((debug.toString()).substring(1)); //Remove the extra /n
                        writer.close();
                        debug.append("\nThis has been saved to the file ./").append(plugin.getName()).append("/errors/").append(FILE_NAME);
                    }
                }
            } catch (Exception e) {
                debug.append("\nErrors occured while saving to file. Not saved.");
                e.printStackTrace();
            }
            System.err.println(debug);
            return true;
        } catch (Exception e) {
            return true;
        }
    }

}
