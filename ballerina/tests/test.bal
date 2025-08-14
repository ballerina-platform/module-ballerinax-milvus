// Copyright (c) 2025 WSO2 LLC (http://www.wso2.com).
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/test;

Client milvusClient = check new(serviceUrl = "http://localhost:19530");

string collectionName = "test_collections";
string id  = "10001";
string primaryKey = "id";

@test:Config {}
function testCreateCollection() returns error? {
    check milvusClient->createCollection({
        collectionName,
        dimension: 3
    });
}

@test:Config {
    groups: ["list"]
}
function testListCollections() returns error? {
    string[] collection = milvusClient->listCollections();
    test:assertNotEquals(collection.indexOf(collectionName), ());
}

@test:Config {
    groups: ["upsert"]
}
function testUpsertEntry() returns error? {
    check milvusClient->upsert({
        collectionName,
        data: {
            id,
            vectors: [0.3, 0.4, 0.5],
            "chunk": {
                "content": "test",
                "type": "text",
                "metadata": {
                    "createdTime": "1723600000"
                }
            }
        }
    });
}

@test:Config {
    groups: ["delete"],
    dependsOn: [testSearchNearVectors]
}
function testDeleteEntry() returns error? {
    _ = check milvusClient->delete({
        collectionName,
        filter: string `${primaryKey} == ${id}`
    });
}

@test:Config {
    groups: ["query"],
    dependsOn: [testUpsertEntry, testCreateCollection]
}
function testSearchNearVectors() returns error? {
    check milvusClient->loadCollection(collectionName);
    SearchResult[][] result = milvusClient->search({
        collectionName,
        vectors: [0.3, 0.4, 0.5],
        topK: 10
    });
    if result.length() > 0 {
        result = milvusClient->search({
            collectionName,
            vectors: [0.3, 0.4, 0.5],
            topK: 1,
            filter: string `${primaryKey} == ${id}`
        });
        test:assertEquals(result[0][0].primaryKey, check int:fromString(id));
    }
}
