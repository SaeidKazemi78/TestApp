package io.javabrains.ratingsdataservice.model;

import ir.donyapardaz.niopdc.order.config.Profiles;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Country.
 */
//Country exists in base microservice
@Entity
@Table(catalog = "niopdcbase_" + Profiles.activeProfile, schema = "dbo", name = "country")
public class Country implements Serializable {


    @Id
    private Long id;

    @NotNull
    @Size(min = 3, max = 42)
    @Column(name = "name", length = 42, nullable = false, unique = true, columnDefinition = "nvarchar(42)")
    private String name;

    @NotNull
    @Size(min = 1, max = 4)
    @Column(name = "code", length = 4, nullable = false, unique = true)
    private String code;

    @Column(name = "check_national_code")
    private Boolean checkNationalCode;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country name(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Country code(String code) {
        this.code = code;
        return this;
    }

    public Boolean isCheckNationalCode() {
        return checkNationalCode;
    }

    public Country checkNationalCode(Boolean checkNationalCode) {
        this.checkNationalCode = checkNationalCode;
        return this;
    }

    public void setCheckNationalCode(Boolean checkNationalCode) {
        this.checkNationalCode = checkNationalCode;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Country country = (Country) o;
        if (country.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), country.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Country{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", checkNationalCode='" + isCheckNationalCode() + "'" +
            "}";
    }
}
