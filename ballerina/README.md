## Overview

[Milvus](https://milvus.io/) is an open-source vector database built for scalable similarity search and AI applications. It provides high-performance vector storage and retrieval capabilities, making it ideal for applications involving machine learning, deep learning, and similarity search scenarios.

The Ballerina Milvus connector provides seamless integration with Milvus vector database, enabling developers to perform operations such as creating collections, inserting vector data, building indexes, and conducting similarity searches within Ballerina applications. It supports [Milvus SDK Java v2.6.1](https://github.com/milvus-io/milvus-sdk-java).

## Setup guide

To utilize the Milvus connector, you must have access to a running Milvus instance. You can use one of the following methods for that.

### Option 1: Using Docker

1. Make sure Docker is installed on your system.

2. Use the following command to start a Milvus standalone instance in docker

```bash
docker run -d --name milvus -p 19530:19530 -p 9091:9091 milvusdb/milvus:v2.6.0 milvus run standalone
```

### Option 2: Using Milvus Cloud by Zilliz

1. Create a Zilliz account: Visit [Zilliz Cloud](https://cloud.zilliz.com/) and create an account.

2. Create a new cluster.

3. Navigate to the API Keys section and generate an API key.

## Quickstart

To use the Milvus connector in your Ballerina project, modify the `.bal` file as follows.

### Step 1: Import the module

Import the `ballerinax/milvus` module into your Ballerina project.

```ballerina
import ballerinax/milvus;
```

### Step 2: Instantiate a new connector

Create a `milvus:Client` with the Milvus server URL and optional configuration.

```ballerina
configurable string milvusUrl = ?;

milvus:Client milvusClient = check new(milvusUrl);
```

For authenticated connections, you can provide additional configuration.

```ballerina
milvus:Client milvusClient = check new(milvusUrl);
```

### Step 3: Invoke the connector operations

You can now utilize the operations available within the connector.

```ballerina
public function main() returns error? {
    check milvusClient->createCollection({
        collectionName: "example_collection",
        dimension: 2
    });
}
```

### Step 4: Run the Ballerina application

Use the following command to compile and run the Ballerina program.

```bash
bal run
```

## Issues and projects

The **Issues** and **Projects** tabs are disabled for this repository as this is part of the Ballerina library. To report bugs, request new features, start new discussions, view project boards, etc., visit the Ballerina library [parent repository](https://github.com/ballerina-platform/ballerina-library).

This repository only contains the source code for the package.

## Building from the source

### Prerequisites

1. Download and install Java SE Development Kit (JDK) version 17. You can download it from either of the following sources:

   - [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
   - [OpenJDK](https://adoptium.net/)

    > **Note:** After installation, remember to set the `JAVA_HOME` environment variable to the directory where JDK was installed.

2. Download and install [Ballerina Swan Lake](https://ballerina.io/).

3. Download and install [Docker](https://www.docker.com/get-started).

    > **Note**: Ensure that the Docker daemon is running before executing any tests.

4. Generate a Github access token with read package permissions, then set the following `env` variables:

    ```bash
   export packageUser=<Your GitHub Username>
   export packagePAT=<GitHub Personal Access Token>
    ```

### Build options

Execute the commands below to build from the source.

1. To build the package:

   ```bash
   ./gradlew clean build
   ```

2. To run the tests:

   ```bash
   ./gradlew clean test
   ```

3. To build the without the tests:

   ```bash
   ./gradlew clean build -x test
   ```

4. To debug package with a remote debugger:

   ```bash
   ./gradlew clean build -Pdebug=<port>
   ```

5. To debug with Ballerina language:

   ```bash
   ./gradlew clean build -PbalJavaDebug=<port>
   ```

6. Publish the generated artifacts to the local Ballerina central repository:

   ```bash
   ./gradlew clean build -PpublishToLocalCentral=true
   ```

7. Publish the generated artifacts to the Ballerina central repository:

   ```bash
   ./gradlew clean build -PpublishToCentral=true
   ```

## Contributing to Ballerina

As an open source project, Ballerina welcomes contributions from the community.

For more information, go to the [contribution guidelines](https://github.com/ballerina-platform/ballerina-lang/blob/master/CONTRIBUTING.md).

## Code of conduct

All contributors are encouraged to read the [Ballerina Code of Conduct](https://ballerina.io/code-of-conduct).

## Useful links

- Discuss code changes of the Ballerina project in [ballerina-dev@googlegroups.com](mailto:ballerina-dev@googlegroups.com).
- Chat live with us via our [Discord server](https://discord.gg/ballerinalang).
- Post all technical questions on Stack Overflow with the [#ballerina](https://stackoverflow.com/questions/tagged/ballerina) tag.
