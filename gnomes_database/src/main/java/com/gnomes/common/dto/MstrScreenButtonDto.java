package com.gnomes.common.dto;

/*
 * 画面に権限があるボタンリスト
 *
 */
public class MstrScreenButtonDto
{

    /** nkボタンID */
    private String buttonId;

    /**
     * MstrScreenButtonDto・コンストラクタ
     */
    public MstrScreenButtonDto()
    {
    }

    /**
     * PullDownDTO・コンストラクタ
     * @param buttonId nkボタンID
     */
    public MstrScreenButtonDto(String buttonId)
    {
        super();
        this.buttonId = buttonId;
    }

    /**
     * nkボタンIDを取得
     * @return nkボタンID
     */
    public String getButtonId()
    {
        return this.buttonId;
    }

    /**
     * nkボタンIDを設定
     * @param button_id nkボタンID (null不可)
     */
    public void setButtonId(String buttonId)
    {
        this.buttonId = buttonId;
    }

}
