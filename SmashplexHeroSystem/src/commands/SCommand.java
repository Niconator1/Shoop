package commands;

import org.bukkit.command.CommandSender;

public abstract class SCommand {
	private String name;
	private String perm;

	public SCommand(String name, String permission) {
		this.name = name;
		this.perm = permission;
	}

	public String getName() {
		return name;
	}

	public String getPermission() {
		return perm;
	}

	public abstract boolean handle(CommandSender sender, String[] args);
}
