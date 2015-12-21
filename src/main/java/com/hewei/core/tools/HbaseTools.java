package com.hewei.core.tools;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hewei
 * @version 5.0
 * @date 15/12/21  17:35
 * @desc
 */
public class HbaseTools {

    private static final Logger logger = LoggerFactory.getLogger(HbaseTools.class);

    static Connection connection;

    static {
        Configuration conf = HBaseConfiguration.create();

        String zk = System.getenv().get("hbase.zookeeper.quorum");

        if (StringUtils.isEmpty(zk)) {
            throw new RuntimeException("hbase.zookeeper.quorum is empty");
        }

        conf.set("hbase.zookeeper.quorum", zk);
        //conf.set("hbase.zookeeper.property.clientPort", "2181");

        try {
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(TableName tableName, String[] columnFamilys) throws Exception {
        Admin admin = connection.getAdmin();
        if (admin.tableExists(tableName)) {
            logger.info("表已经存在");
        } else {
            HTableDescriptor tableDesc = new HTableDescriptor(tableName);
            for (String columnFamily : columnFamilys) {
                tableDesc.addFamily(new HColumnDescriptor(columnFamily));
            }
            admin.createTable(tableDesc);
            logger.info("创建表成功");
        }
    }

    public static void deleteTable(TableName tableName) throws Exception {
        Admin admin = connection.getAdmin();
        if (admin.tableExists(tableName)) {
            admin.disableTable(tableName);// 关闭一个表
            admin.deleteTable(tableName); // 删除一个表
            logger.info("删除表成功");
        } else {
            logger.info("删除的表不存在");
        }
    }

    public static void addRow(TableName tableName, String row, String columnFamily, String column, String value) throws Exception {
        Table table = connection.getTable(tableName);
        Put put = new Put(Bytes.toBytes(row));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));// 参数出分别：列族、列、值
        table.put(put);
        logger.info("添加行成功");
    }

    public static BufferedMutator mutator (String tableName) throws IOException {
        return connection.getBufferedMutator(TableName.valueOf(tableName));
    }

    public static void delRow(TableName tableName, String row) throws Exception {
        Table table = connection.getTable(tableName);
        Delete del = new Delete(Bytes.toBytes(row));
        table.delete(del);
        logger.info("删除行成功");
    }

    public static void delMultiRows(TableName tableName, String... rows) throws Exception {
        Table table = connection.getTable(tableName);
        List<Delete> list = new ArrayList<>();
        for (String row : rows) {
            Delete del = new Delete(Bytes.toBytes(row));
            list.add(del);
        }
        table.delete(list);
        logger.info("删除多行成功");
    }

    public static void getRow (TableName tableName, String row) throws Exception {
        Table table = connection.getTable(tableName);
        Get get = new Get(Bytes.toBytes(row));
        Result result = table.get(get);
        printlnResult(result);
    }

    public static void getAllRows (TableName tableName) throws Exception {
        Table table = connection.getTable(tableName);
        Scan scan = new Scan();
        try (ResultScanner results = table.getScanner(scan)) {
            for (Result result : results) {
                printlnResult(result);
            }
        }
    }

    private static void printlnResult (Result result) {
        for (Cell cell : result.rawCells()) {
            logger.info("Row Name: {}", new String(CellUtil.cloneRow(cell)));
            logger.info("Timestamp: {}", cell.getTimestamp());
            logger.info("column Family: {}", new String(CellUtil.cloneFamily(cell)));
            logger.info("Row Name:  {}", new String(CellUtil.cloneQualifier(cell)));
            logger.info("Value: {}", new String(CellUtil.cloneValue(cell)));
        }
    }

}
