package de.iani.cubesideutils.forge.commands.exceptions;

import de.iani.cubesideutils.forge.commands.CommandRouter;
import de.iani.cubesideutils.forge.commands.SubCommand;
import net.minecraft.commands.CommandSourceStack;

public class RequiresPlayerException extends SubCommandException {

    private static final long serialVersionUID = -4621194287775434508L;

    public RequiresPlayerException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, alias, subCommand, args, message);
    }

    public RequiresPlayerException(CommandRouter router, CommandSourceStack sender, String alias, SubCommand subCommand, String[] args) {
        this(router, sender, alias, subCommand, args, "This command can only be executed by players!");
    }

}
