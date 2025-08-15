# Ballerina Milvus Connector

[![Build](https://github.com/ballerina-platform/module-ballerinax-milvus/actions/workflows/ci.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerinax-milvus/actions/workflows/ci.yml)
[![Trivy](https://github.com/ballerina-platform/module-ballerinax-milvus/actions/workflows/trivy-scan.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerinax-milvus/actions/workflows/trivy-scan.yml)
[![GraalVM Check](https://github.com/ballerina-platform/module-ballerinax-milvus/actions/workflows/build-with-bal-test-native.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerinax-milvus/actions/workflows/build-with-bal-test-native.yml)
[![GitHub Last Commit](https://img.shields.io/github/last-commit/ballerina-platform/module-ballerinax-milvus.svg)](https://github.com/ballerina-platform/module-ballerinax-milvus/commits/main)
[![GitHub Issues](https://img.shields.io/github/issues/ballerina-platform/ballerina-library/module/milvus.svg?label=Open%20Issues)](https://github.com/ballerina-platform/ballerina-library/labels/module%2Fmilvus)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[Milvus](https://milvus.io/) is an open-source vector database built for scalable similarity search and AI applications. It provides high-performance vector storage and retrieval capabilities, making it ideal for applications involving machine learning, deep learning, and similarity search scenarios.

The Ballerina Milvus connector provides seamless integration with Milvus vector database, enabling developers to perform operations such as creating collections, inserting vector data, building indexes, and conducting similarity searches within Ballerina applications. It supports [Milvus SDK Java v2.6.1](https://github.com/milvus-io/milvus-sdk-java).

## Setup guide

To utilize the Milvus connector, you must have access to a running Milvus instance. You can use one of the following methods for that.

### Option 1: Using Docker

1. Make sure Docker is installed on your system.

2. Use the following command to start a Milvus standalone instance in docker

```bash
# Download the installation script
$ curl -sfL https://raw.githubusercontent.com/milvus-io/milvus/master/scripts/standalone_embed.sh -o standalone_embed.sh

#Start the Docker container
$ bash standalone_embed.sh start
```

For detailed installation instructions, refer to the official Milvus documentation.

- **Linux/macOS**: [Run Milvus in Docker](https://milvus.io/docs/install_standalone-docker.md)
- **Windows**: [Run Milvus in Docker on Windows](https://milvus.io/docs/install_standalone-windows.md)

### Option 2: Using Milvus Cloud by Zilliz

[Zilliz Cloud](https://cloud.zilliz.com/) provides a fully managed Milvus service. Follow these steps to set up your cloud instance:

1. **Sign up to Zilliz Cloud**: Visit [Zilliz Cloud](https://cloud.zilliz.com/) and create an account.

   <img src="https://raw.githubusercontent.com/ballerina-platform/module-ballerinax-milvus/main/ballerina/resources/sign_up.png" alt="Zilliz Cloud Sign Up" width="60%">

2. **Set up your account**: Complete the account setup process with your details.

   <img src="https://raw.githubusercontent.com/ballerina-platform/module-ballerinax-milvus/main/ballerina/resources/setup_account.png" alt="Account Setup" width="60%">

3. **Create a new cluster**: From the welcome page, select "Create Cluster" to start setting up your Milvus instance.

   <img src="https://raw.githubusercontent.com/ballerina-platform/module-ballerinax-milvus/main/ballerina/resources/welcome_page.png" alt="Welcome Page" width="60%">

4. **Configure cluster details**: Provide the necessary configuration details for your cluster, including cluster name, cloud provider, and region.

   <img src="https://raw.githubusercontent.com/ballerina-platform/module-ballerinax-milvus/main/ballerina/resources/create_cluster.png" alt="Create Cluster" width="60%">

5. **Download credentials**: Once your cluster is created, download the authentication credentials and connection details.

   <img src="https://raw.githubusercontent.com/ballerina-platform/module-ballerinax-milvus/main/ballerina/resources/cluster_creation.png" alt="Cluster Creation Complete" width="60%">

6. **Generate API Key**: Navigate to the API Keys section in your cluster dashboard and generate an API key for authentication.

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
