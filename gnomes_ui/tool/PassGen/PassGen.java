

import com.gnomes.common.util.Crypto;

/**
 * パスワード暗号化
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/04/21 YJP/H.Gojo               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

/**
 * パスワード暗号化
 *   使用方法： PassGen パスワード [Enetr]
 */
public class PassGen {

    public static void main(String[] args) {
    	if(args.length < 1) {
    		System.out.println("Usage: PassGen 文字列 [Enter]");
    	}
    	else {
    		try {
        		System.out.println("Password(" + args[0] + "): " + Crypto.encrypt(args[0]));
    		}
    		catch (Exception ex) {
        		System.out.println("Encrypt Error " + ex.getMessage());
    		}
    	}
    }

}
