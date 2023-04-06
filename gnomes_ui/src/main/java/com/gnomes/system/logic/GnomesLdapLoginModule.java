package com.gnomes.system.logic;


import java.security.acl.Group;
import java.util.Hashtable;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.LdapLoginModule;

import com.gnomes.system.constants.BLSecurityConstants;

/**
 * Gnomes Ldap Login Modue
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/05 YJP/H.Gojo                  初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesLdapLoginModule extends LdapLoginModule  {

    private String userName = "";

    /**

     * (required) The UsernamePasswordLoginModule modules compares the result of this

     * method with the actual password.

    */

    @Override
    protected String getUsersPassword() throws LoginException {

        System.out.format("GnomesLdapLoginModule: authenticating user '%s'%n", getUsername());

        // Lets pretend we got the password from somewhere and that it's, by a chance, same as the username

        String password = super.getUsersPassword();

        // Let's also pretend that we haven't got it in plain text but encrypted

        // (the encryption being very simple, namely capitalization)

        //password = password.toUpperCase();

        return password;

    }

    @Override
    protected String getUsername() {
        System.out.println("Search getUsername():");

        System.out.println(this.userName);
        if (getIdentity() != null) {
            // JAAS の処理として呼ばれた場合は通常通り処理を行う。
            return getIdentity().getName();
        }
        else {
            // LDAP 認証のみ個別に行う場合は、呼び元からユーザID を渡す。
            return this.userName;
        }
    }

    /**

     * (required) The groups of the user, there must be at least one group called

     * "Roles" (though it likely can be empty) containing the roles the user has.

     */

    @Override
    protected Group[] getRoleSets() throws LoginException {

        SimpleGroup group = new SimpleGroup("Roles");

        try {

            System.out.println("Search here group for user: "+super.getUsername());

            group.addMember(new SimplePrincipal(BLSecurityConstants.ROLE_GROUP_MEMBER));

            Group[] roleSets = { group };

            return roleSets;

        } catch (Exception e) {

            throw new LoginException("Failed to create group member for " + group);

        }

    }


    /**
     * LDAP 認証（ログイン以外の認証）
     * @param    ユーザID
     * @param    パスワード
     * @param    モジュールオプション
     * @return   認証結果
     */
    public boolean validateUser(String userId, String inputPassword, Hashtable<String, String> moduleOption) throws LoginException {

        this.userName = userId;

        Subject subject = null;
        CallbackHandler callbackHandler = null;

        Hashtable<String, String> sharedState = new Hashtable<String, String>();

        Hashtable<String, String> option = moduleOption;

        try {
            initialize(subject, callbackHandler,sharedState,option);
            return super.validatePassword(inputPassword, "");

        } catch (Exception e) {
            // LDAP 認証エラー時
            throw new LoginException("Failed to authenticate against an LDAP: " + e.getMessage() );

        }
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);
    }

}
