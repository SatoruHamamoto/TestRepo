package com.gnomes.common.exportdata;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * CSV 共通処理クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/25 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class CsvCommon {

    @Inject
    MstrSystemDefineDao mstrSystemDefineDao;

    /**
     * システム定義マスタのCSVファイル文字コード
     */
    String sysCharsetName;

    /**
     * システム定義マスタのCSVファイル区切り文字
     */
    char sysSeparator;

    /**
     * システム定義マスタのCSVファイル囲い文字を取得
     */
    char sysQuotechar;

    /**
     * システム定義マスタのCSVファイル改行コード
     */
    String sysEndLine;

    /**
     * システム定義マスタのCSVファイル文字コードを取得
     * @return sysCharsetName システム定義マスタのCSVファイル文字コード
     * @throws GnomesAppException
     */
    public String getSysCharsetName() throws GnomesAppException {

        if (sysCharsetName == null) {
            MstrSystemDefine item = mstrSystemDefineDao
                    .getMstrSystemDefine(
                            SystemDefConstants.IMPORT_EXPORT,
                            SystemDefConstants.IMPORT_EXPORT_CSV_CHARSET_NAME);

            sysCharsetName = item.getChar1();
        }

        return sysCharsetName;
    }

    /**
     * システム定義マスタのCSVファイル区切り文字を取得
     * @return sysSeparator システム定義マスタのCSVファイル区切り文字
     * @throws GnomesAppException
     */
    public char getSysSeparator() throws GnomesAppException {

        if (sysSeparator == 0) {
            MstrSystemDefine item = mstrSystemDefineDao
                    .getMstrSystemDefine(
                            SystemDefConstants.IMPORT_EXPORT,
                            SystemDefConstants.IMPORT_EXPORT_CSV_SEPARATOR);

            sysSeparator = stripslashes(item.getChar1()).charAt(0);
        }

        return sysSeparator;
    }

    /**
     * システム定義マスタのCSVファイル囲い文字を取得
     * @return sysQuotechar システム定義マスタのCSVファイル囲い文字
     * @throws GnomesAppException
     */
    public char getSysQuotechar() throws GnomesAppException {

        if (sysQuotechar == 0) {
            MstrSystemDefine item = mstrSystemDefineDao
                    .getMstrSystemDefine(
                            SystemDefConstants.IMPORT_EXPORT,
                            SystemDefConstants.IMPORT_EXPORT_CSV_QUOTECHAR);

            sysQuotechar = stripslashes(item.getChar1()).charAt(0);
        }

        return sysQuotechar;
    }

    /**
     * システム定義マスタのCSVファイル改行コードを取得
     * @return sysEndLine システム定義マスタのCSVファイル改行コード
     * @throws GnomesAppException
     */
    public String getSysEndLine() throws GnomesAppException {

        if (sysEndLine == null) {
            MstrSystemDefine item = mstrSystemDefineDao
                    .getMstrSystemDefine(
                            SystemDefConstants.IMPORT_EXPORT,
                            SystemDefConstants.IMPORT_EXPORT_CSV_END_LINE);

            sysEndLine = stripslashes(item.getChar1());
        }

        return sysEndLine;
    }

    /**
     * エスケープされた文字を戻す
     * @param str エスケープされた文字
     * @return 戻した文字
     */
    private String stripslashes(String str) {
        return str.replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r")
                .replaceAll("\\\\t", "\t").replaceAll("\\\\\\\\", "\\\\");
    }
}
