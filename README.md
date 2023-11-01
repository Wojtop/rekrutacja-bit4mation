# Launching project

### Docker
Suggested form for launching project is to launch it with docker.

To launch type `docker compose up -d` in project directory (where `docker-compose.yml` is located).

### Launching outside docker

Launching project outside docker requires maven, postgres and Node.js.

To launch project without docker:
1. Create database tables from script and adjust connection string in `tree-backend/src/main/resources/application.properties` . Script creating required tables and populating them with data is located in `./db-files/db-creation.sql`.</li>
2. Install Node dependencies from `./tree-frontend/package-lock.json`.
3. In `./tree-backend` Run `mvn spring-boot:run`.
4. In `./tree-fronted` run `npm start`.

# Access to application
[Frontend application](http://localhost:3000) is accessible at `http://localhost:3000`.

[Backend api](http://localhost:8080/swagger-ui/index.html#/) is exposed at `http://localhost:8080/tree`. Api documentation is accessible at `http://localhost:8080/swagger-ui/index.html#/`.

# Project structure
* `./db-files` - directory shared with docker container, where postgres data is persisted. This allows to keep data between container creations. File `db-creation.sql` contains scripts for table creation and inserts example data.
* `./tree-backend` - directory containing backend Spring application. Backend is using maven as a build tool.
* `./tree-frontend` - directory containing React frontend application. Frontend requires Node.js for running.






