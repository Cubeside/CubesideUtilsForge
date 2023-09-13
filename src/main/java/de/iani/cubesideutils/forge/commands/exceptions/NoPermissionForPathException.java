package de.iani.cubesideutils.forge.commands.exceptions;

import de.iani.cubesideutils.forge.commands.CommandRouter;
import net.minecraft.commands.CommandSourceStack;

public class NoPermissionForPathException extends CommandRouterException {

    private static final long serialVersionUID = 1295353884134111903L;

    public NoPermissionForPathException(CommandRouter router, CommandSourceStack sender, String alias, String[] args, String message) {
        super(router, sender, alias, args, message);
    }

    public NoPermissionForPathException(CommandRouter router, CommandSourceStack sender, String alias, String[] args) {
        this(router, sender, alias, args, "No permission!");
    }

}
