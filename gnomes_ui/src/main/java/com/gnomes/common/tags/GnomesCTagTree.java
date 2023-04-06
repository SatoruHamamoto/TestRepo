package com.gnomes.common.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnomes.common.data.TreeData;
import com.gnomes.common.data.TreeSelectInfo;

/**
 * ツリー カスタムタグ
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/01/17 YJP/I.Shibasaka           初版
 * R0.01.01 2018/09/28 YJP/S.Hosokawa            JSON形式に変更
 * R0.01.02 2018/11/07 YJP/A.Oomori              選択中のツリー項目にクラスを付与
 *                                               jquery.treeview適用スクリプトを追加出力
 *                                               ツリー項目のOnclick内容を修正
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesCTagTree extends GnomesCTagBase {

    /** 辞書：ツリーID */
    private static final String INFO_TREE_ID = "tree_id";

    /** ツリーデータ定義Bean */
    private static final String INFO_TREE_DATA_BEAN = "tree_data_bean";

    /** 辞書：スタイルシート追加クラス */
    private static final String INFO_ADD_STYLE = "add_style";

    /** 辞書：選択・開閉項目情報参照Bean */
    private static final String INFO_TREE_NODE_INFO_BEAN = "tree_node_info_bean";

    /** ツリーコマンド実行フォーマット */
    private static final String COMMAND_SCRIPT_FORMAT_TREE = "treeCommandSubmit(this.id, \'%s\');";

    /** 辞書ID */
    private String dictId;

    /** bean */
    private Object bean;

    /** ツリー選択項目ID */
    private String treeSelectedId;

    /** ツリー選択項目ID格納INPUTタグ名 */
    private String treeSelectedIdInputName;

    /** 選択・開閉項目情報参照Bean名 */
    private String treeNodeInfoBeanName;

	/**
     * 辞書IDを取得
     * @return dictId
     */
    public String getDictId() {
        return dictId;
    }

    /**
     * 辞書IDを設定
     * @param dictId 辞書ID
     */
    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    /**
     * Bean取得
     * @return bean
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Bean設定
     * @param bean Bean
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * ツリータグ出力
     */
    public int doStartTag() throws JspException {

        JspWriter out = null;

        try {

            out = pageContext.getOut();

            // 辞書取得
            GnomesCTagDictionary dict = getCTagDictionary();
            // ラベル辞書取得
            Map<String,Object> mapInfo = dict.getTreeInfo(this.dictId);

            // 出力元データのクラス
            Class<?> clsBean = bean.getClass();

            // ツリーID
            String id = (String)mapInfo.get(INFO_TREE_ID);

            // ツリー選択項目ID格納INPUTタグ名
            treeSelectedIdInputName = id + "_SelectedNodeId";

            // スタイルシート追加
            String add_style = (String)mapInfo.get(INFO_ADD_STYLE);
            if (add_style == null) {
                add_style ="";
            }

            //R1.02追加（接続領域ごと操作可否で領域が違うと非活性になる)
            String inactive = judgeAndSetActivityInClass(mapInfo);

            // 固定
            out.print("<ul " + inactive + " id=\"" + id + "\" class=\"treeview " +add_style + "\" >\n");

            // 選択・開閉項目情報
            String treeNodeInfoJson = "";
            // ツリーデータ定義Bean
            String treeDataBeanName = (String)mapInfo.get(INFO_TREE_DATA_BEAN);
            if (!StringUtil.isNullOrEmpty(treeDataBeanName)) {
                TreeData treeData = (TreeData)getData(clsBean, bean, treeDataBeanName);
                // ツリーデータが存在する場合
                if (treeData != null && treeData.getTreeList() != null && treeData.getTreeList().size() > 0) {

                    List<TreeData> treeDataList = treeData.getTreeList();

                    // 選択・開閉項目情報を取得
                    treeNodeInfoBeanName = (String)mapInfo.get(INFO_TREE_NODE_INFO_BEAN);

                    // 選択・開閉状態の更新
                    if (!StringUtil.isNullOrEmpty(treeNodeInfoBeanName)) {
                    	treeNodeInfoJson = (String)getData(clsBean, bean, treeNodeInfoBeanName);

                        if (!StringUtil.isNullOrEmpty(treeNodeInfoJson)) {

                        	//JSONを取得
                        	ObjectMapper mapper = new ObjectMapper();
                        	TreeSelectInfo dataObject = mapper.readValue(treeNodeInfoJson, TreeSelectInfo.class);
                        	// 閉じている項目ID配列を取得
                        	String[] idList = dataObject.getTreeClosedIdArray();
                        	// 閉じている項目IDが存在する場合
                        	if(idList != null && idList.length > 0){
                        		// ツリーデータ項目の閉じている状態を更新
                        		treeDataList = setTreeDataIsClosed(treeDataList, idList);
                        	}

                        	// ツリー選択項目IDを設定
                        	treeSelectedId = dataObject.getTreeExecuteId();

                        }
                        else {
                        	treeNodeInfoJson = "";
                        }

                    }
                    // ツリー表示
                    outTreeView(out, treeDataList);
                }
            }

            out.print("</ul>\n");

            // ツリー選択項目ID格納INPUT(隠し項目)
            out.print("<input type=\"hidden\" name=\"" + treeSelectedIdInputName + "\" value=\"" + treeSelectedId + "\" />\n");

            // ツリー選択・開閉項目情報
            out.print("<input type=\"hidden\" name=\"" + treeNodeInfoBeanName + "\" value='" + treeNodeInfoJson + "' />\n");

            out.print("<script>\n");
            out.print("$(document).ready(function () {\n");
            // jquery.treeviewの適用、toggle…子データを持つノードが開閉した後に実行する処理
            out.print("  $(\"#" + id + "\").treeview({ \n");
            out.print("    toggle : function(){treeCommandSubmit($('input[name=" + treeSelectedIdInputName + "]').val(), '" + treeNodeInfoBeanName + "');} \n");
            out.print("  });\n");
            out.print("});\n");
            out.print("</script>\n");

        } catch(Exception e) {
            if (out != null) {
                try {
                    out.print(this.getCTagErrorToMenu());
                    out.flush();
                } catch (Exception e1) {
                    throw new JspTagException(e1);
                }
            }

            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    /**
     * ツリー項目出力(Bean定義)
     * @param out 出力先
     * @param treeDataList ツリーデータ
     * @throws Exception 例外
     */
    protected void outTreeView(JspWriter out, List<TreeData> treeDataList) throws Exception {

        String id = "";
        String name = "";
        String onclick = "";
        String isClosedClass = "";
        String isSelectedClass = "";

        List<TreeData> treeDataChildList = new ArrayList<TreeData>();

        // ツリーデータ（親）のデータ数繰り返す
        for (TreeData treeData: treeDataList) {

            id = treeData.getId();
            name = this.getStringEscapeHtml(treeData.getName());

            // 閉じている項目の場合
            if (!StringUtil.isNullOrEmpty(treeData.getIsClosed())) {
                isClosedClass = " class=\"closed\"";
            } else {
                isClosedClass = "";
            }

            // IDがツリー選択項目IDと一致する場合
            if (!StringUtil.isNullOrEmpty(treeSelectedId) && treeSelectedId.equals(id)) {
            	isSelectedClass = " selected";
            } else {
            	isSelectedClass = "";
            }

            treeDataChildList = treeData.getTreeList();

            onclick = "";

            // 子データが存在する場合
            if (treeDataChildList != null && treeDataChildList.size() > 0) {
                if (!StringUtil.isNullOrEmpty(treeData.getCommandId())) {
                	// コマンドIDとツリー選択項目IDを更新
                    onclick = String.format(COMMAND_SET_FORMAT, treeData.getCommandId()) + "$('input[name=" + treeSelectedIdInputName + "]').val(this.id);";
                }
                out.print("<li" + isClosedClass + "><span id=\"" + id + "\" class=\"client-treeview-column folder" + isSelectedClass + "\" onclick=\"" + onclick + "\">" + name + "</span>");
                out.print("<ul>\n");
                outTreeView(out, treeDataChildList);
                out.print("</ul>\n");
            }
            // 子データが存在しない場合
            else {
                if (!StringUtil.isNullOrEmpty(treeData.getCommandId())) {
                	// コマンドIDを更新、ツリーの選択・開閉状態を取得し、コマンド実行。
                    onclick = String.format(COMMAND_SET_FORMAT, treeData.getCommandId()) + String.format(COMMAND_SCRIPT_FORMAT_TREE, treeNodeInfoBeanName);
                }
                out.print("<li><span id=\"" + id + "\" class=\"client-treeview-column" + isSelectedClass + "\" onclick=\"" + onclick + "\">" + name + "</span>");

            }
            out.print("</li>\n");

        }

    }

    /**
     * ツリー項目をIDを元に閉じているかどうかを設定
     * @param treeDataList ツリーデータ
     * @param idList idリスト
     * @return ツリーデータ情報リスト
     * @throws Exception 例外
     */
    protected List<TreeData> setTreeDataIsClosed(List<TreeData> treeDataList, String[] idList) throws Exception {

        String id = "";

        List<TreeData> resultTreeList = new ArrayList<>();

        // ツリーデータ（親）のデータ数繰り返す
        for (TreeData treeData: treeDataList) {

            id = treeData.getId();

            // 閉じている項目の場合
            if (Arrays.asList(idList).contains(id)) {
                treeData.setIsClosed("true");
            } else {
                treeData.setIsClosed("");
            }
            treeDataList = treeData.getTreeList();

            // 子データが存在する場合
            if (treeDataList != null && treeDataList.size() > 0) {

                List<TreeData> data = setTreeDataIsClosed(treeDataList, idList);
                treeData.setTreeList(data);
            }
            resultTreeList.add(treeData);

        }

        return resultTreeList;

    }

    /**
     * 終了処理
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * 解放処理
     */
    public void release() {}

}