/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com)
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.lib.milvus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.ballerina.runtime.api.creators.TypeCreator;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.ArrayType;
import io.ballerina.runtime.api.types.RecordType;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.LoadCollectionReq;
import io.milvus.v2.service.index.request.CreateIndexReq;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.UpsertReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.DeleteResp;
import io.milvus.v2.service.vector.response.SearchResp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.ballerina.lib.milvus.Utils.applyDynamicFields;

public class Client {

    public static void initiateClient(BObject clientObj, BString serviceUrl, BMap<String, Object> config) {
        BMap<?, ?> authConfig = config.getMapValue(StringUtils.fromString("auth"));
        String token = null;
        if (authConfig != null) {
            BString tokenValue = authConfig.getStringValue(StringUtils.fromString("token"));
            if (tokenValue != null) {
                token = tokenValue.getValue();
            }
        }
        ConnectConfig connectConfig = ConnectConfig.builder()
                .uri(serviceUrl.getValue())
                .token(token)
                .build();
        MilvusClientV2 client = new MilvusClientV2(connectConfig);
        clientObj.addNativeData("client", client);
    }

    public static Object createIndex(BObject clientObject, BMap<String, Object> request) {
        String collectionName = request.getStringValue(StringUtils.fromString("collectionName")).getValue();
        BArray fieldNames = request.getArrayValue(StringUtils.fromString("fieldNames"));
        BString primaryKey = request.getStringValue(StringUtils.fromString("primaryKey"));
        MilvusClientV2 client = (MilvusClientV2) clientObject.getNativeData("client");
        List<IndexParam> indexParams = new ArrayList<>();
        for (String fieldName : fieldNames.getStringArray()) {
            indexParams.add(IndexParam.builder().fieldName(fieldName).build());
        }
        indexParams.add(IndexParam.builder()
                .indexType(IndexParam.IndexType.AUTOINDEX)
                .fieldName(primaryKey.getValue())
                .build());
        CreateIndexReq createIndexReq = CreateIndexReq.builder()
                .collectionName(collectionName)
                .indexParams(indexParams)
                .build();
        client.createIndex(createIndexReq);
        return null;
    }

    public static Object loadCollection(BObject clientObject, BString collectionName) {
        MilvusClientV2 client = (MilvusClientV2) clientObject.getNativeData("client");
        client.loadCollection(LoadCollectionReq.builder()
                .collectionName(collectionName.getValue())
                .build());
        return null;
    }

    public static Object createCollection(BObject clientObject, BMap<String, Object> request) {
        MilvusClientV2 client = (MilvusClientV2) clientObject.getNativeData("client");
        String collectionName = request.getStringValue(StringUtils.fromString("collectionName")).getValue();
        String primaryKeyField = request.getStringValue(StringUtils.fromString("primaryKeyName")).getValue();
        Long dimension = request.getIntValue(StringUtils.fromString("dimension"));
        if (client == null) {
            throw new RuntimeException("Milvus client is not initialized");
        }
        CreateCollectionReq.CollectionSchema collectionSchema = MilvusClientV2.CreateSchema();
        collectionSchema.addField(AddFieldReq.builder()
                .fieldName(primaryKeyField)
                .dataType(DataType.Int64)
                .isPrimaryKey(Boolean.TRUE)
                .autoID(Boolean.FALSE)
                .description("Primary Key").build());

        collectionSchema.addField(AddFieldReq.builder()
                .fieldName("vector").dataType(DataType.FloatVector)
                .dimension(dimension.intValue()).build());

        CreateCollectionReq createCollectionRequest = CreateCollectionReq.builder()
                .collectionName(collectionName)
                .dimension(dimension.intValue())
                .collectionSchema(collectionSchema)
                .build();
        client.createCollection(createCollectionRequest);
        return null;
    }

    public static BArray listCollections(BObject clientObject) {
        MilvusClientV2 client = (MilvusClientV2) clientObject.getNativeData("client");
        if (client == null) {
            throw new RuntimeException("Milvus client is not initialized");
        }
        List<String> collectionsList = client.listCollections().getCollectionNames();
        BString[] collectionNames = collectionsList.stream()
                .map(StringUtils::fromString)
                .toArray(BString[]::new);
        return ValueCreator.createArrayValue(collectionNames);
    }

