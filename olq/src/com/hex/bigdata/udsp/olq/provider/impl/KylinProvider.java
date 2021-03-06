package com.hex.bigdata.udsp.olq.provider.impl;

import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.provider.model.Datasource;
import com.hex.bigdata.udsp.common.provider.model.Page;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.olq.model.OLQQuerySql;
import com.hex.bigdata.udsp.olq.provider.Provider;
import com.hex.bigdata.udsp.olq.provider.impl.model.KylinDatasource;
import com.hex.bigdata.udsp.olq.provider.model.OLQRequest;
import com.hex.bigdata.udsp.olq.provider.model.OLQResponse;
import com.hex.bigdata.udsp.olq.provider.model.OLQResponseFetch;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * Created by junjiem on 2017-2-15.
 */
@Component("com.hex.bigdata.udsp.olq.provider.impl.KylinProvider")
public class KylinProvider implements Provider {
    private static Logger logger = LogManager.getLogger(KylinProvider.class);
    private static Map<String, BasicDataSource> dataSourcePool;

    private synchronized BasicDataSource getDataSource(KylinDatasource kylinDatasource) {
        String dsId = kylinDatasource.getId();
        if (dataSourcePool == null) {
            dataSourcePool = new HashMap<String, BasicDataSource>();
        }
        BasicDataSource dataSource = dataSourcePool.get(dsId);
        if (dataSource == null) {
            dataSource = new BasicDataSource();

            if (StringUtils.isNotBlank(kylinDatasource.getDriverClass()))
                dataSource.setDriverClassName(kylinDatasource.getDriverClass());
            if (StringUtils.isNotBlank(kylinDatasource.getJdbcUrl()))
                dataSource.setUrl(kylinDatasource.getJdbcUrl());
            if (StringUtils.isNotBlank(kylinDatasource.getUsername()))
                dataSource.setUsername(kylinDatasource.getUsername());
            if (StringUtils.isNotBlank(kylinDatasource.getPassword()))
                dataSource.setPassword(kylinDatasource.getPassword());
            if (StringUtils.isNotBlank(kylinDatasource.getInitialSize()))
                dataSource.setInitialSize(Integer.valueOf(kylinDatasource.getInitialSize()));// 数据库初始化时，创建的连接个数
            if (StringUtils.isNotBlank(kylinDatasource.getMinIdle()))
                dataSource.setMinIdle(Integer.valueOf(kylinDatasource.getMinIdle()));// 最小空闲连接数
            if (StringUtils.isNotBlank(kylinDatasource.getMaxIdle()))
                dataSource.setMaxIdle(Integer.valueOf(kylinDatasource.getMaxIdle()));// 数据库最大连接数
            if (StringUtils.isNotBlank(kylinDatasource.getMaxActive()))
                dataSource.setMaxActive(Integer.valueOf(kylinDatasource.getMaxActive()));// 设置最大并发数
            if (StringUtils.isNotBlank(kylinDatasource.getMaxWait()))
                dataSource.setMaxWait(Integer.valueOf(kylinDatasource.getMaxWait()));// 最长等待时间，单位毫秒
            if (StringUtils.isNotBlank(kylinDatasource.getValidationQuery()))
                dataSource.setValidationQuery(kylinDatasource.getValidationQuery()); // 验证链接的SQL语句，必须能返回一行及以上数据
            if (StringUtils.isNotBlank(kylinDatasource.getValidationQueryTimeout()))
                dataSource.setValidationQueryTimeout(Integer.valueOf(kylinDatasource.getValidationQueryTimeout())); // 自动验证连接的时间
            if (StringUtils.isNotBlank(kylinDatasource.getTimeBetweenEvictionRunsMillis()))
                dataSource.setTimeBetweenEvictionRunsMillis(Integer.valueOf(kylinDatasource.getTimeBetweenEvictionRunsMillis())); // N毫秒检测一次是否有死掉的线程
            if (StringUtils.isNotBlank(kylinDatasource.getMinEvictableIdleTimeMillis()))
                dataSource.setMinEvictableIdleTimeMillis(Integer.valueOf(kylinDatasource.getMinEvictableIdleTimeMillis()));// 空闲连接N毫秒中后释放
            if (StringUtils.isNotBlank(kylinDatasource.getTestWhileIdle()))
                dataSource.setTestWhileIdle(Boolean.valueOf(kylinDatasource.getTestWhileIdle()));
            if (StringUtils.isNotBlank(kylinDatasource.getTestOnBorrow()))
                dataSource.setTestOnBorrow(Boolean.valueOf(kylinDatasource.getTestOnBorrow()));
            if (StringUtils.isNotBlank(kylinDatasource.getTestOnReturn()))
                dataSource.setTestOnReturn(Boolean.valueOf(kylinDatasource.getTestOnReturn()));

            dataSourcePool.put(dsId, dataSource);
        }
        return dataSource;
    }

    private Connection getConnection(KylinDatasource kylinDatasource) throws SQLException {
        Connection conn = null;
        BasicDataSource dataSource = getDataSource(kylinDatasource);
        if (dataSource != null) {
            conn = dataSource.getConnection();
        }
        return conn;
    }

