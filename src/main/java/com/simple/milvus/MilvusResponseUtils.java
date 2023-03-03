package com.simple.milvus;

import io.milvus.param.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilvusResponseUtils {
    private static final Logger log = LoggerFactory.getLogger(MilvusResponseUtils.class);


    public static void checkResponse(R response) throws MilvusException {
        if(response == null) {
            throw new MilvusException("response is null");
        }
        if(response.getStatus() != R.Status.Success.getCode()) {
            log.error("milvus response occurs Exception", R.Status.valueOf(response.getStatus()).toString());
            throw new MilvusException(response);
        }
    }
}
