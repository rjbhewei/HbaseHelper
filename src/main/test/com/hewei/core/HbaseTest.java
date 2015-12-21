package com.hewei.core;

import com.hewei.core.tools.HbaseTools;
import org.apache.hadoop.hbase.TableName;

/**
 * @author hewei
 * @version 5.0
 * @date 15/12/21  17:40
 * @desc
 */
public class HbaseTest {
    public static void main(String[] args) throws Exception {
        //TableName tableName = TableName.valueOf("test.hbase");
        //String[] familyNames = new String[]{"host", "url", "text"};
        //String row = "hewei";
        //deleteTable(tableName);
        //createTable(tableName, familyNames);
        //addRow(tableName, row, "host", "shuyun", "hostvalue" + System.currentTimeMillis() / 1000);
        ////        getRow(tableName, row);
        //getAllRows(tableName);
        ////        delRow(tableName, "hewei");
        ////        delMultiRows(tableName, "hewei");
        //
        ////        deleteTable(tableName);


        TableName tableName = TableName.valueOf("log.trace");
        //deleteTable(tableName);
        HbaseTools.getAllRows(tableName);
    }
}
