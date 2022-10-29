package io.github.romvoid95.util.discord;

import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.readonlydev.command.event.CommandEvent;
import com.readonlydev.command.slash.SlashCommandEvent;
import com.readonlydev.common.utils.ResultLevel;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class Reply
{
    public static void InvalidPermissions(SlashCommandEvent event)
    {        
        event.replyEmbeds(
            simpleEmbed(event.getMember().getAsMention() + ", you do not have the required permissions to run this command**", Color.RED)
        ).queue();
    }
	
    public static void EphemeralReply(SlashCommandEvent event, ResultLevel level, String message)
    {
        event.replyEmbeds(simpleEmbed(message, level.getColor())).setEphemeral(true).queue();
    }
    
    public static void EphemeralReply(SlashCommandEvent event, ResultLevel level,EmbedBuilder embed)
    {
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
    
    public static ReplyCallbackAction ReplyCallback(SlashCommandEvent event, MessageEmbed... embeds)
    {
    	return event.replyEmbeds(Arrays.asList(embeds));
    }

    public static ReplyCallbackAction EphemeralReplyCallback(SlashCommandEvent event, String message)
    {
        return event.replyEmbeds(simpleEmbed(message, ResultLevel.SUCCESS.getColor())).setEphemeral(true);
    }
    
    public static void Success(SlashCommandEvent event, String message)
    {
        event.replyEmbeds(simpleEmbed(message, ResultLevel.SUCCESS.getColor())).queue();
    }
    
    public static void Success(CommandEvent event, String message)
    {
        event.reply(simpleEmbed(message, ResultLevel.SUCCESS.getColor()));
    }

    public static void Success(SlashCommandEvent event, EmbedBuilder embed)
    {
        event.replyEmbeds(embed.setColor(ResultLevel.SUCCESS.getColor()).build()).queue();
    }
    
    public static void Warning(SlashCommandEvent event, String message)
    {
        event.replyEmbeds(simpleEmbed(message, ResultLevel.WARNING.getColor())).queue();
    }

    public static void Error(SlashCommandEvent event, String message)
    {
        event.replyEmbeds(simpleEmbed(message, ResultLevel.ERROR.getColor())).queue();
    }
    
    public static void Error(CommandEvent event, String message)
    {
        event.reply(simpleEmbed(message, ResultLevel.ERROR.getColor()));
    }

    public static void Temporary(MessageChannelUnion channel, ResultLevel level, String message, int time, TimeUnit unit)
    {
        channel.sendMessageEmbeds(simpleEmbed(message, level.getColor())).queue(success ->
        {
            success.delete().queueAfter(time, unit);
        });
    }

    public static void temporaryReply(MessageChannelUnion channel, MessageEmbed embed, int time, TimeUnit unit)
    {
        channel.sendMessageEmbeds(embed).queue(success ->
        {
            success.delete().queueAfter(time, unit);
        });
    }

    public static MessageEmbed simpleEmbed(final String message)
    {
        return simpleEmbed(message, null);
    }

    public static MessageEmbed simpleEmbed(final String message, @Nullable Color color)
    {
        EmbedBuilder builder = new EmbedBuilder().setDescription(message);
        if (color != null)
        {
            builder.setColor(color);
        }
        return builder.build();
    }
}
