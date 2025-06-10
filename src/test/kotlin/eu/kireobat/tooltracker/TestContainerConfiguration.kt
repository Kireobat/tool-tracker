package eu.kireobat.tooltracker

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [TestContainerConfiguration.Initializer::class])
@Testcontainers
class TestContainerConfiguration {

    @Component
    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {

            TestPropertyValues.of(
                // flyway property overrides
                "spring.datasource.url=${postgresContainer.jdbcUrl}",
                "spring.flyway.enabled=true",
                "spring.flyway.baseline-on-migrate=true",
                "spring.datasource.username=postgres",
                "spring.datasource.password=postgres",
                "spring.flyway.locations=classpath:/db/migration",
                "spring.flyway.schemas=postgres",
                "spring.flyway.user=postgres",
                "spring.flyway.password=postgres",
                "spring.flyway.url=${postgresContainer.jdbcUrl}",
                "spring.flyway.table=TOOL_TRACKER_SCHEMA_VERSION",
                "spring.flyway.baseline-on-migrate=true"
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        val postgresContainer: PostgreSQLContainer<*>
        private const val RE_USE_CONTAINERS: Boolean = false

        init {
            // define postgres test container

            postgresContainer = PostgreSQLContainer(
                DockerImageName.parse("postgres:17.5-alpine3.22")
                    .asCompatibleSubstituteFor("postgres")
            )
                .withEnv(
                    "POSTGRES_PASSWORD",
                    "postgres"
                )
                .withClasspathResourceMapping(
                    "db/postgres_testcontainer_pre_migration_script.sql",
                    "docker-entrypoint-initdb.d/0_init.sql",
                    BindMode.READ_ONLY
                ).withReuse(RE_USE_CONTAINERS).withDatabaseName("postgres").withExposedPorts(5432).withUsername("postgres").withPassword("postgres")

            // starting all defined containers

            postgresContainer.start()

        }
    }
}
