package com.solbeg.nplusoneproblem;

import com.solbeg.nplusoneproblem.dao.CachedPersistRepository;
import com.solbeg.nplusoneproblem.entity.CachedPersist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.liquibase.enabled=true"
})
@Import(TestConfig.class)
public class CachedPersistTest {

    @Autowired
    CachedPersistRepository repository;

    @Test
    @Transactional
    void shouldPersistData() {
        List<CachedPersist> persist = generateTestData(100);
        repository.saveAll(persist);
    }

    private List<CachedPersist> generateTestData(int count) {
        List<CachedPersist> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CachedPersist cachedPersist = new CachedPersist();
            cachedPersist.setTestData("data: " + i);
            result.add(cachedPersist);
        }
        return result;
    }
}
