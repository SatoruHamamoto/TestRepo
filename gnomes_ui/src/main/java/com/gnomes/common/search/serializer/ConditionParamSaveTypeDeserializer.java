package com.gnomes.common.search.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gnomes.common.search.SearchInfoController.ConditionParamSaveType;

/**
 * 検索共通  条件保存タイプのJsonDeserializerクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/02/05 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ConditionParamSaveTypeDeserializer extends JsonDeserializer<Object> {

    @Override
    public ConditionParamSaveType deserialize(JsonParser jp, DeserializationContext arg1)
            throws IOException, JsonProcessingException {

        int jsonValue = jp.getValueAsInt();

        for (final ConditionParamSaveType enumValue : ConditionParamSaveType.values()) {
            if (enumValue.getValue() == jsonValue) {
                return enumValue;
            }
        }
        return null;
    }


}
