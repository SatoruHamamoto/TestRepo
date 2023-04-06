package com.gnomes.common.model;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.uiservice.ContainerResponse;

/**
 * ページングモデル 基底クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/06/27 YJP/K.fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public abstract class PagingBaseModel extends BaseModel {

    @Inject
    protected
    ContainerResponse responseContext;

    /** ページコマンド */
    public enum PagingCommand {
        PagingCommand_Defualt("0"),                 // デフォルト（機能処理なし）
        PagingCommand_ToTop("1"),                   // 先頭ページを表示
        PagingCommand_Prev("2"),                    // 前ページを表示
        PagingCommand_Next("3"),                    // 次ページを表示
        PagingCommand_ToLast("4"),                  // 最終ページを表示
        PagingCommand_ChangeOnePageMaxOunt("5"),    // １ページ表示件数変更
        PagingCommand_ChangeNowPage("6");           // 現在ページ変更

        private String value;

        private PagingCommand(String n) {
            this.value = n;
        }

        public String getValue() {
            return this.value;
        }

        public static PagingCommand getEnum(String num) {
            // enum型全てを取得します。
            PagingCommand[] enumArray = PagingCommand.values();

            // 取得出来たenum型分ループします。
            for(PagingCommand enumInt : enumArray) {
                // 引数intとenum型のvalueを比較します。
                if (enumInt.getValue().compareTo(num) == 0){
                    return enumInt;
                }
            }
            return null;
        }
    }



    /**
     * ページングモデルリスナー
     *
     */
    public interface IPagingBaseModelListener<T> {

        /** 件数取得 */
        public int getDataCount() throws Exception;

        /** データ取得 */
        public List<T> getData() throws Exception;

    }

    /**
     * ページングデータ取得
     * @param pagingCommand ページングコマンド
     * @param pagingParam ページパラメータ
     * @param searchSetting 検索条件
     * @param listener ページングモデルリスナー
     * @return 取得データ
     * @throws Exception 例外
     */
    protected <T> List<T> getPagingBaseData(
            PagingCommand pagingCommand,
            Integer pagingParam,
            SearchSetting searchSetting,
            IPagingBaseModelListener<T> listener) throws Exception {

        /** 件数取得 */
        int allDataCount = listener.getDataCount();
        searchSetting.setAllDataCount(allDataCount);

        switch(pagingCommand) {
            /** 先頭ページへ */
            case PagingCommand_ToTop:
                searchSetting.setNowPage(1);
                break;

            /** 前ページ */
            case PagingCommand_Prev:
                {
                    int nowPage = searchSetting.getNowPage();
                    if (nowPage > 1) {
                        nowPage --;
                    }
                    searchSetting.setNowPage(nowPage);
                    break;
                }

            /** 次ページ */
            case PagingCommand_Next:
                {
                    int nowPage = searchSetting.getNowPage();
                    nowPage ++;
                    searchSetting.setNowPage(nowPage);
                    break;
                }

            /** 最終ページへ */
            case PagingCommand_ToLast:
                {
                    int lastPage = searchSetting.getAllDataCount() / searchSetting.getOnePageDispCount();
                    if ((searchSetting.getAllDataCount() % searchSetting.getOnePageDispCount()) != 0) {
                        lastPage ++;
                    }
                    searchSetting.setNowPage(lastPage);
                    break;
                }

            /** 現在ページ変更 */
            case PagingCommand_ChangeNowPage:
                {
                    searchSetting.setNowPage(pagingParam);
                    break;
                }

            /** １ページ表示件数変更 */
            case PagingCommand_ChangeOnePageMaxOunt:
                {
                	if (pagingParam > searchSetting.getMaxDispCount()) {
                	    pagingParam = searchSetting.getMaxDispCount();

                        // MG01.0014：1ページ表示件数は最大表示件数({0})以内で入力してください。」
                        responseContext.setMessageInfo(GnomesMessagesConstants.MG01_0014,searchSetting.getMaxDispCount());

                	}

                    searchSetting.setOnePageDispCount(pagingParam);
                    searchSetting.setNowPage(1);
                    break;
                }
            default:
                {
                    break;
                }
        }
        if (allDataCount == 0) {
            searchSetting.setNowPage(1);
        } else {
            // 現ページチェックと調整
            int nowPage = searchSetting.getNowPage();
            int lastPage = 1;
            if (searchSetting.getAllDataCount() > 0) {
                // 最終ページ
                lastPage = searchSetting.getAllDataCount() / searchSetting.getOnePageDispCount();
                if ((searchSetting.getAllDataCount() % searchSetting.getOnePageDispCount()) != 0) {
                    lastPage ++;
                }
            }
            if (nowPage > lastPage) {
                nowPage = lastPage;
            }
            searchSetting.setNowPage(nowPage);
        }

        List<T> result = listener.getData();
        return result;
    }



}
