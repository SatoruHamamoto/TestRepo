package com.gnomes.rest.service;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Web サービス エントリポイント
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/11/16 YJP/H.Gojo              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

// Web サービスのパス
@ApplicationPath("/rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    public void addRestResourceClasses(Set<Class<?>> resources) {

        // Web サービスのクラスを resources に追加する。
        resources.add(A01001S000.class);
        resources.add(A01001S001.class);
        resources.add(M01001S001.class);
        resources.add(Y99002S001.class);
        resources.add(Y99003S001.class);
        resources.add(Y99004S001.class);
        resources.add(GnomesCommonBatService.class);
        resources.add(GnomesInternalWebService.class);
        resources.add(WeighMachineService.class);
        resources.add(K01CPrintOutCallbackService.class);
        resources.add(K02CPrintPreviewCallbackService.class);
        resources.add(K02CPrintPreviewPrintoutCallbackService.class);
        resources.add(MstrDataCacheGroupService.class);


    }
}
