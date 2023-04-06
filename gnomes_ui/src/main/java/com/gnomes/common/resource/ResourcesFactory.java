package com.gnomes.common.resource;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.annotation.Priority;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.gnomes.common.constants.CommonConstants;
import com.gnomes.common.data.GnomesSessionBean;

/**
 * ここにクラス概要を入力してください。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/07/12 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@SessionScoped
@Alternative
@Priority(CommonConstants.GNOMESINTERCEPTOR_PLATFORM)
public class ResourcesFactory implements Serializable {


    // セッション Baen
    @Inject
    GnomesSessionBean gnomesSessionBean;

    /**
     * デフォルト・コンストラクタ
     */
    public ResourcesFactory() {
    }

    /**
     * リソースバンドル取得 プロデューサー
     * @return
     */
    @GnomesResources
    @Produces
    public ResourceBundle resourceBundleProducer() {
        return InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.GNOMES_RESOURCES, gnomesSessionBean.getUserLocale());
    }

    /**
     * メッセージリソースバンドル取得 プロデューサー
     * @return
     */
    @GnomesMessages
    @Produces
    public ResourceBundle messageResourceBundleProducer() {
        return InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.GNOMES_MESSAGES, gnomesSessionBean.getUserLocale());
    }

    /**
     * JSTL用 リソースバンドル取得 プロデューサー
     * @return
     */
    @Named(value = "GnomesResources")
    @Produces
    public LocalizationContext localizationContextProducer() {
        // リソース読込み
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.GNOMES_RESOURCES, gnomesSessionBean.getUserLocale());
        // LocalizationContext
        return (bundle == null ? null : new LocalizationContext(bundle));
    }

    @Named(value = "GnomesResources_Contents")
    @Produces
    public LocalizationContext localizationContextProducerContents() {
        // リソース読込み
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.GNOMES_RESOURCES_CONTENTS, gnomesSessionBean.getUserLocale());
        // LocalizationContext
        return (bundle == null ? null : new LocalizationContext(bundle));
    }

    @Named(value = "GnomesResources_Contents_Job")
    @Produces
    public LocalizationContext localizationContextProducerContentsJob() {
        // リソース読込み
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.GNOMES_RESOURCES_CONTENTS_JOB, gnomesSessionBean.getUserLocale());
        // LocalizationContext
        return (bundle == null ? null : new LocalizationContext(bundle));
    }


    /**
     * JSTL用 メッセージリソースバンドル取得 プロデューサー
     * @return
     */
    @Named(value = "GnomesMessages")
    @Produces
    public LocalizationContext messageLocalizationContextProducer() {
        // リソース読込み
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.GNOMES_MESSAGES, gnomesSessionBean.getUserLocale());
        // LocalizationContext
        return (bundle == null ? null : new LocalizationContext(bundle));
    }

    @Named(value = "GnomesMessages_Contents")
    @Produces
    public LocalizationContext messageLocalizationContextProducerContents() {
        // リソース読込み
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.GNOMES_MESSAGES_CONTENTS, gnomesSessionBean.getUserLocale());
        // LocalizationContext
        return (bundle == null ? null : new LocalizationContext(bundle));
    }

    @Named(value = "GnomesMessages_Contents_Job")
    @Produces
    public LocalizationContext messageLocalizationContextProducerContentsJob() {
        // リソース読込み
        ResourceBundle bundle = InheritedBundleManager.getInstance().getBundle(InheritedBundleManager.GNOMES_MESSAGES_CONTENTS_JOB, gnomesSessionBean.getUserLocale());
        // LocalizationContext
        return (bundle == null ? null : new LocalizationContext(bundle));
    }
}
