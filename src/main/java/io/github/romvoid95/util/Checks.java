package io.github.romvoid95.util;

import com.readonlydev.command.slash.SlashCommandEvent;

import io.github.romvoid95.Conf;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@UtilityClass
public class Checks
{
	public static <T> Boolean validate(MessageReceivedEvent e, MessageChannel channel, User user) {
		return e.getAuthor().equals(user) && e.getChannel().equals(channel);
	}
	
    public static boolean forRole(SlashCommandEvent event, String roleId)
    {
    	if(event.getAuthor().getId().equals(Conf.Bot().getOwner()))
    	{
    		return true;
    	}
        boolean checksPass = false;
        Role role = event.getGuild().getRoleById(roleId);
        if(role != null)
        {
            if(event.getMember().getRoles().contains(role))
            {
                checksPass = true;
            }
        }
        return checksPass;
    }
}
