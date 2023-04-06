package com.gnomes.common.dto;


/*
 *
 * ツールによって自動生成されたDTOです。
 * 生成日時：2016/10/17 9:43:21
 *
 */

public class PullDownDto {

    /** 名前 */
    private String name;
    /** 表示 */
    private String value;

    /**
     * PullDownDto・コンストラクタ
     */
    public PullDownDto() {
    }

    /**
     * PullDownDTO・コンストラクタ
     * @param name 名前
     * @param value 表示
     */
    public PullDownDto(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * 名前を取得
     * @return 名前
     */
    public String getName() {
        return this.name;
    }

    /**
     * 名前を設定
     * @param name 名前
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 表示を取得
     * @return 表示
     */
    public String getValue() {
        return this.value;
    }

    /**
     * 表示を設定
     * @param value 表示
     */
    public void setValue(String value) {
        this.value = value;
    }

}
