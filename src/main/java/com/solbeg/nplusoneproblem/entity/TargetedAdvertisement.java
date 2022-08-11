package com.solbeg.nplusoneproblem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "targeted_ads")
@Data
@NoArgsConstructor
public class TargetedAdvertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ad_banner_url", nullable = false)
    private String bannerUrl;

    @ToString.Exclude
    @ManyToOne
    private Topic topic;
}
