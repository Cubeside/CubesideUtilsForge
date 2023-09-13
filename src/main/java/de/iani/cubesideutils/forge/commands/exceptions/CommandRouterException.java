package de.iani.cubesideutils.forge.commands.exceptions;

import de.iani.cubesideutils.forge.commands.CommandRouter;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;

public abstract class CommandRouterException extends Exception {

    private static final long serialVersionUID = 3550234682652991485L;

    private CommandRouter router;
    private CommandSourceStack sender;
    private String alias;
    private String[] args;

    public CommandRouterException(CommandRouter router, CommandSourceStack sender, String alias, String[] args, String message, Throwable cause) {
        super(message, cause);
        init(router, sender, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSourceStack sender, String alias, String[] args, String message) {
        super(message);
        init(router, sender, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSourceStack sender, String alias, String[] args, Throwable cause) {
        super(cause);
        init(router, sender, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSourceStack sender, String alias, String[] args) {
        super();
        init(router, sender, alias, args);
    }

    private void init(CommandRouter router, CommandSourceStack sender, String alias, String[] args) {
        this.router = router;
        this.sender = Objects.requireNonNull(sender);
        this.alias = Objects.requireNonNull(alias);
        this.args = args.clone();
    }

    public CommandRouter getRouter() {
        return router;
    }

    public CommandSourceStack getSender() {
        return sender;
    }

    public String getAlias() {
        return alias;
    }

    public String[] getArgs() {
        return args.clone();
    }

}
