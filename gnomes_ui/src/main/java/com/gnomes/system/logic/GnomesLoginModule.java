package com.gnomes.system.logic;

import java.security.acl.Group;

import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.DatabaseServerLoginModule;

import com.gnomes.common.util.GnomesPasswordEncoder;

/**
 * Gnomes Login Modue
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/12/05 YJP/H.Gojo                初版
 *          2019/11/08 YJP/K.Tada                パスワード比較ロジックを変更
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesLoginModule extends DatabaseServerLoginModule {

	@Override
	protected String getUsersPassword() throws LoginException {
		System.out.format("GnomesLoginModule: authenticating user '%s'%n", getUsername());

		// standalone.xmlに設定されたSQLでパスワードを検索
		return super.getUsersPassword();
	}

	/**
	 * @param inputPassword 与えられたパスワード
	 * @param expectedPassword 期待されるパスワード
	 */
	@Override
	protected boolean validatePassword(String inputPassword, String expectedPassword) {
		if (inputPassword == null || expectedPassword == null) {
			return false;
		}

		//暗号パスワードが初めから入っている場合は暗号処理せずそのまま比較する
		if(inputPassword.equals(expectedPassword)) {
		    return true;
		}

		return GnomesPasswordEncoder.matches(inputPassword, expectedPassword);
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
		try {
			System.out.println("Search here group for user: " + getUsername());

			// standalone.xmlに設定されたSQLでロールを検索
			return super.getRoleSets();

		} catch (Exception e) {
			SimpleGroup group = new SimpleGroup("Roles");
			throw new LoginException("Failed to create group member for " + group);
		}
	}

}
