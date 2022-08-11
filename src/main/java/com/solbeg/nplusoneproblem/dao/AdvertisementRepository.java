package com.solbeg.nplusoneproblem.dao;

import com.solbeg.nplusoneproblem.entity.TargetedAdvertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository extends JpaRepository<TargetedAdvertisement, Long> {
}
