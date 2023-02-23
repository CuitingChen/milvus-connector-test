package cn.ac.ict.connector.demo;

import cn.ac.ict.milvus.MilvusUtils;
import io.milvus.grpc.DataType;
import io.milvus.param.collection.FieldType;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class FileSystemToMilvusDemo {
    private static final Logger log = LoggerFactory.getLogger(FileSystemToMilvusDemo.class);
    /**
     * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     * ！！！确认milvus collection 配置与数据、flink table一致！！！
     * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     * @param milvusUtils
     * @param collection
     */
    public static void createCollection(MilvusUtils milvusUtils, String collection) {
        if(milvusUtils.hasCollection(collection)) {
            log.warn("collection [{}] exists, will be drop it first", collection);
            String response = milvusUtils.dropCollection(collection);
            log.info("drop collection [{}] finished [{}]", collection, response);
        }
        FieldType idField = FieldType.newBuilder()
                .withName("id")
                .withAutoID(false)
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .build();
        FieldType nameField = FieldType.newBuilder()
                .withName("title")
                .withDataType(DataType.VarChar)
                .withMaxLength(255)
                .withPrimaryKey(false)
                .build();
        FieldType introField = FieldType.newBuilder()
                .withName("intro")
                .withDataType(DataType.FloatVector)
                .withDimension(2)
                .build();
        List<FieldType> fieldList = Arrays.asList(idField, nameField, introField);
        String response = milvusUtils.createCollection(collection, fieldList);
        log.info(response);

        boolean created = milvusUtils.hasCollection(collection);
        if(!created) {
            log.error("create milvus collection [{}] failed", collection);
            throw new RuntimeException("create milvus collection failed");
        }
        String description = milvusUtils.describeCollection(collection);
        log.info("collection [{}] description is [{}]", collection, description);

    }

    public static void main(String[] args) throws InterruptedException {
        MilvusUtils milvusUtils = new MilvusUtils();
        /**
         * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
         * 确认collection 与flink talbe 创建是的collection一致！！！
         * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
         */
        String collection = "test";
        createCollection(milvusUtils, collection);

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();
        TableEnvironment tableEnvironment = TableEnvironment.create(settings);

        String sourceTable = "CREATE TABLE sourceTable " +
                "( id BIGINT , title VARCHAR, intro ARRAY<FLOAT> ) " +
                "WITH ( " +
                "'connector' = 'filesystem', " +
                "'path' = './data.json', " +
                "'format' = 'json')";
        tableEnvironment.executeSql(sourceTable);

        String sinkTable = "CREATE TABLE sinkTable " +
                "( id BIGINT, title STRING , intro ARRAY<FLOAT> ) " +
                "WITH ( 'connector' = 'milvus', " +
                "'host' = '127.0.0.1', " +
                "'port'='19530', " +
                "'collName' = 'test' )"; //!!!确认与创建的milvus collection一致！！！
        tableEnvironment.executeSql(sinkTable);

        tableEnvironment.executeSql("select * from sourceTable").print();

        /**确认与milvus collection schema一致**/
        String insertSql = "insert into sinkTable " +
                "select id, title, intro  " +
                "from sourceTable";
        tableEnvironment.executeSql(insertSql);

        Thread.sleep(15000);
        /**
         * 验证数据写入milvus
         */
        long row = milvusUtils.getCollectionRowCount(collection);
        log.info("current collection has [{}] rows", row);
    }



}