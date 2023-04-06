package com.gnomes.system.dto;

public class PersonPrivilegeDto {

    private String processcode;
    private String subprocesscode;
    private String privilegecode;
    private String privilegename;
    private Integer isrestricted;           // 作業権限による制限を行なうか否か
    private Integer isnecessaryconfirm;     // 確認ダイアログ等の表示有無
    private String confirmmessage;          // 確認ダイアログ出力時のメッセージキー
    private Integer isnescessarypassword;   // ユーザ認証の有無
    private Integer isdelete;               // 削除禁止フラグ
    private String sortkey;                 // 表示用ソートキー

    public PersonPrivilegeDto(String processcode, String subprocesscode, String privilegecode, String privilegename,
            Integer isrestricted, Integer isnecessaryconfirm, String confirmmessage, Integer isnescessarypassword,
            Integer isdelete, String sortkey) {
        super();
        this.processcode = processcode;
        this.subprocesscode = subprocesscode;
        this.privilegecode = privilegecode;
        this.privilegename = privilegename;
        this.isrestricted = isrestricted;
        this.isnecessaryconfirm = isnecessaryconfirm;
        this.confirmmessage = confirmmessage;
        this.isnescessarypassword = isnescessarypassword;
        this.isdelete = isdelete;
        this.sortkey = sortkey;
    }
    public String getProcesscode() {
        return processcode;
    }
    public void setProcesscode(String processcode) {
        this.processcode = processcode;
    }
    public String getSubprocesscode() {
        return subprocesscode;
    }
    public void setSubprocesscode(String subprocesscode) {
        this.subprocesscode = subprocesscode;
    }
    public String getPrivilegecode() {
        return privilegecode;
    }
    public void setPrivilegecode(String privilegecode) {
        this.privilegecode = privilegecode;
    }
    public String getPrivilegename() {
        return privilegename;
    }
    public void setPrivilegename(String privilegename) {
        this.privilegename = privilegename;
    }
    public Integer getIsrestricted() {
        return isrestricted;
    }
    public void setIsrestricted(Integer isrestricted) {
        this.isrestricted = isrestricted;
    }
    public Integer getIsnecessaryconfirm() {
        return isnecessaryconfirm;
    }
    public void setIsnecessaryconfirm(Integer isnecessaryconfirm) {
        this.isnecessaryconfirm = isnecessaryconfirm;
    }
    public String getConfirmmessage() {
        return confirmmessage;
    }
    public void setConfirmmessage(String confirmmessage) {
        this.confirmmessage = confirmmessage;
    }
    public Integer getIsnescessarypassword() {
        return isnescessarypassword;
    }
    public void setIsnescessarypassword(Integer isnescessarypassword) {
        this.isnescessarypassword = isnescessarypassword;
    }
    public Integer getIsdelete() {
        return isdelete;
    }
    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }
    public String getSortkey() {
        return sortkey;
    }
    public void setSortkey(String sortkey) {
        this.sortkey = sortkey;
    }




}
