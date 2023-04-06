package com.gnomes.common.search.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gnomes.common.search.SearchInfoController.ConditionType;

/**
 * 検索共通  条件タイプのJsonDeserializerクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ConditionTypeDeserializer extends JsonDeserializer<Object> {

	@Override
	public ConditionType deserialize(JsonParser jp, DeserializationContext arg1)
			throws IOException, JsonProcessingException {

		int jsonValue = jp.getValueAsInt();

        for (final ConditionType enumValue : ConditionType.values()) {
            if (enumValue.getValue() == jsonValue) {
                return enumValue;
            }
        }
        return null;
	}

}
