package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
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
import com.gnomes.system.entity.MstrMessageGroup;

/**
 * メッセージグループ Dao
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/14 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrMessageGroupDao extends BaseDao implements Serializable {

    /** コンストラクタ */
    public MstrMessageGroupDao() {

    }

    /**
     * メッセージグループ取得
     * @return メッセージグループ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrMessageGroup> getMstrMessageGroup() throws GnomesAppException {

        List<MstrMessageGroup> datas = gnomesSystemModel.getMstrMessageGroupList();

        return datas;
    }

    /**
     * メッセージグループリスト取得
     * @param sendMailGroupid メール送信先グループID
     * @return メッセージグループリスト取得
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrMessageGroup> getMstrMessageGroupList(String sendMailGroupid) throws GnomesAppException {

        // パラメータチェック
        if (StringUtil.isNullOrEmpty(sendMailGroupid)) {

            StringBuilder params = new StringBuilder();
            params.append(MstrMessageGroup.COLUMN_NAME_SEND_MAIL_GROUP_ID);
            params.append(CommonConstants.COLON).append(sendMailGroupid);

            // ME01.0050:「パラメータが不正です。({0})」
            throw super.exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());
        }

        List<MstrMessageGroup> result = super.gnomesSystemModel.getMstrMessageGroupList().stream()
                .filter(item -> item.getSend_mail_group_id().equals(sendMailGroupid))
                .collect(Collectors.toList());

        Collections.sort(result, Comparator.comparing(MstrMessageGroup::getMessage_group_key));

        return result;

    }

}
