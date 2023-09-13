package de.iani.cubesideutils.forge.commands.exceptions;

import de.iani.cubesideutils.forge.commands.CommandRouter;
import de.iani.cubesideutils.forge.commands.SubCommand;
import net.minecraft.commands.CommandSourceStack;

public class IllegalSyntaxException extends SubCommandException {

    private static final long serialVersionUID = -9098781538062386012L;

    public IllegalSyntaxException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, alias, subCommand, args, message, cause);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, alias, subCommand, args, message);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(router, sender, alias, subCommand, args, cause);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args) {
        super(router, sender, alias, subCommand, args);
    }

}
