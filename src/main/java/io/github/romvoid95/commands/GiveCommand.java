package io.github.romvoid95.commands;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.common.utils.ResultLevel;

import io.github.romvoid95.BotData;
import io.github.romvoid95.KeyWatcher;
import io.github.romvoid95.database.entity.DBUsers;
import io.github.romvoid95.database.impl.History;
import io.github.romvoid95.util.discord.Reply;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Slf4j
public class GiveCommand extends ParentKeyWatcherCommand
{

	Message							embedMessage;
	private final static OptionData	userOption	= new OptionData(OptionType.USER, "user", "User that will receive the game", true);
	private final static OptionData	gameOption	= new OptionData(OptionType.STRING, "game", "The games name", true);
	private final static OptionData	gameKey		= new OptionData(OptionType.STRING, "key", "The games Key", true);

	public GiveCommand()
	{
		super("give", userOption, gameOption, gameKey);
		this.guildOnly = true;
	}

	@Override
	protected void onExecute(SlashCommandEvent event)
	{
		final User				commandRunner	= event.getAuthor();
		final User				user			= event.getOption("user").getAsUser();
		final String			gameName		= event.getOption("game").getAsString();
		final String			gameKey			= event.getOption("key").getAsString();
		final MessageChannel	channel			= event.getChannel();

		log.info(event.getAuthor().getAsTag() + " ran /give <" + user.getAsTag() + "> <" + gameName + ">");

		DBUsers database = BotData.database().usersDatabase();

		History					userGameHistory	= database.getHistoryForUser(user.getIdLong());
		Map<String, List<Long>>	gameMap			= userGameHistory.getGameMap();
		if (!gameMap.containsKey(gameName))
		{
			sendPrivateMessage(user.openPrivateChannel(), gameName, gameKey).queue(s ->
			{
				gameMap.put(gameName, new ArrayList<>(Arrays.asList(Instant.now().getEpochSecond())));
				database.saveUpdating();
				Reply.Success(event, "Sucessfully sent Key to " + user.getAsTag());
			}, error ->
			{
				Reply.Success(event, "An Error occured trying to send Key to " + user.getAsTag());
			});
			log.info("Added '" + gameName + "' for '" + user.getAsTag() + "' to the Database");
			return;
		} else
		{
			Reply.Warning(event, "User as received this game before. See following Message");

			List<String>	dates	= gameMap.get(gameName).stream().map(l -> getReadableDate(Instant.ofEpochSecond(l))).collect(Collectors.toList());
			EmbedBuilder	builder	= new EmbedBuilder();
			builder.setTitle(gameName);
			builder.setColor(ResultLevel.WARNING.getColor());
			builder.setDescription("Received on the following dates");
			StringBuilder string = new StringBuilder();
			for (String date : dates)
			{
				string.append("> " + date).append("\n");
			}
			builder.addField("Dates", string.toString(), false);
			builder.addField("Send Key to " + user.getAsTag() + "?", EmbedBuilder.ZERO_WIDTH_SPACE, false);

			channel.sendMessageEmbeds(builder.build()).addActionRow(this.getConfirmButton(), this.getDenyButton()).queue(s ->
			{
				embedMessage = s;
			});

			KeyWatcher.getEventWaiter().waitForEvent(ButtonInteractionEvent.class, e -> e.getMessage().equals(embedMessage) && e.getUser().equals(commandRunner), e ->
			{
				if (e.getButton().getId().equals("yes"))
				{
					sendPrivateMessage(user.openPrivateChannel(), gameName, gameKey).queue(s ->
					{
						gameMap.get(gameName).add(Instant.now().getEpochSecond());
						database.saveUpdating();
						channel.sendMessageEmbeds(Reply.simpleEmbed("Sucessfully sent Key to " + user.getAsTag(), ResultLevel.SUCCESS.getColor())).queue();
					}, error ->
					{
						channel.sendMessageEmbeds(Reply.simpleEmbed("An Error occured trying to send Key to " + user.getAsTag(), ResultLevel.ERROR.getColor())).queue();
					});
					log.info("Selected 'YES' to add '" + gameName + "' for '" + user.getAsTag() + "' to the Database");
				} else if (e.getButton().getId().equals("no"))
				{
					channel.sendMessageEmbeds(Reply.simpleEmbed("Key was not sent to " + user.getAsTag(), ResultLevel.SUCCESS.getColor())).queue();
					log.info("Selected 'NO' to add '" + gameName + "' for '" + user.getAsTag() + "' to the Database");
				}
				embedMessage.editMessageComponents(new ArrayList<>()).queue();
			}, 5, TimeUnit.MINUTES, () -> event.reply("Time Exceeded, Terminating"));
		}
	}

	private RestAction<Message> sendPrivateMessage(CacheRestAction<PrivateChannel> action, String gameTitle, String gameKey)
	{
		return action.flatMap(c -> c.sendMessage(new MessageCreateBuilder().setEmbeds(userChannelEmbed(gameTitle, gameKey)).build()));
	}

	private MessageEmbed userChannelEmbed(String gameTitle, String gameKey)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Your Requsted Game Key has Arrived");

		builder.addField("Game Title", "`" + gameTitle + "`", false);
		builder.addField("GameKey", "||" + gameKey + "||", true);
		return builder.build();
	}

	private Button getConfirmButton()
	{
		return Button.primary("yes", "Yes");
	}

	private Button getDenyButton()
	{
		return Button.primary("no", "No");
	}

	private String getReadableDate(Instant instant)
	{
		Date				myDate		= Date.from(instant);
		SimpleDateFormat	formatter	= new SimpleDateFormat("E M yyyy @ HH:mm:ss");
		return formatter.format(myDate);
	}
}
