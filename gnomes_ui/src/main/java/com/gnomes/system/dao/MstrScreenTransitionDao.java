package com.gnomes.system.dao;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.picketbox.util.StringUtil;

import com.gnomes.common.dao.BaseDao;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.interceptor.ErrorHandling;
import com.gnomes.common.interceptor.TraceMonitor;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.system.entity.MstrScreenTransition;

/**
 * 画面遷移情報マスタ Dao
 */
/*
 * ========================== MODIFICATION HISTORY ==========================
 * Release Date ID/Name Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/01 YJP/K.Fujiwara 初版 [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MstrScreenTransitionDao extends BaseDao implements Serializable {

    /**
     * コンストラクタ.
     */
    public MstrScreenTransitionDao() {

    }

    /**
     * 画面遷移情報マスタリストリスト取得
     *
     * @return List<MstrSystemConstant>
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public List<MstrScreenTransition> getMstrScreenTransition() throws GnomesAppException {

        return gnomesSystemModel.getMstrScreenTransitionList();

    }

    /**
     * 画面遷移情報マスタ取得.
     *
     * <pre>
     * 画面遷移情報マスタの取得を行う。
     * </pre>
     *
     * @return 画面遷移情報マスタ
     * @throws GnomesAppException
     */
    @TraceMonitor
    @ErrorHandling
    public MstrScreenTransition getMstrScreenTransition(String screenId)
            throws GnomesAppException {

        if (StringUtil.isNullOrEmpty(screenId)) {

            StringBuilder params = new StringBuilder();
            params.append(MstrScreenTransition.COLUMN_NAME_SCREEN_ID);
            params.append("：").append(screenId);

            // ME01.0050:「パラメータが不正です。({0})」
            throw exceptionFactory.createGnomesAppException(
                    null, GnomesMessagesConstants.ME01_0050, params.toString());

        }

        MstrScreenTransition data = null;

        List<MstrScreenTransition> result = getMstrScreenTransition().stream()
                .filter(item -> item.getScreen_id().equals(screenId))
                .collect(Collectors.toList());

        if (result.size() > 0) {
            data = result.get(0);
        }

        return data;
    }
}
