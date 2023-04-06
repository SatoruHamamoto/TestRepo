package com.gnomes.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.DestroyFailedException;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.exception.GnomesExceptionFactory;
import com.gnomes.common.logging.LogHelper;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/02/04 16:22 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/

/**
 * キーストアユーティリティ
 * @author 03501213
 *
 */
/**
 * @author 03501213
 *
 */
@ApplicationScoped
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class KeyStoreUtilities
{
    //ロガー
    @Inject
    transient Logger            logger;

    /** ログヘルパー */
    @Inject
    protected LogHelper         logHelper;

    @Inject
    protected GnomesExceptionFactory      gnomesExceptionFactory;

    //@Inject
    //ContainerRequest            requestContext;

    /** キーストアプロパティのあるファイル名 */
    static final String         keyStorePropertyFileName = "gnomes-keystore";

    /** プロパティ名：キーストアキータイプ */
    private static final String KEY_TYPE                 = "keystore.type";
    /** プロパティ名：キーストアパスワード */
    private static final String KEY_PASS                 = "keystore.pass";
    /** プロパティ名：キーストアのファイルの有りか*/
    private static final String KEY_PATH                 = "keystore.path";

    private static final String className                = "KeyStoreUtilities";

    /** キーストアオブジェクト */
    private KeyStore            keyStore                 = null;

    /** キーストアパスオブジェクト */
    private Path                keyStorePath             = null;

    /** キーストアパスワード */
    private char[]              keyStorePassword         = null;

    /** キーストアタイプ */
    private String              keyStoreType             = null;

    /** キーストアファイルパス */
    private String              keyStoreFilePath         = null;

    /**
     * 初期処理、プロパティを読み込む
     */
    @PostConstruct
    public void postConstruct()
    {
        final String methodName = "postConstruct";
        try {
            // classPathにあるプロパティファイルをロードする
            ResourceBundle resourceBundle = ResourceBundle.getBundle(keyStorePropertyFileName);

            // プロパティから必要な定義を入出する
            keyStoreType = resourceBundle.getString(KEY_TYPE);
            keyStorePassword = resourceBundle.getString(KEY_PASS).toCharArray();
            keyStoreFilePath = resourceBundle.getString(KEY_PATH);

            //キーストアのファイル場所を使ってPathオブジェクトを取得
            keyStorePath = Paths.get(keyStoreFilePath);

            // KeyStoreから保存されている値（共通鍵）を取得
            keyStore = getKeyStore(keyStorePath, keyStoreType, keyStorePassword);
        }
        catch (Exception e) {
            // キーストアが準備できなくても起動初期処理は続行するのでスローしない
            // その代わりしっかりログをとる
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMessage());

            this.logHelper.severe(this.logger, className, methodName, sb.toString());

            // Server.logに出力する
            e.printStackTrace();
        }
        return;
    }

    /**
     * 終了処理
     */
    @PreDestroy
    public void preDestroy()
    {
    }

    /**
     * ウエイクアップ
     */
    public void Wakeup()
    {

    }
    /**
     * 登録されているエイリアスを使ってシークレット値を得る
     *
     * @param alias キーストアに登録しているパスワードのエイリアス
     * @return パスワード文字
     * @throws Exception
     */
    public String getSecretKeyValue(String alias) throws GnomesAppException
    {
        final String methodName = "getSecretKeyValue";
        KeyStore.SecretKeyEntry secretKeyEntry = null;
        SecretKey secretKey = null;

        //キーストアが取れているかどうかが重要
        if (Objects.isNull(this.keyStore)) {
            // ME01.00236:「キーストアがロードされていないためパスワード文字を取得できませんでした。」
            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0236,
                    keyStorePath.toAbsolutePath());
        }

        try {

            //キーストアからエントリを取得
            secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(
                    this.keyStorePassword));

            //シークレットキーを得る
            secretKey = secretKeyEntry.getSecretKey();

            //バイト配列でパスワード文字を得る
            byte[] result = secretKey.getEncoded();

            secretKey.destroy();
            secretKey = null;

            return new String(result, StandardCharsets.US_ASCII);
        }
        catch (Exception e) {
            if (!Objects.isNull(secretKey)) {
                try {
                    secretKey.destroy();
                }
                catch (DestroyFailedException e1) {
                   //キャッチしても下のエラー処理に継続する
                }
            }

            this.logHelper.severe(this.logger, className, methodName, MessagesHandler.getString(
                    GnomesMessagesConstants.ME01_0237,e.getMessage()));
            e.printStackTrace();

            // ME01.00237:「キーストアの取得ができませんでした。詳細はエラーログを確認ください。」
            throw this.gnomesExceptionFactory.createGnomesAppException(null, GnomesMessagesConstants.ME01_0237,
                    e.getMessage());
        }
    }

    /**
     *
     * キーストアを取得する
     *
     * @param keyStorePath  キーストアファイルが存在するパス
     * @param type          キーストアタイプ
     * @param password      キーストアパスワード
     * @return              キーストアオブジェクト
     * @throws Exception
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     */
    private KeyStore getKeyStore(Path keyStorePath, String type, char[] password) throws Exception
    {
        //キーストアを得る
        try (InputStream is = Files.newInputStream(keyStorePath, StandardOpenOption.READ)) {
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(is, password);
            return ks;
        }
        catch (Exception e) {
            //例外処理は親元に委任する
            throw e;
        }
    }
}
