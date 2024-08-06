package nickawrist.server_Website.commands;

import nickawrist.server_Website.Server_Website;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerWebReload implements CommandExecutor {

    private final Server_Website plugin;

    public ServerWebReload(Server_Website plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("serverweb.reload")) {
                plugin.reloadWebServer();
                player.sendMessage("Web server reloaded successfully.");
                return true;
            } else {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }
        }
        plugin.reloadWebServer();
        sender.sendMessage("Web server reloaded successfully.");
        return true;
    }
}
