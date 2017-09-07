package com.hex.bigdata.udsp.im.provider.impl.model.datasource;

import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.provider.model.Datasource;
import com.hex.bigdata.udsp.common.provider.model.Property;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by JunjieM on 2017-9-6.
 */
public class JdbcDatasource extends Datasource {
    private String driverClass;
    private String jdbcUrl;
    private String username;
    private String password;
    private String initialSize;
    private String minIdle;
    private String maxIdle;
    private String maxWait;
    private String maxActive;
    private String validationQueryTimeout;
    private String timeBetweenEvictionRunsMillis;
    private String minEvictableIdleTimeMillis;
    private String validationQuery;
    private String testWhileIdle;
    private String testOnBorrow;
    private String testOnReturn;

    public String getDriverClass() {
        String value = getProperty("driver.class").getValue();
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException("driver.class不能为空");
        return value;
    }

    public String getJdbcUrl() {
        String value = getProperty("jdbc.url").getValue();
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException("jdbc.url不能为空");
        return value;
    }

    public String getUsername() {
        return getProperty("username").getValue();
    }

    public String getPassword() {
        return getProperty("password").getValue();
    }

    public String getInitialSize() {
        return getProperty("initial.size").getValue();
    }

    public String getMinIdle() {
        return getProperty("min.idle").getValue();
    }

    public String getMaxIdle() {
        return getProperty("max.idle").getValue();
    }

    public String getMaxWait() {
        return getProperty("max.wait").getValue();
    }

    public String getMaxActive() {
        return getProperty("max.active").getValue();
    }

    public String getValidationQueryTimeout() {
        return getProperty("validation.query.timeout").getValue();
    }

    public String getTimeBetweenEvictionRunsMillis() {
        return getProperty("time.between.eviction.runs.millis").getValue();
    }

    public String getMinEvictableIdleTimeMillis() {
        return getProperty("min.evictable.idle.time.millis").getValue();
    }

    public String getValidationQuery() {
        return getProperty("validation.query").getValue();
    }

    public String getTestWhileIdle() {
        return getProperty("test.while.idle").getValue();
    }

    public String getTestOnBorrow() {
        return getProperty("test.on.borrow").getValue();
    }

    public String getTestOnReturn() {
        return getProperty("test.on.return").getValue();
    }

    public JdbcDatasource(List<Property> properties) {
        super(properties);
    }

    public JdbcDatasource(Map<String, Property> propertieMap) {
        super(propertieMap);
    }

    public JdbcDatasource(ComDatasource comDatasource, List<ComProperties> comPropertieList) {
        super(comDatasource, comPropertieList);
    }
}
