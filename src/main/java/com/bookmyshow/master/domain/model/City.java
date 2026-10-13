package com.bookmyshow.master.domain.model;

import com.bookmyshow.shared.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

/**
 * City entity in the Master Data domain representing service locations.
 */
@Entity
@Table(
        name = "cities",
        catalog = "master",
        uniqueConstraints = @UniqueConstraint(name = "uq_master_city_name_state", columnNames = {"name", "state"})
)
public class City extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "country", nullable = false, length = 100)
    private String country = "India";

    @Column(name = "pincode", length = 20)
    private String pincode;

    @Column(name = "is_popular", nullable = false)
    private boolean isPopular = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MasterStatus status = MasterStatus.ACTIVE;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    public City() {
    }

    public City(String name, String state, String country, String pincode, boolean isPopular, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.state = state;
        this.country = country != null ? country : "India";
        this.pincode = pincode;
        this.isPopular = isPopular;
        this.status = MasterStatus.ACTIVE;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void markAsPopular() {
        this.isPopular = true;
    }

    public void unmarkAsPopular() {
        this.isPopular = false;
    }

    public void activate() {
        this.status = MasterStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = MasterStatus.INACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public MasterStatus getStatus() {
        return status;
    }

    public void setStatus(MasterStatus status) {
        this.status = status;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
