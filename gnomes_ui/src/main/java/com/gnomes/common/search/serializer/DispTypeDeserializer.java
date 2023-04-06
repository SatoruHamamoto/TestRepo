package com.gnomes.common.search.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gnomes.common.search.data.SearchSetting.DispType;

/**
 * 検索共通  表示タイプのJsonDeserializerクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class DispTypeDeserializer extends JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext arg1)
            throws IOException, JsonProcessingException {

        int jsonValue = jp.getValueAsInt();

        for (final DispType enumValue : DispType.values()) {
            if (enumValue.getValue() == jsonValue) {
                return enumValue;
            }
        }

        return null;
    }
}
