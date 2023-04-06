package com.gnomes.common.tags;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.view.BaseFormBean;
import com.gnomes.uiservice.ContainerRequest;
import com.gnomes.uiservice.ContainerResponse;

/**
 * JSON出力タグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/07 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagOutJson implements Tag {


    /** bean */
    private Object bean;

    /** パラメータ名 */
    private String paramName;


    protected PageContext pageContext;
    protected Tag parentTag;


    @Inject
    ContainerResponse responseContext;


    @Override
    public int doStartTag() throws JspException {
        JspWriter out = null;

        try {
            out = pageContext.getOut();

            Object value = null;

            // 検索条件情報の場合
            if (ContainerRequest.REQUEST_NAME_SEARCH_SETTING_MAP.equals(paramName)) {
                // エラーありの場合は、formBeanの検索条件は、functionBeanより復元されるのでこのまま使用
                value = ((BaseFormBean)bean).getSearchSettingMap();
            } else {
                value = responseContext.getResponseFormBean(bean, paramName);
            }
            if (value == null) {
                return SKIP_BODY;
            }

            if (value instanceof String) {
                out.print(value);
            } else {
                String json = ConverterUtils.getJson(value);
                out.print(json);
            }


        } catch (SecurityException
                | IllegalArgumentException | IllegalAccessException
                | IOException | GnomesAppException e) {

            e.printStackTrace();
            throw new JspException(e);
        }

        return SKIP_BODY;
    }


    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    @Override
    public Tag getParent() {
        return parentTag;
    }

    @Override
    public void release() {

    }

    @Override
    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    @Override
    public void setParent(Tag parentTag) {
        this.parentTag = parentTag;
    }


    /**
     * @return bean
     */
    public Object getBean() {
        return bean;
    }


    /**
     * @param bean セットする bean
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }


    /**
     * @return paramName
     */
    public String getParamName() {
        return paramName;
    }


    /**
     * @param paramName セットする paramName
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }




}
