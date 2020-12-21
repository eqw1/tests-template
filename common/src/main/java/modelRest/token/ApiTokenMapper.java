package modelRest.token;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ApiTokenMapper implements RowMapper<ApiToken> {

    public ApiToken map(ResultSet r, StatementContext ctx) throws SQLException {
        return ApiToken.builder().authtoken(r.getString("session_id")).build();
    }

}
