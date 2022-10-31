package io.github.romvoid95;

import java.util.EnumSet;

import com.readonlydev.command.client.Client;
import com.readonlydev.command.client.ClientBuilder;
import com.readonlydev.command.client.ServerCommands;
import com.readonlydev.common.waiter.EventWaiter;

import io.github.romvoid95.commands.ClearCommands;
import io.github.romvoid95.commands.EvalCommand;
import io.github.romvoid95.commands.GiveCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class KeyWatcher
{
	private static JDA			jda;
	private static Client		client;
	private static EventWaiter	eventWaiter	= new EventWaiter();

	private void preStart()
	{
		RestAction.setPassContext(true);
		RestAction.setDefaultFailure(ErrorResponseException.ignore(RestAction.getDefaultFailure(), ErrorResponse.UNKNOWN_MESSAGE));
	}

	private KeyWatcher() throws Exception
	{
		preStart();

		ClientBuilder clientBuilder = new ClientBuilder();

		ServerCommands keyDumpster = new ServerCommands(845563322983383041L);
		keyDumpster.addAllCommands(new GiveCommand(), new ClearCommands());

		clientBuilder.addCommand(new EvalCommand());
		clientBuilder.setPrefix("--");
		clientBuilder.setAllRepliesAsEmbed();
		clientBuilder.addAllServerCommands(keyDumpster);
		clientBuilder.setOwnerId(Conf.Bot().getOwner());
		clientBuilder.setActivity(Activity.playing("Keys keys key"));
		clientBuilder.useHelpBuilder(false);

		// @format:off
		EnumSet<GatewayIntent> intents = EnumSet.of(
			GatewayIntent.GUILD_EMOJIS_AND_STICKERS, 
			GatewayIntent.GUILD_MESSAGES, 
			GatewayIntent.GUILD_MESSAGE_REACTIONS, 
			GatewayIntent.MESSAGE_CONTENT
		);

		EnumSet<CacheFlag> caches = EnumSet.of(
			CacheFlag.ACTIVITY, 
			CacheFlag.CLIENT_STATUS, 
			CacheFlag.VOICE_STATE
		);

		KeyWatcher.client = clientBuilder.build();

		KeyWatcher.jda = JDABuilder.create(Conf.Bot().getToken(), intents)
			.disableCache(caches)
			.setActivity(Activity.playing("Init Stage"))
			.addEventListeners(client, eventWaiter).build();
		// @format:on
	}

	public static void main(String[] argv) throws Exception
	{
		new KeyWatcher();
	}

	public static Client getClient()
	{
		return client;
	}

	public static JDA getJda()
	{
		return jda;
	}

	public static EventWaiter getEventWaiter()
	{
		return eventWaiter;
	}
}
