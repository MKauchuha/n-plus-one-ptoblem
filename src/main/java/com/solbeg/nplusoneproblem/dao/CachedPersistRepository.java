package com.solbeg.nplusoneproblem.dao;

import com.solbeg.nplusoneproblem.entity.CachedPersist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CachedPersistRepository extends JpaRepository<CachedPersist, Long> {
}
