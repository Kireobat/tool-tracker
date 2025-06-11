package eu.kireobat.tooltracker

import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

class TestDataLoaderUtil {

    private fun loadTestDataToDataSource(filePath: String, dataSource: DataSource) {
        ResourceDatabasePopulator(false, false, "UTF-8", ClassPathResource(filePath)).execute(dataSource)
    }
    fun cleanAllTestData(dataSource: DataSource) {
        loadTestDataToDataSource("db/delete_all_test_data.sql", dataSource)
        loadTestDataToDataSource("db/reset_all_sequences.sql", dataSource)
    }
    fun syncSequences(dataSource: DataSource) {
        loadTestDataToDataSource("db/sync_sequences.sql", dataSource)
    }
    fun insertUsers(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_users.sql", dataSource)
    }
    fun insertRoles(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_roles.sql", dataSource)
    }
    fun insertUsersMapRoles(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_users_map_roles.sql", dataSource)
    }
    fun insertToolTypes(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_tool_types.sql", dataSource)
    }
    fun insertTools(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_tools.sql", dataSource)
    }
    fun insertLendingAgreements(dataSource: DataSource) {
        loadTestDataToDataSource("db/insert_lending_agreements.sql", dataSource)
    }
}