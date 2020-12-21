package config;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:conf.properties")
public interface TestConfig extends Config {

    /**
     * User info
     */
    @Key("login")
    @DefaultValue("user")
    String login();
    @Key("password")
    @DefaultValue("pwd")
    String password();

    /**
     * DB Connection
     */
    @Key("oracle.db")
    @DefaultValue("jdbc:oracle:thin:USER/PASSWORD@(<TNSNAMES_SID>)")
    String dbConnStr();

    /**
     * Rest connections
     */
    @Key("zk.host")
    @DefaultValue("localhost:2181")
    String zkConnStr();
    @Key("rest.url")
    @DefaultValue("http://localhost:1234")
    String restUrl();

}
