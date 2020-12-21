package testrail;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:conf.properties")
public interface TestRailConfig extends Config {

    @Key("testrail.link")
    @DefaultValue("https://url.com")
    String link();

    @Key("testrail.user")
    @DefaultValue("user")
    String user();

    @Key("testrail.password")
    @DefaultValue("api_key")
    String password();

    @Key("project.id")
    @DefaultValue("1")
    String projectId();
}
