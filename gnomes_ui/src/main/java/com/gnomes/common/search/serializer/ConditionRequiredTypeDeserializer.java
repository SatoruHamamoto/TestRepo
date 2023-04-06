package com.gnomes.common.search.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gnomes.common.search.SearchInfoController.ConditionRequiredType;

/**
 * 検索共通  必須タイプのJsonDeserializerクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/03/09 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ConditionRequiredTypeDeserializer extends JsonDeserializer<Object> {

    @Override
    public ConditionRequiredType deserialize(JsonParser jp, DeserializationContext arg1)
            throws IOException, JsonProcessingException {

        int jsonValue = jp.getValueAsInt();

        for (final ConditionRequiredType enumValue : ConditionRequiredType.values()) {
            if (enumValue.getValue() == jsonValue) {
                return enumValue;
            }
        }
        return null;
    }


}
