package com.lambdaschool.usermodel.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The entity allowing interaction with the roles table.
 */
@Entity
@Table(name = "roles")
public class Role extends Auditable
{
    /**
     * The primary key (long) of the roles table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long roleid;

    /**
     * The name (String) of the role. Cannot be null and must be unique.
     */
    @Column(nullable = false,
        unique = true)
    private String name;


    /**
     * Creates a join table joining Users and Roles in a Many-To-Many relations.
     * Contains a List of Users Objects using this Role.
     */
    @OneToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "roles")
    private List<UserRoles> users = new ArrayList<>();

    /**
     * Default Constructor used primarily by the JPA.
     */
    public Role()
    {
    }

    /**
     * Given the name, create a new role object. User gets added later
     *
     * @param name the name of the role in uppercase
     */
    public Role(String name)
    {
        this.name = name.toUpperCase();
    }

    /**
     * Getter for role id
     *
     * @return the role id, primary key, (long) of this role
     */
    public long getRoleid()
    {
        return roleid;
    }

    /**
     * Setter for role id, used for seeding data
     *
     * @param roleid the new role id, primary key, (long) for this role
     */
    public void setRoleid(long roleid)
    {
        this.roleid = roleid;
    }

    /**
     * Getter for role name
     *
     * @return role name (String) in uppercase
     */
    public String getName()
    {
        if (name == null)
        {
            return null;
        } else
        {
            return name.toUpperCase();
        }
    }

    /**
     * Setter for role name
     *
     * @param name the new role name (String) for this role, in uppercase
     */
    public void setName(String name)
    {
        this.name = name.toUpperCase();
    }

    /**
     * Getter for list of users for this role
     *
     * @return a list of User objects assigned to this role
     */
    public List<UserRoles> getUsers()
    {
        return users;
    }

    /**
     * Setter for list of users for this role
     *
     * @param users a new list of User objects to assign to this role
     */
    public void setUsers(List<UserRoles> users)
    {
        this.users = users;
    }
}
