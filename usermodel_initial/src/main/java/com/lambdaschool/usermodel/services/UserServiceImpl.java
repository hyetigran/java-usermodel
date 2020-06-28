package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements UserService Interface
 */
@Transactional
@Service(value = "userService")
public class UserServiceImpl implements UserService
{
    /**
     * Connects this service to the User table.
     */
    @Autowired
    private UserRepository userrepos;

    /**
     * Connects this service to the Role table
     */
    @Autowired
    private RoleService roleService;


    @Autowired
    private UserAuditing userAuditing;

    public User findUserById(long id) throws EntityNotFoundException
    {
        return userrepos.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User id " + id + " not found!"));
    }

    @Override
    public List<User> findByNameContaining(String username)
    {
        return userrepos.findByUsernameContainingIgnoreCase(username.toLowerCase());
    }

    @Override
    public List<User> findAll()
    {
        List<User> list = new ArrayList<>();
        /*
         * findAll returns an iterator set.
         * iterate over the iterator set and add each element to an array list.
         */
        userrepos.findAll()
            .iterator()
            .forEachRemaining(list::add);
        return list;
    }

    @Transactional
    @Override
    public void delete(long id)
    {
        userrepos.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User id " + id + " not found!"));
        userrepos.deleteById(id);
    }

    @Override
    public User findByName(String name)
    {
        User uu = userrepos.findByUsername(name.toLowerCase());
        if (uu == null)
        {
            throw new EntityNotFoundException("User name " + name + " not found!");
        }
        return uu;
    }

    @Transactional
    @Override
    public User save(User user)
    {
        User newUser = new User();

        if (user.getUserid() != 0)
        {
            User oldUser = userrepos.findById(user.getUserid())
                .orElseThrow(() -> new EntityNotFoundException("User id " + user.getUserid() + " not found!"));
            // delete roles for the old user

            for(UserRoles ur : oldUser.getRoles()){
                deleteUserRole(ur.getUser().getUserid(), ur.getRole().getRoleid());
            }
            newUser.setUserid(user.getUserid());
        }

        newUser.setUsername(user.getUsername()
            .toLowerCase());
        newUser.setPassword(user.getPassword());
        newUser.setPrimaryemail(user.getPrimaryemail()
            .toLowerCase());

        newUser.getRoles()
            .clear();
        if(user.getUserid() == 0) {
            for (UserRoles r : user.getRoles())
            {
                Role newRole = roleService.findRoleById(r.getRole().getRoleid());

                newUser.addRole(newRole);
            }
        } else {
            for (UserRoles ur : user.getRoles()){
                addUserRole(ur.getUser().getUserid(), ur.getRole().getRoleid());
            }
        }


        newUser.getUseremails()
            .clear();
        for (Useremail ue : user.getUseremails())
        {
            newUser.getUseremails()
                .add(new Useremail(newUser,
                    ue.getUseremail()));
        }

        return userrepos.save(newUser);
    }

    @Transactional
    @Override
    public User update(
        User user,
        long id)
    {
        User currentUser = findUserById(id);

        if (user.getUsername() != null)
        {
            currentUser.setUsername(user.getUsername()
                .toLowerCase());
        }

        if (user.getPassword() != null)
        {
            currentUser.setPassword(user.getPassword());
        }

        if (user.getPrimaryemail() != null)
        {
            currentUser.setPrimaryemail(user.getPrimaryemail()
                .toLowerCase());
        }

        if (user.getRoles()
            .size() > 0)
        {
            currentUser.getRoles()
                .clear();
            for (UserRoles ur : currentUser.getRoles())
            {
                deleteUserRole(ur.getUser().getUserid(), ur.getRole().getRoleid());;
            }
            for (UserRoles ur : user.getRoles())
            {
                addUserRole(currentUser.getUserid(), ur.getRole().getRoleid());;
            }
        }

        if (user.getUseremails()
            .size() > 0)
        {
            currentUser.getUseremails()
                .clear();
            for (Useremail ue : user.getUseremails())
            {
                currentUser.getUseremails()
                    .add(new Useremail(currentUser,
                        ue.getUseremail()));
            }
        }

        return userrepos.save(currentUser);
    }

    @Transactional
    @Override
    public void deleteUserRole(long userid, long roleid){
        userrepos.findById(userid).orElseThrow(() -> new EntityNotFoundException("User id " + userid + " Not Found"));

        roleService.findRoleById(roleid);

        if(userrepos.checkUserRolesCombo(userid, roleid).getCount() > 0){
            userrepos.deleteUserRoles(userid, roleid);
        } else {
            throw new EntityNotFoundException("Role and User Combination Not Found");
        }
    }

    @Transactional
    @Override
    public void addUserRole(long userid, long roleid){
        userrepos.findById(userid).orElseThrow(() -> new EntityNotFoundException("User id " + userid + " Not Found"));

        roleService.findRoleById(roleid);

        if(userrepos.checkUserRolesCombo(userid, roleid).getCount() <= 0) {
            userrepos.insertUserRoles(userAuditing.getCurrentAuditor().get(), userid, roleid);
        } else {
            throw new EntityNotFoundException("Role and User Combination Not Found");
        }

    }
}
