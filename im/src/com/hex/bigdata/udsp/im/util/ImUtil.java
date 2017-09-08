package com.hex.bigdata.udsp.im.util;

import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.EnumTrans;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.provider.model.Property;
import com.hex.bigdata.udsp.im.model.ImMetadataCol;
import com.hex.bigdata.udsp.im.provider.model.MetadataCol;
import com.hex.bigdata.udsp.im.provider.util.model.TableColumn;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Created by JunjieM on 2017-9-5.
 */
public class ImUtil {
    /**
     * imMetadataCols类型转换为MetadataCol类型
     *
     * @param imMetadataCols
     * @return
     */
    public static List<MetadataCol> convertToMetadataColList(List<ImMetadataCol> imMetadataCols) {
        List<MetadataCol> metadataCols = new ArrayList<>();
        for (ImMetadataCol item : imMetadataCols) {
            MetadataCol metadataCol = new MetadataCol();
            metadataCol.setName(item.getName());
            metadataCol.setDescribe(item.getDescribe());
            metadataCol.setType(EnumTrans.transDataType(item.getType()));
            metadataCol.setLength(item.getLength());
            metadataCol.setNote(item.getNote());
            metadataCol.setPrimary("0".equals(item.getPrimary()));
            metadataCol.setIndexed("0".equals(item.getIndexed()));
            metadataCol.setStored("0".equals(item.getStored()));
            metadataCols.add(metadataCol);
        }
        return metadataCols;
    }

    /**
     * metadataCol类型转换为TableColumn类型
     *
     * @param metadataCol
     * @return
     */
    public static List<TableColumn> convertToTableColumnList(List<MetadataCol> metadataCol) {
        List<TableColumn> tableColumns = new ArrayList<>();
        for (MetadataCol item : metadataCol) {
            int length = StringUtils.isEmpty(item.getLength()) ? 0 : Integer.parseInt(item.getLength());
            TableColumn tableColumn = new TableColumn(item.getName(),item.getType().getValue(),item.getDescribe(),length);
            tableColumns.add(tableColumn);
        }
        return tableColumns;
    }
}