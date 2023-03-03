package com.simple.connector.demo;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemToKafkaDemo {
    private static final Logger log = LoggerFactory.getLogger(FileSystemToKafkaDemo.class);


    public static void main(String[] args) throws InterruptedException {

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();
        TableEnvironment tableEnvironment = TableEnvironment.create(settings);

        String sourceTable = "CREATE TABLE sourceTable " +
                "( id BIGINT , title VARCHAR) " +
                "WITH ( " +
                "'connector' = 'filesystem', " +
                "'path' = './data.json', " +
                "'format' = 'json')";
        tableEnvironment.executeSql(sourceTable);

        String sinkTable = "CREATE TABLE sinkTable " +
                "( id BIGINT , title VARCHAR) " +
                "WITH ( 'connector' = 'kafka', " +
                "'topic' = 'cct_topic', " +
                "'format' = 'json',  " +
                "'json.fail-on-missing-field' = 'false', " +
                "'properties.bootstrap.servers' = '127.0.0.1:9092', " +
                "'properties.group.id' = 'group_id', " +
                "'properties.enable.auto.commit' = 'true', " +
                "'properties.auto.offset.reset' = 'earliest', " +
                "'properties.auto.commit.interval.ms' = '1000' )";
        tableEnvironment.executeSql(sinkTable);

//        tableEnvironment.executeSql("select * from sourceTable").print();

        /**确认与milvus collection schema一致**/
        String insertSql = "insert into sinkTable " +
                "select id, title  " +
                "from sourceTable";
        tableEnvironment.executeSql(insertSql);

//        tableEnvironment.executeSql("select * from sinkTable").print();

    }



}