    public void init(Datasource datasource) {
        try {
            KylinDatasource kylinDatasource = new KylinDatasource(datasource.getPropertyMap());
            getConnection(kylinDatasource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public OLQResponse execute(OLQRequest request) {
        logger.debug("request=" + JSONUtil.parseObj2JSON(request));
        long bef = System.currentTimeMillis();

        OLQResponse response = new OLQResponse();
        response.setRequest(request);

        Datasource datasource = request.getDatasource();

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        int count = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        KylinDatasource kylinDatasource = new KylinDatasource(datasource.getPropertyMap());
        try {
            conn = getConnection(kylinDatasource);
            stmt = conn.createStatement();
            //获取查询信息
            OLQQuerySql olqQuerySql = request.getOlqQuerySql();
            if (olqQuerySql.getPage() == null){
                rs = stmt.executeQuery(olqQuerySql.getOriginalSql());
            }else {
                rs = stmt.executeQuery(olqQuerySql.getPageSql());
            }
            rs.setFetchSize(1000);
            ResultSetMetaData rsmd = rs.getMetaData();
            response.setMetadata(rsmd);
            int columnCount = rsmd.getColumnCount();
            int max_num_size = kylinDatasource.getMaxNum();
            while (rs.next()) {
                map = new LinkedHashMap<String, String>();
                for (int i = 1; i <= columnCount; i++) {
                    //map.put(rsmd.getColumnName(i), rs.getString(i));
                    map.put(rsmd.getColumnLabel(i), rs.getString(i));
                }
                list.add(map);
                count++;
                if (count >= max_num_size) {
                    break;
                }
            }

            //查询的记录数总数
            String totalSql = olqQuerySql.getTotalSql();
            if (StringUtils.isNotBlank(totalSql)){
                rs = stmt.executeQuery(totalSql);
                int totalCount = 0;
                if (rs.next()){
                    totalCount = rs.getInt(1);
                }
                //设置总记录数
                olqQuerySql.getPage().setTotalCount(totalCount);
                response.setPage(olqQuerySql.getPage());
            }

            response.setStatus(Status.SUCCESS);
            response.setStatusCode(StatusCode.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
            response.setMessage(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;

        response.setRecords(list);
        response.setConsumeTime(consumeTime);
        response.setTotalCount(count);

        logger.debug("consumeTime=" + response.getConsumeTime() + " recordsSize=" + response.getRecords().size());
        return response;
    }

    public synchronized void close(Datasource datasource) {
        BasicDataSource dataSource = dataSourcePool.remove(datasource.getId());
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean testDatasource(Datasource datasource) {
        boolean canConnection = true;
        Connection connection = null;
        try {
            KylinDatasource kylinDatasource = new KylinDatasource(datasource.getPropertyMap());
            connection = getConnection(kylinDatasource);
            if (connection == null) {
                canConnection = false;
            } else {
                canConnection = true;
            }
        } catch (Exception e) {
            canConnection = false;
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return canConnection;
    }

    @Override
    public OLQResponseFetch executeFetch(OLQRequest request) {
        logger.debug("request=" + JSONUtil.parseObj2JSON(request));
        long bef = System.currentTimeMillis();

        OLQResponseFetch response = new OLQResponseFetch();
        response.setRequest(request);

        Datasource datasource = request.getDatasource();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        KylinDatasource kylinDatasource = new KylinDatasource(datasource.getPropertyMap());
        try {
            conn = getConnection(kylinDatasource);
            stmt = conn.createStatement();
            OLQQuerySql olqQuerySql = request.getOlqQuerySql();
            rs = stmt.executeQuery(olqQuerySql.getOriginalSql());
            rs.setFetchSize(1000);
            response.setStatus(Status.SUCCESS);
            response.setStatusCode(StatusCode.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
            response.setMessage(e.getMessage());
        }
        response.setConnection(conn);
        response.setStatement(stmt);
        response.setResultSet(rs);

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);

        logger.debug("consumeTime=" + response.getConsumeTime());
        return response;
    }

    @Override
    public OLQQuerySql getPageSql(String sql, Page page) {
        OLQQuerySql olqQuerySql = new OLQQuerySql(sql);
        if (page == null) {
            return olqQuerySql;
        }
        //分页sql组装
        int pageSize = page.getPageSize();
        int pageIndex = page.getPageIndex();
        pageIndex = pageIndex == 0 ? 1 : pageIndex;
        Integer startRow = (pageIndex - 1) * pageSize;
        StringBuffer pageSqlBuffer = new StringBuffer("SELECT * from (");
        pageSqlBuffer.append(sql);
        pageSqlBuffer.append(")t  limit ");
        pageSqlBuffer.append(pageSize);
        pageSqlBuffer.append(" offset ");
        pageSqlBuffer.append(startRow);
        olqQuerySql.setPageSql(pageSqlBuffer.toString());
        //总记录数查询SQL组装
        StringBuffer totalSqlBuffer = new StringBuffer("select count(*) from (");
        totalSqlBuffer.append(sql);
        totalSqlBuffer.append(")t");
        olqQuerySql.setTotalSql(totalSqlBuffer.toString());
        //page设置
        olqQuerySql.setPage(page);
        return olqQuerySql;
    }

    public static void main(String[] args) {
        String sql = "SELECT * from edwapp.soa_khmrye";
        Page page = new Page();
        page.setPageIndex(1);
        page.setPageSize(20);
        KylinProvider kylinProvider = new KylinProvider();
        System.out.println(kylinProvider.getPageSql(sql, page));
    }

}
