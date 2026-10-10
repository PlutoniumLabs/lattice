package dev.lumentae.lattice;

import dev.lumentae.lattice.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "lattice";
    public static final String MOD_NAME = "lattice";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final ICommand[] COMMANDS = {
            new NightvisionCommand(),
            new LatticeCommand(),
            new NickCommand(),
            new StatusCommand(),
            new HomeCommand(),
            new RequestForceloadCommand()
    };
}
