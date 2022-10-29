package io.github.romvoid95.commands;

public class ClearCommands extends ParentCommand
{

	public ClearCommands()
	{
		super("clear");
		subCommands(new ClearGamesCommand());
	}
}
