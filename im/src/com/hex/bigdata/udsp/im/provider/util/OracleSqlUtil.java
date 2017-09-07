package com.hex.bigdata.udsp.im.provider.util;

import com.hex.bigdata.udsp.im.provider.util.model.TableColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by JunjieM on 2017-9-6.
 */
public class OracleSqlUtil {

    /**
     * 创建表
     *
     * @param tableName
     * @param columns
     * @param tableComment
     * @return
     */
    public static String createTable(String tableName, List<TableColumn> columns, String tableComment) {
        String sql = "CREATE TABLE " + tableName + getColumns(columns) + ";";
        sql += commentTable(tableName, tableComment) + ";";
        TableColumn column = null;
        String colName = "";
        String colComment = "";
        if (columns != null && columns.size() != 0) {
            for (int i = 0; i < columns.size(); i++) {
                column = columns.get(i);
                colName = column.getColName();
                colComment = column.getColComment();
                if (StringUtils.isNoneBlank(colName) && StringUtils.isNoneBlank(colComment)) {
                    sql += commentColumn(tableName, colName, colComment) + ";";
                }
            }
        }
        return sql;
    }

    /**
     * 删除表
     *
     * @param tableName
     * @return
     */
    public static String dropTable(String tableName) {
        return "DROP TABLE " + tableName;
    }


    public static String commentColumn(String tableName, String colName, String colComment) {
        return "COMMENT ON COLUMN " + tableName + "." + colName + " IS '" + colComment + "'";
    }

    public static String commentTable(String tableName, String tableComment) {
        return "COMMENT ON TABLE " + tableName + " IS '" + tableComment + "'";
    }

    private static String getColumns(List<TableColumn> columns) {
        String sql = "";
        TableColumn column = null;
        String colName = "";
        String dataType = "";
        String colComment = "";
        if (columns != null && columns.size() != 0) {
            sql = " (";
            for (int i = 0; i < columns.size(); i++) {
                column = columns.get(i);
                colName = column.getColName();
                dataType = column.getDataType();
                colComment = column.getColComment();
                if (StringUtils.isNoneBlank(colName) && StringUtils.isNoneBlank(dataType)) {
                    if (i == 0) {
                        sql += colName + " " + dataType;
                    } else {
                        sql += ", " + colName + " " + dataType;
                    }
                    if (StringUtils.isNoneBlank(colComment)) {
                        sql += " COMMENT '" + colComment + "'";
                    }
                }
            }
            sql += ")";
        }
        return sql;
    }
}
