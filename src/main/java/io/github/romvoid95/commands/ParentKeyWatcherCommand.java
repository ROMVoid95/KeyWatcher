package io.github.romvoid95.commands;

import java.util.Arrays;

import com.readonlydev.command.slash.SlashCommand;
import com.readonlydev.command.slash.SlashCommandEvent;

import io.github.romvoid95.Conf;
import io.github.romvoid95.util.Checks;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class ParentKeyWatcherCommand extends SlashCommand
{
	public ParentKeyWatcherCommand(String name)
	{
		this.name = name;
		this.guildOnly = true;
	}
	
	public ParentKeyWatcherCommand(String name, OptionData...options)
	{
		this.name = name;
		this.options.addAll(Arrays.asList(options));
		this.guildOnly = true;
	}

	@Override
	protected void execute(SlashCommandEvent event)
	{
		if (!Checks.forRole(event, Conf.Bot().getKeymasterRole()))
		{
			Reply.InvalidPermissions(event);
		} else {
			onExecute(event);
		}
	}
	
	protected abstract void onExecute(SlashCommandEvent event);
}
