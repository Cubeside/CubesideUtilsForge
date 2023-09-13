package de.iani.cubesideutils.forge.commands.exceptions;

import de.iani.cubesideutils.forge.commands.CommandRouter;
import de.iani.cubesideutils.forge.commands.SubCommand;
import net.minecraft.commands.CommandSourceStack;

public class DisallowsCommandBlockException extends SubCommandException {

    private static final long serialVersionUID = -679571981171996226L;

    public DisallowsCommandBlockException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, alias, subCommand, args, message);
    }

    public DisallowsCommandBlockException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args) {
        this(router, sender, alias, subCommand, args, "This command is not allowed for CommandBlocks!");
    }

}
