// Copyright (c) 2025 WSO2 LLC. (http://www.wso2.com).
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

import ballerina/http;

# Represents any error related to Ballerina Milvus connector.
public type Error distinct error;

# Represents the configuration for the Milvus connection.
#
# + username - The username for the Milvus connection
# + password - The password for the Milvus connection
# + dbName - The database name for the Milvus connection
public type ConnectionConfig record {
    *http:ClientConfiguration;
    string username?;
    string password?;
    string dbName?;
};

# Represents the request for the upsert operation.
#
# + collectionName - The name of the collection to upsert data into
# + partitionName - The name of the partition to upsert data into
# + databaseName - The name of the database to upsert data into
# + data - The data to upsert into the Milvus collection
public type UpsertRequest record {
    string collectionName;
    string partitionName?;
    string databaseName?;
    record {
        string primaryKey;
        float[] vectors;
    } data;
};

# Represents the request for the delete operation.
#
# + collectionName - The name of the collection to delete data from
# + partitionName - The name of the partition to delete data from
# + id - The id of the data to delete
# + filter - The filter to delete data from the Milvus collection
public type DeleteRequest record {
    string collectionName;
    string partitionName?;
    int[] id?;
    string filter?;
};

# Represents the request for the search operation.
#
# + collectionName - The name of the collection to search data from
# + partitionName - The name of the partition to search data from
# + vectors - The vectors to search for
# + topK - The number of results to return
# + filter - The filter to search for
# + outputFields - The fields to return
# + roundDecimal - The number of decimal places to round the results to
# + metricType - The metric type to use for the search
# + indexType - The type of index to use for the search
# + indexName - The name of the index to use for the search
public type SearchRequest record {
    string collectionName;
    string partitionName?;
    float[] vectors;
    int topK;
    string filter?;
    string outputFields?;
    string roundDecimal?;
    string metricType?;
    string indexType?;
    string indexName?;
};

# Represents the result of the search operation.
#
# + primaryKey - The primary key of the result
# + similarityScore - The similarity score of the result
# + outputFields - The output fields of the result
public type SearchResult record {
    int primaryKey;
    float similarityScore;
    record{} outputFields?;
};

# Represents the request for the create collection operation.
#
# + collectionName - The name of the collection to create
# + dimension - The dimension of the collection
# + primaryKeyName - The name of the primary key of the collection
public type CreateCollectionRequest record {
    string collectionName;
    int dimension;
    string primaryKeyName = "primary_key";
};

# Represents the request for the create index operation.
#
# + collectionName - The name of the collection to create an index for
# + primaryKey - The name of the primary key of the collection
# + fieldNames - The names of the fields to create an index for
public type CreateIndexRequest record {
    string collectionName;
    string primaryKey;
    string[] fieldNames;
};
