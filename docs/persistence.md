# Persistence

The databse is setup and migrated using flyway. 

**NB - the DB is never created from the java code. It is designed as a database and created using DDL**

Persistence in the application is handled using Spring Boot and JPA.


## Dependencies

        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-jpa</artifactId>
        </dependency>
        <dependency> <!-- Gets the tomcat data source -->
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>persistence-api</artifactId>
        </dependency>
        <dependency> 
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
            <version>${flyway.version}</version>
        </dependency>


## Flyway

[Flyway](https://flywaydb.org/) is used to create and migrate the database between versions. Spring Boot 
will automatically run a flyway migration on startup if both flyway-core and migration files are on the classpath.
In this project both are true, the migration files are in the exchange-dbmigrations module.

## Domain Object IDs

Postgres is used as the DB. The autoincrement type SERIAL is used for the IDs. Under the hood, Postgres
creates a sequence with the name

  classname_idfieldname_seq
  
So for the class 'Orders' with the id field 'id', the sequence will be called

  orders_id_seq

The annotations for this are:

        @Id
        @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="orders_id_seq")
        @SequenceGenerator(name="orders_id_seq", sequenceName="orders_id_seq", allocationSize=1)
        private final long id;
        
These annotations will work for other databases but the sequence will need to be created explicitly.        


## Domain objects


## Repositories

The repositories are wired into Spring Boot by adding the following annotation to the main class

    @EnableJpaRepositories(basePackages = {"net.sorted.exchange.orders.repository"})


## Testing

