package com.mossle.internal.store.rs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.internal.StoreConnector;
import com.mossle.api.internal.StoreDTO;

import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.IoUtils;

import com.mossle.ext.store.StoreResult;

import com.mossle.internal.store.service.StoreService;

import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.InputStreamResource;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

@Component
@Path("store")
public class StoreResource {
    private static Logger logger = LoggerFactory.getLogger(StoreResource.class);
    private StoreService storeService;
    private StoreConnector storeConnector;

    @GET
    @Path("getStore")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO getStore(@QueryParam("model") String model,
            @QueryParam("key") String key) {
        try {
            BaseDTO result = new BaseDTO();

            StoreDTO storeDto = storeConnector.getStore(model, key);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IoUtils.copyStream(storeDto.getDataSource().getInputStream(), baos);

            String base64 = new String(new Base64().encodeBase64(baos
                    .toByteArray()));

            result.setCode(200);
            result.setData(base64);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    @POST
    @Path("saveStore")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO saveStore(@FormParam("model") String model,
            @FormParam("content") String content,
            @FormParam("fileName") String fileName,
            @FormParam("contentType") String contentType) {
        try {
            byte[] bytes = new Base64().decodeBase64(content.getBytes("utf-8"));

            BaseDTO result = new BaseDTO();
            StoreResult storeResult = storeService.saveStore(model, fileName,
                    contentType, bytes);
            result.setCode(200);
            result.setData(storeResult.getKey());

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    @GET
    @Path("view")
    public InputStream view(@QueryParam("model") String model,
            @QueryParam("key") String key) throws Exception {
        StoreDTO storeDto = storeConnector.getStore(model, key);

        return storeDto.getDataSource().getInputStream();
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }
}
