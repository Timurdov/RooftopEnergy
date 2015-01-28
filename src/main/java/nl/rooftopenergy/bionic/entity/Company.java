package nl.rooftopenergy.bionic.entity;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;


/**
 * Created by UFO on 17.11.2014.
 */
@Entity
@Table(name = "Company")
public class Company {
    private Integer companyId;
    private String companyName;
    private String country;
    private String province;
    private String town;
    private String street;
    private Integer zipcode;
    private String description;
    private Boolean publicStatus;
    private RtfBox rtfBox;

    @Id
    @Column(name = "CompanyID")
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    @Basic
    @Column(name = "CompanyName")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Basic
    @Column(name = "Country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Basic
    @Column(name = "Province")
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Basic
    @Column(name = "Town")
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @Basic
    @Column(name = "Street")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Basic
    @Column(name = "Zipcode")
    public Integer getZipcode() {
        return zipcode;
    }

    public void setZipcode(Integer zipcode) {
        this.zipcode = zipcode;
    }

    @Basic
    @Column(name = "PublicStatus")
    public Boolean getPublicStatus() {
        return publicStatus;
    }

    public void setPublicStatus(Boolean publicStatus) {
        this.publicStatus = publicStatus;
    }

    @Basic
    @Column(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToOne(mappedBy = "company")
    public RtfBox getRtfBox() {
        return rtfBox;
    }

    public void setRtfBox(RtfBox rtfBox) {
        this.rtfBox = rtfBox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        if (companyId != company.companyId) return false;
        if (companyName != null ? !companyName.equals(company.companyName) : company.companyName != null)
            return false;
        if (street != null ? !street.equals(company.street) : company.street != null) return false;
        if (town != null ? !town.equals(company.town) : company.town != null) return false;
        if (zipcode != null ? !zipcode.equals(company.zipcode) : company.zipcode != null) return false;
        if (country != null ? !country.equals(company.country) : company.country != null) return false;
        if (province != null ? !province.equals(company.province) : company.province != null) return false;
        if (description != null ? !description.equals(company.description) : company.description != null) return false;
        if (publicStatus != null ? !publicStatus.equals(company.publicStatus) : company.publicStatus != null)
            return false;
        //commented because we have some problems with equals through lazy loading
        //if (rtfBoxList != null ? !rtfBoxList.equals(company.rtfBoxList) : company.rtfBoxList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = companyId;
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (town != null ? town.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (zipcode != null ? zipcode.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (publicStatus != null ? publicStatus.hashCode() : 0);
        return result;
    }

  }
