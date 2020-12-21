package module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import config.TestConfig;
import dao.DbDAO;
import org.aeonbits.owner.ConfigFactory;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class Dbi extends AbstractModule {

    private Jdbi jdbi;
    private TestConfig cfg = ConfigFactory.create(TestConfig.class);

    @Override
    protected void configure() {
        this.jdbi = Jdbi.create(cfg.dbConnStr()).installPlugin(new SqlObjectPlugin());
    }

    @Provides
    public DbDAO provideDAO() {
        return jdbi.onDemand(DbDAO.class);
    }

    public Jdbi getDBI() {
        if(this.jdbi == null) configure();
        return this.jdbi;
    }
}
