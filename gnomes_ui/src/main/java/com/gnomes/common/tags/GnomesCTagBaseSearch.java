package com.gnomes.common.tags;

import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.time.DateUtils;
import org.picketbox.util.StringUtil;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.resource.GnomesResourcesConstants;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.search.ISearchInfoFormBean;
import com.gnomes.common.search.SearchInfoController;
import com.gnomes.common.search.SearchInfoController.ConditionDateType;
import com.gnomes.common.search.SearchInfoController.ConditionParamSaveType;
import com.gnomes.common.search.SearchInfoController.ConditionRequiredType;
import com.gnomes.common.search.SearchInfoController.ConditionType;
import com.gnomes.common.search.data.ConditionInfo;
import com.gnomes.common.search.data.MstCondition;
import com.gnomes.common.search.data.MstSearchInfo;
import com.gnomes.common.search.data.SearchInfoPack;
import com.gnomes.common.search.data.SearchSetting;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * 検索を扱うカスタムタグの基底
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/1/18 YJP/A.Oomori               初版
 * R0.01.02 2019/05/27 YJP/S.Hamamoto            検索条件・ソート条件のindexを廃止
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public abstract class GnomesCTagBaseSearch extends GnomesCTagBase {

    // 属性マップのキー：非活性
    protected static final String MAP_KEY_DISABLE = "disabled";

    // 属性マップのキー：onclick
    protected static final String MAP_KEY_ONCLICK = "onclick";

    // 検索条件必須項目の詳細検索削除ボタン表示無し
    protected static final String HIDE_DELETE_BUTTON = "hide";

    // 検索項目表示順設定無し
    protected static final int DISPLAY_ORDER_NO = 0;

    @Inject
    SearchInfoController searchInfoController;

    @Inject
    GnomesCTagDictionary gnomesCTagDictionary;

    /** システム定義 Dao */
    @Inject
    protected MstrSystemDefineDao mstrSystemDefineDao;

    /**
     * 検索情報を取得
     * @param bean ビーン
     * @return 検索情報
     * @throws Exception 例外
     */
    protected SearchInfoPack getSearchInfoPack(Object bean) throws Exception {

        SearchInfoPack searchInfoPack = null;
        Class<?> clazz = bean.getClass();
        Class<?> intrfc = ISearchInfoFormBean.class;

        // インターフェースを実装したクラスであるかどうかをチェック
        if (!clazz.isInterface() && intrfc.isAssignableFrom(clazz)
                && !Modifier.isAbstract(clazz.getModifiers())) {

            ISearchInfoFormBean tableBean = null;
            tableBean = (ISearchInfoFormBean) bean;

            String jsonSearchMasterInfo = tableBean.getJsonSearchMasterInfo();
            String jsonSearchInfo = tableBean.getJsonSearchInfo();

            // 共通検索ダイアログを使用しないでページングを行うことがあるのでNULL判定
            if (jsonSearchMasterInfo != null) {
                searchInfoPack = searchInfoController.getSearchInfoPack(
                        jsonSearchMasterInfo, jsonSearchInfo);
            }
        }

        return searchInfoPack;
    }

    /**
     * 検索メニュー項目表示出力
     * @param out 出力先
     * @param searchTableTagId 検索対象テーブル
     * @param searchMasterInfo 検索マスター
     * @param searchInfo 検索情報
     * @param isPanecon 工程端末表示
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    public void outSearchMenuItem(JspWriter out,
            String searchTableTagId,
            MstSearchInfo searchMasterInfo,
            SearchSetting searchInfo,
            boolean isPanecon,
            Locale userLocale) throws Exception {

        List<ConditionInfo> conditionInfos = searchInfo.getConditionInfos();

        // テキスト入力補助ダイアログ呼出クラス名
        String textDialogClass = "";

        // 数値入力補助ダイアログ呼出クラス名
        String numberDialogClass = "";

        // プルダウン選択一覧ダイアログ呼出Onclick
        String pulldownDialogOnclick = "";

        // 日付入力補助ダイアログ呼出クラス名
        String dateDialogClass = "datetime";

        // チェックボックスクラス名
        String checkboxClass = "";

        String checkboxStyle = "style=\"width: 1em;\"";

        if (isPanecon) {
            textDialogClass = "common-keyboard-input-char";
            numberDialogClass = "common-keyboard-input-num";
            checkboxClass = "common-input-checkbox";
            checkboxStyle = "style=\"width: 3em;\"";

        }

        out.print("    <ul>\n");

        for (ConditionInfo conditionInfo: conditionInfos) {
          //検索条件項目は表示場合、
          if(!conditionInfo.isHiddenItem()) {
            //検索条件リストからカラムIDを得る
            String columnId = conditionInfo.getColumnId();

            //同じカラムIDの検索条件マスターを得る
            MstCondition mstCondition = searchMasterInfo.getMstCondition(conditionInfo.getColumnId());

            // カラム名
            String columnName = searchTableTagId + "_" + columnId;

            // 表示カラム名
            String text = this.getStringEscapeHtml(mstCondition.getText());

            // タイプ
            ConditionType type = mstCondition.getType();

            // 保存タイプ
            List<ConditionParamSaveType> saveTypes = mstCondition.getSaveParamTypes();

            String dateFrom ="";
            String dateTo ="";

            // 選択肢
            LinkedHashMap<String, String> patterns = mstCondition.getPatterns();

            List<String> parametars = conditionInfo.getParameters();

            if (parametars.size() == 0) {
                parametars.add("");
            }
            if (parametars.size() <= 1) {
                parametars.add("");
            }

            List<String> patternKeys = conditionInfo.getPatternKeys();

            out.print("<li>");
            // 部品の出力
            // 検索条件出力タイプ
            switch (type) {

                // 文字入力＋プルダウン入力(含む、から始まるなど）
                case STRING_LIKE:

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, ResourcesHandler.getString(GnomesResourcesConstants.YY01_0085, userLocale));

                    out.print(text + "\n");
                    out.print("  <input type=\"text\" name=\""+ columnName + "_value\" class=\"" + textDialogClass + "\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                    out.print("      <select name=\""+ columnName + "_condition\" " + " class=\"search_condition_display_none\" " + pulldownDialogOnclick + ">\n");

                    for (int j = 0; j < patterns.size(); j++) {
                        String strIndex = this.getStringEscapeHtmlValue(getStringNumber(j, false, null));
                        if(patternKeys.size() > 0 && patternKeys.get(0).equals(strIndex)){
                            out.print("              <option value=\"" + strIndex + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(patterns.get(strIndex)) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + strIndex + "\">"+ this.getStringEscapeHtmlValue(patterns.get(strIndex)) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");

                    break;
                // プルダウン入力（コード等定数）
                case STRING_PULLDOWN:

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text);

                    out.print(text + "\n");
                    out.print("  <select name=\""+ columnName + "_value\" " + pulldownDialogOnclick + ">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }
                    out.print("  </select>\n");

                    break;
                // プルダウン入力（オートコンプリート有り）
                case STRING_PULLDOWN_AUTOCOMPLETE:
                case STRING_PULLDOWN_AUTOCOMPLETE_1:
                case STRING_PULLDOWN_AUTOCOMPLETE_2:
                {
                    String mode = AUTO_MODE_VALUE_TEXT;
                    if (type == ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_1) {
                        mode = AUTO_MODE_VALUE;
                    }
                    else if (type == ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_2) {
                        mode = AUTO_MODE_TEXT;
                    }

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text);

                    out.print(text + "\n");
                    out.print("  <select name=\""+ columnName + "_value\" class=\"" + CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\" " + pulldownDialogOnclick + ">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }
                    out.print("  </select>\n");
                }
                    break;
                // プルダウン入力(数値）
                case NUMBER_PULLDOWN:

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text);

                    out.print(text + "\n");
                    out.print("  <select name=\""+ columnName + "_value\" " + pulldownDialogOnclick + ">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");

                    break;
                // プルダウン入力(数値）（オートコンプリート有り）
                case NUMBER_PULLDOWN_AUTOCOMPLETE:
                case NUMBER_PULLDOWN_AUTOCOMPLETE_1:
                case NUMBER_PULLDOWN_AUTOCOMPLETE_2:
                {
                    String mode = AUTO_MODE_VALUE_TEXT;
                    if (type == ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_1) {
                        mode = AUTO_MODE_VALUE;
                    }
                    else if (type == ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_2) {
                        mode = AUTO_MODE_TEXT;
                    }


                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text);

                    out.print(text + "\n");
                    out.print("  <select name=\""+ columnName + "_value\" class=\"width-100 " + CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\" " + pulldownDialogOnclick + ">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");
                }
                    break;
                // 数値入力(範囲)
                case NUMBER:
                    out.print(text + "&nbsp;From<br>\n");
                    out.print("  <input type=\"text\" name=\""+ columnName +"_from\"  class=\"common-text-number gnomes-number gnomes-number-format " + numberDialogClass + "\" value=\"" + this.getStringEscapeHtmlValue(getStringNumber(searchTableTagId, text, parametars.get(0), null)) + "\">\n");
                    out.print("</li>\n");
                    out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp;<br>\n");
                    out.print("  <input type=\"text\" name=\""+ columnName +"_to\" class=\"common-text-number gnomes-number gnomes-number-format " + numberDialogClass + "\" value=\"" + this.getStringEscapeHtmlValue(getStringNumber(searchTableTagId, text, parametars.get(1), null)) + "\">\n");


                    break;

               // from (数値 プルダウン) to (数値 プルダウン)
               case NUMBER_PULLDOWN_RANGE:

                   // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                   pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text + "&nbsp;From");

                   out.print(text + "&nbsp;From<br>\n");
                   out.print("  <select name=\""+ columnName + "_from\" " + pulldownDialogOnclick + ">\n");
                   out.print("    <option value=\"\"></option>\n");

                   for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                       if (parametars.get(0).equals(pattern.getKey())) {
                           out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                       }
                       else {
                           out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                       }
                   }

                   out.print("      </select>\n");
                   out.print("</li>\n");

                   // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                   pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text + "&nbsp;To");

                   out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp;<br>\n");
                   out.print("  <select name=\""+ columnName + "_to\" " + pulldownDialogOnclick + ">\n");
                   out.print("    <option value=\"\"></option>\n");

                   for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                       if (parametars.get(1).equals(pattern.getKey())) {
                           out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                       }
                       else {
                           out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                       }
                   }
                   out.print("      </select>\n");
                   break;
               // from (数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り）
               case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE:
               case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1:
               case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2:
               {
                   String mode = AUTO_MODE_VALUE_TEXT;
                   if (type == ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1) {
                       mode = AUTO_MODE_VALUE;
                   }
                   else if (type == ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2) {
                       mode = AUTO_MODE_TEXT;
                   }

                   // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                   pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text + "&nbsp;From");

                   out.print(text + "&nbsp;From<br>\n");
                   out.print("  <select name=\""+ columnName + "_from\" class=\"width-100 " + CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\" " + pulldownDialogOnclick + ">\n");
                   out.print("    <option value=\"\"></option>\n");

                   for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                       if (parametars.get(0).equals(pattern.getKey())) {
                           out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                       }
                       else {
                           out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                       }
                   }

                   out.print("      </select>\n");
                   out.print("</li>\n");

                   // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                   pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text + "&nbsp;To");

                   out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp;<br>\n");
                   out.print("  <select name=\""+ columnName + "_to\" class=\"width-100 " + CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\" " + pulldownDialogOnclick + ">\n");
                   out.print("    <option value=\"\"></option>\n");

                   for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                       if (parametars.get(1).equals(pattern.getKey())) {
                           out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                       }
                       else {
                           out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                       }
                   }
                   out.print("      </select>\n");
               }
                   break;
                // 日付(範囲)(年月日
                case DATE:
                case MULTIFORMAT_DATESTR:

                    dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                    dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                    out.print(text + "&nbsp;From "+ dateFrom +"<br>\n");
                    out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0073, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");

                    out.print("</li>\n");
                    out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"<br>\n");
                    out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0073, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");


                    break;
                // 日付(範囲)(年月
                case DATE_YM:

                    dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                    dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                    out.print(text + "&nbsp;From "+ dateFrom +"<br>\n");
                    out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0095, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");

                    out.print("</li>\n");
                    out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"<br>\n");
                    out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0095, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");


                    break;
                // 日時 (範囲)(年月日時間（分まで
                case DATE_TIME:

                    dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                    dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                    out.print(text + "&nbsp;From "+ dateFrom +"<br>\n");
                    out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0045, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");

                    out.print("</li>\n");
                    out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"<br>\n");
                    out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0045, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");

                    break;
                // 日時 (年月日時間（秒まで
                case DATE_TIME_SS:
                        dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                        dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                        out.print(text + "&nbsp;From "+ dateFrom +"<br>\n");
                        out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0096, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");

                        out.print("</li>\n");
                        out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"<br>\n");
                        out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0096, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");

                        break;
                // 日時 (年月日時間（分まで,分は00固定
                case DATE_TIME_MM00:
                	
                	dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));
                	
                	dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));
                	
                	out.print(text + "&nbsp;From "+ dateFrom +"<br>\n");
                	out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0111, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                	
                	out.print("</li>\n");
                	out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"<br>\n");
                	out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0111, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");
                	
                	break;
                // 日時 (年月日時間（秒まで,秒は00固定
                case DATE_TIME_SS00:
                    dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                    dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                    out.print(text + "&nbsp;From "+ dateFrom +"<br>\n");
                    out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0112, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");

                    out.print("</li>\n");
                    out.print("<li>" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"<br>\n");
                    out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0112, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");

                    break;
                // チェックボックス
                case CHECKBOX:

                    out.print(text + "<br>\n");
                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.contains(pattern.getKey())) {
                            out.print("    <label style=\"vertical-align: middle;\"><input name=\"" + columnName + "_check\" class=\"" + checkboxClass + "\" "+ checkboxStyle +" type=\"checkbox\" value=\"" +  this.getStringEscapeHtmlValue(pattern.getKey()) + "\" checked><span>&nbsp;" +  this.getStringEscapeHtmlValue(pattern.getValue()) + "</span></label><br>\n");
                        }
                        else {
                            out.print("    <label style=\"vertical-align: middle;\"><input name=\"" + columnName + "_check\" class=\"" + checkboxClass + "\" "+ checkboxStyle +" type=\"checkbox\" value=\"" +  this.getStringEscapeHtmlValue(pattern.getKey()) + "\"><span>&nbsp;" +  this.getStringEscapeHtmlValue(pattern.getValue()) + "</span></label><br>\n");
                        }
                    }

                    break;
                default:
                    // 何もしない。
                    break;
            }
            out.print("</li>\n");
        }
        }
        out.print("    </ul>\n");
    }


    /**
     * 検索ダイアログ項目表示出力
     * @param out 出力先
     * @param searchTableId 検索対象テーブルのタグID
     * @param searchTableTagId フィルター部タグID
     * @param searchMasterInfo 検索マスター
     * @param searchInfo 検索情報
     * @param isPanecon 工程端末表示
     * @param userLocale ユーザーロケール
     * @throws Exception 例外
     */
    public void outSearchDialogItem(JspWriter out,
            String searchTableId,
            String searchTableTagId,
            MstSearchInfo searchMasterInfo,
            SearchSetting searchInfo,
            boolean isPanecon,
            Locale userLocale) throws Exception {

        List<MstCondition> mstConditions = searchMasterInfo.getMstConditions();

        List<ConditionInfo> conditionInfos = searchInfo.getConditionInfos();

        // テキスト入力補助ダイアログ呼出クラス名
        String textDialogClass = "";

        // 数値入力補助ダイアログ呼出クラス名
        String numberDialogClass = "";

        // プルダウン選択一覧ダイアログ呼出Onclick
        String pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, ResourcesHandler.getString(GnomesResourcesConstants.YY01_0032, userLocale));

        // 日付入力補助ダイアログ呼出クラス名
        String dateDialogClass = "datetime";

        // チェックボックスクラス名
        String checkboxClass = "common-dialog-col-checkbox";

        // チェックボックススタイル
        String checkboxStyle = "style=\"width: 1.0em; float: left;\"";

        // ダイアログボックスクラス
        String dialogBoxClass = "search-dialog-box-size";
        if (isPanecon) {
            textDialogClass = "common-keyboard-input-char";
            numberDialogClass = "common-keyboard-input-num";
            checkboxClass = "search-dialog-col-checkbox";
            dialogBoxClass = "panecon-searchDialog-box-size ";
            checkboxStyle = "style=\"width: 1.5em; float: left;\"";

        }

        // tabindex取得
        String tabindex = "";
        if(isPanecon){
            tabindex = "-1";
        }

        // フィルタ追加部
        String titleAddFilter = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.DI01_0081, userLocale));
        // フィルタ追加ボタンラベル
        String titleAddButtonLabel = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0032, userLocale));
        // フィルタ削除ボタンラベル
        String titleRemoveButtonLabel = this.getStringEscapeHtml(ResourcesHandler.getString(GnomesResourcesConstants.YY01_0102, userLocale));
        out.print("        <div class=\"common-dialog-col-title\">" + titleAddFilter + "</div>\n");
        out.print("        <div class=\"search-dialog-addFilter\">\n");
        out.print("          <select name=\"" + searchTableTagId + "_filterSelect\" class=\"modal-focus\" " + pulldownDialogOnclick + ">\n");
        out.print("            <option></option>\n");
        for (int k = 0; k < mstConditions.size(); k++) {
            out.print("            <option value=\"" + mstConditions.get(k).getColumnName() + "\">"+ this.getStringEscapeHtml(mstConditions.get(k).getText()) + "</option>\n");
        }
        out.print("          </select>\n");

        out.print("        </div>\n");
        out.print("        <a href=\"#\" name=\"searchAddButton\" id=\"searchAddButton\" onclick=\"addFilter('" + searchTableId + "', '" + searchTableTagId + "', " + String.valueOf(isPanecon) + ", ''); return false;\" tabindex=\"" + tabindex + "\">\n");
        out.print("          <span class=\"common-button\">"+ titleAddButtonLabel +"</span>\n");
        out.print("        </a>\n");
        out.print("        <input type=\"hidden\" id=\"search-dialog-filter-remove-button-label\" value=\"" + titleRemoveButtonLabel + "\">\n");
        out.print("        <div class=\"search-dialog-separation\"></div>\n");

        out.print("        <div class=\"search-dialog-height\">\n");
        out.print("  <div class=\"common-flexMenu-search-box "+ dialogBoxClass +" common-data-area-wrapper-t\">\n");
        out.print("    <div class=\"common-data-area-t\">\n");
        out.print("    <div id=\"" + searchTableTagId + "\">\n");
        //フィルタ部
        for (ConditionInfo conditionInfo: conditionInfos) {
          //検索条件項目は表示場合、
          if(!conditionInfo.isHiddenItem()) {

            String columnId = conditionInfo.getColumnId();
            // カラム名
            String columnName = searchTableTagId + "_" + columnId;

            MstCondition mstCondition = searchMasterInfo.getMstCondition(columnId);

            // 表示カラム名
            String text = this.getStringEscapeHtml(mstCondition.getText());

            // タイプ
            ConditionType type = mstCondition.getType();
            String typeLabel = this.getStringEscapeHtml(getStringNumber(type.getValue(), false, null));

            // 保存タイプ
            List<ConditionParamSaveType> saveTypes = mstCondition.getSaveParamTypes();

            String dateFrom ="";
            String dateTo ="";

            // 選択肢
            LinkedHashMap<String, String> patterns = mstCondition.getPatterns();

            List<String> parametars = conditionInfo.getParameters();

            if (parametars.size() == 0) {
                parametars.add("");
            }
            if (parametars.size() <= 1) {
                parametars.add("");
            }

            List<String> patternKeys = conditionInfo.getPatternKeys();
            out.print("          <div id=\""+ columnName + "\" class=\"tr\" columnId=\"" + columnId + "\">\n");
            out.print("            <input type=\"hidden\" name=\"" + columnName + "_type\" value=\"" + typeLabel + "\">\n");

            // 検索条件出力タイプ
            switch (type) {

                // 文字入力＋プルダウン入力(含む、から始まるなど）
                case STRING_LIKE:

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, ResourcesHandler.getString(GnomesResourcesConstants.YY01_0085, userLocale));

                    out.print("<div class=\"search-dialog-col-title\">" + text + "</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input type=\"text\" name=\""+ columnName + "_value\" class=\"" + textDialogClass + "\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                    out.print("      <select name=\""+ columnName + "_condition\" " + " class=\"search_condition_display_none\" " + pulldownDialogOnclick + ">\n");

                    for (int j = 0; j < patterns.size(); j++) {
                        String strIndex = this.getStringEscapeHtmlValue(getStringNumber(j, false, null));
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(strIndex)) {
                            out.print("              <option value=\"" + strIndex + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(patterns.get(strIndex)) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + strIndex + "\">"+ this.getStringEscapeHtmlValue(patterns.get(strIndex)) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");
                    out.print("</div>\n");

                    break;
                // プルダウン入力（コード等定数）
                case STRING_PULLDOWN:

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text);

                    out.print("<div class=\"search-dialog-col-title\">" + text + "</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <select name=\""+ columnName + "_value\" " + pulldownDialogOnclick + ">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("  </select>\n");
                    out.print("</div>\n");

                    break;
                // プルダウン入力（オートコンプリート有り）
                case STRING_PULLDOWN_AUTOCOMPLETE:
                case STRING_PULLDOWN_AUTOCOMPLETE_1:
                case STRING_PULLDOWN_AUTOCOMPLETE_2:
                {
                    String mode = AUTO_MODE_VALUE_TEXT;
                    if (type == ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_1) {
                        mode = AUTO_MODE_VALUE;
                    }
                    else if (type == ConditionType.STRING_PULLDOWN_AUTOCOMPLETE_2) {
                        mode = AUTO_MODE_TEXT;
                    }

                    out.print("<div class=\"search-dialog-col-title\">" + text + "</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <select name=\""+ columnName + "_value\" class=\"" + CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("  </select>\n");
                    out.print("</div>\n");
                }
                    break;
                // プルダウン入力(数値）
                case NUMBER_PULLDOWN:

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text);

                    out.print("<div class=\"search-dialog-col-title\">" + text + "</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <select name=\""+ columnName + "_value\" " + pulldownDialogOnclick + ">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");
                    out.print("</div>\n");
                    break;
                // プルダウン入力(数値）
                case NUMBER_PULLDOWN_AUTOCOMPLETE:
                case NUMBER_PULLDOWN_AUTOCOMPLETE_1:
                case NUMBER_PULLDOWN_AUTOCOMPLETE_2:
                {
                    String mode = AUTO_MODE_VALUE_TEXT;
                    if (type == ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_1) {
                        mode = AUTO_MODE_VALUE;
                    }
                    else if (type == ConditionType.NUMBER_PULLDOWN_AUTOCOMPLETE_2) {
                        mode = AUTO_MODE_TEXT;
                    }

                    out.print("<div class=\"search-dialog-col-title\">" + text + "</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <select name=\""+ columnName + "_value\" class=\"" + CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (patternKeys.size() > 0 && patternKeys.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");
                    out.print("</div>\n");
                }
                    break;
                // 数値入力(範囲)
                case NUMBER:
                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input type=\"text\" name=\""+ columnName +"_from\" class=\"common-text-number gnomes-number gnomes-number-format " + numberDialogClass + "\" value=\"" + this.getStringEscapeHtmlValue(getStringNumber(searchTableTagId, text, parametars.get(0), null)) + "\">\n");
                    out.print("</div>\n");

                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp;</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input type=\"text\" name=\""+ columnName +"_to\" class=\"common-text-number gnomes-number gnomes-number-format " + numberDialogClass + "\"  value=\"" + this.getStringEscapeHtmlValue(getStringNumber(searchTableTagId, text, parametars.get(1), null)) + "\">\n");

                    out.print("</div>\n");

                    break;
                // from (数値　プルダウン) to (数値　プルダウン)
                case NUMBER_PULLDOWN_RANGE:

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text + "&nbsp;From");

                    // from
                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <select name=\""+ columnName + "_from\" " + pulldownDialogOnclick + ">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (parametars.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");
                    out.print("</div>\n");

                    // 工程端末用プルダウン選択一覧ダイアログ出力処理の取得
                    pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text + "&nbsp;To");
                    // to
                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp;</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <select name=\""+ columnName + "_to\" " + pulldownDialogOnclick + ">\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (parametars.get(1).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");
                    out.print("</div>\n");

                    break;
                // from (数値　プルダウン) to (数値　プルダウン)
                case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE:
                case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1:
                case NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2:
                {
                    String mode = AUTO_MODE_VALUE_TEXT;
                    if (type == ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_1) {
                        mode = AUTO_MODE_VALUE;
                    }
                    else if (type == ConditionType.NUMBER_PULLDOWN_RANGE_AUTOCOMPLETE_2) {
                        mode = AUTO_MODE_TEXT;
                    }

                    // from
                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <select name=\""+ columnName + "_from\" class=\"" + CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\" >\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (parametars.get(0).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");
                    out.print("</div>\n");

                    // to
                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp;</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <select name=\""+ columnName + "_to\" class=\"" + CLASS_AUTO_COMBO + "\" data-mode=\"" + mode + "\" >\n");
                    out.print("    <option value=\"\"></option>\n");

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (parametars.get(1).equals(pattern.getKey())) {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\" selected=\"selected\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                        else {
                            out.print("              <option value=\"" + this.getStringEscapeHtmlValue(pattern.getKey()) + "\">"+ this.getStringEscapeHtmlValue(pattern.getValue()) + "</option>\n");
                        }
                    }

                    out.print("      </select>\n");
                    out.print("</div>\n");
                }
                    break;
                // 日付(範囲)(年月日
                case DATE:
                case MULTIFORMAT_DATESTR:

                    dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                    dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From "+ dateFrom +"</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0073, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                    out.print("</div>\n");
                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"</div>\n");
                    out.print("  <div class=\"search-dialog-col-data\">\n");
                    out.print("<input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0073, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");

                    out.print("  </div>\n");
                    break;
                // 日付(範囲)(年月
                case DATE_YM:

                    dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                    dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From "+ dateFrom +"</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0095, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                    out.print("</div>\n");
                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"</div>\n");
                    out.print("  <div class=\"search-dialog-col-data\">\n");
                    out.print("<input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0095, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");

                    out.print("  </div>\n");
                    break;
                // 日時 (範囲)(年月日時間（分まで
                case DATE_TIME:
                    dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                    dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From "+ dateFrom +"</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0045, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                    out.print("</div>\n");

                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0045, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");

                    out.print("</div>\n");

                    break;
                // 日時 (年月日時間（秒まで
                case DATE_TIME_SS:
                    dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));

                    dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));

                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From "+ dateFrom +"</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0096, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                    out.print("</div>\n");

                    out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"</div>\n");
                    out.print("<div class=\"search-dialog-col-data\">\n");
                    out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0096, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");

                    out.print("</div>\n");
                    break;
                    
                // 日時 (年月日時間（分まで,分は00固定
                case DATE_TIME_MM00:
                	dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));
                	
                	dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));
                	
                	out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From "+ dateFrom +"</div>\n");
                	out.print("<div class=\"search-dialog-col-data\">\n");
                	out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0111, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                	out.print("</div>\n");
                	
                	out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"</div>\n");
                	out.print("<div class=\"search-dialog-col-data\">\n");
                	out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0111, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");
                	
                	out.print("</div>\n");
                	break;
                // 日時 (年月日時間（秒まで,秒は00固定
                case DATE_TIME_SS00:
                	dateFrom = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 0));
                	
                	dateTo = this.getStringEscapeHtml(getSysDateDiff(parametars, type, saveTypes, userLocale, 1));
                	
                	out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;From "+ dateFrom +"</div>\n");
                	out.print("<div class=\"search-dialog-col-data\">\n");
                	out.print("  <input name=\""+ columnName +"_from\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0112, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(0)) + "\">\n");
                	out.print("</div>\n");
                	
                	out.print("<div class=\"search-dialog-col-title\">" + text + "&nbsp;To&nbsp;&nbsp; "+ dateTo +"</div>\n");
                	out.print("<div class=\"search-dialog-col-data\">\n");
                	out.print("  <input name=\""+ columnName +"_to\" class=\"" + dateDialogClass + "\" data-date-format=\""+ ResourcesHandler.getString(GnomesResourcesConstants.YY01_0112, userLocale) +"\" type=\"text\" value=\"" + this.getStringEscapeHtmlValue(parametars.get(1)) + "\">\n");
                	
                	out.print("</div>\n");
                	break;
                    

                // チェックボックス
                case CHECKBOX:

                    int keyIndex = 0;

                    for (Map.Entry<String, String> pattern: patterns.entrySet()) {
                        if (keyIndex == 0) {
                            out.print("<div class=\"search-dialog-col-title\">" + text + "</div>\n");
                        }
                        else {
                            out.print("<div class=\"search-dialog-col-title\"></div>\n");
                        }
                        out.print("<div class=\"search-dialog-col-data\">\n");
                        if (patternKeys.size() > 0 && patternKeys.contains(pattern.getKey())) {
                            out.print("    <label style=\"vertical-align: middle;\"><input name=\"" + columnName + "_check\" class=\"" + checkboxClass + "\" "+ checkboxStyle +" type=\"checkbox\" value=\"" +  this.getStringEscapeHtmlValue(pattern.getKey()) + "\" checked><span>&nbsp;" +  this.getStringEscapeHtmlValue(pattern.getValue()) + "</span></label><br>\n");
                        }
                        else {
                            out.print("    <label style=\"vertical-align: middle;\"><input name=\"" + columnName + "_check\" class=\"" + checkboxClass + "\" "+ checkboxStyle +" type=\"checkbox\" value=\"" +  this.getStringEscapeHtmlValue(pattern.getKey()) + "\"><span>&nbsp;" +  this.getStringEscapeHtmlValue(pattern.getValue()) + "</span></label><br>\n");
                        }
                        out.print("</div>\n");
                        keyIndex++;
                    }

                    break;
                default:
                    // 何もしない。
                    break;
            }

            // 削除ボタンの表示有無
            Boolean dispDeletebtn = true;

            // 検索条件必須項目の詳細検索削除ボタン表示有無を取得
            MstrSystemDefine systemDefineForCondRequired  = this.mstrSystemDefineDao.getMstrSystemDefine(
                     SystemDefConstants.CONDITION_REQUIRED,
                     SystemDefConstants.CODE_DISP_DELETE_BUTTON);

            // 検索条件必須項目の詳細検索削除ボタン表示有無取得が取得できた場合
            if (!Objects.isNull(systemDefineForCondRequired)
                    && !StringUtil.isNullOrEmpty(systemDefineForCondRequired.getChar1())
                    && systemDefineForCondRequired.getChar1().equals(HIDE_DELETE_BUTTON)) {
                // 検索項目の必須入力の取得
                ConditionRequiredType requiredType = ConditionRequiredType.NONE;
                for (MstCondition condition : mstConditions) {
                    if (condition.getColumnName().equals(columnId)) {
                        requiredType = condition.getRequiredType();
                        break;
                    }
                }

                // 必須入力の場合、削除ボタンを非表示
                if (requiredType.equals(ConditionRequiredType.REQUIRED)) {
                    dispDeletebtn = false;
                }
            }

            // 検索条件必須項目の詳細検索削除ボタン表示有無取得が取得できない場合、削除ボタンを表示
            if (dispDeletebtn) {
                if( conditionInfo.isEnable() != null && !conditionInfo.isEnable().equals(DISPLAY_ORDER_NO)){
                    out.print("          <label class=\"commmon-dialog-checkbox\" style=\"display: none;\"><input type=\"checkbox\" name=\"" + columnName + "_enable\" class=\"" + checkboxClass + "\" value=\"1\" checked></label>");
                    out.print("          <a href=\"#\" name onclick=\"hiddenItem("+ "'" + columnName + "'"+ ");\" tabindex=\"" + tabindex + "\"><span class=\"common-button\">" + titleRemoveButtonLabel + "</span></a>");
                }
                else {
                    out.print("          <label class=\"commmon-dialog-checkbox\" style=\"display: none;\"><input type=\"checkbox\" name=\"" + columnName + "_enable\" class=\"" + checkboxClass + "\" value=\"1\"></label>");
                    out.print("          <a href=\"#\" name onclick=\"hiddenItem("+ "'" + columnName + "'"+ ");\" tabindex=\"" + tabindex + "\"><span class=\"common-button\">" + titleRemoveButtonLabel + "</span></a>");
                }
            }
            
            out.print("          </div>\n");

            }
        }
        out.print("    </div>\n");
        out.print("        </div>\n");
        out.print("        </div>\n");
        out.print("        </div>\n");
    }


    /**
     * 差分日数を求める
     * @param params 日付文字
     * @param conditionType タイプ
     * @param saveTypes 保存タイプ
     * @param userLocale ユーザーロケール
     * @param index インデックス
     * @return 差分日数
     * @throws ParseException
     */
    protected String getSysDateDiff(List<String> params, ConditionType conditionType,
            List<ConditionParamSaveType> saveTypes, Locale userLocale, int index) throws ParseException {

        if (params.get(index).length() == 0
                || saveTypes == null
                || saveTypes.get(index) == null
                || saveTypes.get(index) != ConditionParamSaveType.SYSTEM_DATE_SAVE) {
            return "";
        }

        ConditionDateType conditionDateType;
        if (conditionType == ConditionType.DATE || conditionType == ConditionType.MULTIFORMAT_DATESTR ) {
            conditionDateType = ConditionDateType.DATE;
        } else if (conditionType == ConditionType.DATE_TIME) {
            conditionDateType = ConditionDateType.DATE_TIME;
        } else if (conditionType == ConditionType.DATE_TIME_SS){
            conditionDateType = ConditionDateType.DATE_TIME_SS;
        } else if (conditionType == ConditionType.DATE_TIME_MM00){
            conditionDateType = ConditionDateType.DATE_TIME_MM00;
        } else if (conditionType == ConditionType.DATE_TIME_SS00){
            conditionDateType = ConditionDateType.DATE_TIME_SS00;
        } else {
            conditionDateType = ConditionDateType.DATE_YM;
        }

        // 現在
        Date dt = new Date();

        // 指定日
        Date toDt = ConverterUtils.stringToDateFormat(params.get(index),
                ResourcesHandler.getString(conditionDateType.getFormat(), userLocale));

        // 差分日数
        int day = getDiffDays(dt, toDt);

        String result = "";
        if (day < 0) {
            result =  ResourcesHandler.getString(GnomesResourcesConstants.DI01_0125, userLocale)
                    + String.valueOf(Math.abs(day)) + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0127, userLocale);
        }
        else if (day > 0) {
            result = ResourcesHandler.getString(GnomesResourcesConstants.DI01_0126, userLocale)
                    + String.valueOf(day) + ResourcesHandler.getString(GnomesResourcesConstants.DI01_0127, userLocale);
        }
        return result;
    }

    /**
     * <p>[概 要] 日付の差分日数取得処理</p>
     * <p>[詳 細] </p>
     * <p>[備 考] </p>
     * @param  fromDate 開始日付
     * @param  toDate 終了日付
     * @return 差分日数（パラメータがnullの場合は0を返します。）
     */
    protected int getDiffDays(Date fromDate, Date toDate) {

        int diffDays = 0;
        if (fromDate != null && toDate != null) {

            Date fromTran = DateUtils.truncate(fromDate, Calendar.DAY_OF_MONTH);
            Date toTran = DateUtils.truncate(toDate, Calendar.DAY_OF_MONTH);

            long one_date_time = 1000L * 60L * 60L * 24L;
            long datetime1 = fromTran.getTime();
            long datetime2 = toTran.getTime();
            diffDays = (int)((datetime2 - datetime1) / one_date_time);
        }

        return diffDays;

    }


    /**
     * プルダウン選択一覧ダイアログ出力処理の取得
     * @param isPanecon 工程端末表示か否か
     * @param title 項目タイトル
     * @return
     */
    protected String getPulldownDialogOnclick(boolean isPanecon, String title){
        String onclick = "";

        if (isPanecon) {
            onclick = "onclick=\"MakePullDownTableModal($(this), '" + title + "');\"";
        }

        return onclick;
    }

}
