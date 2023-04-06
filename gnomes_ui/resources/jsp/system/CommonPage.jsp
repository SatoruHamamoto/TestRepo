<%@ page contentType = "text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/gnomes_tag" prefix="gnomes" %>

<script type="text/javascript" src="./js/gnomes/js.cookie.js"></script>

  <!-- 共通設定項目 -->
    <!-- ログインユーザID -->
    <gnomes:GnomesCTagHidden dictId="CommonSessionLoginUserId" value="${gnomesSessionBean.userId}" />
  
    <!-- ダブル認証者ID -->
	<gnomes:GnomesCTagHidden dictId="CommonCertUserName" value="${SystemFormBean.certUserId}" />

    <!-- ページトークン -->
	<gnomes:GnomesCTagPageToken bean="${BaseFormBean}" />

    <!-- コマンド -->
	<gnomes:GnomesCTagHidden dictId="CommonCommand" />

    <!-- 入力チェックフラグ -->
	<gnomes:GnomesCTagHidden dictId="CommonInputFlag" value="false" />

	<!-- ページングコマンド -->
	<gnomes:GnomesCTagHidden dictId="CommonPagingCommand" />

	<!-- ページングパラメータ -->
	<gnomes:GnomesCTagHidden dictId="CommonPagingParam" />
	
	<!-- 押下ボタンID -->
	<gnomes:GnomesCTagHidden dictId="CommonButtonId" />
	
    <!-- 日付フォーマット（メッセージダイアログ表示時） -->
	<input type="hidden" name="dateFormat" value="<fmt:message bundle="${GnomesResources}" key="YY01.0045"/>" />

	<!-- スクロール位置保持 -->
	<input type="hidden" name="keepScrollPosition" value="${SystemFormBean.keepScrollPosition}" />
	
	<!-- ロケール -->
	<input type="hidden" id="loginUserLocaleId" value="${gnomesSessionBean.localeId}">
		
	<!-- 日付フォーマット（MM/dd/yyyy HH:mm:ss） -->
	<input type="hidden" id="datetimeFormat-yyyyMMddhhmmss" value="<fmt:message bundle="${GnomesResources}" key="YY01.0001"/>" />
	
	<!-- 日付フォーマット（MM/dd/yyyy HH:mm） -->
	<input type="hidden" id="datetimeFormat-yyyyMMddhhmm" value="<fmt:message bundle="${GnomesResources}" key="YY01.0002"/>" />
	
	<!-- 日付フォーマット（MM/dd/yyyy） -->
	<input type="hidden" id="datetimeFormat-yyyyMMdd" value="<fmt:message bundle="${GnomesResources}" key="YY01.0003"/>" />	
		
	<!-- 日付フォーマット（MM/DD/YYYY HH:mm） -->
	<input type="hidden" id="datetimeFormat-YYYYMMDDhhmm" value="<fmt:message bundle="${GnomesResources}" key="YY01.0045"/>" />	
	
	<!-- 日付フォーマット（MM/DD/YYYY） -->
	<input type="hidden" id="datetimeFormat-YYYYMMDD" value="<fmt:message bundle="${GnomesResources}" key="YY01.0073"/>" />
	
	<!-- 日付フォーマット（MM/yyyy） -->
	<input type="hidden" id="datetimeFormat-yyyyMM" value="<fmt:message bundle="${GnomesResources}" key="YY01.0094"/>" />
	
		<!-- 日付フォーマット（MM/YYYY） -->
	<input type="hidden" id="datetimeFormat-YYYYMM" value="<fmt:message bundle="${GnomesResources}" key="YY01.0095"/>" />
	
		<!-- 日付フォーマット（MM/DD/YYYY HH:mm:ss） -->
	<input type="hidden" id="datetimeFormat-YYYYMMDDhhmmss" value="<fmt:message bundle="${GnomesResources}" key="YY01.0096"/>" />
	
	<!-- 日付フォーマット（MM/DD/YYYY HH:00） -->
	<input type="hidden" id="datetimeFormat-YYYYMMDDhh00" value="<fmt:message bundle="${GnomesResources}" key="YY01.0111"/>" />
	
	<!-- 日付フォーマット（MM/DD/YYYY HH:mm:00） -->
	<input type="hidden" id="datetimeFormat-YYYYMMDDhhmm00" value="<fmt:message bundle="${GnomesResources}" key="YY01.0112"/>" />
	
	<!-- カレンダーヘーダフォーマット（MMMM YYYY） -->
	<input type="hidden" id="calenderHeaderFormat-YYYYMM" value="<fmt:message bundle="${GnomesResources}" key="YY01.0097"/>" />

	<!-- ダイアログエリア -->
	<div id="myAnyModal">
	</div>	
	<div id="messageModal">
	</div>
	<div id="myModal">
	</div>
	<div id="myModalMiddle">
	</div>
	<div id="myModalHigh">
	</div>
    <div id="select-pulldown-modal">
    </div>
    <div id="lock-modal">
    </div>
	<div id="change-pass-modal">
	</div>
	<div id="pulldown-table-modal">
	</div>
	
	
	<!-- ダイアログ 固定文字 -->
	<div style="display: none;">
		<!-- 中止 -->
		<p id="msgBoxBtnNameAbort"><fmt:message bundle="${GnomesResources}" key="YY01.0016"/></p>
		<!-- 再試行 -->
		<p id="msgBoxBtnNameRetry"><fmt:message bundle="${GnomesResources}" key="YY01.0017"/></p>
		<!-- 無視 -->
		<p id="msgBoxBtnNameIgnore"><fmt:message bundle="${GnomesResources}" key="YY01.0018"/></p>
		<!-- OK -->
		<p id="msgBoxBtnNameOk"><fmt:message bundle="${GnomesResources}" key="YY01.0019"/></p>
		<!-- Cancel -->
		<p id="msgBoxBtnNameCancel"><fmt:message bundle="${GnomesResources}" key="DI01.0057"/></p>
		<!-- Yes -->
		<p id="msgBoxBtnNameYes"><fmt:message bundle="${GnomesResources}" key="YY01.0021"/></p>
		<!-- No -->
		<p id="msgBoxBtnNameNo"><fmt:message bundle="${GnomesResources}" key="YY01.0022"/></p>
        <!-- ログアウト -->
        <p id="msgBoxBtnNameLogout"><fmt:message bundle="${GnomesResources}" key="DI01.0058"/></p>
        <!-- 詳細 -->
        <p id="msgBoxLabelDetail"><fmt:message bundle="${GnomesResources}" key="DI90.0262"/></p>
		<!-- 発生者： -->
		<p id="msgBoxLabelOccrUserName"><fmt:message bundle="${GnomesResources}" key="DI01.0042"/></p>
		<!-- 発生場所： -->
		<p id="msgBoxLabelOccrHost"><fmt:message bundle="${GnomesResources}" key="DI01.0043"/></p>
		<!-- メッセージ詳細： -->
		<p id="msgBoxLabelmessageDetail"><fmt:message bundle="${GnomesResources}" key="DI01.0044"/></p>
		<!-- 閉じる -->
		<p id="msgBoxBtnClose"><fmt:message bundle="${GnomesResources}" key="YY01.0038"/></p>
		<!-- タイトル：エラー -->
		<p id="msgBoxTitleError"><fmt:message bundle="${GnomesResources}" key="YY01.0007"/></p>
		<!-- タイトル：確認 -->
		<p id="msgBoxTitleConfirm"><fmt:message bundle="${GnomesResources}" key="YY01.0015"/></p>
		<!-- タイトル：警告 -->
		<p id="msgBoxTitleWarning"><fmt:message bundle="${GnomesResources}" key="YY01.0036"/></p>
		<!-- タイトル：情報 -->
		<p id="msgBoxTitleInfo"><fmt:message bundle="${GnomesResources}" key="YY01.0037"/></p>
		<!-- タイトル：メッセージ -->
		<p id="msgBoxTitleMessage"><fmt:message bundle="${GnomesResources}" key="YY01.0006"/></p>
        <!-- ログアウト確認メッセージ -->
        <p id="msgLogoutMessage"><fmt:message bundle="${GnomesMessages}" key="MC01.0002"/></p>
		<!-- サービス通信エラーメッセージ -->
		<p id="msgServiceErrMessage"><fmt:message bundle="${GnomesMessages}" key="ME01.0041"/></p>
		<!-- サービス通信セッションエラーメッセージ -->
		<p id="msgServiceSessionErrMessage"><fmt:message bundle="${GnomesMessages}" key="ME01.0147"/></p>
		<!-- ブックマーク追加 -->
		<p id="msgAddBookmark"><fmt:message bundle="${GnomesResources}" key="YY01.0070"/></p>
		<!-- ブックマーク削除 -->
		<p id="msgDeleteBookmark"><fmt:message bundle="${GnomesResources}" key="YY01.0071"/></p>
		<!-- 認証ダイアログ タイトル -->
        <p id="msgConfirmDialogTitle"><fmt:message bundle="${GnomesResources}" key="DI01.0021"/></p>
        <!-- 認証ダイアログ ユーザID -->
        <p id="msgConfirmDialogUserId"><fmt:message bundle="${GnomesResources}" key="DI01.0008"/></p>
        <!-- 認証ダイアログ パスワード -->
        <p id="msgConfirmDialogPassword"><fmt:message bundle="${GnomesResources}" key="DI01.0009"/></p>
        <!-- 認証ダイアログ 作業者 -->
        <p id="msgConfirmDialogWorkUser"><fmt:message bundle="${GnomesResources}" key="DI01.0073"/></p>
        <!-- 認証ダイアログ 確認者 -->
        <p id="msgConfirmDialogCertUser"><fmt:message bundle="${GnomesResources}" key="DI01.0119"/></p>
        <!-- ロック解除ダイアログ タイトル -->
        <p id="msgLockDialogTitle"><fmt:message bundle="${GnomesResources}" key="DI01.0007"/></p>
		<!-- パスワード変更ダイアログ タイトル -->
		<p id="msgPasswordChangeDialogTitle"><fmt:message bundle="${GnomesResources}" key="DI01.0011"/></p>		
		<!-- パスワード変更ダイアログ メッセージ -->
		<p id="msgPasswordChangeDialogMessage"><fmt:message bundle="${GnomesResources}" key="DI01.0120"/></p>
		<!-- パスワード変更ダイアログ 古いパスワード -->
		<p id="msgPasswordChangeDialogOldPassword"><fmt:message bundle="${GnomesResources}" key="DI01.0121"/></p>
		<!-- パスワード変更ダイアログ 新しいパスワード -->
		<p id="msgPasswordChangeDialogNewPassword"><fmt:message bundle="${GnomesResources}" key="DI01.0122"/></p>
		<!-- パスワード変更ダイアログ パスワードの確認入力 -->
		<p id="msgPasswordChangeDialogConfirmPassword"><fmt:message bundle="${GnomesResources}" key="DI01.0123"/></p>
		<!-- パスワード変更ダイアログ パスワード初期化 -->
		<p id="msgPasswordChangeDialogInitPassword"><fmt:message bundle="${GnomesResources}" key="DI01.0107"/></p>
        <!-- 検索ダイアログ *前 -->
        <p id="msgSearchDialogPast"><fmt:message bundle="${GnomesResources}" key="DI01.0125"/></p>
        <!-- 検索ダイアログ *後 -->
        <p id="msgSearchDialogFuture"><fmt:message bundle="${GnomesResources}" key="DI01.0126"/></p>
        <!-- 検索ダイアログ 日 -->
        <p id="msgSearchDialogDay"><fmt:message bundle="${GnomesResources}" key="DI01.0127"/></p>
        <!-- 検索ダイアログ 曖昧検索 -->
		<p id="msgSearchDialogConditionLike"><fmt:message bundle="${GnomesResources}" key="YY01.0085"/></p>
        <!-- ソートダイアログ 昇順 -->
        <p id="msgSortDialogAsc"><fmt:message bundle="${GnomesResources}" key="DI91.0226"/></p>
        <!-- ソートダイアログ 降順 -->
        <p id="msgSortDialogDesc"><fmt:message bundle="${GnomesResources}" key="DI91.0227"/></p>
        <!-- ソートダイアログ 表示有無が全部無の場合のエラーメッセージ -->
		<p id="msgSortDialogDispCheckMessage"><fmt:message bundle="${GnomesMessages}" key="ME01.0211"/></p>
		<!-- 検索・ソートダイアログ 保存成功メッセージ -->
		<p id="msgDialogSaveSuccessMessage"><fmt:message bundle="${GnomesMessages}" key="MG01.0031"/></p>
		<!-- 検索・ソートダイアログ 初期化成功メッセージ -->
		<p id="msgDialogInitSuccessMessage"><fmt:message bundle="${GnomesMessages}" key="MG01.0032"/></p>
        <!-- 二重チェック警告メッセージ -->
		<p id="msgDoubleCheckMessage"><fmt:message bundle="${GnomesMessages}" key="MV01.0014"/></p>
		<!-- プルダウン選択一覧ダイアログ 選択 -->
		<p id="msgPulldownTableDialogSelect"><fmt:message bundle="${GnomesResources}" key="YY01.0086"/></p>
		<!-- プルダウン選択一覧ダイアログ 項目名 -->
		<p id="msgPulldownTableDialogName"><fmt:message bundle="${GnomesResources}" key="YY01.0087"/></p>
		<!-- プルダウン選択一覧ダイアログ 項目値 -->
		<p id="msgPulldownTableDialogValue"><fmt:message bundle="${GnomesResources}" key="YY01.0088"/></p>
		
	</div>	
	<!-- メッセージビーン 文字 -->
	<div style="display: none;">
		<p id="msgBeanResourceid">${messageBean.resourceid}</p>
		<p id="msgBeanCategoryName">${messageBean.categoryName}</p>
		<p id="msgBeanIconName">${messageBean.iconName}</p>
		<p id="msgBeanOccurDate">${messageBean.occurDate}</p>
		<p id="msgBeanOccrUserName">${messageBean.occrUserName}</p>
		<p id="msgBeanOccrHost">${messageBean.occrHost}</p>
		<p id="msgBeanMessage">${messageBean.message}</p>
		<p id="msgBeanMessageDetail">${messageBean.messageDetail}</p>
		<p id="msgBeanMsgBtnMode">${messageBean.msgBtnMode}</p>
		<p id="msgBeanDefaultBtn">${messageBean.defaultBtn}</p>
		<p id="msgBeanCommand">${messageBean.command}</p>
		<p id="msgBeanDbAreaDiv">${messageBean.dbAreaDiv}</p>
		<p id="msgBeanCancelOnClick">${messageBean.messageCancelOnClick}</p>
		<p id="msgBeanOkOnClick">${messageBean.messageOkOnClick}</p>
		<p id="msgBeanGuidanceMessage">${messageBean.linkInfo[0]}</p>
		<p id="msgBeanLinkURL">${messageBean.linkInfo[1]}</p>
		<p id="msgBeanLinkName">${messageBean.linkInfo[2]}</p>
	</div>


	
	<script>
