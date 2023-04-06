package com.gnomes.common.tags;

import java.util.Arrays;

public enum TagType {

    CHECKBOX(0, "checkbox"),
    BUTTON(1, "button"),
    IMG_PATTERN(2, "imgPattern"),
    TEXT(3, "text"),
    NUMBER(4, "number"),
    DATE(5, "date"),
    ZONEDDATETIME(6, "zonedDateTime"),
    PULLDOWN_CODE(7, "pulldown_code"),
    PULLDOWN_CODE_NO_SPACE(8, "pulldown_code_no_space"),
    PULLDOWN_DATA(9, "pulldown_data"),
    PULLDOWN_DATA_NO_SPACE(10, "pulldown_data_no_space"),
    HIDDEN(11, "hidden"),
    INPUT_TEXT(12, "input_text"),
    INPUT_NUMBER(13, "input_number"),
    INPUT_DATE(14, "input_date"),
    INPUT_ZONEDDATETIME(15, "input_timezone"),
    INPUT_DATA_TYPE(16, "input_data_type"),
    OUTPUT_DATA_TYPE(17, "output_data_type"),
    PROGRESS(18, "progress"),
    ;

    private int index;
    private String type;

    private TagType(int index, String type) {
        this.index = index;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public static TagType valueOf(int index) {
        return Arrays.stream(TagType.values())
                .filter(tagType -> tagType.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

}
