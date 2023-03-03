package com.simple.milvus;

import com.simple.connector.demo.EnvConstant;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DescribeCollectionResponse;
import io.milvus.grpc.FlushResponse;
import io.milvus.grpc.GetCollectionStatisticsResponse;
import io.milvus.param.ConnectParam;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.response.DescCollResponseWrapper;
import io.milvus.response.GetCollStatResponseWrapper;

import java.util.List;

public class MilvusUtils {

    public static final String HOST = EnvConstant.HOST;
    public static final int PORT = EnvConstant.PORT;
    private MilvusServiceClient milvusServiceClient;

    public MilvusUtils() {
        this.milvusServiceClient = milvusServiceClient(HOST, PORT);
    }


    public MilvusServiceClient milvusServiceClient(String host, int port) {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .build();
        return new MilvusServiceClient(connectParam);
    }

    public String createCollection(String collection, List<FieldType> fieldTypeList) {
        CreateCollectionParam.Builder builder = CreateCollectionParam.newBuilder()
                .withCollectionName(collection)
                .withShardsNum(2)
                .withFieldTypes(fieldTypeList);
        return createCollection(builder.build());

    }

    private String createCollection (CreateCollectionParam createCollectionParam) {
        R<RpcStatus> response = milvusServiceClient.createCollection(createCollectionParam);
        MilvusResponseUtils.checkResponse(response);
        return response.toString();
    }

    public String describeCollection(String collection) {
        R<DescribeCollectionResponse> respDescribeCollection = milvusServiceClient.describeCollection(
                // Return the name and schema of the collection.
                DescribeCollectionParam.newBuilder()
                        .withCollectionName(collection)
                        .build()
        );
        MilvusResponseUtils.checkResponse(respDescribeCollection);
        DescCollResponseWrapper wrapperDescribeCollection = new DescCollResponseWrapper(respDescribeCollection.getData());

        if(respDescribeCollection.getData() != null) {
            return wrapperDescribeCollection.toString();
        }
        return null;
    }

    public boolean hasCollection(String collectionName) {
        R<Boolean> response = milvusServiceClient.hasCollection(
                HasCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
        MilvusResponseUtils.checkResponse(response);
        Boolean has = response.getData();
        return has;
    }

    public String dropCollection(String collectionName) {
        R<RpcStatus> response = milvusServiceClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
        MilvusResponseUtils.checkResponse(response);
        return response.toString();
    }


    public long getCollectionRowCount(final String collectionName) {

        R<GetCollectionStatisticsResponse> respCollectionStatistics = milvusServiceClient.getCollectionStatistics(
                // Return the statistics information of the collection.
                GetCollectionStatisticsParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withFlush(true)
                        .build()
        );
        MilvusResponseUtils.checkResponse(respCollectionStatistics);
        long rowCount = 0;
        if(respCollectionStatistics.getData() != null) {
            GetCollStatResponseWrapper wrapperCollectionStatistics = new GetCollStatResponseWrapper(respCollectionStatistics.getData());
            rowCount = wrapperCollectionStatistics.getRowCount();
        }


        return rowCount;
    }

}
