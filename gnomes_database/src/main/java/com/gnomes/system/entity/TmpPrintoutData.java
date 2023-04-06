package com.gnomes.system.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.gnomes.common.entity.BaseEntity;
import com.gnomes.common.entity.EntityAuditListener;

/**
 * Zi123印字用一時データ エンティティ
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2022/05/12 - / -                     ツールにより自動生成
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "tmp_printout_data")
@NamedQueries({
        @NamedQuery(name = "TmpPrintoutData.findAll", query = "SELECT p FROM TmpPrintoutData p"),
        @NamedQuery(name = "TmpPrintoutData.findByPK", query = "SELECT p FROM TmpPrintoutData p WHERE p.tmp_printout_data_key = :tmp_printout_data_key")
})
public class TmpPrintoutData extends BaseEntity implements Serializable {

    /** テーブル名 */
    public static final String TABLE_NAME = "tmp_printout_data";

    /** 印字用一時データKey */
    public static final String COLUMN_NAME_TMP_PRINTOUT_DATA_KEY = "tmp_printout_data_key";

    /** nk要求イベントID */
    public static final String COLUMN_NAME_EVENT_ID = "event_id";

    /** nk要求内連番 */
    public static final String COLUMN_NAME_REQUEST_SEQ = "request_seq";

    /** nkデータグループID1 */
    public static final String COLUMN_NAME_DATA_GROUP_ID_01 = "data_group_id_01";

    /** nkデータグループID2 */
    public static final String COLUMN_NAME_DATA_GROUP_ID_02 = "data_group_id_02";

    /** nk行番号 */
    public static final String COLUMN_NAME_ROW_NUMBER = "row_number";

    /** データ01 */
    public static final String COLUMN_NAME_DATA_01 = "data_01";

    /** データ02 */
    public static final String COLUMN_NAME_DATA_02 = "data_02";

    /** データ03 */
    public static final String COLUMN_NAME_DATA_03 = "data_03";

    /** データ04 */
    public static final String COLUMN_NAME_DATA_04 = "data_04";

    /** データ05 */
    public static final String COLUMN_NAME_DATA_05 = "data_05";

    /** データ06 */
    public static final String COLUMN_NAME_DATA_06 = "data_06";

    /** データ07 */
    public static final String COLUMN_NAME_DATA_07 = "data_07";

    /** データ08 */
    public static final String COLUMN_NAME_DATA_08 = "data_08";

    /** データ09 */
    public static final String COLUMN_NAME_DATA_09 = "data_09";

    /** データ10 */
    public static final String COLUMN_NAME_DATA_10 = "data_10";

    /** データ11 */
    public static final String COLUMN_NAME_DATA_11 = "data_11";

    /** データ12 */
    public static final String COLUMN_NAME_DATA_12 = "data_12";

    /** データ13 */
    public static final String COLUMN_NAME_DATA_13 = "data_13";

    /** データ14 */
    public static final String COLUMN_NAME_DATA_14 = "data_14";

    /** データ15 */
    public static final String COLUMN_NAME_DATA_15 = "data_15";

    /** データ16 */
    public static final String COLUMN_NAME_DATA_16 = "data_16";

    /** データ17 */
    public static final String COLUMN_NAME_DATA_17 = "data_17";

    /** データ18 */
    public static final String COLUMN_NAME_DATA_18 = "data_18";

    /** データ19 */
    public static final String COLUMN_NAME_DATA_19 = "data_19";

    /** データ20 */
    public static final String COLUMN_NAME_DATA_20 = "data_20";

    /** データ21 */
    public static final String COLUMN_NAME_DATA_21 = "data_21";

    /** データ22 */
    public static final String COLUMN_NAME_DATA_22 = "data_22";

    /** データ23 */
    public static final String COLUMN_NAME_DATA_23 = "data_23";

    /** データ24 */
    public static final String COLUMN_NAME_DATA_24 = "data_24";

    /** データ25 */
    public static final String COLUMN_NAME_DATA_25 = "data_25";

    /** データ26 */
    public static final String COLUMN_NAME_DATA_26 = "data_26";

    /** データ27 */
    public static final String COLUMN_NAME_DATA_27 = "data_27";

    /** データ28 */
    public static final String COLUMN_NAME_DATA_28 = "data_28";

    /** データ29 */
    public static final String COLUMN_NAME_DATA_29 = "data_29";

    /** データ30 */
    public static final String COLUMN_NAME_DATA_30 = "data_30";

    /** データ31 */
    public static final String COLUMN_NAME_DATA_31 = "data_31";

    /** データ32 */
    public static final String COLUMN_NAME_DATA_32 = "data_32";

    /** データ33 */
    public static final String COLUMN_NAME_DATA_33 = "data_33";

    /** データ34 */
    public static final String COLUMN_NAME_DATA_34 = "data_34";

    /** データ35 */
    public static final String COLUMN_NAME_DATA_35 = "data_35";

    /** データ36 */
    public static final String COLUMN_NAME_DATA_36 = "data_36";

    /** データ37 */
    public static final String COLUMN_NAME_DATA_37 = "data_37";

    /** データ38 */
    public static final String COLUMN_NAME_DATA_38 = "data_38";

    /** データ39 */
    public static final String COLUMN_NAME_DATA_39 = "data_39";

    /** データ40 */
    public static final String COLUMN_NAME_DATA_40 = "data_40";

    /** データ41 */
    public static final String COLUMN_NAME_DATA_41 = "data_41";

    /** データ42 */
    public static final String COLUMN_NAME_DATA_42 = "data_42";

    /** データ43 */
    public static final String COLUMN_NAME_DATA_43 = "data_43";

    /** データ44 */
    public static final String COLUMN_NAME_DATA_44 = "data_44";

    /** データ45 */
    public static final String COLUMN_NAME_DATA_45 = "data_45";

    /** データ46 */
    public static final String COLUMN_NAME_DATA_46 = "data_46";

    /** データ47 */
    public static final String COLUMN_NAME_DATA_47 = "data_47";

    /** データ48 */
    public static final String COLUMN_NAME_DATA_48 = "data_48";

    /** データ49 */
    public static final String COLUMN_NAME_DATA_49 = "data_49";

    /** データ50 */
    public static final String COLUMN_NAME_DATA_50 = "data_50";

    /** データ51 */
    public static final String COLUMN_NAME_DATA_51 = "data_51";

    /** データ52 */
    public static final String COLUMN_NAME_DATA_52 = "data_52";

    /** データ53 */
    public static final String COLUMN_NAME_DATA_53 = "data_53";

    /** データ54 */
    public static final String COLUMN_NAME_DATA_54 = "data_54";

    /** データ55 */
    public static final String COLUMN_NAME_DATA_55 = "data_55";

    /** データ56 */
    public static final String COLUMN_NAME_DATA_56 = "data_56";

    /** データ57 */
    public static final String COLUMN_NAME_DATA_57 = "data_57";

    /** データ58 */
    public static final String COLUMN_NAME_DATA_58 = "data_58";

    /** データ59 */
    public static final String COLUMN_NAME_DATA_59 = "data_59";

    /** データ60 */
    public static final String COLUMN_NAME_DATA_60 = "data_60";

    /** データ61 */
    public static final String COLUMN_NAME_DATA_61 = "data_61";

    /** データ62 */
    public static final String COLUMN_NAME_DATA_62 = "data_62";

    /** データ63 */
    public static final String COLUMN_NAME_DATA_63 = "data_63";

    /** データ64 */
    public static final String COLUMN_NAME_DATA_64 = "data_64";

    /** データ65 */
    public static final String COLUMN_NAME_DATA_65 = "data_65";

    /** データ66 */
    public static final String COLUMN_NAME_DATA_66 = "data_66";

    /** データ67 */
    public static final String COLUMN_NAME_DATA_67 = "data_67";

    /** データ68 */
    public static final String COLUMN_NAME_DATA_68 = "data_68";

    /** データ69 */
    public static final String COLUMN_NAME_DATA_69 = "data_69";

    /** データ70 */
    public static final String COLUMN_NAME_DATA_70 = "data_70";

    /** データ71 */
    public static final String COLUMN_NAME_DATA_71 = "data_71";

    /** データ72 */
    public static final String COLUMN_NAME_DATA_72 = "data_72";

    /** データ73 */
    public static final String COLUMN_NAME_DATA_73 = "data_73";

    /** データ74 */
    public static final String COLUMN_NAME_DATA_74 = "data_74";

    /** データ75 */
    public static final String COLUMN_NAME_DATA_75 = "data_75";

    /** データ76 */
    public static final String COLUMN_NAME_DATA_76 = "data_76";

    /** データ77 */
    public static final String COLUMN_NAME_DATA_77 = "data_77";

    /** データ78 */
    public static final String COLUMN_NAME_DATA_78 = "data_78";

    /** データ79 */
    public static final String COLUMN_NAME_DATA_79 = "data_79";

    /** データ80 */
    public static final String COLUMN_NAME_DATA_80 = "data_80";

    /** データ81 */
    public static final String COLUMN_NAME_DATA_81 = "data_81";

    /** データ82 */
    public static final String COLUMN_NAME_DATA_82 = "data_82";

    /** データ83 */
    public static final String COLUMN_NAME_DATA_83 = "data_83";

    /** データ84 */
    public static final String COLUMN_NAME_DATA_84 = "data_84";

    /** データ85 */
    public static final String COLUMN_NAME_DATA_85 = "data_85";

    /** データ86 */
    public static final String COLUMN_NAME_DATA_86 = "data_86";

    /** データ87 */
    public static final String COLUMN_NAME_DATA_87 = "data_87";

    /** データ88 */
    public static final String COLUMN_NAME_DATA_88 = "data_88";

    /** データ89 */
    public static final String COLUMN_NAME_DATA_89 = "data_89";

    /** データ90 */
    public static final String COLUMN_NAME_DATA_90 = "data_90";

    /** データ91 */
    public static final String COLUMN_NAME_DATA_91 = "data_91";

    /** データ92 */
    public static final String COLUMN_NAME_DATA_92 = "data_92";

    /** データ93 */
    public static final String COLUMN_NAME_DATA_93 = "data_93";

    /** データ94 */
    public static final String COLUMN_NAME_DATA_94 = "data_94";

    /** データ95 */
    public static final String COLUMN_NAME_DATA_95 = "data_95";

    /** データ96 */
    public static final String COLUMN_NAME_DATA_96 = "data_96";

    /** データ97 */
    public static final String COLUMN_NAME_DATA_97 = "data_97";

    /** データ98 */
    public static final String COLUMN_NAME_DATA_98 = "data_98";

    /** データ99 */
    public static final String COLUMN_NAME_DATA_99 = "data_99";

    /** データ100 */
    public static final String COLUMN_NAME_DATA_100 = "data_100";

    /** データ101 */
    public static final String COLUMN_NAME_DATA_101 = "data_101";

    /** データ102 */
    public static final String COLUMN_NAME_DATA_102 = "data_102";

    /** データ103 */
    public static final String COLUMN_NAME_DATA_103 = "data_103";

    /** データ104 */
    public static final String COLUMN_NAME_DATA_104 = "data_104";

    /** データ105 */
    public static final String COLUMN_NAME_DATA_105 = "data_105";

    /** データ106 */
    public static final String COLUMN_NAME_DATA_106 = "data_106";

    /** データ107 */
    public static final String COLUMN_NAME_DATA_107 = "data_107";

    /** データ108 */
    public static final String COLUMN_NAME_DATA_108 = "data_108";

    /** データ109 */
    public static final String COLUMN_NAME_DATA_109 = "data_109";

    /** データ110 */
    public static final String COLUMN_NAME_DATA_110 = "data_110";

    /** データ111 */
    public static final String COLUMN_NAME_DATA_111 = "data_111";

    /** データ112 */
    public static final String COLUMN_NAME_DATA_112 = "data_112";

    /** データ113 */
    public static final String COLUMN_NAME_DATA_113 = "data_113";

    /** データ114 */
    public static final String COLUMN_NAME_DATA_114 = "data_114";

    /** データ115 */
    public static final String COLUMN_NAME_DATA_115 = "data_115";

    /** データ116 */
    public static final String COLUMN_NAME_DATA_116 = "data_116";

    /** データ117 */
    public static final String COLUMN_NAME_DATA_117 = "data_117";

    /** データ118 */
    public static final String COLUMN_NAME_DATA_118 = "data_118";

    /** データ119 */
    public static final String COLUMN_NAME_DATA_119 = "data_119";

    /** データ120 */
    public static final String COLUMN_NAME_DATA_120 = "data_120";

    /** データ121 */
    public static final String COLUMN_NAME_DATA_121 = "data_121";

    /** データ122 */
    public static final String COLUMN_NAME_DATA_122 = "data_122";

    /** データ123 */
    public static final String COLUMN_NAME_DATA_123 = "data_123";

    /** データ124 */
    public static final String COLUMN_NAME_DATA_124 = "data_124";

    /** データ125 */
    public static final String COLUMN_NAME_DATA_125 = "data_125";

    /** データ126 */
    public static final String COLUMN_NAME_DATA_126 = "data_126";

    /** データ127 */
    public static final String COLUMN_NAME_DATA_127 = "data_127";

    /** データ128 */
    public static final String COLUMN_NAME_DATA_128 = "data_128";

    /** データ129 */
    public static final String COLUMN_NAME_DATA_129 = "data_129";

    /** データ130 */
    public static final String COLUMN_NAME_DATA_130 = "data_130";

    /** データ131 */
    public static final String COLUMN_NAME_DATA_131 = "data_131";

    /** データ132 */
    public static final String COLUMN_NAME_DATA_132 = "data_132";

    /** データ133 */
    public static final String COLUMN_NAME_DATA_133 = "data_133";

    /** データ134 */
    public static final String COLUMN_NAME_DATA_134 = "data_134";

    /** データ135 */
    public static final String COLUMN_NAME_DATA_135 = "data_135";

    /** データ136 */
    public static final String COLUMN_NAME_DATA_136 = "data_136";

    /** データ137 */
    public static final String COLUMN_NAME_DATA_137 = "data_137";

    /** データ138 */
    public static final String COLUMN_NAME_DATA_138 = "data_138";

    /** データ139 */
    public static final String COLUMN_NAME_DATA_139 = "data_139";

    /** データ140 */
    public static final String COLUMN_NAME_DATA_140 = "data_140";

    /** データ141 */
    public static final String COLUMN_NAME_DATA_141 = "data_141";

    /** データ142 */
    public static final String COLUMN_NAME_DATA_142 = "data_142";

    /** データ143 */
    public static final String COLUMN_NAME_DATA_143 = "data_143";

    /** データ144 */
    public static final String COLUMN_NAME_DATA_144 = "data_144";

    /** データ145 */
    public static final String COLUMN_NAME_DATA_145 = "data_145";

    /** データ146 */
    public static final String COLUMN_NAME_DATA_146 = "data_146";

    /** データ147 */
    public static final String COLUMN_NAME_DATA_147 = "data_147";

    /** データ148 */
    public static final String COLUMN_NAME_DATA_148 = "data_148";

    /** データ149 */
    public static final String COLUMN_NAME_DATA_149 = "data_149";

    /** データ150 */
    public static final String COLUMN_NAME_DATA_150 = "data_150";

    /** 印字用一時データKey */
    private String tmp_printout_data_key;
    /** nk要求イベントID */
    private String event_id;
    /** nk要求内連番 */
    private int request_seq;
    /** nkデータグループID1 */
    private String data_group_id_01;
    /** nkデータグループID2 */
    private String data_group_id_02;
    /** nk行番号 */
    private int row_number;
    /** データ01 */
    private String data_01;
    /** データ02 */
    private String data_02;
    /** データ03 */
    private String data_03;
    /** データ04 */
    private String data_04;
    /** データ05 */
    private String data_05;
    /** データ06 */
    private String data_06;
    /** データ07 */
    private String data_07;
    /** データ08 */
    private String data_08;
    /** データ09 */
    private String data_09;
    /** データ10 */
    private String data_10;
    /** データ11 */
    private String data_11;
    /** データ12 */
    private String data_12;
    /** データ13 */
    private String data_13;
    /** データ14 */
    private String data_14;
    /** データ15 */
    private String data_15;
    /** データ16 */
    private String data_16;
    /** データ17 */
    private String data_17;
    /** データ18 */
    private String data_18;
    /** データ19 */
    private String data_19;
    /** データ20 */
    private String data_20;
    /** データ21 */
    private String data_21;
    /** データ22 */
    private String data_22;
    /** データ23 */
    private String data_23;
    /** データ24 */
    private String data_24;
    /** データ25 */
    private String data_25;
    /** データ26 */
    private String data_26;
    /** データ27 */
    private String data_27;
    /** データ28 */
    private String data_28;
    /** データ29 */
    private String data_29;
    /** データ30 */
    private String data_30;
    /** データ31 */
    private String data_31;
    /** データ32 */
    private String data_32;
    /** データ33 */
    private String data_33;
    /** データ34 */
    private String data_34;
    /** データ35 */
    private String data_35;
    /** データ36 */
    private String data_36;
    /** データ37 */
    private String data_37;
    /** データ38 */
    private String data_38;
    /** データ39 */
    private String data_39;
    /** データ40 */
    private String data_40;
    /** データ41 */
    private String data_41;
    /** データ42 */
    private String data_42;
    /** データ43 */
    private String data_43;
    /** データ44 */
    private String data_44;
    /** データ45 */
    private String data_45;
    /** データ46 */
    private String data_46;
    /** データ47 */
    private String data_47;
    /** データ48 */
    private String data_48;
    /** データ49 */
    private String data_49;
    /** データ50 */
    private String data_50;
    /** データ51 */
    private String data_51;
    /** データ52 */
    private String data_52;
    /** データ53 */
    private String data_53;
    /** データ54 */
    private String data_54;
    /** データ55 */
    private String data_55;
    /** データ56 */
    private String data_56;
    /** データ57 */
    private String data_57;
    /** データ58 */
    private String data_58;
    /** データ59 */
    private String data_59;
    /** データ60 */
    private String data_60;
    /** データ61 */
    private String data_61;
    /** データ62 */
    private String data_62;
    /** データ63 */
    private String data_63;
    /** データ64 */
    private String data_64;
    /** データ65 */
    private String data_65;
    /** データ66 */
    private String data_66;
    /** データ67 */
    private String data_67;
    /** データ68 */
    private String data_68;
    /** データ69 */
    private String data_69;
    /** データ70 */
    private String data_70;
    /** データ71 */
    private String data_71;
    /** データ72 */
    private String data_72;
    /** データ73 */
    private String data_73;
    /** データ74 */
    private String data_74;
    /** データ75 */
    private String data_75;
    /** データ76 */
    private String data_76;
    /** データ77 */
    private String data_77;
    /** データ78 */
    private String data_78;
    /** データ79 */
    private String data_79;
    /** データ80 */
    private String data_80;
    /** データ81 */
    private String data_81;
    /** データ82 */
    private String data_82;
    /** データ83 */
    private String data_83;
    /** データ84 */
    private String data_84;
    /** データ85 */
    private String data_85;
    /** データ86 */
    private String data_86;
    /** データ87 */
    private String data_87;
    /** データ88 */
    private String data_88;
    /** データ89 */
    private String data_89;
    /** データ90 */
    private String data_90;
    /** データ91 */
    private String data_91;
    /** データ92 */
    private String data_92;
    /** データ93 */
    private String data_93;
    /** データ94 */
    private String data_94;
    /** データ95 */
    private String data_95;
    /** データ96 */
    private String data_96;
    /** データ97 */
    private String data_97;
    /** データ98 */
    private String data_98;
    /** データ99 */
    private String data_99;
    /** データ100 */
    private String data_100;
    /** データ101 */
    private String data_101;
    /** データ102 */
    private String data_102;
    /** データ103 */
    private String data_103;
    /** データ104 */
    private String data_104;
    /** データ105 */
    private String data_105;
    /** データ106 */
    private String data_106;
    /** データ107 */
    private String data_107;
    /** データ108 */
    private String data_108;
    /** データ109 */
    private String data_109;
    /** データ110 */
    private String data_110;
    /** データ111 */
    private String data_111;
    /** データ112 */
    private String data_112;
    /** データ113 */
    private String data_113;
    /** データ114 */
    private String data_114;
    /** データ115 */
    private String data_115;
    /** データ116 */
    private String data_116;
    /** データ117 */
    private String data_117;
    /** データ118 */
    private String data_118;
    /** データ119 */
    private String data_119;
    /** データ120 */
    private String data_120;
    /** データ121 */
    private String data_121;
    /** データ122 */
    private String data_122;
    /** データ123 */
    private String data_123;
    /** データ124 */
    private String data_124;
    /** データ125 */
    private String data_125;
    /** データ126 */
    private String data_126;
    /** データ127 */
    private String data_127;
    /** データ128 */
    private String data_128;
    /** データ129 */
    private String data_129;
    /** データ130 */
    private String data_130;
    /** データ131 */
    private String data_131;
    /** データ132 */
    private String data_132;
    /** データ133 */
    private String data_133;
    /** データ134 */
    private String data_134;
    /** データ135 */
    private String data_135;
    /** データ136 */
    private String data_136;
    /** データ137 */
    private String data_137;
    /** データ138 */
    private String data_138;
    /** データ139 */
    private String data_139;
    /** データ140 */
    private String data_140;
    /** データ141 */
    private String data_141;
    /** データ142 */
    private String data_142;
    /** データ143 */
    private String data_143;
    /** データ144 */
    private String data_144;
    /** データ145 */
    private String data_145;
    /** データ146 */
    private String data_146;
    /** データ147 */
    private String data_147;
    /** データ148 */
    private String data_148;
    /** データ149 */
    private String data_149;
    /** データ150 */
    private String data_150;


    /**
     * Zi123印字用一時データエンティティ コンストラクタ
     */
    public TmpPrintoutData() {
    }

    /**
     * Zi123印字用一時データエンティティ コンストラクタ
     * @param tmp_printout_data_key 印字用一時データKey
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param row_number nk行番号
     * @param version 更新バージョン
     */
    public TmpPrintoutData(String tmp_printout_data_key, String event_id, int request_seq, int row_number, int version) {
        this.tmp_printout_data_key = tmp_printout_data_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.row_number = row_number;
        super.setVersion(version);
    }

    /**
     * Zi123印字用一時データエンティティ コンストラクタ
     * @param tmp_printout_data_key 印字用一時データKey
     * @param event_id nk要求イベントID
     * @param request_seq nk要求内連番
     * @param data_group_id_01 nkデータグループID1
     * @param data_group_id_02 nkデータグループID2
     * @param row_number nk行番号
     * @param data_01 データ01
     * @param data_02 データ02
     * @param data_03 データ03
     * @param data_04 データ04
     * @param data_05 データ05
     * @param data_06 データ06
     * @param data_07 データ07
     * @param data_08 データ08
     * @param data_09 データ09
     * @param data_10 データ10
     * @param data_11 データ11
     * @param data_12 データ12
     * @param data_13 データ13
     * @param data_14 データ14
     * @param data_15 データ15
     * @param data_16 データ16
     * @param data_17 データ17
     * @param data_18 データ18
     * @param data_19 データ19
     * @param data_20 データ20
     * @param data_21 データ21
     * @param data_22 データ22
     * @param data_23 データ23
     * @param data_24 データ24
     * @param data_25 データ25
     * @param data_26 データ26
     * @param data_27 データ27
     * @param data_28 データ28
     * @param data_29 データ29
     * @param data_30 データ30
     * @param data_31 データ31
     * @param data_32 データ32
     * @param data_33 データ33
     * @param data_34 データ34
     * @param data_35 データ35
     * @param data_36 データ36
     * @param data_37 データ37
     * @param data_38 データ38
     * @param data_39 データ39
     * @param data_40 データ40
     * @param data_41 データ41
     * @param data_42 データ42
     * @param data_43 データ43
     * @param data_44 データ44
     * @param data_45 データ45
     * @param data_46 データ46
     * @param data_47 データ47
     * @param data_48 データ48
     * @param data_49 データ49
     * @param data_50 データ50
     * @param data_51 データ51
     * @param data_52 データ52
     * @param data_53 データ53
     * @param data_54 データ54
     * @param data_55 データ55
     * @param data_56 データ56
     * @param data_57 データ57
     * @param data_58 データ58
     * @param data_59 データ59
     * @param data_60 データ60
     * @param data_61 データ61
     * @param data_62 データ62
     * @param data_63 データ63
     * @param data_64 データ64
     * @param data_65 データ65
     * @param data_66 データ66
     * @param data_67 データ67
     * @param data_68 データ68
     * @param data_69 データ69
     * @param data_70 データ70
     * @param data_71 データ71
     * @param data_72 データ72
     * @param data_73 データ73
     * @param data_74 データ74
     * @param data_75 データ75
     * @param data_76 データ76
     * @param data_77 データ77
     * @param data_78 データ78
     * @param data_79 データ79
     * @param data_80 データ80
     * @param data_81 データ81
     * @param data_82 データ82
     * @param data_83 データ83
     * @param data_84 データ84
     * @param data_85 データ85
     * @param data_86 データ86
     * @param data_87 データ87
     * @param data_88 データ88
     * @param data_89 データ89
     * @param data_90 データ90
     * @param data_91 データ91
     * @param data_92 データ92
     * @param data_93 データ93
     * @param data_94 データ94
     * @param data_95 データ95
     * @param data_96 データ96
     * @param data_97 データ97
     * @param data_98 データ98
     * @param data_99 データ99
     * @param data_100 データ100
     * @param data_101 データ101
     * @param data_102 データ102
     * @param data_103 データ103
     * @param data_104 データ104
     * @param data_105 データ105
     * @param data_106 データ106
     * @param data_107 データ107
     * @param data_108 データ108
     * @param data_109 データ109
     * @param data_110 データ110
     * @param data_111 データ111
     * @param data_112 データ112
     * @param data_113 データ113
     * @param data_114 データ114
     * @param data_115 データ115
     * @param data_116 データ116
     * @param data_117 データ117
     * @param data_118 データ118
     * @param data_119 データ119
     * @param data_120 データ120
     * @param data_121 データ121
     * @param data_122 データ122
     * @param data_123 データ123
     * @param data_124 データ124
     * @param data_125 データ125
     * @param data_126 データ126
     * @param data_127 データ127
     * @param data_128 データ128
     * @param data_129 データ129
     * @param data_130 データ130
     * @param data_131 データ131
     * @param data_132 データ132
     * @param data_133 データ133
     * @param data_134 データ134
     * @param data_135 データ135
     * @param data_136 データ136
     * @param data_137 データ137
     * @param data_138 データ138
     * @param data_139 データ139
     * @param data_140 データ140
     * @param data_141 データ141
     * @param data_142 データ142
     * @param data_143 データ143
     * @param data_144 データ144
     * @param data_145 データ145
     * @param data_146 データ146
     * @param data_147 データ147
     * @param data_148 データ148
     * @param data_149 データ149
     * @param data_150 データ150
     * @param first_regist_event_id 登録イベントID
     * @param first_regist_user_number 登録従業員No
     * @param first_regist_user_name 登録従業員名
     * @param first_regist_datetime 登録日時
     * @param last_regist_event_id 更新イベントID
     * @param last_regist_user_number 更新従業員No
     * @param last_regist_user_name 更新従業員名
     * @param last_regist_datetime 更新日時
     * @param version 更新バージョン
     */
    public TmpPrintoutData(String tmp_printout_data_key, String event_id, int request_seq, String data_group_id_01, String data_group_id_02, int row_number, String data_01, String data_02, String data_03, String data_04, String data_05, String data_06, String data_07, String data_08, String data_09, String data_10, String data_11, String data_12, String data_13, String data_14, String data_15, String data_16, String data_17, String data_18, String data_19, String data_20, String data_21, String data_22, String data_23, String data_24, String data_25, String data_26, String data_27, String data_28, String data_29, String data_30, String data_31, String data_32, String data_33, String data_34, String data_35, String data_36, String data_37, String data_38, String data_39, String data_40, String data_41, String data_42, String data_43, String data_44, String data_45, String data_46, String data_47, String data_48, String data_49, String data_50, String data_51, String data_52, String data_53, String data_54, String data_55, String data_56, String data_57, String data_58, String data_59, String data_60, String data_61, String data_62, String data_63, String data_64, String data_65, String data_66, String data_67, String data_68, String data_69, String data_70, String data_71, String data_72, String data_73, String data_74, String data_75, String data_76, String data_77, String data_78, String data_79, String data_80, String data_81, String data_82, String data_83, String data_84, String data_85, String data_86, String data_87, String data_88, String data_89, String data_90, String data_91, String data_92, String data_93, String data_94, String data_95, String data_96, String data_97, String data_98, String data_99, String data_100, String data_101, String data_102, String data_103, String data_104, String data_105, String data_106, String data_107, String data_108, String data_109, String data_110, String data_111, String data_112, String data_113, String data_114, String data_115, String data_116, String data_117, String data_118, String data_119, String data_120, String data_121, String data_122, String data_123, String data_124, String data_125, String data_126, String data_127, String data_128, String data_129, String data_130, String data_131, String data_132, String data_133, String data_134, String data_135, String data_136, String data_137, String data_138, String data_139, String data_140, String data_141, String data_142, String data_143, String data_144, String data_145, String data_146, String data_147, String data_148, String data_149, String data_150, String first_regist_event_id, String first_regist_user_number, String first_regist_user_name, Date first_regist_datetime, String last_regist_event_id, String last_regist_user_number, String last_regist_user_name, Date last_regist_datetime, int version) {
        this.tmp_printout_data_key = tmp_printout_data_key;
        this.event_id = event_id;
        this.request_seq = request_seq;
        this.data_group_id_01 = data_group_id_01;
        this.data_group_id_02 = data_group_id_02;
        this.row_number = row_number;
        this.data_01 = data_01;
        this.data_02 = data_02;
        this.data_03 = data_03;
        this.data_04 = data_04;
        this.data_05 = data_05;
        this.data_06 = data_06;
        this.data_07 = data_07;
        this.data_08 = data_08;
        this.data_09 = data_09;
        this.data_10 = data_10;
        this.data_11 = data_11;
        this.data_12 = data_12;
        this.data_13 = data_13;
        this.data_14 = data_14;
        this.data_15 = data_15;
        this.data_16 = data_16;
        this.data_17 = data_17;
        this.data_18 = data_18;
        this.data_19 = data_19;
        this.data_20 = data_20;
        this.data_21 = data_21;
        this.data_22 = data_22;
        this.data_23 = data_23;
        this.data_24 = data_24;
        this.data_25 = data_25;
        this.data_26 = data_26;
        this.data_27 = data_27;
        this.data_28 = data_28;
        this.data_29 = data_29;
        this.data_30 = data_30;
        this.data_31 = data_31;
        this.data_32 = data_32;
        this.data_33 = data_33;
        this.data_34 = data_34;
        this.data_35 = data_35;
        this.data_36 = data_36;
        this.data_37 = data_37;
        this.data_38 = data_38;
        this.data_39 = data_39;
        this.data_40 = data_40;
        this.data_41 = data_41;
        this.data_42 = data_42;
        this.data_43 = data_43;
        this.data_44 = data_44;
        this.data_45 = data_45;
        this.data_46 = data_46;
        this.data_47 = data_47;
        this.data_48 = data_48;
        this.data_49 = data_49;
        this.data_50 = data_50;
        this.data_51 = data_51;
        this.data_52 = data_52;
        this.data_53 = data_53;
        this.data_54 = data_54;
        this.data_55 = data_55;
        this.data_56 = data_56;
        this.data_57 = data_57;
        this.data_58 = data_58;
        this.data_59 = data_59;
        this.data_60 = data_60;
        this.data_61 = data_61;
        this.data_62 = data_62;
        this.data_63 = data_63;
        this.data_64 = data_64;
        this.data_65 = data_65;
        this.data_66 = data_66;
        this.data_67 = data_67;
        this.data_68 = data_68;
        this.data_69 = data_69;
        this.data_70 = data_70;
        this.data_71 = data_71;
        this.data_72 = data_72;
        this.data_73 = data_73;
        this.data_74 = data_74;
        this.data_75 = data_75;
        this.data_76 = data_76;
        this.data_77 = data_77;
        this.data_78 = data_78;
        this.data_79 = data_79;
        this.data_80 = data_80;
        this.data_81 = data_81;
        this.data_82 = data_82;
        this.data_83 = data_83;
        this.data_84 = data_84;
        this.data_85 = data_85;
        this.data_86 = data_86;
        this.data_87 = data_87;
        this.data_88 = data_88;
        this.data_89 = data_89;
        this.data_90 = data_90;
        this.data_91 = data_91;
        this.data_92 = data_92;
        this.data_93 = data_93;
        this.data_94 = data_94;
        this.data_95 = data_95;
        this.data_96 = data_96;
        this.data_97 = data_97;
        this.data_98 = data_98;
        this.data_99 = data_99;
        this.data_100 = data_100;
        this.data_101 = data_101;
        this.data_102 = data_102;
        this.data_103 = data_103;
        this.data_104 = data_104;
        this.data_105 = data_105;
        this.data_106 = data_106;
        this.data_107 = data_107;
        this.data_108 = data_108;
        this.data_109 = data_109;
        this.data_110 = data_110;
        this.data_111 = data_111;
        this.data_112 = data_112;
        this.data_113 = data_113;
        this.data_114 = data_114;
        this.data_115 = data_115;
        this.data_116 = data_116;
        this.data_117 = data_117;
        this.data_118 = data_118;
        this.data_119 = data_119;
        this.data_120 = data_120;
        this.data_121 = data_121;
        this.data_122 = data_122;
        this.data_123 = data_123;
        this.data_124 = data_124;
        this.data_125 = data_125;
        this.data_126 = data_126;
        this.data_127 = data_127;
        this.data_128 = data_128;
        this.data_129 = data_129;
        this.data_130 = data_130;
        this.data_131 = data_131;
        this.data_132 = data_132;
        this.data_133 = data_133;
        this.data_134 = data_134;
        this.data_135 = data_135;
        this.data_136 = data_136;
        this.data_137 = data_137;
        this.data_138 = data_138;
        this.data_139 = data_139;
        this.data_140 = data_140;
        this.data_141 = data_141;
        this.data_142 = data_142;
        this.data_143 = data_143;
        this.data_144 = data_144;
        this.data_145 = data_145;
        this.data_146 = data_146;
        this.data_147 = data_147;
        this.data_148 = data_148;
        this.data_149 = data_149;
        this.data_150 = data_150;
        super.setFirst_regist_event_id(first_regist_event_id);
        super.setFirst_regist_user_number(first_regist_user_number);
        super.setFirst_regist_user_name(first_regist_user_name);
        super.setFirst_regist_datetime(first_regist_datetime);
        super.setLast_regist_event_id(last_regist_event_id);
        super.setLast_regist_user_number(last_regist_user_number);
        super.setLast_regist_user_name(last_regist_user_name);
        super.setLast_regist_datetime(last_regist_datetime);
        super.setVersion(version);
    }

    /**
     * 印字用一時データKeyを取得
     * @return 印字用一時データKey
     */
    @Id
    @Column(nullable = false, length = 38)
    public String getTmp_printout_data_key() {
        return this.tmp_printout_data_key;
    }

    /**
     * 印字用一時データKeyを設定
     * @param tmp_printout_data_key 印字用一時データKey (null不可)
     */
    public void setTmp_printout_data_key(String tmp_printout_data_key) {
        this.tmp_printout_data_key = tmp_printout_data_key;
    }

    /**
     * nk要求イベントIDを取得
     * @return nk要求イベントID
     */
    @Column(nullable = false, length = 38)
    public String getEvent_id() {
        return this.event_id;
    }

    /**
     * nk要求イベントIDを設定
     * @param event_id nk要求イベントID (null不可)
     */
    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    /**
     * nk要求内連番を取得
     * @return nk要求内連番
     */
    @Column(nullable = false, length = 3)
    public int getRequest_seq() {
        return this.request_seq;
    }

    /**
     * nk要求内連番を設定
     * @param request_seq nk要求内連番 (null不可)
     */
    public void setRequest_seq(int request_seq) {
        this.request_seq = request_seq;
    }

    /**
     * nkデータグループID1を取得
     * @return nkデータグループID1
     */
    @Column(length = 20)
    public String getData_group_id_01() {
        return this.data_group_id_01;
    }

    /**
     * nkデータグループID1を設定
     * @param data_group_id_01 nkデータグループID1
     */
    public void setData_group_id_01(String data_group_id_01) {
        this.data_group_id_01 = data_group_id_01;
    }

    /**
     * nkデータグループID2を取得
     * @return nkデータグループID2
     */
    @Column(length = 20)
    public String getData_group_id_02() {
        return this.data_group_id_02;
    }

    /**
     * nkデータグループID2を設定
     * @param data_group_id_02 nkデータグループID2
     */
    public void setData_group_id_02(String data_group_id_02) {
        this.data_group_id_02 = data_group_id_02;
    }

    /**
     * nk行番号を取得
     * @return nk行番号
     */
    @Column(nullable = false, length = 5)
    public int getRow_number() {
        return this.row_number;
    }

    /**
     * nk行番号を設定
     * @param row_number nk行番号 (null不可)
     */
    public void setRow_number(int row_number) {
        this.row_number = row_number;
    }

    /**
     * データ01を取得
     * @return データ01
     */
    @Column(length = 2000)
    public String getData_01() {
        return this.data_01;
    }

    /**
     * データ01を設定
     * @param data_01 データ01
     */
    public void setData_01(String data_01) {
        this.data_01 = data_01;
    }

    /**
     * データ02を取得
     * @return データ02
     */
    @Column(length = 2000)
    public String getData_02() {
        return this.data_02;
    }

    /**
     * データ02を設定
     * @param data_02 データ02
     */
    public void setData_02(String data_02) {
        this.data_02 = data_02;
    }

    /**
     * データ03を取得
     * @return データ03
     */
    @Column(length = 2000)
    public String getData_03() {
        return this.data_03;
    }

    /**
     * データ03を設定
     * @param data_03 データ03
     */
    public void setData_03(String data_03) {
        this.data_03 = data_03;
    }

    /**
     * データ04を取得
     * @return データ04
     */
    @Column(length = 2000)
    public String getData_04() {
        return this.data_04;
    }

    /**
     * データ04を設定
     * @param data_04 データ04
     */
    public void setData_04(String data_04) {
        this.data_04 = data_04;
    }

    /**
     * データ05を取得
     * @return データ05
     */
    @Column(length = 2000)
    public String getData_05() {
        return this.data_05;
    }

    /**
     * データ05を設定
     * @param data_05 データ05
     */
    public void setData_05(String data_05) {
        this.data_05 = data_05;
    }

    /**
     * データ06を取得
     * @return データ06
     */
    @Column(length = 2000)
    public String getData_06() {
        return this.data_06;
    }

    /**
     * データ06を設定
     * @param data_06 データ06
     */
    public void setData_06(String data_06) {
        this.data_06 = data_06;
    }

    /**
     * データ07を取得
     * @return データ07
     */
    @Column(length = 2000)
    public String getData_07() {
        return this.data_07;
    }

    /**
     * データ07を設定
     * @param data_07 データ07
     */
    public void setData_07(String data_07) {
        this.data_07 = data_07;
    }

    /**
     * データ08を取得
     * @return データ08
     */
    @Column(length = 2000)
    public String getData_08() {
        return this.data_08;
    }

    /**
     * データ08を設定
     * @param data_08 データ08
     */
    public void setData_08(String data_08) {
        this.data_08 = data_08;
    }

    /**
     * データ09を取得
     * @return データ09
     */
    @Column(length = 2000)
    public String getData_09() {
        return this.data_09;
    }

    /**
     * データ09を設定
     * @param data_09 データ09
     */
    public void setData_09(String data_09) {
        this.data_09 = data_09;
    }

    /**
     * データ10を取得
     * @return データ10
     */
    @Column(length = 2000)
    public String getData_10() {
        return this.data_10;
    }

    /**
     * データ10を設定
     * @param data_10 データ10
     */
    public void setData_10(String data_10) {
        this.data_10 = data_10;
    }

    /**
     * データ11を取得
     * @return データ11
     */
    @Column(length = 2000)
    public String getData_11() {
        return this.data_11;
    }

    /**
     * データ11を設定
     * @param data_11 データ11
     */
    public void setData_11(String data_11) {
        this.data_11 = data_11;
    }

    /**
     * データ12を取得
     * @return データ12
     */
    @Column(length = 2000)
    public String getData_12() {
        return this.data_12;
    }

    /**
     * データ12を設定
     * @param data_12 データ12
     */
    public void setData_12(String data_12) {
        this.data_12 = data_12;
    }

    /**
     * データ13を取得
     * @return データ13
     */
    @Column(length = 2000)
    public String getData_13() {
        return this.data_13;
    }

    /**
     * データ13を設定
     * @param data_13 データ13
     */
    public void setData_13(String data_13) {
        this.data_13 = data_13;
    }

    /**
     * データ14を取得
     * @return データ14
     */
    @Column(length = 2000)
    public String getData_14() {
        return this.data_14;
    }

    /**
     * データ14を設定
     * @param data_14 データ14
     */
    public void setData_14(String data_14) {
        this.data_14 = data_14;
    }

    /**
     * データ15を取得
     * @return データ15
     */
    @Column(length = 2000)
    public String getData_15() {
        return this.data_15;
    }

    /**
     * データ15を設定
     * @param data_15 データ15
     */
    public void setData_15(String data_15) {
        this.data_15 = data_15;
    }

    /**
     * データ16を取得
     * @return データ16
     */
    @Column(length = 2000)
    public String getData_16() {
        return this.data_16;
    }

    /**
     * データ16を設定
     * @param data_16 データ16
     */
    public void setData_16(String data_16) {
        this.data_16 = data_16;
    }

    /**
     * データ17を取得
     * @return データ17
     */
    @Column(length = 2000)
    public String getData_17() {
        return this.data_17;
    }

    /**
     * データ17を設定
     * @param data_17 データ17
     */
    public void setData_17(String data_17) {
        this.data_17 = data_17;
    }

    /**
     * データ18を取得
     * @return データ18
     */
    @Column(length = 2000)
    public String getData_18() {
        return this.data_18;
    }

    /**
     * データ18を設定
     * @param data_18 データ18
     */
    public void setData_18(String data_18) {
        this.data_18 = data_18;
    }

    /**
     * データ19を取得
     * @return データ19
     */
    @Column(length = 2000)
    public String getData_19() {
        return this.data_19;
    }

    /**
     * データ19を設定
     * @param data_19 データ19
     */
    public void setData_19(String data_19) {
        this.data_19 = data_19;
    }

    /**
     * データ20を取得
     * @return データ20
     */
    @Column(length = 2000)
    public String getData_20() {
        return this.data_20;
    }

    /**
     * データ20を設定
     * @param data_20 データ20
     */
    public void setData_20(String data_20) {
        this.data_20 = data_20;
    }

    /**
     * データ21を取得
     * @return データ21
     */
    @Column(length = 2000)
    public String getData_21() {
        return this.data_21;
    }

    /**
     * データ21を設定
     * @param data_21 データ21
     */
    public void setData_21(String data_21) {
        this.data_21 = data_21;
    }

    /**
     * データ22を取得
     * @return データ22
     */
    @Column(length = 2000)
    public String getData_22() {
        return this.data_22;
    }

    /**
     * データ22を設定
     * @param data_22 データ22
     */
    public void setData_22(String data_22) {
        this.data_22 = data_22;
    }

    /**
     * データ23を取得
     * @return データ23
     */
    @Column(length = 2000)
    public String getData_23() {
        return this.data_23;
    }

    /**
     * データ23を設定
     * @param data_23 データ23
     */
    public void setData_23(String data_23) {
        this.data_23 = data_23;
    }

    /**
     * データ24を取得
     * @return データ24
     */
    @Column(length = 2000)
    public String getData_24() {
        return this.data_24;
    }

    /**
     * データ24を設定
     * @param data_24 データ24
     */
    public void setData_24(String data_24) {
        this.data_24 = data_24;
    }

    /**
     * データ25を取得
     * @return データ25
     */
    @Column(length = 2000)
    public String getData_25() {
        return this.data_25;
    }

    /**
     * データ25を設定
     * @param data_25 データ25
     */
    public void setData_25(String data_25) {
        this.data_25 = data_25;
    }

    /**
     * データ26を取得
     * @return データ26
     */
    @Column(length = 2000)
    public String getData_26() {
        return this.data_26;
    }

    /**
     * データ26を設定
     * @param data_26 データ26
     */
    public void setData_26(String data_26) {
        this.data_26 = data_26;
    }

    /**
     * データ27を取得
     * @return データ27
     */
    @Column(length = 2000)
    public String getData_27() {
        return this.data_27;
    }

    /**
     * データ27を設定
     * @param data_27 データ27
     */
    public void setData_27(String data_27) {
        this.data_27 = data_27;
    }

    /**
     * データ28を取得
     * @return データ28
     */
    @Column(length = 2000)
    public String getData_28() {
        return this.data_28;
    }

    /**
     * データ28を設定
     * @param data_28 データ28
     */
    public void setData_28(String data_28) {
        this.data_28 = data_28;
    }

    /**
     * データ29を取得
     * @return データ29
     */
    @Column(length = 2000)
    public String getData_29() {
        return this.data_29;
    }

    /**
     * データ29を設定
     * @param data_29 データ29
     */
    public void setData_29(String data_29) {
        this.data_29 = data_29;
    }

    /**
     * データ30を取得
     * @return データ30
     */
    @Column(length = 2000)
    public String getData_30() {
        return this.data_30;
    }

    /**
     * データ30を設定
     * @param data_30 データ30
     */
    public void setData_30(String data_30) {
        this.data_30 = data_30;
    }

    /**
     * データ31を取得
     * @return データ31
     */
    @Column(length = 2000)
    public String getData_31() {
        return this.data_31;
    }

    /**
     * データ31を設定
     * @param data_31 データ31
     */
    public void setData_31(String data_31) {
        this.data_31 = data_31;
    }

    /**
     * データ32を取得
     * @return データ32
     */
    @Column(length = 2000)
    public String getData_32() {
        return this.data_32;
    }

    /**
     * データ32を設定
     * @param data_32 データ32
     */
    public void setData_32(String data_32) {
        this.data_32 = data_32;
    }

    /**
     * データ33を取得
     * @return データ33
     */
    @Column(length = 2000)
    public String getData_33() {
        return this.data_33;
    }

    /**
     * データ33を設定
     * @param data_33 データ33
     */
    public void setData_33(String data_33) {
        this.data_33 = data_33;
    }

    /**
     * データ34を取得
     * @return データ34
     */
    @Column(length = 2000)
    public String getData_34() {
        return this.data_34;
    }

    /**
     * データ34を設定
     * @param data_34 データ34
     */
    public void setData_34(String data_34) {
        this.data_34 = data_34;
    }

    /**
     * データ35を取得
     * @return データ35
     */
    @Column(length = 2000)
    public String getData_35() {
        return this.data_35;
    }

    /**
     * データ35を設定
     * @param data_35 データ35
     */
    public void setData_35(String data_35) {
        this.data_35 = data_35;
    }

    /**
     * データ36を取得
     * @return データ36
     */
    @Column(length = 2000)
    public String getData_36() {
        return this.data_36;
    }

    /**
     * データ36を設定
     * @param data_36 データ36
     */
    public void setData_36(String data_36) {
        this.data_36 = data_36;
    }

    /**
     * データ37を取得
     * @return データ37
     */
    @Column(length = 2000)
    public String getData_37() {
        return this.data_37;
    }

    /**
     * データ37を設定
     * @param data_37 データ37
     */
    public void setData_37(String data_37) {
        this.data_37 = data_37;
    }

    /**
     * データ38を取得
     * @return データ38
     */
    @Column(length = 2000)
    public String getData_38() {
        return this.data_38;
    }

    /**
     * データ38を設定
     * @param data_38 データ38
     */
    public void setData_38(String data_38) {
        this.data_38 = data_38;
    }

    /**
     * データ39を取得
     * @return データ39
     */
    @Column(length = 2000)
    public String getData_39() {
        return this.data_39;
    }

    /**
     * データ39を設定
     * @param data_39 データ39
     */
    public void setData_39(String data_39) {
        this.data_39 = data_39;
    }

    /**
     * データ40を取得
     * @return データ40
     */
    @Column(length = 2000)
    public String getData_40() {
        return this.data_40;
    }

    /**
     * データ40を設定
     * @param data_40 データ40
     */
    public void setData_40(String data_40) {
        this.data_40 = data_40;
    }

    /**
     * データ41を取得
     * @return データ41
     */
    @Column(length = 2000)
    public String getData_41() {
        return this.data_41;
    }

    /**
     * データ41を設定
     * @param data_41 データ41
     */
    public void setData_41(String data_41) {
        this.data_41 = data_41;
    }

    /**
     * データ42を取得
     * @return データ42
     */
    @Column(length = 2000)
    public String getData_42() {
        return this.data_42;
    }

    /**
     * データ42を設定
     * @param data_42 データ42
     */
    public void setData_42(String data_42) {
        this.data_42 = data_42;
    }

    /**
     * データ43を取得
     * @return データ43
     */
    @Column(length = 2000)
    public String getData_43() {
        return this.data_43;
    }

    /**
     * データ43を設定
     * @param data_43 データ43
     */
    public void setData_43(String data_43) {
        this.data_43 = data_43;
    }

    /**
     * データ44を取得
     * @return データ44
     */
    @Column(length = 2000)
    public String getData_44() {
        return this.data_44;
    }

    /**
     * データ44を設定
     * @param data_44 データ44
     */
    public void setData_44(String data_44) {
        this.data_44 = data_44;
    }

    /**
     * データ45を取得
     * @return データ45
     */
    @Column(length = 2000)
    public String getData_45() {
        return this.data_45;
    }

    /**
     * データ45を設定
     * @param data_45 データ45
     */
    public void setData_45(String data_45) {
        this.data_45 = data_45;
    }

    /**
     * データ46を取得
     * @return データ46
     */
    @Column(length = 2000)
    public String getData_46() {
        return this.data_46;
    }

    /**
     * データ46を設定
     * @param data_46 データ46
     */
    public void setData_46(String data_46) {
        this.data_46 = data_46;
    }

    /**
     * データ47を取得
     * @return データ47
     */
    @Column(length = 2000)
    public String getData_47() {
        return this.data_47;
    }

    /**
     * データ47を設定
     * @param data_47 データ47
     */
    public void setData_47(String data_47) {
        this.data_47 = data_47;
    }

    /**
     * データ48を取得
     * @return データ48
     */
    @Column(length = 2000)
    public String getData_48() {
        return this.data_48;
    }

    /**
     * データ48を設定
     * @param data_48 データ48
     */
    public void setData_48(String data_48) {
        this.data_48 = data_48;
    }

    /**
     * データ49を取得
     * @return データ49
     */
    @Column(length = 2000)
    public String getData_49() {
        return this.data_49;
    }

    /**
     * データ49を設定
     * @param data_49 データ49
     */
    public void setData_49(String data_49) {
        this.data_49 = data_49;
    }

    /**
     * データ50を取得
     * @return データ50
     */
    @Column(length = 2000)
    public String getData_50() {
        return this.data_50;
    }

    /**
     * データ50を設定
     * @param data_50 データ50
     */
    public void setData_50(String data_50) {
        this.data_50 = data_50;
    }

    /**
     * データ51を取得
     * @return データ51
     */
    @Column(length = 2000)
    public String getData_51() {
        return this.data_51;
    }

    /**
     * データ51を設定
     * @param data_51 データ51
     */
    public void setData_51(String data_51) {
        this.data_51 = data_51;
    }

    /**
     * データ52を取得
     * @return データ52
     */
    @Column(length = 2000)
    public String getData_52() {
        return this.data_52;
    }

    /**
     * データ52を設定
     * @param data_52 データ52
     */
    public void setData_52(String data_52) {
        this.data_52 = data_52;
    }

    /**
     * データ53を取得
     * @return データ53
     */
    @Column(length = 2000)
    public String getData_53() {
        return this.data_53;
    }

    /**
     * データ53を設定
     * @param data_53 データ53
     */
    public void setData_53(String data_53) {
        this.data_53 = data_53;
    }

    /**
     * データ54を取得
     * @return データ54
     */
    @Column(length = 2000)
    public String getData_54() {
        return this.data_54;
    }

    /**
     * データ54を設定
     * @param data_54 データ54
     */
    public void setData_54(String data_54) {
        this.data_54 = data_54;
    }

    /**
     * データ55を取得
     * @return データ55
     */
    @Column(length = 2000)
    public String getData_55() {
        return this.data_55;
    }

    /**
     * データ55を設定
     * @param data_55 データ55
     */
    public void setData_55(String data_55) {
        this.data_55 = data_55;
    }

    /**
     * データ56を取得
     * @return データ56
     */
    @Column(length = 2000)
    public String getData_56() {
        return this.data_56;
    }

    /**
     * データ56を設定
     * @param data_56 データ56
     */
    public void setData_56(String data_56) {
        this.data_56 = data_56;
    }

    /**
     * データ57を取得
     * @return データ57
     */
    @Column(length = 2000)
    public String getData_57() {
        return this.data_57;
    }

    /**
     * データ57を設定
     * @param data_57 データ57
     */
    public void setData_57(String data_57) {
        this.data_57 = data_57;
    }

    /**
     * データ58を取得
     * @return データ58
     */
    @Column(length = 2000)
    public String getData_58() {
        return this.data_58;
    }

    /**
     * データ58を設定
     * @param data_58 データ58
     */
    public void setData_58(String data_58) {
        this.data_58 = data_58;
    }

    /**
     * データ59を取得
     * @return データ59
     */
    @Column(length = 2000)
    public String getData_59() {
        return this.data_59;
    }

    /**
     * データ59を設定
     * @param data_59 データ59
     */
    public void setData_59(String data_59) {
        this.data_59 = data_59;
    }

    /**
     * データ60を取得
     * @return データ60
     */
    @Column(length = 2000)
    public String getData_60() {
        return this.data_60;
    }

    /**
     * データ60を設定
     * @param data_60 データ60
     */
    public void setData_60(String data_60) {
        this.data_60 = data_60;
    }

    /**
     * データ61を取得
     * @return データ61
     */
    @Column(length = 2000)
    public String getData_61() {
        return this.data_61;
    }

    /**
     * データ61を設定
     * @param data_61 データ61
     */
    public void setData_61(String data_61) {
        this.data_61 = data_61;
    }

    /**
     * データ62を取得
     * @return データ62
     */
    @Column(length = 2000)
    public String getData_62() {
        return this.data_62;
    }

    /**
     * データ62を設定
     * @param data_62 データ62
     */
    public void setData_62(String data_62) {
        this.data_62 = data_62;
    }

    /**
     * データ63を取得
     * @return データ63
     */
    @Column(length = 2000)
    public String getData_63() {
        return this.data_63;
    }

    /**
     * データ63を設定
     * @param data_63 データ63
     */
    public void setData_63(String data_63) {
        this.data_63 = data_63;
    }

    /**
     * データ64を取得
     * @return データ64
     */
    @Column(length = 2000)
    public String getData_64() {
        return this.data_64;
    }

    /**
     * データ64を設定
     * @param data_64 データ64
     */
    public void setData_64(String data_64) {
        this.data_64 = data_64;
    }

    /**
     * データ65を取得
     * @return データ65
     */
    @Column(length = 2000)
    public String getData_65() {
        return this.data_65;
    }

    /**
     * データ65を設定
     * @param data_65 データ65
     */
    public void setData_65(String data_65) {
        this.data_65 = data_65;
    }

    /**
     * データ66を取得
     * @return データ66
     */
    @Column(length = 2000)
    public String getData_66() {
        return this.data_66;
    }

    /**
     * データ66を設定
     * @param data_66 データ66
     */
    public void setData_66(String data_66) {
        this.data_66 = data_66;
    }

    /**
     * データ67を取得
     * @return データ67
     */
    @Column(length = 2000)
    public String getData_67() {
        return this.data_67;
    }

    /**
     * データ67を設定
     * @param data_67 データ67
     */
    public void setData_67(String data_67) {
        this.data_67 = data_67;
    }

    /**
     * データ68を取得
     * @return データ68
     */
    @Column(length = 2000)
    public String getData_68() {
        return this.data_68;
    }

    /**
     * データ68を設定
     * @param data_68 データ68
     */
    public void setData_68(String data_68) {
        this.data_68 = data_68;
    }

    /**
     * データ69を取得
     * @return データ69
     */
    @Column(length = 2000)
    public String getData_69() {
        return this.data_69;
    }

    /**
     * データ69を設定
     * @param data_69 データ69
     */
    public void setData_69(String data_69) {
        this.data_69 = data_69;
    }

    /**
     * データ70を取得
     * @return データ70
     */
    @Column(length = 2000)
    public String getData_70() {
        return this.data_70;
    }

    /**
     * データ70を設定
     * @param data_70 データ70
     */
    public void setData_70(String data_70) {
        this.data_70 = data_70;
    }

    /**
     * データ71を取得
     * @return データ71
     */
    @Column(length = 2000)
    public String getData_71() {
        return this.data_71;
    }

    /**
     * データ71を設定
     * @param data_71 データ71
     */
    public void setData_71(String data_71) {
        this.data_71 = data_71;
    }

    /**
     * データ72を取得
     * @return データ72
     */
    @Column(length = 2000)
    public String getData_72() {
        return this.data_72;
    }

    /**
     * データ72を設定
     * @param data_72 データ72
     */
    public void setData_72(String data_72) {
        this.data_72 = data_72;
    }

    /**
     * データ73を取得
     * @return データ73
     */
    @Column(length = 2000)
    public String getData_73() {
        return this.data_73;
    }

    /**
     * データ73を設定
     * @param data_73 データ73
     */
    public void setData_73(String data_73) {
        this.data_73 = data_73;
    }

    /**
     * データ74を取得
     * @return データ74
     */
    @Column(length = 2000)
    public String getData_74() {
        return this.data_74;
    }

    /**
     * データ74を設定
     * @param data_74 データ74
     */
    public void setData_74(String data_74) {
        this.data_74 = data_74;
    }

    /**
     * データ75を取得
     * @return データ75
     */
    @Column(length = 2000)
    public String getData_75() {
        return this.data_75;
    }

    /**
     * データ75を設定
     * @param data_75 データ75
     */
    public void setData_75(String data_75) {
        this.data_75 = data_75;
    }

    /**
     * データ76を取得
     * @return データ76
     */
    @Column(length = 2000)
    public String getData_76() {
        return this.data_76;
    }

    /**
     * データ76を設定
     * @param data_76 データ76
     */
    public void setData_76(String data_76) {
        this.data_76 = data_76;
    }

    /**
     * データ77を取得
     * @return データ77
     */
    @Column(length = 2000)
    public String getData_77() {
        return this.data_77;
    }

    /**
     * データ77を設定
     * @param data_77 データ77
     */
    public void setData_77(String data_77) {
        this.data_77 = data_77;
    }

    /**
     * データ78を取得
     * @return データ78
     */
    @Column(length = 2000)
    public String getData_78() {
        return this.data_78;
    }

    /**
     * データ78を設定
     * @param data_78 データ78
     */
    public void setData_78(String data_78) {
        this.data_78 = data_78;
    }

    /**
     * データ79を取得
     * @return データ79
     */
    @Column(length = 2000)
    public String getData_79() {
        return this.data_79;
    }

    /**
     * データ79を設定
     * @param data_79 データ79
     */
    public void setData_79(String data_79) {
        this.data_79 = data_79;
    }

    /**
     * データ80を取得
     * @return データ80
     */
    @Column(length = 2000)
    public String getData_80() {
        return this.data_80;
    }

    /**
     * データ80を設定
     * @param data_80 データ80
     */
    public void setData_80(String data_80) {
        this.data_80 = data_80;
    }

    /**
     * データ81を取得
     * @return データ81
     */
    @Column(length = 2000)
    public String getData_81() {
        return this.data_81;
    }

    /**
     * データ81を設定
     * @param data_81 データ81
     */
    public void setData_81(String data_81) {
        this.data_81 = data_81;
    }

    /**
     * データ82を取得
     * @return データ82
     */
    @Column(length = 2000)
    public String getData_82() {
        return this.data_82;
    }

    /**
     * データ82を設定
     * @param data_82 データ82
     */
    public void setData_82(String data_82) {
        this.data_82 = data_82;
    }

    /**
     * データ83を取得
     * @return データ83
     */
    @Column(length = 2000)
    public String getData_83() {
        return this.data_83;
    }

    /**
     * データ83を設定
     * @param data_83 データ83
     */
    public void setData_83(String data_83) {
        this.data_83 = data_83;
    }

    /**
     * データ84を取得
     * @return データ84
     */
    @Column(length = 2000)
    public String getData_84() {
        return this.data_84;
    }

    /**
     * データ84を設定
     * @param data_84 データ84
     */
    public void setData_84(String data_84) {
        this.data_84 = data_84;
    }

    /**
     * データ85を取得
     * @return データ85
     */
    @Column(length = 2000)
    public String getData_85() {
        return this.data_85;
    }

    /**
     * データ85を設定
     * @param data_85 データ85
     */
    public void setData_85(String data_85) {
        this.data_85 = data_85;
    }

    /**
     * データ86を取得
     * @return データ86
     */
    @Column(length = 2000)
    public String getData_86() {
        return this.data_86;
    }

    /**
     * データ86を設定
     * @param data_86 データ86
     */
    public void setData_86(String data_86) {
        this.data_86 = data_86;
    }

    /**
     * データ87を取得
     * @return データ87
     */
    @Column(length = 2000)
    public String getData_87() {
        return this.data_87;
    }

    /**
     * データ87を設定
     * @param data_87 データ87
     */
    public void setData_87(String data_87) {
        this.data_87 = data_87;
    }

    /**
     * データ88を取得
     * @return データ88
     */
    @Column(length = 2000)
    public String getData_88() {
        return this.data_88;
    }

    /**
     * データ88を設定
     * @param data_88 データ88
     */
    public void setData_88(String data_88) {
        this.data_88 = data_88;
    }

    /**
     * データ89を取得
     * @return データ89
     */
    @Column(length = 2000)
    public String getData_89() {
        return this.data_89;
    }

    /**
     * データ89を設定
     * @param data_89 データ89
     */
    public void setData_89(String data_89) {
        this.data_89 = data_89;
    }

    /**
     * データ90を取得
     * @return データ90
     */
    @Column(length = 2000)
    public String getData_90() {
        return this.data_90;
    }

    /**
     * データ90を設定
     * @param data_90 データ90
     */
    public void setData_90(String data_90) {
        this.data_90 = data_90;
    }

    /**
     * データ91を取得
     * @return データ91
     */
    @Column(length = 2000)
    public String getData_91() {
        return this.data_91;
    }

    /**
     * データ91を設定
     * @param data_91 データ91
     */
    public void setData_91(String data_91) {
        this.data_91 = data_91;
    }

    /**
     * データ92を取得
     * @return データ92
     */
    @Column(length = 2000)
    public String getData_92() {
        return this.data_92;
    }

    /**
     * データ92を設定
     * @param data_92 データ92
     */
    public void setData_92(String data_92) {
        this.data_92 = data_92;
    }

    /**
     * データ93を取得
     * @return データ93
     */
    @Column(length = 2000)
    public String getData_93() {
        return this.data_93;
    }

    /**
     * データ93を設定
     * @param data_93 データ93
     */
    public void setData_93(String data_93) {
        this.data_93 = data_93;
    }

    /**
     * データ94を取得
     * @return データ94
     */
    @Column(length = 2000)
    public String getData_94() {
        return this.data_94;
    }

    /**
     * データ94を設定
     * @param data_94 データ94
     */
    public void setData_94(String data_94) {
        this.data_94 = data_94;
    }

    /**
     * データ95を取得
     * @return データ95
     */
    @Column(length = 2000)
    public String getData_95() {
        return this.data_95;
    }

    /**
     * データ95を設定
     * @param data_95 データ95
     */
    public void setData_95(String data_95) {
        this.data_95 = data_95;
    }

    /**
     * データ96を取得
     * @return データ96
     */
    @Column(length = 2000)
    public String getData_96() {
        return this.data_96;
    }

    /**
     * データ96を設定
     * @param data_96 データ96
     */
    public void setData_96(String data_96) {
        this.data_96 = data_96;
    }

    /**
     * データ97を取得
     * @return データ97
     */
    @Column(length = 2000)
    public String getData_97() {
        return this.data_97;
    }

    /**
     * データ97を設定
     * @param data_97 データ97
     */
    public void setData_97(String data_97) {
        this.data_97 = data_97;
    }

    /**
     * データ98を取得
     * @return データ98
     */
    @Column(length = 2000)
    public String getData_98() {
        return this.data_98;
    }

    /**
     * データ98を設定
     * @param data_98 データ98
     */
    public void setData_98(String data_98) {
        this.data_98 = data_98;
    }

    /**
     * データ99を取得
     * @return データ99
     */
    @Column(length = 2000)
    public String getData_99() {
        return this.data_99;
    }

    /**
     * データ99を設定
     * @param data_99 データ99
     */
    public void setData_99(String data_99) {
        this.data_99 = data_99;
    }

    /**
     * データ100を取得
     * @return データ100
     */
    @Column(length = 2000)
    public String getData_100() {
        return this.data_100;
    }

    /**
     * データ100を設定
     * @param data_100 データ100
     */
    public void setData_100(String data_100) {
        this.data_100 = data_100;
    }

    /**
     * データ101を取得
     * @return データ101
     */
    @Column(length = 2000)
    public String getData_101() {
        return this.data_101;
    }

    /**
     * データ101を設定
     * @param data_101 データ101
     */
    public void setData_101(String data_101) {
        this.data_101 = data_101;
    }

    /**
     * データ102を取得
     * @return データ102
     */
    @Column(length = 2000)
    public String getData_102() {
        return this.data_102;
    }

    /**
     * データ102を設定
     * @param data_102 データ102
     */
    public void setData_102(String data_102) {
        this.data_102 = data_102;
    }

    /**
     * データ103を取得
     * @return データ103
     */
    @Column(length = 2000)
    public String getData_103() {
        return this.data_103;
    }

    /**
     * データ103を設定
     * @param data_103 データ103
     */
    public void setData_103(String data_103) {
        this.data_103 = data_103;
    }

    /**
     * データ104を取得
     * @return データ104
     */
    @Column(length = 2000)
    public String getData_104() {
        return this.data_104;
    }

    /**
     * データ104を設定
     * @param data_104 データ104
     */
    public void setData_104(String data_104) {
        this.data_104 = data_104;
    }

    /**
     * データ105を取得
     * @return データ105
     */
    @Column(length = 2000)
    public String getData_105() {
        return this.data_105;
    }

    /**
     * データ105を設定
     * @param data_105 データ105
     */
    public void setData_105(String data_105) {
        this.data_105 = data_105;
    }

    /**
     * データ106を取得
     * @return データ106
     */
    @Column(length = 2000)
    public String getData_106() {
        return this.data_106;
    }

    /**
     * データ106を設定
     * @param data_106 データ106
     */
    public void setData_106(String data_106) {
        this.data_106 = data_106;
    }

    /**
     * データ107を取得
     * @return データ107
     */
    @Column(length = 2000)
    public String getData_107() {
        return this.data_107;
    }

    /**
     * データ107を設定
     * @param data_107 データ107
     */
    public void setData_107(String data_107) {
        this.data_107 = data_107;
    }

    /**
     * データ108を取得
     * @return データ108
     */
    @Column(length = 2000)
    public String getData_108() {
        return this.data_108;
    }

    /**
     * データ108を設定
     * @param data_108 データ108
     */
    public void setData_108(String data_108) {
        this.data_108 = data_108;
    }

    /**
     * データ109を取得
     * @return データ109
     */
    @Column(length = 2000)
    public String getData_109() {
        return this.data_109;
    }

    /**
     * データ109を設定
     * @param data_109 データ109
     */
    public void setData_109(String data_109) {
        this.data_109 = data_109;
    }

    /**
     * データ110を取得
     * @return データ110
     */
    @Column(length = 2000)
    public String getData_110() {
        return this.data_110;
    }

    /**
     * データ110を設定
     * @param data_110 データ110
     */
    public void setData_110(String data_110) {
        this.data_110 = data_110;
    }

    /**
     * データ111を取得
     * @return データ111
     */
    @Column(length = 2000)
    public String getData_111() {
        return this.data_111;
    }

    /**
     * データ111を設定
     * @param data_111 データ111
     */
    public void setData_111(String data_111) {
        this.data_111 = data_111;
    }

    /**
     * データ112を取得
     * @return データ112
     */
    @Column(length = 2000)
    public String getData_112() {
        return this.data_112;
    }

    /**
     * データ112を設定
     * @param data_112 データ112
     */
    public void setData_112(String data_112) {
        this.data_112 = data_112;
    }

    /**
     * データ113を取得
     * @return データ113
     */
    @Column(length = 2000)
    public String getData_113() {
        return this.data_113;
    }

    /**
     * データ113を設定
     * @param data_113 データ113
     */
    public void setData_113(String data_113) {
        this.data_113 = data_113;
    }

    /**
     * データ114を取得
     * @return データ114
     */
    @Column(length = 2000)
    public String getData_114() {
        return this.data_114;
    }

    /**
     * データ114を設定
     * @param data_114 データ114
     */
    public void setData_114(String data_114) {
        this.data_114 = data_114;
    }

    /**
     * データ115を取得
     * @return データ115
     */
    @Column(length = 2000)
    public String getData_115() {
        return this.data_115;
    }

    /**
     * データ115を設定
     * @param data_115 データ115
     */
    public void setData_115(String data_115) {
        this.data_115 = data_115;
    }

    /**
     * データ116を取得
     * @return データ116
     */
    @Column(length = 2000)
    public String getData_116() {
        return this.data_116;
    }

    /**
     * データ116を設定
     * @param data_116 データ116
     */
    public void setData_116(String data_116) {
        this.data_116 = data_116;
    }

    /**
     * データ117を取得
     * @return データ117
     */
    @Column(length = 2000)
    public String getData_117() {
        return this.data_117;
    }

    /**
     * データ117を設定
     * @param data_117 データ117
     */
    public void setData_117(String data_117) {
        this.data_117 = data_117;
    }

    /**
     * データ118を取得
     * @return データ118
     */
    @Column(length = 2000)
    public String getData_118() {
        return this.data_118;
    }

    /**
     * データ118を設定
     * @param data_118 データ118
     */
    public void setData_118(String data_118) {
        this.data_118 = data_118;
    }

    /**
     * データ119を取得
     * @return データ119
     */
    @Column(length = 2000)
    public String getData_119() {
        return this.data_119;
    }

    /**
     * データ119を設定
     * @param data_119 データ119
     */
    public void setData_119(String data_119) {
        this.data_119 = data_119;
    }

    /**
     * データ120を取得
     * @return データ120
     */
    @Column(length = 2000)
    public String getData_120() {
        return this.data_120;
    }

    /**
     * データ120を設定
     * @param data_120 データ120
     */
    public void setData_120(String data_120) {
        this.data_120 = data_120;
    }

    /**
     * データ121を取得
     * @return データ121
     */
    @Column(length = 2000)
    public String getData_121() {
        return this.data_121;
    }

    /**
     * データ121を設定
     * @param data_121 データ121
     */
    public void setData_121(String data_121) {
        this.data_121 = data_121;
    }

    /**
     * データ122を取得
     * @return データ122
     */
    @Column(length = 2000)
    public String getData_122() {
        return this.data_122;
    }

    /**
     * データ122を設定
     * @param data_122 データ122
     */
    public void setData_122(String data_122) {
        this.data_122 = data_122;
    }

    /**
     * データ123を取得
     * @return データ123
     */
    @Column(length = 2000)
    public String getData_123() {
        return this.data_123;
    }

    /**
     * データ123を設定
     * @param data_123 データ123
     */
    public void setData_123(String data_123) {
        this.data_123 = data_123;
    }

    /**
     * データ124を取得
     * @return データ124
     */
    @Column(length = 2000)
    public String getData_124() {
        return this.data_124;
    }

    /**
     * データ124を設定
     * @param data_124 データ124
     */
    public void setData_124(String data_124) {
        this.data_124 = data_124;
    }

    /**
     * データ125を取得
     * @return データ125
     */
    @Column(length = 2000)
    public String getData_125() {
        return this.data_125;
    }

    /**
     * データ125を設定
     * @param data_125 データ125
     */
    public void setData_125(String data_125) {
        this.data_125 = data_125;
    }

    /**
     * データ126を取得
     * @return データ126
     */
    @Column(length = 2000)
    public String getData_126() {
        return this.data_126;
    }

    /**
     * データ126を設定
     * @param data_126 データ126
     */
    public void setData_126(String data_126) {
        this.data_126 = data_126;
    }

    /**
     * データ127を取得
     * @return データ127
     */
    @Column(length = 2000)
    public String getData_127() {
        return this.data_127;
    }

    /**
     * データ127を設定
     * @param data_127 データ127
     */
    public void setData_127(String data_127) {
        this.data_127 = data_127;
    }

    /**
     * データ128を取得
     * @return データ128
     */
    @Column(length = 2000)
    public String getData_128() {
        return this.data_128;
    }

    /**
     * データ128を設定
     * @param data_128 データ128
     */
    public void setData_128(String data_128) {
        this.data_128 = data_128;
    }

    /**
     * データ129を取得
     * @return データ129
     */
    @Column(length = 2000)
    public String getData_129() {
        return this.data_129;
    }

    /**
     * データ129を設定
     * @param data_129 データ129
     */
    public void setData_129(String data_129) {
        this.data_129 = data_129;
    }

    /**
     * データ130を取得
     * @return データ130
     */
    @Column(length = 2000)
    public String getData_130() {
        return this.data_130;
    }

    /**
     * データ130を設定
     * @param data_130 データ130
     */
    public void setData_130(String data_130) {
        this.data_130 = data_130;
    }

    /**
     * データ131を取得
     * @return データ131
     */
    @Column(length = 2000)
    public String getData_131() {
        return this.data_131;
    }

    /**
     * データ131を設定
     * @param data_131 データ131
     */
    public void setData_131(String data_131) {
        this.data_131 = data_131;
    }

    /**
     * データ132を取得
     * @return データ132
     */
    @Column(length = 2000)
    public String getData_132() {
        return this.data_132;
    }

    /**
     * データ132を設定
     * @param data_132 データ132
     */
    public void setData_132(String data_132) {
        this.data_132 = data_132;
    }

    /**
     * データ133を取得
     * @return データ133
     */
    @Column(length = 2000)
    public String getData_133() {
        return this.data_133;
    }

    /**
     * データ133を設定
     * @param data_133 データ133
     */
    public void setData_133(String data_133) {
        this.data_133 = data_133;
    }

    /**
     * データ134を取得
     * @return データ134
     */
    @Column(length = 2000)
    public String getData_134() {
        return this.data_134;
    }

    /**
     * データ134を設定
     * @param data_134 データ134
     */
    public void setData_134(String data_134) {
        this.data_134 = data_134;
    }

    /**
     * データ135を取得
     * @return データ135
     */
    @Column(length = 2000)
    public String getData_135() {
        return this.data_135;
    }

    /**
     * データ135を設定
     * @param data_135 データ135
     */
    public void setData_135(String data_135) {
        this.data_135 = data_135;
    }

    /**
     * データ136を取得
     * @return データ136
     */
    @Column(length = 2000)
    public String getData_136() {
        return this.data_136;
    }

    /**
     * データ136を設定
     * @param data_136 データ136
     */
    public void setData_136(String data_136) {
        this.data_136 = data_136;
    }

    /**
     * データ137を取得
     * @return データ137
     */
    @Column(length = 2000)
    public String getData_137() {
        return this.data_137;
    }

    /**
     * データ137を設定
     * @param data_137 データ137
     */
    public void setData_137(String data_137) {
        this.data_137 = data_137;
    }

    /**
     * データ138を取得
     * @return データ138
     */
    @Column(length = 2000)
    public String getData_138() {
        return this.data_138;
    }

    /**
     * データ138を設定
     * @param data_138 データ138
     */
    public void setData_138(String data_138) {
        this.data_138 = data_138;
    }

    /**
     * データ139を取得
     * @return データ139
     */
    @Column(length = 2000)
    public String getData_139() {
        return this.data_139;
    }

    /**
     * データ139を設定
     * @param data_139 データ139
     */
    public void setData_139(String data_139) {
        this.data_139 = data_139;
    }

    /**
     * データ140を取得
     * @return データ140
     */
    @Column(length = 2000)
    public String getData_140() {
        return this.data_140;
    }

    /**
     * データ140を設定
     * @param data_140 データ140
     */
    public void setData_140(String data_140) {
        this.data_140 = data_140;
    }

    /**
     * データ141を取得
     * @return データ141
     */
    @Column(length = 2000)
    public String getData_141() {
        return this.data_141;
    }

    /**
     * データ141を設定
     * @param data_141 データ141
     */
    public void setData_141(String data_141) {
        this.data_141 = data_141;
    }

    /**
     * データ142を取得
     * @return データ142
     */
    @Column(length = 2000)
    public String getData_142() {
        return this.data_142;
    }

    /**
     * データ142を設定
     * @param data_142 データ142
     */
    public void setData_142(String data_142) {
        this.data_142 = data_142;
    }

    /**
     * データ143を取得
     * @return データ143
     */
    @Column(length = 2000)
    public String getData_143() {
        return this.data_143;
    }

    /**
     * データ143を設定
     * @param data_143 データ143
     */
    public void setData_143(String data_143) {
        this.data_143 = data_143;
    }

    /**
     * データ144を取得
     * @return データ144
     */
    @Column(length = 2000)
    public String getData_144() {
        return this.data_144;
    }

    /**
     * データ144を設定
     * @param data_144 データ144
     */
    public void setData_144(String data_144) {
        this.data_144 = data_144;
    }

    /**
     * データ145を取得
     * @return データ145
     */
    @Column(length = 2000)
    public String getData_145() {
        return this.data_145;
    }

    /**
     * データ145を設定
     * @param data_145 データ145
     */
    public void setData_145(String data_145) {
        this.data_145 = data_145;
    }

    /**
     * データ146を取得
     * @return データ146
     */
    @Column(length = 2000)
    public String getData_146() {
        return this.data_146;
    }

    /**
     * データ146を設定
     * @param data_146 データ146
     */
    public void setData_146(String data_146) {
        this.data_146 = data_146;
    }

    /**
     * データ147を取得
     * @return データ147
     */
    @Column(length = 2000)
    public String getData_147() {
        return this.data_147;
    }

    /**
     * データ147を設定
     * @param data_147 データ147
     */
    public void setData_147(String data_147) {
        this.data_147 = data_147;
    }

    /**
     * データ148を取得
     * @return データ148
     */
    @Column(length = 2000)
    public String getData_148() {
        return this.data_148;
    }

    /**
     * データ148を設定
     * @param data_148 データ148
     */
    public void setData_148(String data_148) {
        this.data_148 = data_148;
    }

    /**
     * データ149を取得
     * @return データ149
     */
    @Column(length = 2000)
    public String getData_149() {
        return this.data_149;
    }

    /**
     * データ149を設定
     * @param data_149 データ149
     */
    public void setData_149(String data_149) {
        this.data_149 = data_149;
    }

    /**
     * データ150を取得
     * @return データ150
     */
    @Column(length = 2000)
    public String getData_150() {
        return this.data_150;
    }

    /**
     * データ150を設定
     * @param data_150 データ150
     */
    public void setData_150(String data_150) {
        this.data_150 = data_150;
    }

}
