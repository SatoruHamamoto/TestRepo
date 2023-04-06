package com.gnomes.common.command;

import com.gnomes.common.exportdata.ExportDataTableDef;

/**
 * 一覧エクスポートインターフェイス。
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/08/25 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public interface IExportTableCommand {

    public ExportDataTableDef getExportDataTableDefinition();
}
