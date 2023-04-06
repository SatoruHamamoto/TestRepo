package com.gnomes.common.tags;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import org.picketbox.util.StringUtil;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.resource.spi.ResourcesHandler;
import com.gnomes.common.view.IScreenFormBean;
/**
 * プログレスバー カスタムタグ
 *
 * <gnomes:GnomesCTagProgressBar labelResourceId="ME001.0001" bean="${MGE002PHFormBean}"/>
 *
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/02/05 YJP/ S.Hamamoto               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagProgressBar extends GnomesCTagBasePrivilege {

    /** Bean={bean} の定義誤りのエラーの出力内容 */
    protected static final String NO_VAR_ERR_MES = "The specified value or max name is not defined or misspelled.";

    protected static final Integer DEFAILT_MAX_VALUE = 100;


    /** bean */
    private Object bean;

    /**
     * Bean取得
     * @return bean
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Bean設定
     * @param bean Bean
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * ラベルリソースID
     *
     */
    private String labelResourceId;


    /**
     * ラベルリソースIDの取得
     * @return
     */
    public String getLabelResourceId() {
        return labelResourceId;
    }

    /**
     * ラベルリソースIDの設定
     * @param labelResourceId
     */
    public void setLabelResourceId(String labelResourceId) {
        this.labelResourceId = labelResourceId;
    }

    /**
     * バーの値のリスト変数名
     */
    private String barValue;

    /**
     * バーの値のリスト変数名の取得
     * @return
     */
    public String getBarValue() {
        return barValue;
    }

    /**
     * バーの値のリスト変数名の設定
     * @param varList
     */
    public void setBarValue(String varValue) {
        this.barValue = varValue;
    }

    /**
     * バーのMax値のリスト変数名
     */
    private String barMaxValue;

    /**
     * @return maxList
     */
    public String getBarMaxValue() {
        return barMaxValue;
    }

    /**
     * @param maxList セットする maxList
     */
    public void setBarMaxValue(String barMaxValue) {
        this.barMaxValue = barMaxValue;
    }



    /*
     * プログレスバータグ出力
     */
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = null;
        try {
            //Beanの指定が間違ってnullになっているのをチェック
            if ( this.bean == null){
                logHelper.severe(this.logger,null,null,NO_BEAN_ERR_MES);
                throw new GnomesAppException(NO_BEAN_ERR_MES);
            }

            Locale userLocale = ((IScreenFormBean)this.bean).getUserLocale();
            out = pageContext.getOut();

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            //左側ラベル出力
            if ( this.labelResourceId != null) {
                // 出力ラベル名
                String strLabel = this.getStringEscapeHtml(ResourcesHandler.getString(this.labelResourceId, userLocale));
                if(StringUtil.isNullOrEmpty(strLabel)){
                    strLabel = "";
                }

                // 左ラベルの出力
                out.print("  <div class=\"common-header-col-title\">" + strLabel + "</div>");

            }

            //プログレスバーの出力
            out.print( "<div class=\"common-header-col-data\">");

            //バー値の変数名の指定が間違ってnullになっているのをチェック
            if ( this.barValue == null){
                logger.severe(NO_VAR_ERR_MES);
                throw new GnomesAppException(NO_VAR_ERR_MES);
            }

            //プログレスバーの値のリストを取得
            Integer barData = null;
            if(this.barValue != null){
                barData = (Integer) getData(clsBean, bean, this.barValue);
            }

            //プログレスバーのMaxのリストを取得
            Integer maxValue = null;
            if(this.barMaxValue != null){
                maxValue = (Integer) getData(clsBean, bean, this.barMaxValue);
            }
            //Maxリストを全く指定しなかった場合、デフォルトで100を指定
            else {
                maxValue = DEFAILT_MAX_VALUE;
            }

            if(barData >= 0){
                //どの型でも％として整数にする（マイナス値はないとする）
                String formatedValue = String.format("%3d", barData).replaceAll(" ", "&nbsp");
                out.print(formatedValue + "%");
            }
            else {
                //マイナス値は"-"にする
                out.print("&nbsp;&nbsp;&nbsp;-");
            }

            //プログレスバーを出力
            out.print("<progress ");
            out.print(" value=\"" + barData + "\"");
            out.print(" max=\"" + maxValue + "\"");
            out.print(" style=\"width: 90%;\">");
            out.print("</progress>\n");
            out.print("</div>\n");

        }
        catch(Exception e) {
            if (!Objects.isNull(out)) {
                try {
                    out.print(getTagErrMes());
                    out.flush();
                } catch (IOException e1) {
                    throw new JspTagException(e1);
                } catch (Exception e2) {
                    throw new JspTagException(e2);
                }
            }

            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public void release() {
        // TODO 自動生成されたメソッド・スタブ

    }




}
