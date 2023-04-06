package com.gnomes.common.tags;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.gnomes.common.data.GnomesSessionBean;

/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2020/02/20 09:58 YJP/S.Hamamoto           初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
*/
/**
 * ページトークン専用のカスタムタグ
 * CommonPage.jspに搭載
 * @author 03501213
 *
 */
public class GnomesCTagPageToken extends GnomesCTagBase {

    @Inject
    protected GnomesSessionBean gnomesSessionBean;

    /** 辞書：テキスト入力用のBean **/
    private static final String INFO_PARAM_NAME = "param_name";

    /** 辞書：テキスト入力参照用名称 **/
    private static final String INFO_INPUT_TEXT_NAME = "input_text_name";

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
     * hiddenのPageTokenタグ出力
     * PageTokenは特別で、GnomesSessionBeanでWindowIdをキーにするMapが
     * 管理されており、WindowIdは各FormBeanにあるものからトークン文字を
     * 設定する
     */
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {
            out = pageContext.getOut();
            String strInputText;

            // hidden出力
            //Beanがnullかどうか
            if (! Objects.isNull(this.bean)) {
                Class<?> clsBean = bean.getClass();

                //WindowIdをBeanから入手
                String windowId = (String) getData(clsBean, this.bean, "windowId");

                //WindowIdからPageTokenを入手
                //WindowIdがnullの時はデフォルトWindowになる
                strInputText = gnomesSessionBean.getPageToken(windowId);

                //値がないならば空白にする
                if (StringUtil.isNullOrEmpty(strInputText)) {
                    strInputText = "";
                }

            } else {
                //Beanがnullの場合は出力しない
                strInputText = null;
            }

            if (StringUtil.isNullOrEmpty(strInputText)) {
                strInputText = "";
            } else {
                strInputText = " value=\"" + strInputText + "\"";
            }

            // 左側ラベル出力

            out.print("<input type=\"text\" name=\"pageToken\" hidden" + strInputText + ">\n");

        } catch (Exception e) {
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

    /**
     * 終了処理
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * 解放処理
     */
    public void release() {
    }
}
