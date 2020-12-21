package config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.aeonbits.owner.ConfigFactory;

public class TestConfigProvider extends AbstractModule {

    @Provides
    public TestConfig provideConfig() { return ConfigFactory.newInstance().create(TestConfig.class);  }

}
