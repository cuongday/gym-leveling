package com.gymleveling.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gymleveling.TestClockConfiguration;
import com.gymleveling.TestcontainersConfiguration;
import com.gymleveling.shared.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import({ TestcontainersConfiguration.class, TestClockConfiguration.class })
@SpringBootTest
class UserServiceTest {

	@Autowired
	UserService users;

	@Autowired
	JdbcTemplate jdbc;

	@BeforeEach
	void clean() {
		jdbc.update("delete from refresh_tokens");
		jdbc.update("delete from pause_periods");
		jdbc.update("delete from users");
	}

	@Test
	void register_persistsNormalizedEmailAndDefaults() {
		UserProfile p = users.register(" A@B.com ", "hash", "Cường", "vi", null);
		assertThat(p.email()).isEqualTo("a@b.com");
		assertThat(p.timezone()).isEqualTo("Asia/Ho_Chi_Minh");
		assertThat(p.timezoneChangeAvailableAt()).isNull();
		assertThat(p.premium()).isFalse();
		assertThat(users.getProfile(p.id()).displayName()).isEqualTo("Cường");
	}

	@Test
	void register_duplicateEmailDifferentCase_throwsEmailTaken() {
		users.register(" A@B.com ", "hash", "Cường", "vi", null);
		assertThatThrownBy(() -> users.register("a@b.COM", "hash", "X", "en", null))
			.isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.getCode()).isEqualTo("auth.email_taken"));
	}

	@Test
	void register_invalidTimezone_throws() {
		for (String tz : new String[] { "+07:00", "Mars/Base" }) {
			assertThatThrownBy(() -> users.register("tz@b.com", "hash", "X", "en", tz)).isInstanceOfSatisfying(
					ApiException.class, e -> assertThat(e.getCode()).isEqualTo("user.invalid_timezone"));
		}
	}

	@Test
	void findAccountByEmail_isCaseInsensitive() {
		UserProfile p = users.register("a@b.com", "hash", "X", "en", "UTC");
		assertThat(users.findAccountByEmail(" A@B.COM ")).hasValueSatisfying(a -> {
			assertThat(a.id()).isEqualTo(p.id());
			assertThat(a.passwordHash()).isEqualTo("hash");
		});
		assertThat(users.findAccountByEmail("none@b.com")).isEmpty();
	}

}
