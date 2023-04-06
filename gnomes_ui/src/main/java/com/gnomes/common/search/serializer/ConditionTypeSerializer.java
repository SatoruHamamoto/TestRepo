package com.gnomes.common.search.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gnomes.common.search.SearchInfoController.ConditionType;

/**
 * 検索共通  条件タイプのJsonSerializerクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class ConditionTypeSerializer extends JsonSerializer<ConditionType> {

	@Override
	public void serialize(ConditionType value, JsonGenerator jgen, SerializerProvider arg2)
			throws IOException, JsonProcessingException {

		jgen.writeNumber(value.getValue());
	}
}
