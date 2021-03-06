package net.madmanmarkau.MultiHome;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * JOREN
 */
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*
 * /JOREN + LIHAD
 */

public class MultiHome extends JavaPlugin {
	public HomeManager homes;
	public InviteManager invites;
	public WarmUpManager warmups;
	public CoolDownManager cooldowns;
	public CommandExecutor commandExecutor;

	private MultiHomePlayerListener playerListener = new MultiHomePlayerListener(this);
	private MultiHomeEntityListener entityListener = new MultiHomeEntityListener(this);
	
	@Override
	public void onDisable() {
		Messaging.logInfo("Version " + this.getDescription().getVersion() + " unloaded.", this);
	}

	@Override
	public void onEnable() {
		String pluginDataPath = this.getDataFolder().getAbsolutePath() + File.separator;
		
		File dataPath = new File(pluginDataPath);
		if (!dataPath.exists()) {
			dataPath.mkdirs();
		}

		this.homes = new HomeManager(new File(pluginDataPath + "homes.txt"), this);
		this.invites = new InviteManager(new File(pluginDataPath + "invites.txt"), this);
		this.warmups = new WarmUpManager(new File(pluginDataPath + "warmups.txt"), this);
		this.cooldowns = new CoolDownManager(new File(pluginDataPath + "cooldowns.txt"), this);
		
		this.commandExecutor = new CommandExecutor(this);
		
		if (!HomePermissions.initialize(this)) return;
		disableEssentials();
		Settings.initialize(this);
		Settings.loadSettings(new File(pluginDataPath + "config.yml"));
		MultiHomeEconManager.initialize(this);
		
		this.homes.loadHomes();
		this.invites.loadInvites();
		this.warmups.loadWarmups();
		this.cooldowns.loadCooldowns();
		
		registerEvents();
		
		Messaging.logInfo("Version " + this.getDescription().getVersion() + " loaded.", this);
	}
	
	private void disableEssentials() {
		// Disable EssentialsHome
		Plugin essentialsHome = getServer().getPluginManager().getPlugin("EssentialsHome");

		if (essentialsHome != null) {
			if (!essentialsHome.isEnabled()) {
				// Load the plugin so we can disable it. Yeah, it's weird, but hopefully works.
				getServer().getPluginManager().enablePlugin(essentialsHome);
			}
			getServer().getPluginManager().disablePlugin(essentialsHome);
		}
	}

    private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener,this);
		pm.registerEvents(entityListener,this);
	}
    
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player)) {
			// Command sent by console/plugin
			onCommandFromConsole(sender, cmd, commandLabel, args);
		} else {
			// Command sent by player
			onCommandFromPlayer((Player) sender, cmd, commandLabel, args);
		}
		return true;
	}

	private void onCommandFromPlayer(Player player, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().compareToIgnoreCase("home") == 0 || cmd.getName().compareToIgnoreCase("mhome") == 0) {

			if (args.length == 0) {
				this.commandExecutor.goDefaultHome(player);
			} else if (args.length == 1) {
				String homeArgs[] = Util.splitHome(args[0]);

				if (homeArgs.length > 1) {
					this.commandExecutor.goPlayerNamedHome(player, homeArgs[0], homeArgs[1]);
				} else {
					this.commandExecutor.goNamedHome(player, homeArgs[0]);
				}
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("sethome") == 0 || cmd.getName().compareToIgnoreCase("msethome") == 0) {

			if (args.length == 0) {
				this.commandExecutor.setDefaultHome(player);
			} else if (args.length == 1) {
				String homeArgs[] = Util.splitHome(args[0]);
				
				if (homeArgs.length > 1) {
					this.commandExecutor.setPlayerNamedHome(player, homeArgs[0], homeArgs[1]);
				} else {
					this.commandExecutor.setNamedHome(player, homeArgs[0]);
				}
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("deletehome") == 0 || cmd.getName().compareToIgnoreCase("mdeletehome") == 0) {

			if (args.length == 0) {
				this.commandExecutor.deleteDefaultHome(player);
			} else if (args.length == 1) {
				String homeArgs[] = Util.splitHome(args[0]);

				if (homeArgs.length > 1) {
					this.commandExecutor.deletePlayerNamedHome(player, homeArgs[0], homeArgs[1]);
				} else {
					this.commandExecutor.deleteNamedHome(player, homeArgs[0]);
				}
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("listhomes") == 0 || cmd.getName().compareToIgnoreCase("mlisthomes") == 0) {
			
			if (args.length == 0) {
				this.commandExecutor.listHomes(player);
			} else if (args.length == 1) {
				this.commandExecutor.listPlayerHomes(player, args[0]);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("invitehome") == 0 || cmd.getName().compareToIgnoreCase("minvitehome") == 0) {

			if (args.length == 1) {
				this.commandExecutor.inviteDefaultHome(player, args[0]);
			} else if (args.length == 2) {
				this.commandExecutor.inviteNamedHome(player, args[0], args[1]);
			}

		} else if (cmd.getName().compareToIgnoreCase("invitehometimed") == 0 || cmd.getName().compareToIgnoreCase("minvitehometimed") == 0) {

			if (args.length == 2) {
				this.commandExecutor.inviteDefaultTimedHome(player, args[0], Util.decodeTime(args[1]));
			} else if (args.length == 3) {
				this.commandExecutor.inviteNamedTimedHome(player, args[0], Util.decodeTime(args[1]), args[2]);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("uninvitehome") == 0 || cmd.getName().compareToIgnoreCase("muninvitehome") == 0) {

			if (args.length == 1) {
				this.commandExecutor.uninviteDefaultHome(player, args[0]);
			} else if (args.length == 2) {
				this.commandExecutor.uninviteNamedHome(player, args[0], args[1]);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("listinvites") == 0 || cmd.getName().compareToIgnoreCase("mlistinvites") == 0) {

			if (args.length == 0) {
				this.commandExecutor.listInvitesToMe(player);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("listmyinvites") == 0 || cmd.getName().compareToIgnoreCase("mlistmyinvites") == 0) {

			if (args.length == 0) {
				this.commandExecutor.listInvitesToOthers(player);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		}
	}

    private void onCommandFromConsole(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().compareToIgnoreCase("multihome_home") == 0) {

			Messaging.sendError(sender, "This command is not available from console.");

		} else if (cmd.getName().compareToIgnoreCase("multihome_sethome") == 0) {

			Messaging.sendError(sender, "This command is not available from console.");

		} else if (cmd.getName().compareToIgnoreCase("multihome_deletehome") == 0) {

			Messaging.sendError(sender, "This command is not available from console.");

		} else if (cmd.getName().compareToIgnoreCase("multihome_listhomes") == 0) {

			if (args.length == 0) {
				Messaging.sendError(sender, "Missing <player> argument.");
			} else if (args.length == 1) {
				this.commandExecutor.listPlayerHomesConsole(sender, args[0]);
			} else {
				Settings.sendMessageTooManyParameters(sender);
			}

		}
    }
    
    /**
     * JOREN
     */
    
    protected WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
     
        return (WorldGuardPlugin) plugin;
    }
    
    /*
     * /JOREN
     */
}
