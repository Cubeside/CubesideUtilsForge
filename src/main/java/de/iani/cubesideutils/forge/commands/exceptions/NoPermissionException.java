package de.iani.cubesideutils.forge.commands.exceptions;

import de.iani.cubesideutils.forge.commands.CommandRouter;
import de.iani.cubesideutils.forge.commands.SubCommand;
import net.minecraft.commands.CommandSourceStack;

public class NoPermissionException extends SubCommandException {

    private static final long serialVersionUID = 426296281527518966L;

    private String permission;

    public NoPermissionException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, String permission, String message) {
        super(router, sender, alias, subCommand, args, message);

        this.permission = permission;
    }

    public NoPermissionException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, String permission) {
        this(router, sender, alias, subCommand, args, permission, "No permission!");
    }

    public NoPermissionException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args) {
        this(router, sender, alias, subCommand, args, null, "No permission!");
    }

    public String getPermission() {
        return permission;
    }

}