    public static Object upsert(BObject clientObject, BMap<String, Object> request) {
        MilvusClientV2 client = (MilvusClientV2) clientObject.getNativeData("client");
        String collectionName = request.getStringValue(StringUtils.fromString("collectionName")).getValue();
        BMap<?, ?> data = request.getMapValue(StringUtils.fromString("data"));
        double[] vectors = ((BArray) data.get(StringUtils.fromString("vectors"))).getFloatArray();
        String primaryKey = data.getStringValue(StringUtils.fromString("primaryKey")).getValue();

        if (client == null) {
            throw new RuntimeException("Milvus client is not initialized");
        }
        Gson gson = new Gson();
        JsonObject row = new JsonObject();
        List<Double> vectorList = new ArrayList<>();
        for (double vector : vectors) {
            vectorList.add(vector);
        }
        row.add("vector", gson.toJsonTree(vectorList));
        row.add("primary_key", gson.toJsonTree(primaryKey));
        applyDynamicFields(data, gson, row);
        List<JsonObject> dataList = new ArrayList<>();
        dataList.add(row);
        UpsertReq upsertRequest = UpsertReq.builder()
                .collectionName(collectionName)
                .data(dataList)
                .build();
        client.upsert(upsertRequest);
        return null;
    }

    public static Object delete(BObject clientObject, BMap<String, Object> request) {
        MilvusClientV2 client = (MilvusClientV2) clientObject.getNativeData("client");
        BString collectionName = request.getStringValue(StringUtils.fromString("collectionName"));
        BString partitionName = request.getStringValue(StringUtils.fromString("partitionName"));
        BArray id = request.getArrayValue(StringUtils.fromString("id"));
        BString filter = request.getStringValue(StringUtils.fromString("filter"));
        
        DeleteReq.DeleteReqBuilder<?, ?> deleteReq = DeleteReq.builder();
        deleteReq = (collectionName != null) ? deleteReq.collectionName(collectionName.getValue()) : deleteReq;
        deleteReq = (partitionName != null) ? deleteReq.partitionName(partitionName.getValue()) : deleteReq;
        deleteReq = (id != null)
                ? deleteReq.ids(Arrays.stream(id.getIntArray()).boxed().collect(Collectors.toList())) : deleteReq;
        deleteReq = (filter != null) ? deleteReq.filter(filter.getValue()) : deleteReq;

        DeleteResp deleteResp = client.delete(deleteReq.build());
        return deleteResp.getDeleteCnt();
    }

    public static BArray search(BObject clientObject, BMap<String, Object> request) {
        MilvusClientV2 client = (MilvusClientV2) clientObject.getNativeData("client");
        BString collectionName = request.getStringValue(StringUtils.fromString("collectionName"));
        BArray partitionName = request.getArrayValue(StringUtils.fromString("partitionNames"));
        BArray vectors = request.getArrayValue(StringUtils.fromString("vectors"));
        BString filter = request.getStringValue(StringUtils.fromString("filter"));
        Long topK = request.getIntValue(StringUtils.fromString("topK"));

        SearchReq.SearchReqBuilder<?, ?> searchReq = SearchReq.builder();
        searchReq = (collectionName != null) ? searchReq.collectionName(collectionName.getValue()) : searchReq;
        searchReq = (partitionName != null)
                ? searchReq.partitionNames(Arrays.asList(partitionName.getStringArray())) : searchReq;
        searchReq = (vectors != null)
                ? searchReq.data(Collections.singletonList(new FloatVec(Arrays.stream(vectors.getFloatArray())
                        .mapToObj(d -> (float) d)
                        .collect(Collectors.toList()))))
                : searchReq;
        searchReq = (filter != null) ? searchReq.filter(filter.getValue()) : searchReq;
        searchReq = (topK != null) ? searchReq.topK(topK.intValue()) : searchReq;

        SearchResp searchR = client.search(searchReq.build());
        List<List<SearchResp.SearchResult>> searchResults = searchR.getSearchResults();

        RecordType recordType = TypeCreator.createRecordType("SearchResult",
                ModuleUtils.getModule(), 0, false, 1);
        ArrayType arrayType = TypeCreator.createArrayType(recordType);
        BArray[] resultArrays = new BArray[searchResults.size()];

        for (List<SearchResp.SearchResult> result : searchResults) {
            BMap<BString, Object>[] responses = new BMap[result.size()];
            for (SearchResp.SearchResult res : result) {
                BMap<BString, Object> response =
                        ValueCreator.createRecordValue(ModuleUtils.getModule(), "SearchResult");
                response.put(StringUtils.fromString("primaryKey"), res.getId());
                response.put(StringUtils.fromString("similarityScore"), res.getScore().doubleValue());
                responses[result.indexOf(res)] = response;
            }
            BArray responseArray = ValueCreator.createArrayValue(responses,
                    TypeCreator.createArrayType(recordType));
            resultArrays[searchResults.indexOf(result)] = responseArray;
        }
        return ValueCreator.createArrayValue(resultArrays, TypeCreator.createArrayType(arrayType));
    }
}
