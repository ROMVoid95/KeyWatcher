package io.github.romvoid95.database.entity;

import java.beans.ConstructorProperties;
import java.util.HashMap;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.romvoid95.database.ManagedObject;
import io.github.romvoid95.database.impl.History;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBUsers implements ManagedObject
{

	public static final String	DB_TABLE	= "database";
	private HashMap<Long, History>	users;

	@JsonCreator
	@ConstructorProperties(
	{ "users" })
	public DBUsers(@JsonProperty("users") HashMap<Long, History> users)
	{
		this.users = users;
	}

	public static DBUsers create()
	{
		DBUsers botObj = new DBUsers(new HashMap<>());
		botObj.save();
		return botObj;
	}

	public void addUser(final long userId)
	{
		if (!users.containsKey(userId))
		{
			users.put(userId, new History());
		}
	}
	
	public History getHistoryForUser(final long userId)
	{
		if (!users.containsKey(userId))
		{
			users.put(userId, new History());
		}
		return users.get(userId);
	}

	@Override
	@Nonnull
	public String getId()
	{
		return "users";
	}

	@JsonIgnore
	@Override
	@Nonnull
	public String getTableName()
	{
		return DB_TABLE;
	}
}
