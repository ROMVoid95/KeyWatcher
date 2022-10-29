package io.github.romvoid95.commands;

import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class ParentCommand extends ParentKeyWatcherCommand
{

	public ParentCommand(String name, OptionData... options)
	{
		super(name, options);
	}
	
    protected void subCommands(SlashCommand... children)
    {
        this.children = children;
    }
    
    @Override
    protected void onExecute(SlashCommandEvent event)
    {
    	// NO-OP
    }
}
