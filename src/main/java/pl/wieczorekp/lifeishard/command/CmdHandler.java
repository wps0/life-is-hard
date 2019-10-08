package pl.wieczorekp.lifeishard.command;

import org.bukkit.command.*;
import pl.wieczorekp.lifeishard.LifeIsHard;
import pl.wieczorekp.lifeishard.command.available.*;

import java.util.HashMap;

public class CmdHandler implements CommandExecutor {
    private HashMap<String, Cmd> _commands;

    public CmdHandler() {
        _commands = new HashMap<>(8);

        this._commands.put("hp", new HpCmd());
        LifeIsHard.getInst().getCommand("hp").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (this._commands.containsKey(command.getName()))
            return this._commands.get(command.getName()).execute(sender, command, label, args);
        return false;
    }
}
