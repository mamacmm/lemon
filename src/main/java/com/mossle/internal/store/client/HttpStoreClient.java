package com.mossle.internal.store.client;

import java.io.*;

import java.net.*;

import java.text.SimpleDateFormat;

import java.util.*;

import com.mossle.api.internal.StoreConnector;
import com.mossle.api.internal.StoreDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.IoUtils;

import com.mossle.ext.store.InputStreamDataSource;
import com.mossle.ext.store.StoreResult;

import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.InputStreamResource;

import org.springframework.stereotype.Component;

import org.springframework.util.FileCopyUtils;

public class HttpStoreClient implements StoreClient {
    private Logger logger = LoggerFactory.getLogger(HttpStoreClient.class);
    private String baseUrl;
    private String model;

    public StoreDTO saveStore(InputStream inputStream, String fileName,
            String contentType) throws Exception {
        URL url = new URL(baseUrl + "/saveStore");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IoUtils.copyStream(inputStream, baos);

        String content = new String(new Base64().encodeBase64(baos
                .toByteArray()));

        String queryString = "model=" + model + "&fileName=" + fileName
                + "&contentType=" + URLEncoder.encode(contentType, "utf-8")
                + "&content=" + URLEncoder.encode(content, "utf-8");
        logger.debug("queryString : {}", queryString);
        conn.getOutputStream().write(queryString.getBytes("utf-8"));
        conn.getOutputStream().flush();

        InputStream is = conn.getInputStream();
        int len = -1;
        byte[] b = new byte[1024];

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

        while ((len = is.read(b, 0, 1024)) != -1) {
            baos2.write(b, 0, len);
        }

        is.close();

        String json = new String(baos2.toByteArray());
        Map<String, String> map = new JsonMapper().fromJson(json, Map.class);

        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(model);
        storeDto.setKey(map.get("data"));
        storeDto.setDataSource(new InputStreamDataSource(inputStream));

        return storeDto;
    }

    public StoreDTO getStore(String key) throws Exception {
        String queryString = "model=" + model + "&key=" + key;
        URL url = new URL(baseUrl + "/getStore?" + queryString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoInput(true);

        logger.debug("queryString : {}", queryString);

        InputStream is = conn.getInputStream();
        int len = -1;
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

        while ((len = is.read(b, 0, 1024)) != -1) {
            baos2.write(b, 0, len);
        }

        is.close();

        String json = new String(baos2.toByteArray());
        Map<String, String> map = new JsonMapper().fromJson(json, Map.class);
        String base64 = map.get("data");
        logger.debug(base64);

        byte[] b2 = new Base64().decodeBase64(base64.getBytes());
        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(model);
        storeDto.setKey(key);
        storeDto.setDataSource(new InputStreamDataSource(
                new ByteArrayInputStream(b2)));

        return storeDto;
    }

    @Value("${store.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${store.model}")
    public void setModel(String model) {
        this.model = model;
    }
}
