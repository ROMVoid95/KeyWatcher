package io.github.romvoid95.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.readonlydev.command.slash.SlashCommandEvent;

import io.github.romvoid95.BotData;
import io.github.romvoid95.database.entity.DBUsers;
import io.github.romvoid95.database.impl.History;
import io.github.romvoid95.util.discord.Reply;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ClearGamesCommand extends ParentKeyWatcherCommand
{

	private final static OptionData	userOption	= new OptionData(OptionType.USER, "user", "The target User", true);
	private final static OptionData	gameOption	= new OptionData(OptionType.STRING, "game", "The Game's name (specify `all` to clear all games)", true);

	public ClearGamesCommand()
	{
		super("games", userOption, gameOption);
		this.help = "Clears a given game or all games for a user in the database";
	}

	@Override
	protected void onExecute(SlashCommandEvent event)
	{
		final User		user		= event.getOption("user").getAsUser();
		final String	gameName	= event.getOption("game").getAsString();

		DBUsers database = BotData.database().usersDatabase();

		History					userGameHistory	= database.getHistoryForUser(user.getIdLong());
		HashMap<String, List<Long>>	gameMap			= userGameHistory.getGameMap();

		if (gameName.equalsIgnoreCase("all"))
		{
			gameMap.clear();
			database.save();
			Reply.Success(event, "Cleared all games for `" + user.getAsTag() + "` in the Database");
		} else
		{
			if (gameMap.containsKey(gameName))
			{
				HashMap<String, List<Long>> newMap = new HashMap<>();
				for(Entry<String, List<Long>> e : gameMap.entrySet())
				{
					if(!e.getKey().equals(gameName))
					{
						newMap.put(e.getKey(), e.getValue());
					}
				}
				userGameHistory.setGameMap(newMap);
				database.save();
				Reply.Success(event, "Cleared `" + gameName + "` for `" + user.getAsTag() + "` in the Database");
			} else
			{
				Reply.Success(event, "User has never received `" + gameName + "` before");
			}
		}
	}
}
