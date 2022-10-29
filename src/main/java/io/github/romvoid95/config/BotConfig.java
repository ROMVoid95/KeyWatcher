package io.github.romvoid95.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String              token           = "TOKEN";
    private Boolean             debug           = false;
    private String				keymasterRole   = "845575686336937994";
    private String              owner           = "219644513109409793";
    private String[]            commandPackages = new String[] {};
    private RethinkDataseConfig database        = new RethinkDataseConfig();

    @JsonIgnore
    public RethinkDataseConfig RethinkDatabase()
    {
        return database;
    }

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
    public static class RethinkDataseConfig
    {

        private String hostname     = "127.0.0.1";
        private int    port         = 28015;
        private String databaseName = "readonly";
        private String user         = "USER";
        private String password     = "PASSWORD";

        public Connection.Builder buildConnection(Connection.Builder builder)
        {
            return builder.hostname(getHostname()).port(getPort()).db(getDatabaseName()).user(getUser(), getPassword());
        }
    }
}
