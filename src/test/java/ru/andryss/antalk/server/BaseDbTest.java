package ru.andryss.antalk.server;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.andryss.antalk.server.config.MockBeansConfig;

@SpringBootTest
@ActiveProfiles("functionalTest")
@AutoConfigureEmbeddedDatabase(
        refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY
)
@Import(MockBeansConfig.class)
public abstract class BaseDbTest {
}
