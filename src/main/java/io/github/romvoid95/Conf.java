package io.github.romvoid95;

import io.github.romvoid95.config.BotConfig;
import io.github.romvoid95.util.data.JsonDataManager;

public final class Conf
{
	private static JsonDataManager<BotConfig> botConfig;

	private static JsonDataManager<BotConfig> jsonBotConfig()
	{
		if (botConfig == null)
		{
			botConfig = new JsonDataManager<>(BotConfig.class, "bot.json", BotConfig::new);
		}

		return botConfig;
	}

	public static void saveBotConfigJson()
	{
		jsonBotConfig().save();
	}

	public static BotConfig Bot()
	{
		return jsonBotConfig().get();
	}
}
