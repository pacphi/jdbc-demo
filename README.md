# JDBC Experiment  

This is a simple experiment to test a [Spring Boot/MVC](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc) interaction against a [PostgreSQL](https://www.postgresql.org) instance via the Postgres JDBC [driver](https://jdbc.postgresql.org). 

> I am the older brother of [Reactive ~~JDBC~~ Experiment](https://github.com/pacphi/reactive-jdbc-demo).

## Prerequisites

* An account with [Space Developer role](https://docs.cloudfoundry.org/concepts/roles.html#roles) access on a Cloud Foundry foundation, e.g., [Pivotal Web Services](https://run.pivotal.io)
* [CF CLI](https://github.com/cloudfoundry/cli#downloads) 6.37.0 or better if you want to push the application to a Cloud Foundry (CF) instance
* [httpie](https://httpie.org/#installation) 0.9.9 or better to simplify interaction with API endpoints
* Java [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 1.8u172 or better to compile and run the code
* [Gradle](https://gradle.org/releases/) 4.8 or better to build and package source code
* Docker for [Mac](https://store.docker.com/editions/community/docker-ce-desktop-mac) or [Windows](https://store.docker.com/editions/community/docker-ce-desktop-windows) for spinning up a local instance of Postgres and Adminer (a database administration interface)


## Clone

```bash
git clone https://github.com/pacphi/jdbc-demo.git
```

## How to build

```bash
cd jdbc-demo
gradle build
```

## How to run locally 

1. Prepare database

    Open a Terminal session, then type

    ```bash
    docker-compose up -d
    ```

2. Login to [Adminer](https://www.adminer.org) interface 

    Open a browser and visit `http://localhost:9090`

    Credentials are:

    * System => `PostgreSQL`
    * Server => `db`
    * Username => `admin`
    * Password => `passw0rd`
    * Database => `people`

    Click the `Login` button

    > At this point we're just verifying that you can connect to the database instance successfully.

3. Start the application

    Start a new Terminal session and type

    ```bash
    gradle bootRun \
    -Dspring.datasource.url=jdbc:postgresql://localhost:5432/people \
    -Dspring.datasource.username=admin \
    -Dspring.datasource.password=passw0rd \
    -Dspring.datasource.driver-class-name=org.postgresql.Driver 
    ```

4. Let's create some data using the API

    ```bash
    http POST localhost:8080/person firstName=Dweezil lastName=Zappa age=48

    HTTP/1.1 201 Created
    Content-Length: 0
    Date: Mon, 25 Jun 2018 00:36:35 GMT
    Location: /person/582279d1-9bd1-4e49-946c-ac720de0e04f
    ```

5. Verify that we can find the person we added

    ```bash
    http localhost:8080/person

    HTTP/1.1 200 OK
    Content-Type: application/json
    transfer-encoding: chunked

    [
        {
            "age": 48,
            "firstName": "Dweezil",
            "id": "582279d1-9bd1-4e49-946c-ac720de0e04f",
            "lastName": "Zappa"
        }
    ]
    ```

6. Let's ask for a person by id

    ```bash
    http localhost:8080/person/582279d1-9bd1-4e49-946c-ac720de0e04f

    HTTP/1.1 200 OK
    Content-Length: 95
    Content-Type: application/json

    {
        "age": 48,
        "firstName": "Dweezil",
        "id": "582279d1-9bd1-4e49-946c-ac720de0e04f",
        "lastName": "Zappa"
    }
    ```

## How to shutdown locally

1. Stop the application

    Visit the Terminal session where you started application and press `Ctrl+c`

2. Shutdown Postgres and Adminer interface

    Visit the Terminal session where you invoked `docker-compose-up -d` and type

    ```bash
    docker-compose down
    ```

    Note: the data volume is persistent!  If you want to destroy all unused volumes and reclaim some additional space, type

    ```bash
    docker volume prune
    ```

## How to run on Cloud Foundry

1. Authenticate to a foundation using the API endpoint. 
    > E.g., login to [Pivotal Web Services](https://run.pivotal.io)

    ```bash
    cf login -a https:// api.run.pivotal.io
    ```

2. Push the app, but don't start it

    ```bash
    cf push jdbc-demo --random-route --no-start -p ./build/libs/jdbc-demo-0.0.1-SNAPSHOT.jar -m 1G -b https://github.com/cloudfoundry/java-buildpack.git
    ```

3. Let's fire fire up a Postgres instance

    > We're going to use [ElephantSQL](https://www.elephantsql.com)

    ```bash
    cf cs elephantsql panda {service name}
    ```

    > Note: this is going to cost you $19/month to keep alive
    > Replace {service name} above with your desired service name

4. Next we'll bind the service to the application

    ```bash
    cf bs jdbc-demo {service name}
    ```

    > Make sure {service name} above matches what you defined in Step 3

5. Let's verify that `VCAP_SERVICES` was properly injected

    ```bash
    cf env jdbc-demo
 
    Getting env variables for app jdbc-demo in org scooby-doo / space dev as dweezil@zappa.com...
    OK

    System-Provided:
    {
    "VCAP_SERVICES": {
    "elephantsql": [
    {
        "binding_name": null,
        "credentials": {
        "max_conns": "20",
        "uri": "postgres://mkxolhau:BBB0bLLyWpiUqKozCRhzygyhnpOMlMC@stampy-01.db.elephantsql.com:5432/lankmxwt"
        },
        ...
    ```

    > We're interested in `vcap_services.elephantsql.uri`
    > The URI consists of {vendor}://{username}:{password}@{server}:5432/{database}

6. Now let's startup the application

    ```bash
    cf start jdbc-demo
    ```

7. Launch Adminer to administer the database

    > We want to verify that we can connect to the database and see that it contains both `flyway_schema_history` and `people` tables

    ```bash
    docker-compose up -d
    ```

    Open a browser and visit `http://localhost:9090`

    Credentials are:

    * System => `PostgreSQL`
    * Server => `{server}`
    * Username => `{username}`
    * Password => `{password}`
    * Database => `{database}`

    Replace all bracketed values above with what you learned from Step 5

    Click the `Login` button

8. Follow steps 4-6 above in `How to run locally` to interact with API

    But replace occurrences of `localhost:8080` with URL to application hosted on Cloud Foundry


## How to spin down workloads on Cloud Foundry

1. Stop the application

    ```bash
    cf stop jdbc-demo
    ```

2. Unbind the database instance

    ```bash
    cf us jdbc-demo {service name}
    ```
    > `{service name}` above should match value in `How to run on Cloud Foundry` steps 3 and 4

3. Delete the database instance

    ```bash
    cf ds {service name}
    ```
    > `{service name}` above should match value in `How to run on Cloud Foundry` steps 3 and 4

4. Delete the application

    ```bash
    cf delete jdbc-demo
    ```
