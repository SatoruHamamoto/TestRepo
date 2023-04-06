package com.gnomes.common.data;

import java.security.InvalidParameterException;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.picketbox.util.StringUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnomes.common.exception.GnomesException;
import com.gnomes.common.resource.GnomesMessagesConstants;
import com.gnomes.common.util.ConverterUtils;

/**
 * ツリーデータ情報
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/06 YJP/A.Oomori             初版
 * R0.01.01 2018/09/28 YJP/S.Hosokawa           JSON用のAPI追加
 * R0.01.02 2018/10/26 YJP/S.Hamamoto			JavaDoc用に説明を加え、ツリーのアイコンをおけるようにする
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */

/**
 * ツリーデータの1要素を持つクラス。
 * ツリーカスタムタグを使う場合にツリーの親子関係をこのクラスの
 * オブジェクトを親子関係に結びつけて渡すことにより表示および制御を実現する
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TreeData {

	/**
	 * ツリーの項目（ノードや要素とも言う）を一意に識別するためのIDを格納。
	 * フォーマットは用途に応じて、GUIDやナチュラルキーを指定する。
	 * ツリーを選択したときにコマンドが呼ばれるようにする際に、どのツリーの
	 * 項目が選択されたかを識別するために使うので、使う側にとって識別可能な
	 * IDを使うこと。
	 */
	@XmlAttribute
	private String id;

	/**
	 * 表示項目名。
	 * ツリーを表示する際の項目の文字に該当する。
	 */
	@XmlAttribute
	private String name;

	/**
	 * 項目コマンドID
	 * ツリーの要素を選択するとコマンドを呼ぶ。何もしなければ空白またはNULLで良い。
	 * 選択した際にコマンドを呼びたい場合は、コマンドID文字列を指定する。
	 */
	@XmlAttribute
	private String commandId;

	/**
	 * 項目OK状態。
	 * OKマークの付いたアイコンが表示項目名の左位置に表示される。
	 * 空白またはnull以外（何か文字を入れること）でアイコンが表示される
	 */
	@XmlAttribute
	private String ok;

	/**
	 * 表示項目名の左位置にアイコンを表示するのに、okプロパティだとOKマークしか表示されないが
	 * CSSにあらかじめアイコンのビットマップを定義し、そのクラス名をここで指定すると
	 * アイコンが表示される。
	 * okプロパティ定義は排他的になり、okプロパティが優先。値が空白、nullでないと有効にならない。
	 */
	@XmlAttribute
	private String iconDisplayClass;

	/**
	 * ツリーの閉じている状態
	 * この項目の子孫が定義されており、かつ表示時に閉じている様に表現する際は
	 * 本プロパティを"1"にする。nullまたは、空白の場合は、開いた状態で表示される
	 */
	@XmlAttribute
	private String isClosed;

	/**
	 * 子項目のリスト
	 * 子項目を定義する場合このプロパティに追加する。
	 */
	@XmlElementWrapper
    @XmlElement(name = "TreeData")
	private List<TreeData> treeList;

    /**
     * TreeData コンストラクタ
     */
	public TreeData() {

	}

	/**
	 * ツリーの項目（ノードや要素とも言う）を一意に識別するためのIDを取得。
	 * フォーマットは用途に応じて、GUIDやナチュラルキーを指定する。
	 * ツリーを選択したときにコマンドが呼ばれるようにする際に、どのツリーの
	 * 項目が選択されたかを識別するために使うので、使う側にとって識別可能な
	 * IDを使うこと。
	 *
	 * @return 項目ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * ツリーの項目（ノードや要素とも言う）を一意に識別するためのIDを格納。
	 * フォーマットは用途に応じて、GUIDやナチュラルキーを指定する。
	 * ツリーを選択したときにコマンドが呼ばれるようにする際に、どのツリーの
	 * 項目が選択されたかを識別するために使うので、使う側にとって識別可能な
	 * IDを使うこと。
	 *
	 * @param id 項目ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 表示項目名を取得。
	 * ツリーを表示する際の項目の文字に該当する。
	 *
	 * @return 項目表示名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 表示項目名を設定。
	 * ツリーを表示する際の項目の文字に該当する。
	 *
	 * @param name 項目表示名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 項目コマンドIDの取得
	 * ツリーの要素を選択するとコマンドを呼ぶ。何もしなければ空白またはNULLで良い。
	 * 選択した際にコマンドを呼びたい場合は、コマンドID文字列を指定する。
	 *
	 * @return 項目コマンドID
	 */
	public String getCommandId() {
		return commandId;
	}

	/**
	 * 項目コマンドIDの設定
	 * ツリーの要素を選択するとコマンドを呼ぶ。何もしなければ空白またはNULLで良い。
	 * 選択した際にコマンドを呼びたい場合は、コマンドID文字列を指定する。
	 *
	 * @param commandId 項目コマンドID
	 */
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	/**
	 * 項目OK状態の取得
	 * OKマークの付いたアイコンが表示項目名の左位置に表示される。
	 * 空白またはnull以外（何か文字を入れること）でアイコンが表示される
	 * @return 項目OK状態
	 */
	public String getOk() {
		return ok;
	}

	/**
	 * 項目OK状態の設定
	 * OKマークの付いたアイコンが表示項目名の左位置に表示される。
	 * 空白またはnull以外（何か文字を入れること）でアイコンが表示される
	 *
	 * @param ok 項目OK状態
	 */
	public void setOk(String ok) {
		this.ok = ok;
	}

	/**
	 * CSSに定義したアイコンビットマップのクラス名文字列を取得。
	 * 表示項目名の左位置にアイコンを表示するのに、okプロパティだとOKマークしか表示されないが
	 * CSSにあらかじめアイコンのビットマップを定義し、そのクラス名をここで指定すると
	 * アイコンが表示される。
	 * okプロパティ定義は排他的になり、okプロパティが優先。値が空白、nullでないと有効にならない。
	 *
	 * @return CSSに定義したアイコンビットマップのクラス名文字列
	 */
	public String getIconDisplayClass(){
		return this.iconDisplayClass;
	}

	/**
	 * CSSに定義したアイコンビットマップのクラス名文字列を設定。
	 * 表示項目名の左位置にアイコンを表示するのに、okプロパティだとOKマークしか表示されないが
	 * CSSにあらかじめアイコンのビットマップを定義し、そのクラス名をここで指定すると
	 * アイコンが表示される。
	 * okプロパティ定義は排他的になり、okプロパティが優先。値が空白、nullでないと有効にならない。
	 *
	 * @param iconDisplayClass CSSに定義したアイコンビットマップのクラス名文字列
	 */
	public void setIconDisplayClass(String iconDisplayClass){
		this.iconDisplayClass = iconDisplayClass;
	}



	/**
	 * ツリーの閉じている状態の取得。
	 * この項目の子孫が定義されており、かつ表示時に閉じている様に表現する際は
	 * 本プロパティをnullまたは、空白の以外の文字を入れる。それ以外は開いた状態で表示される
	 *
	 * @return 閉じているかどうか
	 */
	public String getIsClosed() {
		return isClosed;
	}

	/**
	 * ツリーの閉じている状態の設定。
	 * この項目の子孫が定義されており、かつ表示時に閉じている様に表現する際は
	 * 本プロパティをnullまたは、空白の以外の文字を入れる。それ以外は開いた状態で表示される
	 *
	 * @param isClosed 閉じているかどうか
	 */
	public void setIsClosed(String isClosed) {
		this.isClosed = isClosed;
	}

	/**
	 * 子項目のリストを取得。
	 * 子項目を定義する場合このプロパティに追加する。
	 *
	 * @return ツリーデータ（子）
	 */
	public List<TreeData> getTreeList() {
		return treeList;
	}

	/**
	 * 子項目のリストを設定。
	 * 子項目を定義する場合このプロパティに追加する。
	 *
	 * @param treeList ツリーデータ（子）
	 */
	public void setTreeList(List<TreeData> treeList) {
		this.treeList = treeList;
	}


	/**
	 * クリックしたツリー項目のIDをJSON文字列から取得。
	 * 画面からFormBean経由でツリーの選択状態および、閉じている項目を1つのJSON文字に格納されたものを
	 * 引数で渡すことにより、選択されたツリーの項目（ノードや要素とも言う）を一意に識別するためのIDを返す。
	 *
	 * @param JSONString　ツリーIDのJSOM文字列
	 * @return 実行するコマンドID
	 */
	public static String parseTreeExecuteId(String JSONString) {

		String executeId = null;

		try {
			//JSON文字列からJSONオブジェクトを作成
			ObjectMapper mapper = new ObjectMapper();
			TreeSelectInfo dataObject;
			if (!StringUtil.isNullOrEmpty(JSONString)) {
				dataObject = mapper.readValue(JSONString, TreeSelectInfo.class);
				executeId = dataObject.getTreeExecuteId();
			}
		} catch (Exception e) {
			// (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
			GnomesException ex = new GnomesException(e, GnomesMessagesConstants.ME01_0001);
			throw ex;

		}

		return executeId;
	}
	/**
	 * クリックしたツリー項目のIDをJSON文字列から、選択されたノードIDを
	 * クリアして（選択状態を解除する）また、JSON文字列に戻す
	 *
	 * @param JSONString ツリー項目のIDが入っているJSON文字列
	 * @return　選択されたツリーのIDが消えたJSON文字列
	 */
	public static String clearTreeExecuteId(String JSONString)
	{
	    //一度オブジェクト化する
        try {
            //JSON文字列からJSONオブジェクトを作成
            ObjectMapper mapper = new ObjectMapper();
            TreeSelectInfo dataObject;
            if (!StringUtil.isNullOrEmpty(JSONString)) {
                dataObject = mapper.readValue(JSONString, TreeSelectInfo.class);
                //データの選択IDをクリアする
                dataObject.setTreeExecuteId("");

                //ClosedIdArrayがnullの場合は空のString[]を作る
                if(dataObject.getTreeClosedIdArray() == null){
                    dataObject.setTreeClosedIdArray(new String[0]);
                }

                //そのデータをJSON化する
                return ConverterUtils.getJson(dataObject);
            }
            else {
                //NULLの場合は空の情報を作成して返却する
                dataObject = new TreeSelectInfo();
                dataObject.setTreeExecuteId("");
                dataObject.setTreeClosedIdArray(new String[0]);
                return ConverterUtils.getJson(dataObject);

            }
        } catch (Exception e) {
            // (ME01.0001) アプリケーションエラーが発生しました。 詳細についてはログを確認してください。
            GnomesException ex = new GnomesException(e, GnomesMessagesConstants.ME01_0001);
            throw ex;
        }
	}
}
