package com.gnomes.system.dao;


import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.resource.spi.MessagesHandler;
import com.gnomes.system.entity.MstrScreenButton;

/**
 * 画面ボタンマスタ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/07/13 KCC/A.Oomori               初版
 * R0.01.02 2018/12/12 YJP/S.Kohno                1件取得時に取得出来なければ例外するメソッドを追加。
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrScreenButtonDao extends BaseDao implements Serializable {

	/**
	 * テーブル名
	 */
    protected final String TABLE_NAME = "MstrScreenButton";
    /**
     * クエリ名
     */
    protected final String QUERY_NAME = "gnomesSystemModel";

    /**
     * コンストラクタ
     */
    public MstrScreenButtonDao() {
    }


    /**
     * 画面ボタンマスタ 取得
     * @return 画面ボタンマスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrScreenButton> getMstrScreenButton() throws GnomesAppException {

        List<MstrScreenButton> datas = gnomesSystemModel.getMstrScreenButtonList();

        return datas;
    }

    /**
     * 画面ボタンマスタ(1件) 取得
     * @param screenId 画面ID
     * @param buttonId ボタンID
     * @param mustGetFlag trueで取得0件はログを出力する
     * @return 画面ボタンマスタ(1件)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrScreenButton getMstrScreenButton(String screenId, String buttonId, Boolean mustGetFlag) throws GnomesAppException {

    	final String paramNames = "screenId,buttonId";

        if (StringUtil.isNullOrEmpty(screenId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(screenId));

        }

        if (StringUtil.isNullOrEmpty(buttonId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(buttonId));

        }

        List<MstrScreenButton> result = gnomesSystemModel.getMstrScreenButtonList().stream()
                .filter(item -> item.getScreen_id().equals(screenId) && item.getButton_id().equals(buttonId))
                .collect(Collectors.toList());


        if (result.isEmpty()) {
        	if (mustGetFlag == true){
        		String message = MessagesHandler.getString(GnomesMessagesConstants.ME01_0204, TABLE_NAME, QUERY_NAME,
                        paramNames, screenId + CommonConstants.COMMA + buttonId);
        		this.logHelper.info(this.logger, null, null, message);
        	}
            return null;
        }

        return result.get(0);
    }

    /**
     * 画面ボタンマスタ(1件) 取得
     * @param screenId 画面ID
     * @param buttonId ボタンID
     * @return 画面ボタンマスタ(1件)
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrScreenButton getMstrScreenButton(String screenId, String buttonId) throws GnomesAppException {
    	return this.getMstrScreenButton(screenId, buttonId, false);
    }

    /**
     * 画面ボタンマスタ 取得
     * @param screenId 画面ID
     * @return 画面ボタンマスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrScreenButton> getMstrScreenButton(String screenId) throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(screenId)) {
            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050,String.valueOf(screenId));

        }

        List<MstrScreenButton> result = gnomesSystemModel.getMstrScreenButtonList().stream()
                .filter(item -> item.getScreen_id().equals(screenId))
                .collect(Collectors.toList());

        return result;
    }
}
