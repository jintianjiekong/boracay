package com.hex.bigdata.metadata.db.jdbcinfo.helper;

import com.hex.bigdata.metadata.db.jdbcinfo.BaseJdbcInfoHelper;
import com.hex.bigdata.metadata.db.model.ColumnType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by junjiem on 2016-6-29.
 */
public class DB2JdbcInfoHelper extends BaseJdbcInfoHelper {
    public DB2JdbcInfoHelper(Connection conn) {
        super(conn);
    }

    @Override
    protected String getDbType() {
        return null;
    }

    @Override
    public String getCurrentDbNameSql() {
        return null;
    }

    @Override
    public String getDatabasesSql() {
        return null;
    }

    @Override
    public String getTablesSql(String dbName) {
        return null;
    }

    @Override
    public String getColumnsSql(String dbName, String tbName) {
        return null;
    }

    @Override
    public List<ColumnType> getColumnTypes() throws SQLException {
        return null;
    }
}