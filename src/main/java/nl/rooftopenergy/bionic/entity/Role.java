package nl.rooftopenergy.bionic.entity;

import javax.persistence.*;

/**
 * Created by Владислав on 28.01.2015.
 */
@Entity
@Table(name = "role")
public class Role {

    private Integer roleId;
    private String name;

    @Id
    @Column(name = "roleId")
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
