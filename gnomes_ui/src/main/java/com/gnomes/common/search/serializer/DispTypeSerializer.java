package com.gnomes.common.search.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gnomes.common.search.data.SearchSetting.DispType;

/**
 * 検索共通  表示タイプのJsonSerializerクラス
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/03/01 YJP/30022467              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class DispTypeSerializer extends JsonSerializer<DispType> {

    @Override
    public void serialize(DispType value, JsonGenerator jgen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {

        jgen.writeNumber(value.getValue());

    }
}
