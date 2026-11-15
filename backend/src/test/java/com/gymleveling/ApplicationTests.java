package com.gymleveling;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApplicationTests {

	@Autowired
	JdbcTemplate jdbc;

	@Test
	void contextLoads_andBaselineMigrationApplied() {
		Integer count = jdbc.queryForObject(
				"select count(*) from flyway_schema_history where version = '1' and success", Integer.class);
		assertThat(count).isEqualTo(1);
	}

}
