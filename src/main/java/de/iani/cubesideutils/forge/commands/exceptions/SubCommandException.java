package de.iani.cubesideutils.forge.commands.exceptions;

import de.iani.cubesideutils.forge.commands.CommandRouter;
import de.iani.cubesideutils.forge.commands.SubCommand;
import net.minecraft.commands.CommandSourceStack;

public class SubCommandException extends CommandRouterException {

    private static final long serialVersionUID = -6734610669148837489L;

    private SubCommand subCommand;

    public SubCommandException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, alias, args, message, cause);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, alias, args, message);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(router, sender, alias, args, cause);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args) {
        super(router, sender, alias, args);
        this.subCommand = subCommand;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

}