//<![CDATA[

	// ロック用タイマー
    var lockTimeID = 0;

	// ロック画面中セッション維持タイマー
	var lockDogTimeID = 0;

    // ポップアップメッセージ監視用タイマー
    var watchPeriodForPopupTimeID = 0;
    
    // ロックタイマー時間
    var lockTime = ${gnomesSessionBean.screenLockTimeoutTime} * 1000 * 60;

    // ポップアップメッセージ監視時間
    var watchPeriodForPopupTime = ${gnomesSessionBean.watchPeriodForPopup} * 1000 * 60;

    // 関連画面クリック状況
	localStorage.setItem('otherScreenClick', 0);
	
    localStorage.setItem('otherScreenLock', 0);

    // ロックタイマーのクリア
    function clearLockTimer() {
	   	if (lockTimeID != 0) {
    		clearTimeout(lockTimeID);
    		lockTimeID = 0;
    	}
    }

    // ポップアップメッセージ監視タイマーのクリア
    function clearWatchPeriodForPopupTimer() {
	   	if (watchPeriodForPopupTimeID != 0) {
    		clearTimeout(watchPeriodForPopupTimeID);
    		watchPeriodForPopupTimeID = 0;
    	}
    }

    // ロックタイマー設定
    function setLockTimer() {
	    lockTimeID = setTimeout("lockTimeOut()", lockTime);
	}

    var checkLockStatusTimeID = 0;
    // ロック状態確認タイマー設定
    function setCheckLockStatusTimer() {
    	checkLockStatusTimeID = setTimeout("checkLockStatus()", 600);
	}
    // ロック状態確認タイマーのクリア
    function clearCheckLockStatusTimer() {
	   	if (checkLockStatusTimeID != 0) {
    		clearTimeout(checkLockStatusTimeID);
    		checkLockStatusTimeID = 0;
    	}
    }
    
    // ポップアップメッセージ監視タイマー設定
    function setWatchPeriodForPopupTimer() {
	    watchPeriodForPopupTimeID = setTimeout("getPoupuMessage()", watchPeriodForPopupTime);
	}

	// ポップアップメッセージ更新が可能な状態か判定
	function isDoUpdatePoupuMessage() {
		// ロック中の場合
		if (lockTimeID == 0) {
			return false;
		}

		// myAnyModalを表示している場合
// myAnyModalを表示時は処理しない場合下記処理が必要
//		if ($('#myAnyModal').hasClass('in')) {
//			return false;
//		}
		
		// メッセージドロップダウンが開いている場合
		if ($('#messageDropDown').hasClass('open')) {
			return false;
		}
		
		return true;
	}

	// ポップアップメッセージ監視タイムアウト処理
	function getPoupuMessage() {

		clearWatchPeriodForPopupTimer();

		// ポップアップメッセージ更新が可能でない場合
		if (isDoUpdatePoupuMessage() == false) {
			// ポップアップメッセージ監視タイマー設定
			setWatchPeriodForPopupTimer();
			return;
		}

		// ポップアップメッセージを取得
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: "rest/M01001S001/getPoupuMessage",
			async: true,
			dataType: "json",
			data: formToWatchPeriodForPopupJSON(),
			success: function(data, textStatus, jqXHR){

				// 処理成功の場合
				if(data.isSuccess == 1){

					// ポップアップメッセージ更新が可能の場合
					if (isDoUpdatePoupuMessage()) {
						// データが存在する場合
						if (data.popupMessageInfoList != null && data.popupMessageInfoList.length > 0) {
							// データ更新
							updatePopupMessageDatas(data.popupMessageInfoList);
							makePopupMessageList();
						}
					}

				// 処理失敗の場合
				} else {
					// 処理なし
				}
				// ポップアップメッセージ監視タイマー設定
				setWatchPeriodForPopupTimer();
			},
			error: function(jqXHR, textStatus, errorThrown){
				// 再度タイマー設定
				setWatchPeriodForPopupTimer();
			}
		});
		
	}

	// ポップアップメッセージ情報をJSONデータに格納
	function formToWatchPeriodForPopupJSON() {
	    return JSON.stringify({
	    });
	}

	// ポップアップメッセージリストデータを設定
	function updatePopupMessageDatas(datas) {
	
		var str = "";
	
		for (var i = 0; i < datas.length; i++) {
			var data = datas[i];
			
			// 発生日時
			str = str + "<p name=\"popupMessageInfo_occurDate\">" + data.occurDate + "<p>\n";

			// メッセージno.
			str = str + "<p name=\"popupMessageInfo_messageNo\">" + data.messageNo + "<p>\n";
			
			// 発生者id
			str = str + "<p name=\"popupMessageInfo_occrUserId\">" + data.occrUserId + "<p>\n";

			// 発生者名
			str = str + "<p name=\"popupMessageInfo_occrUserName\">" + data.occrUserName + "<p>\n";
			
			// 発生元コンピュータ名
			str = str + "<p name=\"popupMessageInfo_occrHost\">" + data.occrHost + "<p>\n";

			// 分類
			str = str + "<p name=\"popupMessageInfo_msgType\">" + data.msgType + "<p>\n";

			// 分類(名称)
			str = str + "<p name=\"popupMessageInfo_msgTypeName\">" + data.msgTypeName + "<p>\n";

			// メッセージソース
			str = str + "<p name=\"popupMessageInfo_msgSource\">" + data.msgSource + "<p>\n";

			// メッセージソース(名称)
			str = str + "<p name=\"popupMessageInfo_msgSourceName\">" + data.msgSourceName + "<p>\n";

			// 種別
			str = str + "<p name=\"popupMessageInfo_category\">" + data.category + "<p>\n";

			// 種別(名称)
			str = str + "<p name=\"popupMessageInfo_categoryName\">" + data.categoryName + "<p>\n";

			// メッセージ重要度
			str = str + "<p name=\"popupMessageInfo_msgLevel\">" + data.msgLevel + "<p>\n";

			// メッセージ重要度(名称)
			str = str + "<p name=\"popupMessageInfo_msgLevelName\">" + data.msgLevelName + "<p>\n";

			// メッセージ
			str = str + "<p name=\"popupMessageInfo_message\">" + data.message + "<p>\n";

			// メッセージ詳細
			str = str + "<p name=\"popupMessageInfo_messageDetail\">" + data.messageDetail + "<p>\n";

			// メッセージアイコン名
			str = str + "<p name=\"popupMessageInfo_iconName\">" + data.iconName + "<p>\n";

			// アクセスされた領域
			str = str + "<p name=\"popupMessageInfo_dbAreaDiv\">" + data.dbAreaDiv + "<p>\n";

			if(data.guidanceMessage!=null){
				// ガイダンスメッセージ
				str = str + "<p name=\"popupMessageInfo_guidanceMessage\">" + data.guidanceMessage + "<p>\n";
	
				// リンクURL
				str = str + "<p name=\"popupMessageInfo_linkURL\">" + data.linkURL + "<p>\n";
	
				// リンク名
				str = str + "<p name=\"popupMessageInfo_linkName\">" + data.linkName + "<p>\n";
			}
			else{
				// ガイダンスメッセージ
				str = str + "<p name=\"popupMessageInfo_guidanceMessage\"><p>\n";
	
				// リンクURL
				str = str + "<p name=\"popupMessageInfo_linkURL\"><p>\n";
	
				// リンク名
				str = str + "<p name=\"popupMessageInfo_linkName\"><p>\n";
			}
		}

		$('#popupMessageInfos').html(str);
	}



	// ポップアップメッセージリスト作成
	function makePopupMessageList() {
		var str = "";
		var dataCount = $('p[name=popupMessageInfo_occurDate').length;
	
		for (var i=0 ; i<dataCount ; i++){
			var occurdate = $('p[name=popupMessageInfo_occurDate').eq(i).text();
			//var message = $('p[name=popupMessageInfo_message').eq(i).text();
			var message = $('p[name=popupMessageInfo_message').eq(i).html();
			var iconName = $('p[name=popupMessageInfo_iconName').eq(i).text();

			
			if (i==0) {
				str = "<button class=\"btn btn-default dropdown-toggle common-header-messageList-size\"";
				str = str + "    id=\"dropdownMenu1\" style=\"z-index: 2;\" type=\"button\"";
				str = str + "    data-toggle=\"dropdown\" >";

				str = str + "<table style=\"width: 100%; white-space: nowrap;\">";
				str = str + "<tbody>";
				str = str + "<tr>";
				str = str + "<td class=\"popupmenu-list-topselect popupmenu-list-topselect2\">";
				str = str + "<img style=\"margin-right: 1.0em;\" src=\"./images/gnomes/icons/" + iconName + "\" width=\"24\" height=\"24\"><span style=\"vertical-align: middle;\">" + occurdate + " " + message + "</span><span class=\"caret\">";
				str = str + "</span>";
				str = str + "</td>";
				str = str + "</tr>";
				str = str + "</tbody>";
				str = str + "</table>";
				str = str + "</button>\n";
				str = str + "<ul class=\"dropdown-menu scrollable-menu message-dropdown-menu\" role=\"menu\" aria-labelledby=\"dropdownMenu1\">";
			}

			str = str + "    <li role=\"presentation\"><a class=\"popupmenu-list\" tabindex=\"-1\" role=\"menuitem\"";
			str = str + "        onclick=\"popupMessageSelect(" + i + ")\"> <img style=\"margin-right: 1.0em;\" src=\"./images/gnomes/icons/" + iconName + "\" width=\"24\" height=\"24\"><span style=\"vertical-align: middle;\">" + occurdate + " " + message+ "</span>";
			str = str + "    </a></li>\n";
		}
		if (dataCount > 0) {
			str = str + "</ul>\n";
		}
		$('#messageDropDown').html(str);
		
		// 高さ調整
		setDropdownMenuMaxHeight()

//		$('#dropdownMessageMenu').parent().on("hide.bs.dropdown", function() {
//			return true;
//  	});
		
	}





	// ポップアップメッセージ選択処理
	function popupMessageSelect( key ) {
	
		var title = $('p[name=popupMessageInfo_categoryName]').eq(key).text();
		var occurDate = $('p[name=popupMessageInfo_occurDate]').eq(key).text();
		var iconName = $('p[name=popupMessageInfo_iconName]').eq(key).text();
		var occrUserName = $('p[name=popupMessageInfo_occrUserName]').eq(key).text();
		var occrHost = $('p[name=popupMessageInfo_occrHost]').eq(key).text();
		var message = $('p[name=popupMessageInfo_message').eq(key).html();
		var messageDetail = $('p[name=popupMessageInfo_messageDetail]').eq(key).html();
		var guidanceMessage = $('p[name=popupMessageInfo_guidanceMessage]').eq(key).html();
		var linkURL = $('p[name=popupMessageInfo_linkURL]').eq(key).html();
		var linkName = $('p[name=popupMessageInfo_linkName]').eq(key).html();
		var dbAreaDiv = $('p[name=popupMessageInfo_dbAreaDiv]').eq(key).html();
		var linkInfo = [guidanceMessage,linkURL, linkName];
		// メッセージダイアログを表示
		makeMessageModalBase( 
				title,					//メッセージタイトル
				iconName,               //アイコン名
				occurDate,              //発生日時
				occrUserName,           //操作ユーザ名
				occrHost,               //操作ホスト
				message,                //メッセージ本文
				messageDetail,          //メッセージ詳細
				MesBtnMode_OK,          //ボタンモード
				MesDefaultBtn_Button3,  //デフォルトボタン
				null,                   //コマンド
				linkInfo,               //リンク情報
				null,                   //オープン時詳細を開くかどうか
                null,                   //OKボタン押下時呼び出し関数
                null,                   //Cancelボタン押下呼び出し関数
                null,                   //ボタンID
                ""                      //DB領域
				);
	}



	// ロックタイムアウト処理
	function lockTimeOut() {

		clearLockTimer();

		// スクリーンロック状態の更新
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: "rest/A01001S001/LockSession",
			async: true,
			dataType: "json",
			data: formToLockSessionJSON(),
			success: function(data, textStatus, jqXHR){
				if(!data.message){
                    var isPanecon = false;
                    if(document.body.className.indexOf('common-font-panecon') != -1){
                        isPanecon = true;
                    }
                    localStorage.setItem('otherScreenLock', 1);
                    //ロック状態を定周期でチェック
                    setCheckLockStatusTimer();
                    //ロック中は定周期でセッションを維持する(1分)
                    setWatchPeriodForWatchdogTimer();
                    MakeLockModal(isPanecon);
				}
				else {
					MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				clearWatchPeriodForWatchdogTimer();
				setLockTimer();
			}
		});
	}
	
    //定周期セッション維持をする
    function setWatchPeriodForWatchdogTimer()
    {
	    lockDogTimeID = setTimeout("watchDogForLocking()", 1*1000*60);
    }
    //定周期セッション維持をクリアする
	function clearWatchPeriodForWatchdogTimer()
    {
		if (lockDogTimeID != 0) {
    		clearTimeout(lockDogTimeID);
    		lockDogTimeID = 0;
    	}
    }

    //ロックアウト画面中に定周期でAJAXにアクセスする
    function watchDogForLocking()
    {
        var otherScreenLock = localStorage.getItem('otherScreenLock');
        
        if ( otherScreenLock == 1) {
	        // スクリーンロック状態の更新
	        $.ajax({
	            type: 'POST',
	            contentType: 'application/json',
	            url: "rest/A01001S001/WatchDogSession",
	            async: true,
	            dataType: "json",
	            data: formToLockSessionJSON(),
				beforeSend: function(xhr) {
	        		xhr.setRequestHeader('X-CSRF-Token', $('meta[name="csrf-token"]').attr('content'));
	    		},
	            success: function(data, textStatus, jqXHR){
	            	setWatchPeriodForWatchdogTimer();
	            },
	            error: function(jqXHR, textStatus, errorThrown){
	            	setWatchPeriodForWatchdogTimer();
	            }
	        });
	    }
    }

    function checkLockStatus() {
        var otherScreenLock = localStorage.getItem('otherScreenLock');
        
        if ( otherScreenLock == 0) {
            clearLockTimer();
			// 2重チェック処理
			$("#certify-button").prop("disabled", true);
			$('#certify-button').val('<fmt:message bundle="${GnomesResources}" key="DI01.0072"/>');
			$('#lock-dialog').modal('hide');
            $('#lock-modal').html('');
			clearWatchPeriodForWatchdogTimer();
			clearCheckLockStatusTimer();
			setLockTimer();
	    }
	    else {
	        setCheckLockStatusTimer();
	    }
    }
    
	// ログイン情報をJSONデータに格納
	function formToLockSessionJSON() {
	    return JSON.stringify({
	    });
	}

	// 画面クリック時のロックタイマー処理
	function clickLockTimer() {
		if (lockTimeID != 0) {
			clearLockTimer();
			setLockTimer();	
	    	localStorage.setItem('otherScreenClick', 1);
		    // 他タブを待つため1秒後にリセット
		    setTimeout(function() {
		    	localStorage.setItem('otherScreenClick', 0);
		    }, 1000);
		}
	}

	// 画面クリック時の処理
	$(document).click(function(event){
		clickLockTimer();
	});

	// 初期表示で、ロック状態の場合は、ロック解除画面を表示する
	<c:if test="${gnomesSessionBean.isScreenLocked == true}" >
		clearLockTimer();	
		// ロック解除ダイアログ表示
		$('#login-modal').on('shown.bs.modal', function () {
			// 初期表示フォーカス設定
   			 $('#login_password').focus();
		}).modal();
	</c:if>


	// 初期表示で、ロック状態でない場合は、ロックタイマー起動
	<c:if test="${gnomesSessionBean.isScreenLocked == false}" >
		setLockTimer();
	</c:if>

    // 関連画面のクリックで画面ロック回避
	window.addEventListener("storage", function (event) {
		console.info("key : " + event.key);
		console.info("oldValue : " + event.oldValue); //変更前の値
		console.info("newValue : " + event.newValue); //変更後の値
		console.info("url : " + event.url); //イベント発生元のURL
		console.info("storageArea : " + event.storageArea);
		console.log("key : " + event.key);
		console.log("oldValue : " + event.oldValue); //変更前の値
		console.log("newValue : " + event.newValue); //変更後の値
		console.log("url : " + event.url); //イベント発生元のURL
		console.log("storageArea : " + event.storageArea);
			
	    // 	クリック状況の確認
	    var otherScreenClick = localStorage.getItem('otherScreenClick');

	    if( otherScreenClick == 1){
			if (lockTimeID != 0) {
				clearLockTimer();
				setLockTimer();	
			}
	    }
	}, false);

	// TODO 今回の再表示ボタンは？ 画面ごとに対応？
	// 再表示ボタン押下処理
	$("#menuRefreshBtn").click(function() {
		// ポップアップメッセージ情報を取得して表示
		getPoupuMessage();
	});

	// TODO 不要？
	// ポップアップメッセージ一覧作成
	makePopupMessageList();
	
	// ポップアップメッセージ監視タイマー設定
	setWatchPeriodForPopupTimer();

	// TODO 端末情報系は？
	// プルダウンリスト作成 (CommonPage)
	function makePullDownList(pullDownList, selectedUserValue){
	
		var str = "";
		for(var i=0; i<pullDownList.length; i++){
			// ユーザが設定されている値と一致するデータがある場合
			if(pullDownList[i].value == selectedUserValue){
				str += "<option selected value=\""+pullDownList[i].name+"\">"+ pullDownList[i].value +"</option>";
			}
			else{
				str += "<option value=\""+pullDownList[i].name+"\">"+ pullDownList[i].value +"</option>";
			}
		}
		
		$("#pulldown").html(str).selectpicker('refresh');

	}

	// add<R1.01.01>----------------------------------------
	<!-- ========== LOGIN ========================================= -->

	// ロック解除 POST メソッド
	function certifyForResume() {
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: "rest/A01001S001/CertifyForResume",
			async: true,
			dataType: "json",
			data: formToCertifyJSON(),
			success: function(data, textStatus, jqXHR){
				if(data.isSuccess == 1){
					if(data.isChangePassword == 1){
                        var isPanecon = false;
                        if (document.body.className.indexOf('common-font-panecon') != -1) {
                            isPanecon = true;
                        }
						// パスワード変更画面表示
                        MakeModalMessageHigh('<fmt:message bundle="${GnomesResources}" key="YY01.0036"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>','');
                        $('#my-dialog2').on('hidden.bs.modal', function () {
                            $('#my-dialog1').modal('hide');
                            $('#myModalMiddle').html('');
                            $('#lock-dialog').modal('hide');
                            $('#lock-modal').html('');
                            MakeChangePasswordModal('${gnomesSessionBean.userId}', isPanecon);
                        }).modal();
					}
					else {
						if(data.message){
							// パスワード期限警告ダイアログのみ出力
                            MakeModalMessageHigh('<fmt:message bundle="${GnomesResources}" key="YY01.0036"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>', '', '', '');
						}
						// 2重チェック処理
						$("#certify-button").prop("disabled", true);
						$('#certify-button').val('<fmt:message bundle="${GnomesResources}" key="DI01.0072"/>');
						$('#lock-dialog').modal('hide');
                        $('#lock-modal').html('');
						clearWatchPeriodForWatchdogTimer();
						setLockTimer();
					}
					localStorage.setItem('otherScreenLock', 0);
				}
				else {
                    MakeModalMessageHigh('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>','','', data.linkInfo);
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
                MakeModalMessageHigh('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
			}
		});
	}

	// ログイン情報をJSONデータに格納
	function formToCertifyJSON() {
	    return JSON.stringify({
	        "userId": $('#login_user_id').val(),
	        "password": $('#login_password').val(),
	        "localeId": '${gnomesSessionBean.localeId}',
	        "siteCode": '${gnomesSessionBean.siteCode}',
	    });
	}

    <!-- ========================================================== -->
    <!-- ========== CHANGE PASSWORD =============================== -->
    // パスワード変更 POST メソッド
    function changePassword(isInitPassword) {

  	  $('[name=isInitPassword]').val(isInitPassword);
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: "rest-pharms/Y01001S001/ChangePassword",
            async: true,
            dataType: "json",
            data: formToChangePasswordJSON(),
            success: function(data, textStatus, jqXHR){
                if(data.isSuccessChange == false){
                    MakeModalMessageMiddle( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>','','', data.linkInfo);
                }
                else {

              	    // パスワード初期化の場合、変更後パスワードの表示
                    if(data.isInitPassword == 1){
                        MakeModalMessageMiddle('<fmt:message bundle="${GnomesResources}" key="YY01.0037"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                    }
                    else{
                        // コマンド実行
                        MakeModalMessageMiddle('<fmt:message bundle="${GnomesResources}" key="YY01.0037"/>', '<fmt:message bundle="${GnomesMessages}" key="MG01.0019"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                    }

                    $('#my-dialog1').on('hidden.bs.modal', function () {
                        $('#change-pass-dialog').modal('hide');
                        $('#change-pass-modal').html(''); 
						clearWatchPeriodForWatchdogTimer();
						setLockTimer();	
					}).modal();
                }
            },
            error: function(jqXHR, textStatus, errorThrown){
                MakeModalMessageMiddle('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
            }
        });
    }
      
    // パスワード変更情報をJSONデータに格納
    function formToChangePasswordJSON() {
        return JSON.stringify({
            "userId": $('#change_user_id').val(),
            "password": $('#login_old_password').val(),
            "newPassword": $('#login_new_password').val(),
            "newPasswordConfirm": $('#login_new_password_confirm').val(),
            "isInitPassword":$('[name=isInitPassword]').val()
        });
    }

	<!-- ========================================================== -->
	<!-- ========== LOGOUT ======================================== -->

	//ログアウト POST メソッド
	function logout() {
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: "rest/A01001S001/Logout",
			async: true,
			dataType: "json",
			data: formToLogoutJSON(),
			success: function(data, textStatus, jqXHR){
				if(!data.message) {
				    saveLocalStorage("LOGINED",0);
					message_broadcast({'command':'logout','userid':	$('#userId').html()});
                    MakeModalMessageHigh('<fmt:message bundle="${GnomesResources}" key="YY01.0037"/>', '<fmt:message bundle="${GnomesMessages}" key="MG01.0015"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                    $('#my-dialog2').on('hidden.bs.modal', function () {
                        $('#lock-dialog').modal('hide');
                        $('#lock-modal').html('');
                        var action="";
                        if(isPanecon()){
                        	action = "/UI/gnomes-operate";
                        }
                        else {
                            action = "/UI/gnomes-manage";
                        }
						var useragent = window.navigator.userAgent;
						if(useragent.indexOf('Edge') != -1){
	                        document.forms.main.action = action;
							document.main.submit();
						}
						else if(useragent.indexOf('Chrome') != -1) {
							window.close();
	                        document.forms.main.action = action;
							document.main.submit();
						}
						else {
	                        document.forms.main.action = action;
							document.main.submit();
						}
					}).modal();
				}
				else {
                    MakeModalMessageHigh('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
					message_broadcast({'command':'logout','userid':	$('#userId').html()});
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
                MakeModalMessageHigh('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
			}
		});
	}
	//パスワード変更情報をJSONデータに格納
	function formToLogoutJSON() {
	 return JSON.stringify({
	 });
	}
	
	<!-- ========================================================== -->
	<!-- ========== MENU ========================================== -->
	// メニュー処理(端末情報取得) POST メソッド
 	function menuGetComputer() {
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: "rest-pharms/Y99001S001/Menu",
			async: true,
			dataType: "json",
			data: formToMenuGetComputerJSON(),
			success: function(data, textStatus, jqXHR){
				if(data.message == null){
					GnomesSelectPullDownDialogBTN('<fmt:message bundle="${GnomesResources}" key="DI01.0078"/>',  /* 端末選択 */
							'<fmt:message bundle="${GnomesMessages}" key="ME01.0036"/>',				/* 設定する端末を指定してください。*/
							'<fmt:message bundle="${GnomesResources}" key="DI01.0008"/>',				/* ユーザID */
							'${gnomesSessionBean.userId}',
							'<fmt:message bundle="${GnomesResources}" key="DI01.0079"/>',				/* 端末 */
							data.pullDownInfo, data.computerName,
							'<fmt:message bundle="${GnomesResources}" key="DI01.0005"/>',				/* 設定 */
							'menuGetComputer_onclick();'
					);
				}
				else {
                    MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
					
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
                MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
			}
		});
	}

	// メニュー処理(端末情報取得)をJSONデータに格納
	function formToMenuGetComputerJSON() {
	    return JSON.stringify({
	    	"callProcess": "getComputer", 
	    });
	}

	// メニュー処理(端末情報取得) 設定ボタン onclickリスナー
	function menuGetComputer_onclick(){
	     saveLocalStorage('<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>', $('#pulldown option:selected').text());
	     menuSetComputer();
	}

	
	// メニュー処理(端末情報設定) POST メソッド
 	function menuSetComputer() {
 		if(getLocalStorage('<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>')==null){
 			MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', '<fmt:message bundle="${GnomesMessages}" key="ME01.0038"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
 	 		return;
 		}
 		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: "rest-pharms/Y99001S001/Menu",
			async: true,
			dataType: "json",
			data: formToMenuSetComputerJSON(),
			success: function(data, textStatus, jqXHR){
				if(data.message == null){
					MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0037"/>','<fmt:message bundle="${GnomesMessages}" key="YY01.0014"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                    // ローカルストレージに端末IDを保存
                    saveLocalStorage('<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>', $('#pulldown').val());
					if ( data.selectPullDownName != null ){
                        document.getElementById('dispComputerName').innerHTML = $('#pulldown option:selected').text();
					}
                    $('#select-pulldown-dialog').modal('hide');
                    $('#select-pulldown-modal').html('');
				}
				else {
					MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');

				}
			},
			error: function(jqXHR, textStatus, errorThrown){
                MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
			}
		});
	}

	// メニュー処理(端末情報設定)をJSONデータに格納
	
	function formToMenuSetComputerJSON() {
	    return JSON.stringify({
	    	"callProcess": "setComputer", 
	    	"selectPullDownName": $('#pulldown').val(),
	    });
	}

	// ローカルストレージ内の端末情報削除
	function deleteLocalStrageForComputer(){
		deleteLocalStrage('<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>');
		menuRemoveComputer();
	}

	
	// メニュー処理(端末情報削除) POST メソッド
 	function menuRemoveComputer() {
 		if(getLocalStorage('<fmt:message bundle="${GnomesResources}" key="YY01.0047"/>')!=null){
 			MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', '<fmt:message bundle="${GnomesMessages}" key="ME01.0039"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
 	 		return;
 		}
 		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: "rest-pharms/Y99001S001/Menu",
			async: true,
			dataType: "json",
			data: formToMenuRemoveComputerJSON(),
			success: function(data, textStatus, jqXHR){
				if(data.message == null){
					MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0037"/>', '<fmt:message bundle="${GnomesMessages}" key="YY01.0014"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
					if ( data.computerName != null ){
	                    document.getElementById('dispComputerName').innerHTML = data.computerName;
					}
				}
				else {
					MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
					
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
			}
		});
	}

	// メニュー処理(端末情報削除)をJSONデータに格納
	function formToMenuRemoveComputerJSON() {
	    return JSON.stringify({
	    	"callProcess": "removeComputer",
	    });
	}
	

    // メニュー処理(サイト情報取得) POST メソッド
    function menuGetSite() {
       $.ajax({
           type: 'POST',
           contentType: 'application/json',
           url: "rest-pharms/Y99001S001/Menu",
           async: true,
           dataType: "json",
           data: formToMenuGetSiteJSON(),
           success: function(data, textStatus, jqXHR){
               if(data.message == null){
					GnomesSelectPullDownDialogBTN('<fmt:message bundle="${GnomesResources}" key="YY01.0078"/>',  /* 拠点選択 */
							'<fmt:message bundle="${GnomesMessages}" key="ME01.0172"/>',				/* 設定する拠点を指定してください。*/
							'<fmt:message bundle="${GnomesResources}" key="DI01.0008"/>',				/* ユーザID */
							'${gnomesSessionBean.userId}',
							'<fmt:message bundle="${GnomesResources}" key="YY01.0079"/>',				/* 拠点 */
							data.pullDownInfo, data.siteName,
							'<fmt:message bundle="${GnomesResources}" key="DI01.0005"/>',				/* 設定 */
							'menuSetSite();'
					);
               }
               else {
                   MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
               }
           },
           error: function(jqXHR, textStatus, errorThrown){
               MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
           }
       });
   }

   // メニュー処理(サイト情報取得)をJSONデータに格納
   function formToMenuGetSiteJSON() {
       return JSON.stringify({
           "callProcess": "getSite", 
       });
   }

   // メニュー処理(サイト情報設定) POST メソッド
    function menuSetSite() {
       $.ajax({
           type: 'POST',
           contentType: 'application/json',
           url: "rest-pharms/Y99001S001/Menu",
           async: true,
           dataType: "json",
           data: formToMenuSetSiteJSON(),
           success: function(data, textStatus, jqXHR){
               if(data.message == null){
                   MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0037"/>', '<fmt:message bundle="${GnomesMessages}" key="YY01.0014"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                   $('#select-pulldown-dialog').modal('hide');
                   $('#select-pulldown-modal').html('');
                   if(data.areaName == null){
                	   document.getElementById('siteAreaName').innerHTML = data.siteName;
                   }
                   else{
                	   document.getElementById('siteAreaName').innerHTML = data.areaName +" "+ data.siteName;
                   }

               }
               else {
                   MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                   
               }
           },
           error: function(jqXHR, textStatus, errorThrown){
               MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
           }
       });
   }

   // メニュー処理(サイト情報設定)をJSONデータに格納
   function formToMenuSetSiteJSON() {
       return JSON.stringify({
           "callProcess": "setSite", 
           "selectPullDownValue": $('#pulldown').val(),
       });
   }

	<!-- ========================================================== -->
	
	// ----------------------------------------add<R1.01.01>
	
	$(document).ready(function(){
		<c:if test="${messageBean.message != null}" >

				// メッセージダイアログを表示
				var linkInfo = [$('#msgBeanGuidanceMessage').text(),$('#msgBeanLinkURL').text(), $('#msgBeanLinkName').text()];
	 			makeMessageModalBase( 
				  $('#msgBeanCategoryName').text(),	//メッセージタイトル
				  $('#msgBeanIconName').text(),     //アイコン名
				  $('#msgBeanOccurDate').text(),    //発生日時
				  $('#msgBeanOccrUserName').text(), //操作ユーザ名
				  $('#msgBeanOccrHost').text(),     //操作ホスト
				  $('#msgBeanMessage').html(),      //メッセージ本文
				  $('#msgBeanMessageDetail').html(),//メッセージ詳細
				  $('#msgBeanMsgBtnMode').text(),   //ボタンモード
				  $('#msgBeanDefaultBtn').text(),   //デフォルトボタン
				  $('#msgBeanCommand').text(),      //コマンド
				  linkInfo,                         //リンク情報
				  false,                            //オープン時詳細を開くかどうか
				  $('#msgBeanOkOnClick').text(),    //OKボタン押下時呼び出し関数
				  $('#msgBeanCancelOnClick').text(),//Cancelボタン押下呼び出し関数
				  null,                             //ボタンID
				  $('#msgBeanDbAreaDiv').text()		//DB領域
				);
		</c:if>
	});	

    // bootstrapのモーダルダイアログから入力支援を表示時、入力支援が正しく動作しない対処
    $.fn.modal.Constructor.prototype.enforceFocus = function() {};

	// メッセージドロップダウンの最大高さ指定
	function setDropdownMenuMaxHeight() {
		var meunScrollPer = 8;	
        $('.scrollable-menu').css('max-height', ($(window).height() - $('#headerBox').outerHeight()) / 10 * meunScrollPer + 'px');
	}
	
	// F5 キー、ESCキーの無効化
	document.onkeydown = keydown;

	<!-- ========== Preventing Double Form Submission============== -->


	
	// 二重チェック処理
	function doubleCheck(){
		isCheckDoubleSubmit = true;

	
	}

	// 二重チェックリセット
	function resetDoubleCheck()
	{
		//submitFlag = false;
		isCheckDoubleSubmit = false;
	}

	
	// 入力チェック処理
	function inputCheck(title, message, okBtnLabel, isnecessarypassword, command, onclick, loginUserId, buttonId){

		// 2重チェック対象時
		if(isCheckDoubleSubmit){
			if(submitFlag){
				MakeModalMessage($('#msgBoxTitleWarning').text(), $('#msgDoubleCheckMessage').text(), $('#msgBoxBtnNameOk').text());
				return;
			}
		}
		
		// 入力フラグがtrue（：入力有）の場合
		if($('[name=inputFlag]').val()=='true'){
			GnomesConfirmBTN(title, message, okBtnLabel, isnecessarypassword, command, onclick, loginUserId, buttonId, true);
			return;
		}

		// 認証入力タイプより、認証入力ダイアログを表示
		if (isnecessarypassword != undefined && isnecessarypassword != null) {
		  switch (isnecessarypassword) {
		    case PrivilegeIsnecessaryPassword_SINGLE:
		      IsnecessaryPassword(title,loginUserId, command, buttonId, false);
		      break;
		    case PrivilegeIsnecessaryPassword_DOUBLE:
		      IsnecessaryPassword(title,loginUserId, command, buttonId, true);
		      break;
		    default:
		      setButtonId(buttonId);
		      var cmdFunc = Function(command);
		      cmdFunc();
		      break;
		  }
		}
		else {
		  setButtonId(buttonId);
		  var cmdFunc = Function(command);
		  cmdFunc();
		}

	}

	// 入力チェック
	function inputFlagCheck(){
		$('[name=inputFlag]').val(true);
	}

	// ヘッダーのチェックボックス選択時、全チェックボックスをチェック
	$(function() {
		$('[name=NMLCHK_COM_CHECK]').on("click",function() {
			$('[name=selKeys]').prop("checked", $(this).prop("checked"));
		});
	});
	
	// 新規ウィンドウを開く
	function openWindow(command, inArray) {
		open_window_submit_command_post(command, inArray, false);
	}
	
	
	function closeWindow(command) {
		okOnclick = "doCloseWindow(\'" + command + "\')";
		GnomesConfirmBTN('<fmt:message bundle="${GnomesResources}" key="YY01.0015"/>', '<fmt:message bundle="${GnomesMessages}" key="MC01.0009"/>', $('#msgBoxBtnNameOk').text(), PrivilegeIsnecessaryPassword_NONE, '', okOnclick, '', '')

	}

	//===================================================================
	/**
	* [機能]	ブックマーク処理の実行
	* [引数]	command		実行コマンド
	* [戻値]	なし
	*/
	//===================================================================
	function bookmark_command( command ) {

		var formData = new FormData();
		formData.append("bookmarkScreenId", getScreenId());
		
        // 基本情報の設定
        var paramArray = get_common_command_base(command);
        for(var key in paramArray) {
            formData.append(key, paramArray[key]);
        }
        
        $.ajax({
            processData:false,
            type: 'POST',
            contentType: false,
            url: "rest/BookMarkService",
            async: true,
            dataType: "json",
            data: formData,
            success: function(data, textStatus, jqXHR){
                if (data.isSuccess == true) {

 	           		var text = document.getElementsByName('bookmark')[0].innerText;
 	           		// ブックマーク追加した場合
 	        		if(text == $('#msgAddBookmark').text() ){

 	        			MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0037"/>', '<fmt:message bundle="${GnomesMessages}" key="MG01.0016"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
 	        			$('#my-dialog').on('hidden.bs.modal', function () {
 	 	        			document.getElementsByName('bookmark')[0].innerText = $('#msgDeleteBookmark').text();
 	 	        			document.getElementsByName('bookmark')[0].onclick = new Function("bookmark_command('Y99003C001');");
 	        			 }).modal();
 	        		}
 	        		// ブックマーク削除した場合
 	        		else if(text == $('#msgDeleteBookmark').text()){
 	 	        		
 	        			MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0037"/>', '<fmt:message bundle="${GnomesMessages}" key="MG01.0017"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
 	        			$('#my-dialog').on('hidden.bs.modal', function () {
 	 	        			document.getElementsByName('bookmark')[0].innerText = $('#msgAddBookmark').text();
 	 	        			document.getElementsByName('bookmark')[0].onclick = new Function("bookmark_command('Y99003C002');");
 	        			}).modal();
 	        		}
                } else {

                      MakeModalMessage( '<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>', data.messageBean.message, '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
                }

            },
            error: function(XMLHttpRequest, textStatus, errorThrown){

             	MakeModalMessage('<fmt:message bundle="${GnomesResources}" key="YY01.0007"/>','<fmt:message bundle="${GnomesMessages}" key="ME01.0041"/>', '<fmt:message bundle="${GnomesResources}" key="DI01.0039"/>');
            }
        });
	}

	//===================================================================
	/**
	* [機能] document.main.submit直前に、スクロール位置を保持する。
	* [引数]	なし
	* [戻値]	なし
	*/
	//===================================================================
	// document.main.submit()のオリジナルの処理を保持するために、original_submitに代入している。
	var original_submit = document.main.submit;
	document.main.submit = function() {
		// document.main.submit()の実行直前にスクロール位置の保持処理を割り込ませるために、
		// document.main.submit()にスクロール位置の保持処理を含む処理として、再定義している。
		// document.main.submit()が実行されるたびに、以下の処理が実行される。

		var screenId = getScreenId();
		
		var scrollPosJson = {}

		// メインペインのスクロール位置を取得
	    $('div.common-area-scroll')
	        .each(function(index, element) {
	        	scrollPosJson.mainPane = $(element).scrollTop();

		        // 一件目のみ取得し、.eachループを中断する。
		        return false;
	    });

		// 一画面に一つのみのテーブルのスクロール位置を取得
		$("div.common-table-ttl-fix")
			.not(".sort-dialog-table-area, .fixed_header_display_none_at_print, .save-scroll-pos")
	        .each(function(index, element) {
	        	scrollPosJson.table = $(element).scrollTop();

	        	// 一件目のみ取得し、.eachループを中断する。
		        return false;
	    });

		// 1画面の中に、スクロール位置保持をしたいdivタグが複数ある場合、JSPのdivタグの修正が必要。
		// それぞれのスクロール位置保持をしたいdivタグのclassにsave-scroll-posを追加し、
		// idを指定すると、スクロール位置保持対象となる。
		// ここでは、class = save-scroll-posのdivタグのクロール位置を取得し、
		// idをキーにスクロール位置を保存する。
		$("div.save-scroll-pos")
			.not(".fixed_header_display_none_at_print")
	        .each(function(index, element) {
	        	scrollPosJson[$(element).attr("id")] = $(element).scrollTop();
	    });
		
	    // 管理端末のツリーのスクロール位置を取得
	    $("#client-treeview")
	    	.each(function(index, element) {
	    		scrollPosJson.tree = $(element).scrollTop();

	    		// 一件目のみ取得し、.eachループを中断する。
		        return false;
	    	});

	    // スクロール位置を、「画面ID + "_scroll_pos"」をキーとした、JSON形式で保存する。
        sessionStorage.setItem(screenId + "_scroll_pos", JSON.stringify(scrollPosJson));

    	// document.main.submitのオリジナルの処理を実行する。
	    var result = original_submit.apply(this, arguments);

	    return result;
	}

	//===================================================================
	/**
	* [機能] スクロール位置の復元
	* [引数]	なし
	* [戻値]	なし
	*/
	//===================================================================
	$(document).ready( function () {

		if ($('input[name="keepScrollPosition"]').val() == "true") {
			var screenId = getScreenId();
	
			var key = screenId + "_scroll_pos";
			if (sessionStorage.getItem(key)) {
	
				var scrollPosJson = JSON.parse(sessionStorage.getItem(key));
				// メインペインのスクロール位置を復元
			    $('div.common-area-scroll')
			        .each(function(index, element) {
				        $(element).scrollTop(scrollPosJson.mainPane);
	
				        // 一件目のみ復元し、.eachループを中断する。
				        return false;
			    });
	
				// 一画面に一つのみのテーブルのスクロール位置を復元
				$("div.common-table-ttl-fix")
					.not(".sort-dialog-table-area, .fixed_header_display_none_at_print, .save-scroll-pos")
			        .each(function(index, element) {
			        	$(element).scrollTop(scrollPosJson.table);
	
			        	// 一件目のみ復元し、.eachループを中断する。
				        return false;
			    });

				// 1画面の中に、スクロール位置保持をしたいdivタグが複数ある場合、JSPのdivタグの修正が必要。
				// それぞれのスクロール位置保持をしたいdivタグのclassにsave-scroll-posを追加し、
				// idを指定すると、スクロール位置保持対象となる。
				// ここでは、class = save-scroll-posのdivタグを取得し、
				// idをキーにスクロール位置を復元している。
				$("div.save-scroll-pos")
					.not(".fixed_header_display_none_at_print")
			        .each(function(index, element) {
			        	$(element).scrollTop(scrollPosJson[$(element).attr("id")]);
			    });
				
			    // 管理端末のツリーのスクロール位置を復元
			    $("#client-treeview")
			    	.each(function(index, element) {
			    		$(element).scrollTop(scrollPosJson.tree);
	
			    		// 一件目のみ復元し、.eachループを中断する。
				        return false;
			    	});
				sessionStorage.removeItem(key);
			}
		}
	});

	<!-- ========================================================== -->
//]]>
	</script>
	