package com.gnomes.common.command;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import com.gnomes.common.importdata.ImportExportDefBase;

/**
 * リクエストサービスファイル情報
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/02 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class RequestServiceFileInfo {

    /**
     * コンストラクター
     */
    public RequestServiceFileInfo() {
    }

    private Map<String, List<?>> requestServiceFileInfos;

    /**
     * インポート時の定義情報
     */
    private List<ImportExportDefBase> importExportDefinitions;

    public ImportExportDefBase getImportExportDefinition(String key) {

        for (ImportExportDefBase e : importExportDefinitions) {
            if (key.equals(e.getKey())) {
                return e;
            }
        }
        return null;
    }

    /**
     * @return requestServiceFileInfos
     */
    public Map<String, List<?>> getRequestServiceFileInfos() {
        return requestServiceFileInfos;
    }

    /**
     * @param requestServiceFileInfos セットする requestServiceFileInfos
     */
    public void setRequestServiceFileInfos(
            Map<String, List<?>> requestServiceFileInfos) {
        this.requestServiceFileInfos = requestServiceFileInfos;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> get(String key) {
        return (List<T>) requestServiceFileInfos.get(key);
    }

    public List<ImportExportDefBase> getImportExportDefinitions() {
        return importExportDefinitions;
    }

    public void setImportExportDefinitions(
            List<ImportExportDefBase> importExportDefinitions) {
        this.importExportDefinitions = importExportDefinitions;
    }

}