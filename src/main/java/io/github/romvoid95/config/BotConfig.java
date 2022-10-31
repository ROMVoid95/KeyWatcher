package io.github.romvoid95.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.rethinkdb.net.Connection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class BotConfig
{
	private String					token;
	private Boolean					debug				= false;
	private String					keymasterRole		= "845575686336937994";
	private String					owner				= "219644513109409793";
	private RethinkDatabase			rethinkdb			= new RethinkDatabase();

	public boolean isOwner(Member member)
	{
		return isOwner(member.getUser());
	}

	public boolean isOwner(User user)
	{
		return isOwner(user.getId());
	}

	public boolean isOwner(String id)
	{
		return owner.equals(id);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class RethinkDatabase
	{

		private String	hostname		= "127.0.0.1";
		private int		port			= 28015;
		private String	databaseName	= "keywatcher";
		private String	user			= "admin";
		private String	password		= "";

		public Connection.Builder buildConnection(Connection.Builder builder)
		{
			return builder.hostname(getHostname()).port(getPort()).db(getDatabaseName()).user(getUser(), getPassword());
		}
	}
}
