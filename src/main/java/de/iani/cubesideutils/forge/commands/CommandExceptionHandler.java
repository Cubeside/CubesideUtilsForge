package de.iani.cubesideutils.forge.commands;

import de.iani.cubesideutils.forge.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.forge.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.forge.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.forge.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.forge.commands.exceptions.NoPermissionForPathException;
import de.iani.cubesideutils.forge.commands.exceptions.RequiresPlayerException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;

public interface CommandExceptionHandler {

    public static final CommandExceptionHandler DEFAULT_HANDLER = new CommandExceptionHandler() {
    };

    public default int handleDisallowsCommandBlock(DisallowsCommandBlockException thrown) {
        CommandSourceStack sender = thrown.getSender();
        sender.sendFailure(new TextComponent(getErrorMessagePrefix() + thrown.getMessage()));
        return 0;
    }

    public default int handleRequiresPlayer(RequiresPlayerException thrown) {
        CommandSourceStack sender = thrown.getSender();
        sender.sendFailure(new TextComponent(getErrorMessagePrefix() + thrown.getMessage()));
        return 0;
    }

    public default int handleNoPermission(NoPermissionException thrown) {
        CommandSourceStack sender = thrown.getSender();
        sender.sendFailure(new TextComponent(getErrorMessagePrefix() + thrown.getMessage()));
        return 0;
    }

    public default int handleNoPermissionForPath(NoPermissionForPathException thrown) {
        CommandSourceStack sender = thrown.getSender();
        sender.sendFailure(new TextComponent(getErrorMessagePrefix() + thrown.getMessage()));
        return 0;
    }

    public default int handleIllegalSyntax(IllegalSyntaxException thrown) {
        CommandRouter router = thrown.getRouter();
        CommandSourceStack sender = thrown.getSender();
        String alias = thrown.getAlias();
        String[] args = thrown.getArgs();
        router.showHelp(sender, alias, args);
        return 0;
    }

    public default int handleInternalException(InternalCommandException thrown) {
        if (thrown.getMessage() != null) {
            CommandSourceStack sender = thrown.getSender();
            sender.sendFailure(new TextComponent(getErrorMessagePrefix() + thrown.getMessage()));
        }

        Throwable cause = thrown.getCause();
        if (cause instanceof Error) {
            throw (Error) cause;
        } else if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else {
            throw new RuntimeException(cause);
        }
    }

    public default String getErrorMessagePrefix() {
        return "";
    }

    public default String getHelpMessagePrefix() {
        return "";
    }
}
