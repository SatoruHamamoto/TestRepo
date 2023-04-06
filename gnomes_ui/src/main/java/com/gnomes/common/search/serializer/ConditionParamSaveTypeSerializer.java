package com.gnomes.common.search.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gnomes.common.search.SearchInfoController.ConditionParamSaveType;

/**
 * 検索共通  条件保存タイプのJsonSerializerクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/05 YJP/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ConditionParamSaveTypeSerializer extends JsonSerializer<ConditionParamSaveType> {

    @Override
    public void serialize(ConditionParamSaveType value, JsonGenerator jgen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {

        jgen.writeNumber(value.getValue());
    }
}