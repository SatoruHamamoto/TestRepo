package com.gnomes.common.search.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gnomes.common.search.SearchInfoController.OrderType;

/**
 * 検索共通  順序タイプのJsonDeserializerクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class OrderTypeDeserializer extends JsonDeserializer<Object> {

	@Override
	public Object deserialize(JsonParser jp, DeserializationContext arg1)
			throws IOException, JsonProcessingException {

		int jsonValue = jp.getValueAsInt();

        for (final OrderType enumValue : OrderType.values()) {
            if (enumValue.getValue() == jsonValue) {
                return enumValue;
            }
        }

        return null;
	}

}
