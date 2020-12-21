package dao;

import modelRest.token.ApiToken;
import modelRest.token.ApiTokenMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface DbDAO {
    @RegisterRowMapper(ApiTokenMapper.class)
    @SqlQuery("select session_id from tokens where user = :user")
    List<ApiToken> getUserTokens(@Bind("user") String userName);
}
