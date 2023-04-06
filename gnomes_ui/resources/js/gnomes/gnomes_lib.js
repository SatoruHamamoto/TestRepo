//===================================================================
/**
 * 管理端末でメニューから展開された画面んお戻るボタンの処理スクリプト
 * メニュー画面に戻る場合、メニュー画面が存在したらwindows.close()して
 * メニュー画面が存在しなかったら引数のコマンドIDでSubmitする。引数は
 * 通常メニューのコマンドIDを指定する
 * これを呼ぶには画面アイテム定義のボタンカスタムタグ定義で
 *  OnClick="command_backbtn('commandId');"
 * と設定することを想定
 *
 * @param commandid メニュー画面が存在しないときにディスパッチされるコマンドID
 */
//===================================================================
function common_backbtn(commandid)
{
    var submitflg = true;
    var parent = window.opener;
    if(parent != null){
        var parentScreendId = window.opener.getScreenId();
        if(parentScreendId == "MGY008_PH"){
            submitflg = false;
        }
    }
    if(submitflg){
        commandSubmit(commandid);
    }
    else {
        window.close();
    }
}

//===================================================================
/**
 * 工程端末と管理端末の判別を行う
 *
 */
//===================================================================

function isPanecon()
{
    if(document.body.className.indexOf('common-font-panecon') != -1){
        return true;
    }
    else {
        return false;
    }
}
//===================================================================
/**
 * 画面全体の動きとして、ログアウトなど（今はログアウトのみ）
 * 複数のタブ、複数のユーザでログアウトを実行するとログアウトを行った
 * ユーザと自分以外のウインドウに対してログアウトを連動する
 *
 */
//===================================================================
//画面表示時にデフォルトでイベント登録する
$(document).ready(function(){
    $(window).on('storage', message_receive);
});

//他の処理から呼ばれる。メッセージとしてローカルストレージ
//に登録すると、message_recieveがコールされる
function message_broadcast(message)
{
    localStorage.setItem('message',JSON.stringify(message));
    localStorage.removeItem('message');
}
//各タブで呼ばれるイベント
function message_receive(ev)
{
    if (ev.originalEvent.key!='message') return; // ignore other keys
    var message=JSON.parse(ev.originalEvent.newValue);
    if (!message) return; // ignore empty msg or msg reset

    //ログアウトの処理。引数のユーザIDと自分のIDを比較し一致したら
    //ログアウトを実施する
    if (message.command == 'logout') {
        if(message.userid == $('#userId').html()){
            window.close();

            var action="";
            if(isPanecon()){
                action = $(document).attr("location").protocol + "//" + location.host + "/UI/gnomes-operate";
            }
            else {
                action = $(document).attr("location").protocol + "//" + location.host + "/UI/gnomes-manage";
            }
            window.open(action, '_self');
            //document.forms.main.action = action;
            //document.main.submit();
        }
    }
    //他のイベント処理も追加できる
}

//===================================================================
/**
 * ボタン送信処理 ＧＮＯＭＥＳ事前チェック版
 * サーブレットに渡すHIDDEN項目の値を押されたボタンの名前にする
 * @param obj valueにセットする値
 */
//===================================================================
function GnomesSendBTN(title, message, okBtnLabel, commandName, objName, idName) {

    MakeModalAlert(title, message, okBtnLabel, commandName, objName, idName);

}

function MakeModalAlert(title, message, okBtnLabel, commandName, objName, idName) {
    // モーダルダイアログのヘッダとボディの作成
    var str = MakeModalHeaderBody(title, message);

    str += "<div class=\"modal-footer\">";

    if (commandName != null && commandName != '') {
        // 閉じる
        str += "<button type=\"button\" id=\"myAnyClose\" class=\"btn btn-default\" data-dismiss=\"modal\">"
                + $('#msgBoxBtnClose').text() + "</button>";
        str += "<button type=\"button\" class=\"btn btn-primary\"";

        str += "onclick=\"$('#myAnyModal').modal('hide');$('#myLoadingModal').modal();document.main.command.value='"
                + commandName + "';document.main.submit();\"";
    }
    else {
        str += "<button type=\"button\" class=\"btn btn-primary\"";
        str += "onclick=\"$('#myAnyModal').modal('hide');\"";
    }
    str += ">" + okBtnLabel + "</button>";
    str += "</div>";
    str += "</div>";
    str += "</div>";
    $('#myAnyModal').html(str);
    $('#myAnyModal').on('shown.bs.modal', function() {
        if (commandName != null && commandName != '') {
            $('#myAnyClose').focus();
        }
        else {
            $('.btn', '#myAnyModal').focus();
        }
    }).modal();
}

//===================================================================
/**
 * 戻るボタンの無効化
 */
//===================================================================
if (window.history.pushState) {
    history.pushState("nohb", null, "");
    $(window).on("popstate", function(event) {
        if (!event.originalEvent.state) {
            history.pushState("nohb", null, "");
            return;
        }
    });
}

//===================================================================
/**
 * F5キー、ESCキーの無効化
 *
 * キーマップは URL http://www.programming-magic.com/file/20080205232140/keycode_table.html を参照
 *
 */
//===================================================================
function keydown() {
    if (event.keyCode == 116) {
        event.keyCode = 0;
        return false;
    }
}
// 2重チェックフラグ
var submitFlag = false;

// 2重チェック有無
var isCheckDoubleSubmit = false;

// 長時間サブミット定周期除外フラグ
var isLongTimeSubmit = false;


/**
 * コマンド実行共通処理
 *  optProcessDialogDisp: 処理中ダイアログを表示したくない場合はfalse（デフォルト＝true）
 */
function commandSubmit(commandId,optProcessDialogDisp,useAjax,ajaxOptions) {

    var exit = false;
    // 検索ボタンを押す場合、禁則文字をチェック
    $(".common-flexMenu-search-box.common-flexMenu-size").each(function() {
        var valid = checkInvalidValSearchCondition();
        if(valid == false) {
            var format = $('[name=dateFormat]').val() + ":ss";
            var dateFormat = comDateFormat(new Date(), format);
            makeMessageModalBase(
                    $('[name=mesgCategoryName]').val(),   //メッセージタイトル
                    $('[name=mesgIconName]').val(),       //アイコン名
                    dateFormat,                           //発生日時
                    $(".common-header-userName").html(),  //操作ユーザ名
                    $(".common-header-deviceName").html(),//操作ホスト
                    $('[name=mesgBodyName]').val(),       //メッセージ本文
                    null,                                 //メッセージ詳細
                    null,                                 //ボタンモード
                    null,                                 //デフォルトボタン
                    null,                                 //コマンド
                    null,                                 //リンク情報
                    false,                                //オープン時詳細
                    null,                                 //OKボタン押下時呼び出し関数
                    null,                                 //Cancelボタン押下呼び出し関数
                    null,                                 //ボタンID
                    ""                                    //DB領域
            );
            exit = true;
        }
    });
    if (exit) {
        return;
    }

    var isProcessingDialog = optProcessDialogDisp === undefined ? true : optProcessDialogDisp;

    // 共通処理
    commonSubmit(commandId);

    //処理中にダイアログを表示
    if(isProcessingDialog){
        processingDialog();
    }


    //サブミット後、処理が長時間かかる場合、定周期を起動しない
    if(isLongTimeSubmit && commandId == "WATCH_CONVERSION"){
        //何もしない
    } else {
        var command_bak = document.main.command.value;
        document.main.command.value = commandId;

		if(useAjax) {
			var method = document.main.method;
			var url = document.main.action;
			var options = {
				type: method,
				url: url,
				dataType: "text",
				data: $(document.main).serialize(),
				success: function(){
					//nothind
				},
				error: function(){
					//noting
				}
			}
			if(ajaxOptions){
				$.each(ajaxOptions,function(key){
					options[key] = ajaxOptions[key];
				});
			}

			$.ajax(options)
			document.main.command.value = command_bak;
		}
		else {
			document.main.submit();
		}
        document.main.command.value = command_bak;
	}



    //処理中キャンセル検知を行う
    if($.cookie){
		var windowId = $('input[name=windowId]').val();
		var count=0;
		var responceTimer = setInterval(function() {
			if($.cookie('response')){
				var cookieWindowId = $.cookie('windowId');
				if(windowId === cookieWindwId){
					MakeModalMessage($('#msgBoxTitleWarinig').text(),$.cookie("response"),$('#msgBoxBtnNameOk'));
					$.removeCookie("response",{ path: "/" });
					$.removeCookie("windowId",{ path: "/" });
					clearInterval(responceTimer);
					$('#process-dialog').modal('hide');
					$('#process-dialog').html('');
					submitFlag = false;
				}
			}
			count++;
			if(count >= 10){
				clearInterval(responceTimer);
			}
		},3000);
	}
}

// コマンド実行共通処理
function traceLogDownload(commandId) {
    // 共通処理
    commonSubmit(commandId);

    document.main.command.value = commandId;
    document.main.submit();

    //ダウンロード検知を行う
    var downloadTimer = setInterval(function () {
        if ($.cookie("downloaded")) {
          $.removeCookie("downloaded", { path: "/" });
          clearInterval(downloadTimer);
          submitFlag = false;
        }
    }, 1000);

}

// 共通処理
function commonSubmit(commandId) {

    // ページ遷移レイアウト
    if (document.querySelector('.common-side-none') !== null) {
        sessionStorage.setItem($('#screenId').html(),1);
    } else {
        sessionStorage.setItem($('#screenId').html(),0);
    }

    // 2重チェック対象時
    if (isCheckDoubleSubmit != undefined && isCheckDoubleSubmit ) {
        if (isCheckDoubleSubmit != undefined && submitFlag && commandId != "WATCHDOG_CONVERSION") {
            MakeModalMessage($('#msgBoxTitleWarning').text(), $('#msgDoubleCheckMessage').text(), $('#msgBoxBtnNameOk')
                    .text());
            return;
        }
        //定周期延命サブミットの場合はsubmitFlagを制御しないようにする
        if(commandId != "WATCHDOG_CONVERSION"){
            submitFlag = true;
            //延命タイマーリセット
            clearTimerArrivalFunctionBean();
            setTimerArrivalFunctionBean();
            //長時間サブミット定周期除外フラグをfalseにしてクリアする
            isLongTimeSubmit = false;
        } else {
            //長時間サブミット定周期除外フラグをtrueにして2重サブミットを防止する
            isLongTimeSubmit = true;
        }
    }
    // 検索メニューから送信情報作成
    makeSearchMenuMap();
}

// リンク（URL指定）をクリックした時の処理
function openURL(url) {

    var window_obj = window.open(url, "_blank");
    window_obj.focus();

}

// メニュー画面のドロップリスト
$(function() {
    // メニューボックスをクリックした時の処理
    $('.common-menu-box').on('click', function() {

        if (!$(this).hasClass('panecon-menu-box')) {
            if ($('+div', this).hasClass('common-menu-list-selected')) {
                $('+div', this).removeClass('common-menu-list-selected');
            }
            else {
                $('+div', this).removeClass('common-menu-list-selected');
                $('+div', this).addClass('common-menu-list-selected');
            }
        }
    });

    // メニューボックス以外の部分をクリックした時の処理
    $(document).on('click touchend', function(event) {
        if (!$(event.target).closest('.common-menu-panel').length) {
            if ($('+div', this).hasClass("common-menu-list-selected")) {
                $('+div', this).removeClass("common-menu-list-selected");
            }
        }
    });
});

// 管理端末ヘッダー部分のメッセージ表示
$(function() {
    $('.common-header-messageBox').on('click', function() {
        if ($('+div', this).hasClass('common-header-messageList-show')) {
            $('.common-header-messageList').removeClass('common-header-messageList-show');
        }
        else {
            $('.common-header-messageList').removeClass('common-header-messageList-show');
            $('+div', this).addClass('common-header-messageList-show');
        }
    });
    // ヘッダーメニュー以外の部分をクリックした時の処理
    $(document).on('click touchend', function(event) {
        if (!$(event.target).closest('.common-header-messagePanel').length) {
            if ($('.common-header-messageList ').hasClass("common-header-messageList-show")) {
                $('.common-header-messageList ').removeClass("common-header-messageList-show");
            }
        }
    });
});
//ヘッダーメニューのドロップリスト
$(function() {

    // ヘッダーメニューアイコンをクリックした時の処理
    $('.common-header-menuIcon').on('click', function() {
        if ($('+div', this).hasClass('common-menu-list-selected')) {
            $('.common-header-menu-list', this).removeClass('common-menu-list-selected');
        }
        else {
            $('.common-header-menu-list', this).removeClass('common-menu-list-selected');
            $('+div', this).addClass('common-menu-list-selected');
        }
    });

    // ヘッダーメニュー以外の部分をクリックした時の処理
    $(document).on('click touchend', function(event) {
        if (!$(event.target).closest('.common-header-menu-unit').length) {
            if ($('.common-header-menu-list', this).hasClass("common-menu-list-selected")) {
                $('.common-header-menu-list', this).removeClass("common-menu-list-selected");
            }
        }

        if (!$(event.target).closest('.common-pageNum-current').length) {
            if ($('.common-pageNum-select', this).hasClass("common-pageNum-select-show")) {
                $('.common-pageNum-select', this).removeClass("common-pageNum-select-show");
            }
        }
    });
});

// 左右展開の処理
$(function() {
    $('.common-separator-vartical').on('click', function() {
        $('+div', this).toggleClass('common-menu-nonShowX');
        $('img', this).toggleClass('common-icon-roteto180');
        $('.common-main-area').toggleClass('common-side-none');

        //内部のfixed_midashiのリフレッシュ
        if(typeof FixedMidashi !== 'undefined')  {
            FixedMidashi.create();

            // 別途FixedMidashi.createが実行されるので、ここではダブルクリック関係は省略
        }


        // 詳細系(タイプ3)用の表示エリア拡大処理
        //$('.p0004-tab').toggleClass('common-width-1000');

    });
});

// ページ遷移レイアウト
$(function() {
    $('.common-pageNavi-area .common-button').not('.common-page-navi-button-disable').on('click', function() {

        if (document.querySelector('.common-side-none') !== null) {
            sessionStorage.setItem($('#screenId').html(),1);
        } else {
            sessionStorage.setItem($('#screenId').html(),0);
        }
    });

    if(sessionStorage.getItem($('#screenId').html()) == 1) {
        $('.common-main-area').addClass('common-side-none');
        $('.common-main-area-right').addClass('common-menu-nonShowX');
    }
});

//初期表示のみ展開状態にする
$(document).ready(function(){
    if(document.body.className.indexOf('common-font-panecon') == -1){
        if(sessionStorage.getItem($('#screenId').html()) == undefined) {
            sessionStorage.setItem($('#screenId').html(),1);
            $('.common-main-area').addClass('common-side-none');
            $('.common-main-area-right').addClass('common-menu-nonShowX');
        }
    }
});

// 左右展開の処理
$(function() {
    $('.common-separator-vartical-left').on('click', function() {
        $('.common-main-area').toggleClass('common-side-none');
        //内部のfixed_midashiのリフレッシュ
        if(typeof FixedMidashi !== 'undefined')  {
            FixedMidashi.create();

            // 固定ヘッダにダブルクリックでのイベントを追加
            setDblClickEvent();
        }
    });
});

// 機能メニュー郡展開縮小処理
$(function() {
    $('.common-flexMenu-function-header, .common-flexMenu-search-header').on('click', function() {
        // 隣接するdiv要素にクラス追加
        $('+div', this).toggleClass('common-menu-nonShowY');
        // アイコンの向きを反転させる
        $(this).find('img').toggleClass('common-icon-roteto180');
    });
});

//管理端末 共通
$(function() {
    $('.common-flexMenu-function-header, .common-flexMenu-search-header').on('click', function() {

        // 展開縮小をコントロール
        // メニュー4つ表示時
        if ($('.flexMenu-unit-4').length > 0) {
            // 親要素にcloseを付与
            $(this).closest('.flexMenu-unit-4').toggleClass('flexMenu-close');

            // flexMenu-closeの数をカウント
            var counter = $('.flexMenu-unit-4.flexMenu-close').length;
        }
        // メニュー3つ表示時
        else if ($('.flexMenu-unit-3').length > 0) {
            // 親要素にcloseを付与
            $(this).closest('.flexMenu-unit-3').toggleClass('flexMenu-close');

            // flexMenu-closeの数をカウント
            var counter = $('.flexMenu-unit-3.flexMenu-close').length;
        }
        // メニュー2つ表示時
        else if ($('.flexMenu-unit-2').length > 0) {
            // 親要素にcloseを付与
            $(this).closest('.flexMenu-unit-2').toggleClass('flexMenu-close');

            // flexMenu-closeの数をカウント
            var counter = $('.flexMenu-unit-2.flexMenu-close').length;
        }
        // メニュー1つ表示時
        else if ($('.flexMenu-unit-1').length > 0) {
            // 親要素にcloseを付与
            $(this).closest('.flexMenu-unit-1').toggleClass('flexMenu-close');

            // flexMenu-closeの数をカウント
            var counter = $('.flexMenu-unit-1.flexMenu-close').length;
        }

        // 管理しているidを外す
        $('.common-flex-area-vartical').attr('id', '');
        if (counter) {
            // クローズしている個数に合わせたidを付与する
            $('.common-flex-area-vartical').attr('id', 'flexMenu-close-' + counter);
        }
    });
});

// 工程端末 一覧系(検索条件付き)
$(function() {
    $('.p1001-function-header, .p1001-search-header').on('click', function() {

        // 展開縮小をコントロール
        // 親要素にcloseを付与
        $(this).closest('.p1001-flexMenu-unit').toggleClass('flexMenu-close');

        // flexMenu-closeの数をカウント
        var counter = $('.p1001-flexMenu-unit.flexMenu-close').length;

        // 管理しているidを外す
        $('.common-flex-area-vartical').attr('id', '');
        if (counter) {
            // クローズしている個数に合わせたidを付与する
            $('.common-flex-area-vartical').attr('id', 'flexMenu-close-' + counter);
        }
    });
});

// 管理端末 一覧系のサブメニュー表示
$(function() {
    $('.common-subMenu-box').on('click', function() {
        $('+div', this).toggleClass('common-subMenu-list-show');
    });
});

// 工程端末 一覧系のサブメニュー表示
$(function() {
    $('.common-koutei-subMenu-box').on('click', function() {
        $('+div', this).toggleClass('common-koutei-subMenu-list-show');
    });
});

// ダイアログ表示
function showDialog(dialogId) {

    // キーボード操作などにより、オーバーレイが多重起動するのを防止する
    $(this).blur(); // ボタンからフォーカスを外す
    if ($("#common-dialog-overlay")[0])
        return false; //新しくモーダルウィンドウを起動しない

    // オーバーレイを出現させる
    $("body").append('<div id="common-dialog-overlay"></div>');
    $("#common-dialog-overlay").fadeIn("fast");

    // コンテンツをセンタリングする
    centeringModalSyncer();

    //詳細部分の縮小処理
    $('.common-flexMenu-function-header').on('click', function() {
        $('+div', this).toggleClass('common-menu-nonShowY');
        $(this).find('img').toggleClass('common-icon-roteto180');
    });

    // コンテンツをフェードインする
    $(dialogId).fadeIn("fast");

    $("[name=common-dialog-cancel]").unbind().click(function() {

        //[#p0009-dialog-content]と[#p0009-dialog-overlay]をフェードアウトした後に
        $(dialogId).fadeOut("fast", function() {
            $("#common-dialog-overlay").fadeOut("fast", function() {

                //[#p0009-dialog-overlay]を削除する
                $('#common-dialog-overlay').remove();
                $(dialogId).remove();

            });
        });
    });
    // リサイズされたら、センタリングをする関数[centeringModalSyncer()]を実行する
    $(window).resize(centeringModalSyncer);

    $("[name=common-dialog-ok]").unbind().click(function() {

        //[#p0009-dialog-content]と[#p0009-dialog-overlay]をフェードアウトした後に
        $(dialogId).fadeOut("fast", function() {
            $("#common-dialog-overlay").fadeOut("fast", function() {

                //[#p0009-dialog-overlay]を削除する
                $('#common-dialog-overlay').remove();
                $(dialogId).remove();
            });
        });
    });

    // センタリングを実行する関数
    function centeringModalSyncer() {

        // 画面(ウィンドウ)の幅、高さを取得
        var w = $(window).width();
        var h = $(window).height();

        // コンテンツ(#p0009-dialog-content)の幅、高さを取得
        var cw = $(dialogId).outerWidth();
        var ch = $(dialogId).outerHeight();

        // センタリングを実行する
        $(dialogId).css({
            "left" : ((w - cw) / 2) + "px",
            "top" : ((h - ch) / 2) + "px"
        });
    }
}

// 検索・ソートダイアログ表示
function showSearchDialog(dialogName, isPanecon) {

    // キーボード操作などにより、オーバーレイが多重起動するのを防止する
    $(this).blur(); // ボタンからフォーカスを外す
    if ($("#common-dialog-overlay")[0])
        return false; //新しくモーダルウィンドウを起動しない

    // オーバーレイを出現させる
    $("body").append('<div id="common-dialog-overlay"></div>');
    $("#common-dialog-overlay").fadeIn("fast");

    // コンテンツをフェードインする
    $(dialogName).fadeIn("fast", function() {
        // 工程端末表示時
        if (isPanecon) {
            //customScrollbar();
            //tableHeaderReset();
            $('.common-keyboard-input').keyboard();
            $('.deliveryDate').datepicker({
                "dateFormat" : "yy/mm/dd"
            });
        }
        // 管理端末表示時
        else {
            if(typeof FixedMidashi !== 'undefined')  {
                FixedMidashi.create();

                // 固定ヘッダにダブルクリックでのイベントを追加
                setDblClickEvent();
            }
        }
    });

    centeringModalSyncer();

    // [#p0009-dialog-cancel]をクリックしたら
    $("[name=common-dialog-cancel]").unbind().click(function() {

        //[#p0009-dialog-content]と[#p0009-dialog-overlay]をフェードアウトした後に
        $(dialogName).fadeOut("fast", function() {
            $("#common-dialog-overlay").fadeOut("fast", function() {

                //[#p0009-dialog-overlay]を削除する
                $('#common-dialog-overlay').remove();
            });
        });
    });
    // リサイズされたら、センタリングをする関数[centeringModalSyncer()]を実行する
    $(window).resize(centeringModalSyncer);

    // センタリングを実行する関数
    function centeringModalSyncer() {

        // 画面(ウィンドウ)の幅、高さを取得
        var w = $(window).width();
        var h = $(window).height();

        // コンテンツ(#p0009-dialog-content)の幅、高さを取得
        var cw = $(dialogName).outerWidth();
        var ch = $(dialogName).outerHeight();

        // センタリングを実行する
        $(dialogName).css({
            "left" : ((w - cw) / 2) + "px",
            "top" : ((h - ch) / 2) + "px"
        });
    }

}

//ユーザ認証の有無
const
PrivilegeIsnecessaryPassword_NONE = 0; // 何もしない
const
PrivilegeIsnecessaryPassword_SINGLE = 1; // 認証ダイアログを表示する
const
PrivilegeIsnecessaryPassword_DOUBLE = 2; // ダブル認証ダイアログを表示する

//権限確認メッセージ
//onclick でjavascriptを指定時は、PrivilegeIsnecessaryPassword_NONEとすること、commandは無視する
function GnomesConfirmBTN(title, message, okBtnLabel, isnecessarypassword, commandScript, onclick, loginUserId,
        buttonId, isConfirm) {

    // 2重チェック対象時
    if (isCheckDoubleSubmit) {
        if (submitFlag) {
            MakeModalMessage($('#msgBoxTitleWarning').text(), $('#msgDoubleCheckMessage').text(), $('#msgBoxBtnNameOk')
                    .text());
            return;
        }
    }

    var format = $('[name=dateFormat]').val() + ":ss";
    var dateFormat = comDateFormat(new Date(), format);

    var escCommandScript = commandScript.split('\'').join('\\\'');

    var titleLabel = title;

    if (isConfirm) {
        titleLabel = $('#msgBoxTitleConfirm').text();
    }
    // ダイアログの内容
    var str = "<div class=\"modal\" id=\"confirm-dialog\" data-keyboard=\"false\" data-backdrop=\"static\" >";
    str += "<div class=\"modal-dialog\">";
    str += "<div class=\"common-dialog-content\" style=\"width: 600px;\">";
    str += "  <div class=\"common-dialog-header-title clearfix\">" + titleLabel + "</div>";
    str += "  <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "    <div class=\"common-dialog-body-column\">";
    str += "      <table>";
    str += "        <tbody>";
    str += "          <tr>";
    str += "            <td rowspan=\"2\">";
    str += "              <img src=\"./images/gnomes/icons/kaku.png\" class=\"common-dialog-guidance-icon\">";
    str += "            </td>";
    str += "            <td style=\"height: 20px; font-size: 8pt;\">" + dateFormat;
    str += "            </td>";
    str += "          </tr>";
    str += "          <tr>";
    str += "            <td style=\"-ms-word-break: break-all;\">" + message;
    str += "            </td>";
    str += "          </tr>";
    str += "        </tbody>";
    str += "      </table>";
    str += "    </div>";
    str += "  </div>";

    var strOnclick = "";

    if (commandScript != undefined && commandScript != null && commandScript != '') {
        switch (isnecessarypassword) {
        case PrivilegeIsnecessaryPassword_NONE:
            var setButtonId = "";
            if (buttonId != undefined && buttonId != '') {
                setButtonId = "setButtonId('" + buttonId + "'); ";
            }

            if (onclick == undefined || onclick == '') {
                strOnclick += setButtonId + commandScript;

            }
            else {
                strOnclick += setButtonId + onclick;
            }
            break;
        case PrivilegeIsnecessaryPassword_SINGLE:
            strOnclick += "IsnecessaryPassword('" + title + "','" + loginUserId + "','" + escCommandScript + "','"
                    + buttonId + "', false);";
            break;

        case PrivilegeIsnecessaryPassword_DOUBLE:
            strOnclick += "IsnecessaryPassword('" + title + "','" + loginUserId + "','" + escCommandScript + "','"
                    + buttonId + "', true);";
            break;
        }
    }
    else {
        if (onclick != undefined && onclick != null && onclick != '') {
            strOnclick += onclick;

        }
        else {
            strOnclick += "";
        }
    }
    str += "  <div class=\"common-dialog-footer clearfix\">";

    str += "    <div class=\"common-dialog-footer-button-left\"><a class=\"modal-focus\" name=\"common-dialog-ok_test\" onclick=\"$('#confirm-dialog').modal('hide');$('#myAnyModal').html(''); "
            + strOnclick
            + "; return false;\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
            + okBtnLabel + "</span></a></div>";
    str += "    <div class=\"common-dialog-footer-button-right\"><a class=\"modal-focus\" name=\"common-dialog-cancel\" onclick=\"$('#confirm-dialog').modal('hide');$('#myAnyModal').html(''); return false;\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
            + $('#msgBoxBtnNameCancel').text() + "</span></a></div>";

    str += "  </div>";
    str += "</div>";
    str += "</div>";
    str += "</div>";

    $('#myAnyModal').html(str);
    $('#confirm-dialog').on('shown.bs.modal', function() {
        //$('#ok').focus();
    }).modal();

}

//認証ダイアログ(ダブル認証ダイアログ)を表示する
function IsnecessaryPassword(title, loginUserId, commandScript, buttonId, isDoubleCheck, substituteFlag) {
    //ロックアウト解除ダイアログ
    $('#unlock-dialog').modal('hide');
    // パスワード変更ダイアログ
    $('#change-pass-dialog').modal('hide');
    // パスワード変更ダイアロ
    $('#change-pass-dialog').modal('hide');
    var certUserId = $('[name=certUserName]').val();

    var strFlagChange = "";
    // 2重チェック対象時
    if (isCheckDoubleSubmit) {
        if (submitFlag) {
            MakeModalMessage($('#msgBoxTitleWarning').text(), $('#msgDoubleCheckMessage').text(), $('#msgBoxBtnNameOk')
                    .text());
            return;
        }
    }

    var isPanecon = false;
    var keyboardClass = "";
    if (document.body.className.indexOf('common-font-panecon') != -1) {
        isPanecon = true;
        keyboardClass = " common-keyboard-input-char";
    }

    // ダイアログの内容
    var str = "<div class=\"modal\" id=\"confirm-dialog\" data-keyboard=\"false\" data-backdrop=\"static\" >";
    str += "<div class=\"modal-dialog\">";
    str += "<div class=\"common-dialog-content modal-dialog\">";
    str += "    <div class=\"common-dialog-header-title clearfix common-dialog-title-style\" style=\"text-align:left;\">" + title + "</div>";
    str += "    <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "      <div class=\"common-dialog-body-column\">";
    str += "        <div class=\"common-dialog-guidance-message\">" + $('#msgConfirmDialogTitle').text() + "</div>";

    if (isDoubleCheck) {
        str += "        <div class=\"common-dialog-guidance-message\">" + $('#msgConfirmDialogWorkUser').text()
                + "</div>";
    }
    str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
            + $('#msgConfirmDialogUserId').text() + "</div>";
    str += "        <div class=\"common-dialog-header-col-data\">";
    str += "          <input name=\"loginUserId\" id=\"loginUserId\" class=\"common-dialog-dataarea-col-data\" type=\"text\" value=\""
            + loginUserId + "\" readonly>";
    str += "        </div><br>";
    str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
            + $('#msgConfirmDialogPassword').text() + "</div>";
    str += "        <div class=\"common-dialog-header-col-data\">";
    str += "          <input name=\"loginUserPassword\" id=\"loginUserPassword\" class=\"common-dialog-dataarea-col-data modal-focus"
            + keyboardClass + "\" type=\"password\">";
    str += "        </div><br>";

    if (isDoubleCheck) {
        str += "        <br>";
        str += "        <div class=\"common-dialog-guidance-message\">" + $('#msgConfirmDialogCertUser').text()
                + "</div>";
        str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
                + $('#msgConfirmDialogUserId').text() + "</div>";
        str += "        <div class=\"common-dialog-header-col-data\">";
        str += "          <input name=\"certUserId\" id=\"certUserId\" class=\"common-dialog-dataarea-col-data modal-focus"
                + keyboardClass + "\" type=\"text\" value=\"" + certUserId + "\" onInput=\"checkInputKana(this)\">";
        str += "        </div><br>";
        str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
                + $('#msgConfirmDialogPassword').text() + "</div>";
        str += "        <div class=\"common-dialog-header-col-data\">";
        str += "          <input name=\"certUserPassword\" id=\"certUserPassword\" class=\"common-dialog-dataarea-col-data modal-focus"
                + keyboardClass + "\" type=\"password\">";
        str += "        </div><br>";
    }
    else {
        str += "        <input type=\"hidden\" name=\"certUserId\" value=\"\">";
        str += "        <input type=\"hidden\" name=\"certUserPassword\" value=\"\">";
    }

    str += "        <input type=\"hidden\" name=\"isDoubleCheck\" value=\"" + isDoubleCheck + "\"/>";

    str += "      </div>";
    str += "    </div>";
    str += "    <div class=\"common-dialog-footer clearfix\">";
    str += "      <div class=\"common-dialog-footer-button-left\">";
    if (substituteFlag) {
        str += "        <a class=\"modal-focus\" name=\"common-dialog-ok\" onclick=\"changeHash();setButtonId('" + buttonId + "');setSubstituteFlag('" + substituteFlag + "');"
        + strFlagChange + commandScript + " return false;\" href=\"#\">";
    } else {
        str += "        <a class=\"modal-focus\" name=\"common-dialog-ok\" onclick=\"changeHash();setButtonId('" + buttonId + "');"
        + strFlagChange + commandScript + " return false;\" href=\"#\">";
    }
    str += "          <span class=\"common-button common-dialog-button\" style=\"width: 90%;\">"
            + $('#msgBoxBtnNameOk').text() + "</span>";
    str += "        </a>";
    str += "      </div>";
    str += "      <div class=\"common-dialog-footer-button-right\">";
    str += "        <a class=\"modal-focus\" name=\"common-dialog-cancel\" onclick=\"$('#confirm-dialog').modal('hide');$('#myAnyModal').html(''); return false;\" href=\"#\">";
    str += "          <span class=\"common-button common-dialog-button\" style=\"width: 90%;\">"
            + $('#msgBoxBtnNameCancel').text() + "</span>";
    str += "        </a>";
    str += "      </div>";
    str += "    </div>";
    str += "  </div>";
    str += "</div>";
    str += "</div>";

    $('#myAnyModal').html(str);
    $('#confirm-dialog').on('shown.bs.modal', function() {
        if (!isPanecon) {
            $('#loginUserPassword').focus();
        }
    }).modal();

}

//メッセージ詳細Modal
function makeMessageModalBase(
        title,                  //メッセージタイトル
        iconName,               //アイコン名
        occurDate,              //発生日時
        occrUserName,           //操作ユーザ名
        occrHost,               //操作ホスト
        message,                //メッセージ本文
        messageDetail,          //メッセージ詳細
        msgBtnMode,             //ボタンモード
        defaultBtn,             //デフォルトボタン
        commands,               //コマンド
        linkInfo,               //リンク情報
        isOpenDetail,           //オープン時詳細
        okOnClickFunc,          //OKボタン押下時呼び出し関数
        cancelOnClickFunc,      //Cancelボタン押下呼び出し関数
        buttonId,               //ボタンID
        dbAreaDiv               //DB領域
    ) {

    // messageModalにすでに内容がある場合、
    if ($('#messageModal').children().length > 0) {
        $('.modal-backdrop').remove();
    }

    var str = "<div class=\"modal\" id=\"message-dialog\" data-keyboard=\"false\" data-backdrop=\"static\" >";
    str += "<div class=\"modal-dialog\">";
    str += "<div class=\"common-dialog-content modal-dialog\">";

    str += "  <div class=\"common-dialog-header-title clearfix\">" + dbAreaDiv + title + "</div>";
    str += "  <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "    <div class=\"common-dialog-body-column\">";
    str += "      <table class=\"common-messagedialog-table\">";
    str += "        <tbody>";
    str += "          <tr>";
    str += "            <td rowspan=\"2\">";
    str += "              <img src=\"./images/gnomes/icons/" + iconName + "\" class=\"common-dialog-guidance-icon\">";
    str += "            </td>";
    str += "            <td style=\"height: 20px; font-size: 8pt;\">" + occurDate;
    str += "            </td>";
    str += "          </tr>";
    str += "          <tr>";
    str += "            <td style=\"word-break: break-all;\">" + message;
    str += "            </td>";
    str += "          </tr>";
    str += "        </tbody>";
    str += "      </table>";
    str += "    </div>";
    str += "    <div id=\"detailArea\">";
    str += "      <div class=\"dialog-message-unit\">";
    str += "        <div class=\"common-dialog-function-header clearfix\">";
    str += "          <span class=\"common-dialog-function-header-title \">" + $('#msgBoxLabelDetail').text();
    str += "            <span class=\"common-dialog-function-header-icon\">";
    str += "              <img alt=\"\" src=\"./images/gnomes/icons/icon-arrow-7.png\">";
    str += "            </span>";
    str += "          </span>";
    str += "        </div>";
    str += "        <div class=\"common-flexMenu-function-box common-flexMenu-size\" style=\"\">";
    str += "          <div style=\"padding-left: 10px; -ms-word-break: break-all;\">"
            + $('#msgBoxLabelOccrUserName').text() + occrUserName + "</div>";
    str += "          <div style=\"padding-left: 10px; -ms-word-break: break-all;\">"
            + $('#msgBoxLabelOccrHost').text() + occrHost + "</div>";
    if (messageDetail != null && messageDetail.length > 0) {
        str += "          <div style=\"padding-left: 10px; -ms-word-break: break-all;\">"
                + $('#msgBoxLabelmessageDetail').text() + "</div>";
        str += "          <div class=\"common-messagedialog-detail\">" + messageDetail;
        str += "          </div>";
    }

    str += "        </div>";
    str += "      </div>";
    str += "    </div>";
    if (linkInfo && linkInfo[0] != null && linkInfo[0] != '') {
        // モーダルダイアログのガイダンス部の作成
        str += MakeModalGuidance(linkInfo[0], linkInfo[1], linkInfo[2]);
    }

    str += "  </div>";
    str += "  <div class=\"common-dialog-footer clearfix\">";

    var okOnclick = "";
    var cancelOnClick = "return false;";

    if (commands != null && commands != "" && commands != undefined) {

        okOnclick = "commandSubmit('" + commands
                + "'); $('#message-dialog').modal('hide');$('#messageModal').html(''); return false;";
    }
    else {
        okOnclick = "$('#message-dialog').modal('hide');$('#messageModal').html(''); return false;";
    }
    if (okOnClickFunc != null && okOnClickFunc != "" && okOnClickFunc != undefined) {
         if (buttonId != null) {
             okOnclick = "doubleCheck();setButtonId('"+ buttonId + "');" + "$('#message-dialog').modal('hide');" + okOnClickFunc + " return false;";
         } else {
             okOnclick = "$('#message-dialog').modal('hide');" + okOnClickFunc + " return false;";
         }
    }
    else {
        okOnclick = "$('#message-dialog').modal('hide');$('#messageModal').html(''); return false;";
    }
    if (cancelOnClickFunc != null && cancelOnClickFunc != "" && cancelOnClickFunc != undefined) {
        cancelOnClick = "$('#message-dialog').modal('hide'); " + cancelOnClickFunc + " return false;";
    }
    else {
        cancelOnClick = "$('#message-dialog').modal('hide');$('#messageModal').html(''); return false;";
    }

    switch (msgBtnMode) {
    case MesBtnMode_OK:

        // OK
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" name=\"common-dialog-ok\" id=\"ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameOk').text() + "</span></a>";
        str += "    </div>";
        break;

    case MesBtnMode_OKCancel:

        // Cancel
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" name=\"common-dialog-cancel\" id=\"cancel\" onclick=\""
                + cancelOnClick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameCancel').text() + "</span></a>";
        str += "    </div>";
        // OK
        str += "    <div class=\"common-dialog-footer-button-left\">";
        str += "      <a class=\"modal-focus\" name=\"common-dialog-ok\" id=\"ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameOk').text() + "</span></a>";
        str += "    </div>";
        break;
    default:
        // OK
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" name=\"common-dialog-ok\" id=\"ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameOk').text() + "</span></a>";
        str += "    </div>";
        break;
    }

    str += "  </div>";
    str += "</div>";
    str += "</div>";
    str += "</div>";

    $('#messageModal').html(str);
    $('#message-dialog').on('shown.bs.modal', function() {
        //$('#ok').focus();
    }).modal();

    // ダイアログ表示後に初期のフォーカス設定
    if (defaultBtn == 2) {
        $('#cancel').focus();
    }
    else if (defaultBtn == 1) {
        $('#ok').focus();
    }
    else {
        // メッセージデフォルトボタンモードが【０】場合、フォーカスしない
        $(function() {
           $(":focus").each(function() {
                $('[name=' + this.name + ']').blur();
            });
        });
    }

    // 詳細メッセージ部の開閉
    $(function() {
        $('.common-dialog-function-header').on('click', function() {

            // 展開縮小をコントロール
            if ($('.dialog-message-unit').length > 0) {
                // 親要素にcloseを付与
                $(this).closest('.dialog-message-unit').toggleClass('dialog-message-close');

            }

            // 隣接するdiv要素にクラス追加
            $('+div', this).toggleClass('common-menu-nonShowY');
            // アイコンの向きを反転させる
            $(this).find('img').toggleClass('common-icon-roteto180');

        });
    });
}
// 日付入力
$(function() {
    $(".datepicker").datepicker({
        "dateFormat" : "yy/mm/dd"
    });
});

//===================================================================
/**
 * [機能] モーダルダイアログのガイダンス部の作成
 * [引数] guindanceMessage        ガイダンスメッセージ
 *          linkURL     URL
 *          linkName    リンク名
 * [戻値] 結果      html
 */
//===================================================================
function MakeModalGuidance(guidanceMessage, linkURL, linkName) {
    var str = "    <div class=\"common-messagedialog-separation\"></div>";
    str += "    <div class=\"modal-guidance\" style=\"height: 40px;\">" + guidanceMessage + "<br>";
    str += "      <a class=\"common-link\" href=\"" + linkURL + "\" target=\"_blank\">" + linkName + "</a>";
    str += "    </div>";
    str += "    <div class=\"common-messagedialog-separation\"></div>";

    return str;
}

// メッセージダイアログ
function MakeModalMessage(title, message, okBtnLabel, okCommand, onclick, linkInfo, msgBtnMode) {
    if (okCommand == undefined) {
        okCommand = null;
    }

    // モーダルダイアログのヘッダとボディの作成
    var str = MakeModalHeaderBody(title, message, "", "my-dialog");

    if (linkInfo && linkInfo[0] != null && linkInfo[0] != '') {
        // モーダルダイアログのガイダンス部の作成
        str += MakeModalGuidance(linkInfo[0], linkInfo[1], linkInfo[2]);
    }

    str += "  </div>";
    str += "  <div class=\"common-dialog-footer clearfix\" style=\"z-index: 6000\">";

    var okOnclick = "";

    if (okCommand != null && okCommand != "" && okCommand != undefined) {

        okOnclick = "commandSubmit('" + okCommand
                + "'); $('#my-dialog').modal('hide');$('#myModal').html(''); return false;";
    }
    else if (onclick != null && onclick != "" && onclick != undefined) {
        okOnclick = onclick + " return false;";
    }
    else {
        okOnclick = "$('#my-dialog').modal('hide');$('#myModal').html(''); return false;";
    }

    switch (msgBtnMode) {
    case MesBtnMode_OK:

        // OK
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" id=\"ok\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameOk').text() + "</span></a>";
        str += "    </div>";
        break;

    case MesBtnMode_OKCancel:

        // Cancel
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" id=\"cancel\" name=\"common-dialog-cancel\" onclick=\"$('#my-dialog').modal('hide');$('#myModal').html('');  return false;\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameCancel').text() + "</span></a>";
        str += "    </div>";
        // OK
        str += "    <div class=\"common-dialog-footer-button-left\">";
        str += "      <a class=\"modal-focus\" id=\"ok\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameOk').text() + "</span></a>";
        str += "    </div>";
        break;
    default:
        // OK
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" id=\"ok\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameOk').text() + "</span></a>";
        str += "    </div>";
        break;
    }

    str += "    </div>";
    str += "  </div>";
    str += "</div>";
    str += "</div>";
    $('#myModal').html(str);
    $('#my-dialog').on('shown.bs.modal', function() {
        //$('#ok').focus();
    }).modal();
}

// パスワード変更ダイアログより上に表示するメッセージダイアログ
function MakeModalMessageMiddle(title, message, okBtnLabel, okCommand, onclick, linkInfo, msgBtnMode) {
    $('#confirm-dialog').modal('hide');
    if (okCommand == undefined) {
        okCommand = null;
    }

    // モーダルダイアログのヘッダとボディの作成
    var str = MakeModalHeaderBody(title, message, "modal-dialog-style", "my-dialog1");

    if (linkInfo && linkInfo[0] != null && linkInfo[0] != '') {
        // モーダルダイアログのガイダンス部の作成
        str += MakeModalGuidance(linkInfo[0], linkInfo[1], linkInfo[2]);
    }

    str += "  </div>";
    str += "  <div class=\"common-dialog-footer clearfix\" style=\"z-index: 6000\">";

    var okOnclick = "";

    if (okCommand != null && okCommand != "" && okCommand != undefined) {

        okOnclick = "commandSubmit('" + okCommand
                + "');$('#my-dialog1').modal('hide');$('#myModalMiddle').html('');  return false;";
    }
    else if (onclick != null && onclick != "" && onclick != undefined) {
        okOnclick = onclick + " $('#my-dialog1').modal('hide');$('#myModalMiddle').html('');  return false;";
    }
    else {
        okOnclick = "$('#my-dialog1').modal('hide');$('#myModalMiddle').html(''); return false;";
    }

    switch (msgBtnMode) {
    case MesBtnMode_OK:

        // OK
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a id=\"ok\" class=\"modal-focus\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + okBtnLabel + "</span></a>";
        str += "    </div>";
        break;

    case MesBtnMode_OKCancel:

        // Cancel
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a id=\"cancel\" class=\"modal-focus\" name=\"common-dialog-cancel\" onclick=\"$('#my-dialog1').modal('hide');$('#myModalMiddle').html('');  return false;\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameCancel').text() + "</span></a>";
        str += "    </div>";
        // OK
        str += "    <div class=\"common-dialog-footer-button-left\">";
        str += "      <a id=\"ok\" class=\"modal-focus\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + okBtnLabel + "</span></a>";
        str += "    </div>";
        break;
    default:
        // OK
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a id=\"ok\" class=\"modal-focus\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + okBtnLabel + "</span></a>";
        str += "    </div>";
        break;
    }

    str += "    </div>";
    str += "  </div>";
    str += "</div>";
    str += "</div>";

    $('#myModalMiddle').html(str);
    $('#my-dialog1').on('shown.bs.modal', function() {
        //$('#ok').focus();
    }).modal();
}

// ブックマークダイアログ
function MakeModalBookmark(title, message, okBtnLabel, onclickValue) {

    var format = $('[name=dateFormat]').val() + ":ss";
    var dateFormat = comDateFormat(new Date(), format);

    // ダイアログの内容
    var str = "<div class=\"modal\" id=\"confirm-dialog\" data-keyboard=\"false\" data-backdrop=\"static\" >";
    str += "<div class=\"modal-dialog\">";
    str += "<div class=\"common-dialog-content\" style=\"width: 600px;\">";
    str += "  <div class=\"common-dialog-header-title clearfix\">" + title + "</div>";
    str += "  <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "    <div class=\"common-dialog-body-column\">";
    str += "      <table>";
    str += "        <tbody>";
    str += "          <tr>";
    str += "            <td rowspan=\"2\">";
    str += "              <img src=\"./images/gnomes/icons/kaku.png\" class=\"common-dialog-guidance-icon\">";
    str += "            </td>";
    str += "            <td style=\"height: 20px; font-size: 8pt;\">" + dateFormat;
    str += "            </td>";
    str += "          </tr>";
    str += "          <tr>";
    str += "            <td style=\"-ms-word-break: break-all;\">" + message;
    str += "            </td>";
    str += "          </tr>";
    str += "        </tbody>";
    str += "      </table>";
    str += "    </div>";
    str += "  </div>";
    str += "  <div class=\"common-dialog-footer clearfix\" style=\"z-index: 6000\">";

    var okOnclick = "";

    if (onclickValue != null && onclickValue != "" && onclickValue != undefined) {
        okOnclick = "bookmark_command('" + onclickValue + "');$('#confirm-dialog').modal('hide');$('#confirm-dialog').html(''); return false;";

        // Cancel
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" id=\"cancel\" name=\"common-dialog-cancel\" onclick=\"$('#confirm-dialog').modal('hide');$('#myModal').html('');  return false;\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameCancel').text() + "</span></a>";
        str += "    </div>";
        // OK
        str += "    <div class=\"common-dialog-footer-button-left\">";
        str += "      <a class=\"modal-focus\" id=\"ok\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + okBtnLabel + "</span></a>";
        str += "    </div>";
    }
    else {
        okOnclick = "$('#confirm-dialog').modal('hide');$('#myModal').html(''); return false;";
    }

    str += "    </div>";
    str += "  </div>";
    str += "</div>";
    str += "</div>";

    $('#myModal').html(str);
    $('#confirm-dialog').on('shown.bs.modal', function() {
        //$('#ok').focus();
    }).modal();
}

//ロック解除ダイアログより上に表示するメッセージダイアログ
function MakeModalMessageHigh(title, message, okBtnLabel, okCommand, onclick, linkInfo, msgBtnMode) {
    if (okCommand == undefined) {
        okCommand = null;
    }

    // モーダルダイアログのヘッダとボディの作成
    var str = MakeModalHeaderBody(title, message, "modal-dialog-style", "my-dialog2");

    if (linkInfo && linkInfo[0] != null && linkInfo[0] != '') {
        // モーダルダイアログのガイダンス部の作成
        str += MakeModalGuidance(linkInfo[0], linkInfo[1], linkInfo[2]);
    }

    str += "  </div>";
    str += "  <div class=\"common-dialog-footer clearfix\" style=\"z-index: 6000\">";

    var okOnclick = "";

    if (okCommand != null && okCommand != "" && okCommand != undefined) {

        okOnclick = "commandSubmit('" + okCommand
                + "');$('#my-dialog2').modal('hide');$('#myModalHigh').html('');  return false;";
    }
    else if (onclick != null && onclick != "" && onclick != undefined) {
        okOnclick = onclick + " $('#my-dialog2').modal('hide');$('#myModalHigh').html('');  return false;";
    }
    else {
        okOnclick = "$('#my-dialog2').modal('hide');$('#myModalHigh').html(''); return false;";
    }

    switch (msgBtnMode) {
    case MesBtnMode_OK:

        // OK
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" id=\"ok\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + okBtnLabel + "</span></a>";
        str += "    </div>";
        break;

    case MesBtnMode_OKCancel:

        // Cancel
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" id=\"cancel\" name=\"common-dialog-cancel\" onclick=\"$('#my-dialog2').modal('hide');$('#myModalHigh').html('');  return false;\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + $('#msgBoxBtnNameCancel').text() + "</span></a>";
        str += "    </div>";
        // OK
        str += "    <div class=\"common-dialog-footer-button-left\">";
        str += "      <a class=\"modal-focus\" id=\"ok\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + okBtnLabel + "</span></a>";
        str += "    </div>";
        break;
    default:
        // OK
        str += "    <div class=\"common-dialog-footer-button-right\">";
        str += "      <a class=\"modal-focus\" id=\"ok\" name=\"common-dialog-ok\" onclick=\""
                + okOnclick
                + "\" href=\"#\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%; font-size: 18px; padding-top: 15px; height: 30px;\">"
                + okBtnLabel + "</span></a>";
        str += "    </div>";
        break;
    }

    str += "    </div>";
    str += "  </div>";
    str += "</div>";
    str += "</div>";

    $('#myModalHigh').html(str);
    $('#my-dialog2').on('shown.bs.modal', function() {
        //$('#ok').focus();
    }).modal();
}

//===================================================================
/**
 * [機能] モーダルダイアログのヘッダとボディの作成
 * [引数] title       タイトル文言
 *          message     ダイアログメッセージ
 * [戻値] 結果      html
 */
//===================================================================
function MakeModalHeaderBody(title, message, addclass, id) {
    var format = $('[name=dateFormat]').val() + ":ss";
    var dateFormat = comDateFormat(new Date(), format);

    //  var str = "<div class=\"common-dialog-content\" id=\"my-dialog\""+ strZIndex + "><div class=\"modal-content\"><div class=\"modal-header\">";
    //  str += "<button type=\"button\" class=\"close\" data-dismiss=\"modal\"><span ariea-hidden=\"true\"></span></button>";
    //  str += "<h4 class=\"modal-title\" id=\"modal-label\">" + title + "</h4></div>";
    //  str += "<div class=\"modal-body\">";
    //  str += "<table>";
    //  str += "<tr>";
    //

    var str = "<div class=\"modal\" id=\"" + id + "\" data-keyboard=\"false\" data-backdrop=\"static\" >";
    str += "<div class=\"modal-dialog " + addclass + "\">";
    str += "<div class=\"common-dialog-content common-dialog-content-style\" style=\"width: 600px; display: inline-block;\">";
    str += "  <div class=\"common-dialog-header-title clearfix\">" + title + "</div>";
    str += "  <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "    <div class=\"common-dialog-body-column\">";
    str += "      <table>";
    str += "          <tr>";

    // タイトルからアイコンを設定
    // 情報
    if (title == $('#msgBoxTitleInfo').text()) {
        str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/mes.png\" class=\"common-dialog-guidance-icon\"/></td>";
        // 警告
    }
    else if (title == $('#msgBoxTitleWarning').text()) {
        str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/minor-process-alert.png\" class=\"common-dialog-guidance-icon\"/></td>";
        // 確認
    }
    else if (title == $('#msgBoxTitleConfirm').text()) {
        str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/kaku.png\" class=\"common-dialog-guidance-icon\"/></td>";
        // メッセージ
    }
    else if (title == $('#msgBoxTitleMessage').text()) {
        str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/mes.png\" class=\"common-dialog-guidance-icon\"/></td>";
    }
    else {
        str += "<td rowspan=\"2\"><img src=\"./images/gnomes/icons/batu.png\" class=\"common-dialog-guidance-icon\"/></td>";
    }
    str += "<td style=\"font-size: 8pt; height:20px;\">" + dateFormat + "</td>";
    str += "</tr>";
    str += "<tr>";
    str += "<td>" + message + "</td>";
    str += "</tr>";
    str += "</table>";
    str += "</div>";

    return str;
}

// ロック解除ダイアログの表示
function MakeLockModal(isPanecon) {
    var paneconDialogStyle = "";
    var keyboardClass = "";

    //工程端末表示の場合
    if (isPanecon) {
        paneconDialogStyle = " style=\"line-height: 18px;\"";
        keyboardClass = " common-keyboard-input-char";
    }

    var str = "<div class=\"modal\" id=\"lock-dialog\" data-keyboard=\"false\" data-backdrop=\"false\">";
    str += "<div class=\"modal-dialog\"" + paneconDialogStyle + ">";
    str += "  <div class=\"common-dialog-content modal-dialog\" >";

    // タイトル
    str += "    <div class=\"common-dialog-header-title clearfix common-dialog-title-style\">"
            + $('#msgLockDialogTitle').text() + "</div>";
    str += "    <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "      <div class=\"common-dialog-body-column\">";
    // ユーザID
    str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
            + $('#msgConfirmDialogUserId').text() + "</div>";
    str += "        <div class=\"common-dialog-header-col-data\">";
    str += "          <input id=\"login_user_id\" name=\"loginUserId\" class=\"common-dialog-dataarea-col-data modal-focus"
            + keyboardClass + "\" type=\"text\" readonly value=\"" + $('[name=sessionLoginUserId]').val() + "\" onInput=\"checkInputKana(this)\">";
    str += "        </div>";
    str += "        <br>";
    // パスワード
    str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
            + $('#msgConfirmDialogPassword').text() + "</div>";
    str += "        <div class=\"common-dialog-header-col-data\">";
    str += "          <input id=\"login_password\" class=\"common-dialog-dataarea-col-data modal-focus" + keyboardClass
            + "\" type=\"password\"  value=\"\">";
    str += "        </div>";
    str += "        <br>";
    str += "      </div>";
    str += "    </div>";
    str += "    <div class=\"common-dialog-footer clearfix\">";
    // ロック解除
    str += "      <div class=\"login-dialog-footer-button-left\">";
    str += "        <a id=\"certify-button\" class=\"modal-focus\" onclick=\"certifyForResume(); return false;\" href=\"#\">";
    str += "          <span class=\"common-button common-dialog-button\" style=\"width: 90%;\">"
            + $('#msgLockDialogTitle').text() + "</span></a>";
    str += "      </div>";
    // ログアウト
    str += "      <div class=\"login-dialog-footer-button-right\">";
    str += "        <a id=\"logout-button\" class=\"modal-focus\" onclick=\"MakeModalMessageHigh('"
            + $('#msgBoxTitleConfirm').text() + "', '" + $('#msgLogoutMessage').text() + "', '"
            + $('#msgBoxBtnNameLogout').text() + "', '','logout();','',MesBtnMode_OKCancel);\" href=\"#\">";
    str += "          <span class=\"common-button common-dialog-button\" style=\"width: 90%;\">"
            + $('#msgBoxBtnNameLogout').text() + "</span></a>";
    str += "      </div>";
    str += "    </div>";
    str += "  </div>";
    str += "</div>";
    str += "</div>";

    $('#lock-modal').html(str);
     //開いたキーボードを削除する
    $( ".ui-keyboard" ).remove();
    //add custom backdrop for lock modal with new class(lock-backdrop)
    $('<div class="modal-backdrop in lock-backdrop"></div>').appendTo(document.body);

    $('#lock-dialog').on('shown.bs.modal', function() {
        $('#login_password').val("");
        if (!isPanecon) {
            // 初期表示フォーカス設定
            $('#login_password').focus();
            $(".common-keyboard-input-char").keyboard({
                userClosed: true
            })
            .addTyping();
        }
    }).modal();

}

//パスワード変更ダイアログの作成
function createChangePasswordModal(flag) {
    var userId = $('#userId').html();
    MakeChangePasswordModal(userId, flag);
}

//パスワード変更ダイアログの表示
function MakeChangePasswordModal(userId, isPanecon) {

    var paneconDialogStyle = "";
    var inputClass = "common-dialog-dataarea-col-data";
    var keyboardClass = "";
    var oldPasswordKeyboardClass = "";
    var modalFocusClass = "modal-focus ";
    //工程端末表示の場合
    if (isPanecon) {
        paneconDialogStyle = " style=\"line-height: 18px;\"";
        keyboardClass = " common-keyboard-input-char";
        oldPasswordKeyboardClass = " common-keyboard-input-char";
        modalFocusClass = "";
    }

    var str = "<div class=\"modal\" id=\"change-pass-dialog\" data-keyboard=\"false\" data-backdrop=\"static\">";
    str += "<div class=\"modal-dialog\"" + paneconDialogStyle + ">";
    str += "  <div class=\"common-dialog-content modal-dialog\" >";
    // タイトル
    str += "    <div class=\"common-dialog-header-title clearfix common-dialog-title-style\">"
            + $('#msgPasswordChangeDialogTitle').text() + "</div>";
    str += "    <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "      <div class=\"common-dialog-body-column\">";
    // メッセージ
    str += "        <div class=\"common-dialog-guidance-message\">" + $('#msgPasswordChangeDialogMessage').text()
            + "</div>";
    // ユーザID
    str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
            + $('#msgConfirmDialogUserId').text() + "</div>";
    str += "        <div class=\"common-dialog-header-col-data\">";
    str += "          <input id=\"change_user_id\" name=\"loginUserId\" class=\"" + inputClass
            + "\" type=\"text\" readonly value=\"" + userId + "\">";
    str += "        </div>";
    str += "        <br>";

    var strReadOnly = "";

    if (userId != $('[name=sessionLoginUserId]').val()) {
        strReadOnly = " readonly";
        oldPasswordKeyboardClass = "";
        modalFocusClass = "";
    }

    // 旧パスワード
    str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
            + $('#msgPasswordChangeDialogOldPassword').text() + "</div>";
    str += "        <div class=\"common-dialog-header-col-data\">";
    str += "          <input id=\"login_old_password\" class=\"" + modalFocusClass + inputClass
            + oldPasswordKeyboardClass + "\" type=\"password\" value=\"\"" + strReadOnly + ">";
    str += "        </div>";
    str += "        <br>";
    // 新パスワード
    str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
            + $('#msgPasswordChangeDialogNewPassword').text() + "</div>";
    str += "        <div class=\"common-dialog-header-col-data\">";
    str += "          <input id=\"login_new_password\" class=\"modal-focus " + inputClass + keyboardClass
            + "\" type=\"password\" value=\"\">";
    str += "        </div>";
    str += "        <br>";
    // パスワード確認
    str += "        <div class=\"common-header-col-title common-client-header-col-data\">"
            + $('#msgPasswordChangeDialogConfirmPassword').text() + "</div>";
    str += "        <div class=\"common-dialog-header-col-data\">";
    str += "          <input id=\"login_new_password_confirm\" class=\"modal-focus " + inputClass + keyboardClass
            + "\" type=\"password\" value=\"\">";
    str += "        </div>";
    str += "        <br>";
    str += "      </div>";
    str += "    </div>";

    // 工程端末表示の場合
    if (isPanecon) {

        str += "    <div class=\"common-dialog-footer clearfix\">";
        // OK
        str += "      <div class=\"login-dialog-footer-button-left\">";
        str += "        <a id=\"update_psw_btn\" class=\"modal-focus\" onclick=\"changePassword(0); return false;\" href=\"#\">";
        str += "          <span class=\"common-button common-dialog-button\" style=\"width: 90%;\">"
                + $('#msgBoxBtnNameOk').text() + "</span>";
        str += "        </a>";
        str += "      </div>";
        // キャンセル
        str += "      <div class=\"login-dialog-footer-button-right\">";
        str += "        <a class=\"modal-focus\" onclick=\"$('#change-pass-dialog').modal('hide');$('#change-pass-modal').html('');setLockTimer(); return false;\" href=\"#\">";
        str += "          <span class=\"common-button common-dialog-button\" style=\"width: 90%;\">"
                + $('#msgBoxBtnNameCancel').text() + "</span>";
        str += "        </a>";
        str += "      </div>";
        str += "    </div>";
    }
    else {

        str += "    <div class=\"common-dialog-footer-bottom-3button clearfix\">";
        // OK
        str += "      <div class=\"common-dialog-footer-button-3\">";
        str += "        <a id=\"update_psw_btn\" class=\"modal-focus\" onclick=\"changePassword(0); return false;\" href=\"#\">";
        str += "          <span class=\"common-button common-dialog-button\" style=\"width: 80%;\">"
                + $('#msgBoxBtnNameOk').text() + "</span>";
        str += "        </a>";
        str += "      </div>";
        // キャンセル
        str += "      <div class=\"common-dialog-footer-button-3\">";
        str += "        <a class=\"modal-focus\" onclick=\"$('#change-pass-dialog').modal('hide');$('#change-pass-modal').html('');setLockTimer(); return false;\" href=\"#\">";
        str += "          <span class=\"common-button common-dialog-button\" style=\"width: 80%;\">"
                + $('#msgBoxBtnNameCancel').text() + "</span>";
        str += "        </a>";
        str += "      </div>";
        // パスワード初期化
        str += "      <div class=\"common-dialog-footer-button-3\">";
        str += "        <a id=\"update_initial_psw_btn\" class=\"modal-focus\" onclick=\"changePassword(1); return false;\" href=\"#\">";
        str += "          <span class=\"common-button common-dialog-button\" style=\"width: 85%;\">"
                + $('#msgPasswordChangeDialogInitPassword').text() + "</span>";
        str += "        </a>";
        str += "      </div>";
        str += "    </div>";
    }
    str += "  </div>";
    str += "</div>";
    str += "</div>";

    str += "<input type=\"hidden\" name=\"isInitPassword\" value=\"\"/>";

    $('#change-pass-modal').html(str);

    // パスワード変更の為、ユーザ認証チェックする処理
    addAuthenticationProcessForUpdPsw();

    $('#change-pass-dialog').on('shown.bs.modal', function(e) {
        var pIsPanecon = e.relatedTarget.isPanecon;
        var pUserId = e.relatedTarget.userId;
        var pSessionLoginUserId = e.relatedTarget.sessionLoginUserId;

        if (pIsPanecon === false) {
            // 初期表示フォーカス設定
            if (pUserId != pSessionLoginUserId) {
                $('#login_new_password').focus();
            }
            else {
                $('#login_old_password').focus();
            }
        }

    }).modal({}, {
        isPanecon : isPanecon,
        userId : userId,
        sessionLoginUserId : $('[name=sessionLoginUserId]').val()
    });
}

//メッセージボタンモード
const
MesBtnMode_OK = '1'; // [OK] ボタン
const
MesBtnMode_OKCancel = '2'; // [OK] ボタンと [キャンセル] ボタン

//メッセージデフォルトボタンモード
const
MesDefaultBtn_Button1 = '0'; // 最初のボタン
const
MesDefaultBtn_Button2 = '1'; // 2 番目のボタン
const
MesDefaultBtn_Button3 = '2'; // 3 番目のボタン

// 押下ボタンIDの設定
function setButtonId(buttonId) {
    // hiddenパラメータ書き換え
    $('input[name=buttonId]').val(buttonId);
}

//代替フラグの設定
function setSubstituteFlag (substituteFlag) {
    // hiddenパラメータ書き換え
    $('input[name=substituteFlag]').val(substituteFlag);
}

/*
 * bootstrapのモーダルダイアログ多重表示の手前表示処理
 */
$(function() {
    $(document).on('show.bs.modal', '.modal', function() {
        var maxZ = parseInt($('.modal-backdrop').css('z-index')) || 1040;

        $('.modal:visible').each(function() {
            //メッセージダイアログのZ-Indexを無視する
            //それ以外のダイアログの関係を調べる
            if($(this).attr("id") != "message-dialog"){
                maxZ = Math.max(parseInt($(this).css('z-index')), maxZ);
            }
        });
        $('.modal-backdrop').css('z-index', maxZ);

        //メッセージダイアログについては特別全面表示
        if($(this).attr("id") === "message-dialog"){
            maxZ=10000;
        }

        $(this).css("z-index", maxZ + 1);
        $('.modal-dialog', this).css("z-index", maxZ + 2);

        //stack モーダルの表示
        var zIndex = 1040 + (10 * $('.modal:visible').length);
        $(this).css('z-index', zIndex);
        setTimeout(function() {
            $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
        }, 0);

        //メッセージダイアログがあったらそちらが上になる
        $('#message-dialog').css('z-index', zIndex + 100);

        setModalFocusLoop(this);
    });

    $(document).on('hidden.bs.modal', '.modal', function() {
        if ($('.modal:visible').length) {
            $(document.body).addClass('modal-open');

            var maxZ = 1040;

            $('.modal:visible').each(function() {
                maxZ = Math.max(parseInt(jQuery(this).css('z-index')), maxZ);
            });

            $('.modal-backdrop').css('z-index', maxZ - 1);

        }

        // バーコードにフォーカスを戻す
        if ($("input").hasClass("common-input-barcode-style")) {
            var barcodeInput = $(".common-input-barcode-style").attr("name");
            $("[name='"+ barcodeInput + "']").focus();
            $("[name='"+ barcodeInput + "']").select();
        }

        // フォーカスを戻す
        if (backFocusArray.length > 0) {
            var f = backFocusArray.pop();
            f.focus();
        }
        // 固定ヘッダの再設定
        if(typeof FixedMidashi !== 'undefined') {
            FixedMidashi.create();

            // 固定ヘッダにダブルクリックでのイベントを追加
            setDblClickEvent();
        }

    });

    // フォーカス位置記憶領域
    var backFocusArray = new Array();

    $(document).on('show.bs.modal', '.modal', function() {
        // モーダル表示前のフォーカスを記憶
        backFocusArray.push($(':focus'));
    });


});

/*
 * bootstrapのモーダルダイアログ内のフォーカス制御
 */
function setModalFocus(el, e) {

    var tabbables = $(el).parents('.modal').find('.modal-focus'), first = tabbables.filter(':first'), last = tabbables
            .filter(':last');

    // 選択除隊解除
    el.value = el.value;

    if (el === last[0]) {
        if (e.shiftKey === false) {
            first[0].focus();
            return false;
        }
    }
    if (el === first[0]) {
        if (e.shiftKey === true) {
            last[0].focus();
            return false;
        }
    }
    return true;
}

/*
 * bootstrapのモーダルダイアログ内のTABキーでのフォーカス制御
 */
function setModalFocusLoop(el) {

    $('.modal-focus', el).each(function() {
        $(this).keydown(function(e) {

            if (e.which === 9) {
                return setModalFocus(this, e);
            }
            else {
                return true;
            }
        });
    });
}

//===================================================================
/**
* [機能]  日付オブジェクトから文字列に変換します
* [引数]  date    対象の日付オブジェクト
*           format  フォーマット
* [戻値]  フォーマット後の文字列
*/
//===================================================================
function comDateFormat(date, format) {

    var result = format;

    var f;
    var rep;

    f = 'YYYY';
    if (result.indexOf(f) > -1) {
        rep = date.getFullYear();
        result = result.replace(/YYYY/, rep);
    }

    f = 'MM';
    if (result.indexOf(f) > -1) {
        rep = comPadZero(date.getMonth() + 1, 2);
        result = result.replace(/MM/, rep);
    }

    f = 'DD';
    if (result.indexOf(f) > -1) {
        rep = comPadZero(date.getDate(), 2);
        result = result.replace(/DD/, rep);
    }

    f = 'HH';
    if (result.indexOf(f) > -1) {
        rep = comPadZero(date.getHours(), 2);
        result = result.replace(/HH/, rep);
    }

    f = 'mm';
    if (result.indexOf(f) > -1) {
        rep = comPadZero(date.getMinutes(), 2);
        result = result.replace(/mm/, rep);
    }

    f = 'ss';
    if (result.indexOf(f) > -1) {
        rep = comPadZero(date.getSeconds(), 2);
        result = result.replace(/ss/, rep);
    }

    f = 'fff';
    if (result.indexOf(f) > -1) {
        rep = comPadZero(date.getMilliseconds(), 3);
        result = result.replace(/fff/, rep);
    }

    return result;

}

//===================================================================
/**
* [機能]  文字列から日付オブジェクトに変換します
* [引数]  date    日付を表す文字列
*           format  フォーマット
* [戻値]  変換後の日付オブジェクト
*/
//===================================================================
function comDateParse(date, format) {

    var year = 1990;
    var month = 01;
    var day = 01;
    var hour = 00;
    var minute = 00;
    var second = 00;
    var millisecond = 000;

    var f;
    var idx;

    f = 'YYYY';
    idx = format.indexOf(f);
    if (idx > -1) {
        year = date.substr(idx, f.length);
    }

    f = 'MM';
    idx = format.indexOf(f);
    if (idx > -1) {
        month = parseInt(date.substr(idx, f.length), 10) - 1;
    }

    f = 'DD';
    idx = format.indexOf(f);
    if (idx > -1) {
        day = date.substr(idx, f.length);
    }

    f = 'HH';
    idx = format.indexOf(f);
    if (idx > -1) {
        hour = date.substr(idx, f.length);
    }

    f = 'mm';
    idx = format.indexOf(f);
    if (idx > -1) {
        minute = date.substr(idx, f.length);
    }

    f = 'ss';
    idx = format.indexOf(f);
    if (idx > -1) {
        second = date.substr(idx, f.length);
    }

    f = 'fff';
    idx = format.indexOf(f);
    if (idx > -1) {
        millisecond = date.substr(idx, f.length);
    }

    var result = new Date(year, month, day, hour, minute, second, millisecond);

    return result;

}

//===================================================================
/**
* [機能]  ゼロパディングを行います
* [引数]  value   対象の文字列
*           length  長さ
* [戻値]  結果文字列
*/
//===================================================================
function comPadZero(value, length) {
    return new Array(length - ('' + value).length + 1).join('0') + value;
}

//===================================================================
/**
 * [機能] 共通パラメータを取得
 * [引数] command フォームデータ
 *          inArray     追加データ
 * [戻値] Array       キー：パラメータ名 値：パラメータ値
 */
//===================================================================
function get_common_command_base(command, inArray) {

    var result = new Array();

    if (inArray != undefined && inArray != null) {
        for ( var key in inArray) {
            result[key] = inArray[key];
        }
    }

    result['command'] = command;

    if ($(':hidden[name="pageToken"]').length) {
        var pageToken = $(':hidden[name="pageToken"]').val();
        result['pageToken'] = pageToken;
    }

    if ($('#loginUserId').length) {
        var loginUserId = $('#loginUserId').val();
        result['loginUserId'] = loginUserId;
    }

    if ($('#loginUserPassword').length) {
        var loginUserPassword = $('#loginUserPassword').val();
        result['loginUserPassword'] = loginUserPassword;
    }

    if ($('#certUserId').length) {
        var certUserId = $('#certUserId').val();
        result['certUserId'] = certUserId;
    }

    if ($('#certUserPassword').length) {
        var certUserPassword = $('#certUserPassword').val();
        result['certUserPassword'] = certUserPassword;
    }

    if ($(':hidden[name="buttonId"]').length) {
        var buttonId = $(':hidden[name="buttonId"]').val();
        result['buttonId'] = buttonId;
    }

    if ($(':hidden[name="isDoubleCheck"]').length) {
        var isDoubleCheck = $(':hidden[name="isDoubleCheck"]').val();
        result['isDoubleCheck'] = isDoubleCheck;
    }

    return result;
}

//===================================================================
/**
 * [機能] ajax通信共通処理
 * [引数] action      url
 *          formData    送信フォームデータ
 *          callbacks   コールバック
 * [戻値] なし
 */
//===================================================================
function ajax_submit(action, formData, callbacks) {

    // コールバックのデフォルト値を設定
    var defaults = {
        'begin' : function() {
        },
        'success' : function() {
        },
        'cmdsuccess' : function() {
        },
        'cmderror' : function() {
        },
        'successfinal' : function() {
        },
        'error' : function() {
        },
        'complete' : function() {
        },
    };
    // デフォルト値とマージ
    var callbacks = $.extend(defaults, callbacks);
    // 開始コールバックを起動
    callbacks['begin']();

    // 非同期通信 -> 同期型に変更
    $.ajax({
        url : action,
        type : 'POST',
        data : formData,
        contentType : false,
        async: false,
        cache : false,
        processData : false,
        dataType : 'json',
        success : function(data, textStatus, jqXHR) {

            callbacks['success'](data);

            if (data.isSuccess == true) {
                // 成功コールバックを起動
                callbacks['cmdsuccess'](data.commandResponse);
            }
            else {
                // 失敗コールバックを起動
                callbacks['cmderror'](data.commandResponse);

            }

            callbacks['successfinal'](data);

        },
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            // 失敗コールバックを起動
            callbacks['error'](XMLHttpRequest, textStatus, errorThrown);
        },
        complete : function(result) {
            // 完了コールバックを起動
            callbacks['complete'](result);
        }
    });
}

//===================================================================
/**
 * [機能] ajaxでコマンド実行
 * [引数] action      url
 *          command     実行コマンド
 *          formData    送信フォームデータ
 *          callbacks   コールバック
 *          isCheckSessionError     セッションエラー判定フラグ
 * [戻値] なし
 */
//===================================================================
function ajax_submit_command(action, command, formData, callbacks, isCheckSessionError) {

    if (isCheckSessionError === undefined)
        isCheckSessionError = true;

    // コールバックのデフォルト値を設定
    var submit_defaults = {
        'begin' : function() {
            // ロックタイマーのクリア
            clearLockTimer();
            if ('begin' in callbacks) {
                callbacks['begin']();
            }
        },
        'success' : function(data) {
            // 次回ダブル認証表示時の承認者IDを設定
            $(':hidden[name="certUserName"]').val(data.certUserId);

            if ('success' in callbacks) {
                callbacks['success']();
            }
        },
        'cmdsuccess' : function(commandResponse) {
            if ('cmdsuccess' in callbacks) {
                callbacks['cmdsuccess'](commandResponse);
            }
        },
        'cmderror' : function(commandResponse) {
            if ('cmderror' in callbacks) {
                callbacks['cmderror'](commandResponse);
            }
        },
        'successfinal' : function(data) {
            if ('successfinal' in callbacks) {
                callbacks['successfinal'](data);
            }
            if (data.messageBean.message != null) {
                // メッセージダイアログを表示
                var linkInfo = [ '', '', '' ];

                if (data.messageBean.linkInfo != null) {
                    linkInfo = [ data.messageBean.linkInfo[0], data.messageBean.linkInfo[1],
                            data.messageBean.linkInfo[2] ];
                }
                makeMessageModalBase(
                        data.messageBean.categoryName,          //メッセージタイトル
                        data.messageBean.iconName,              //アイコン名
                        data.messageBean.occurDate,             //発生日時
                        data.messageBean.occrUserName,          //操作ユーザ名
                        data.messageBean.occrHost,              //操作ホスト
                        data.messageBean.message,               //メッセージ本文
                        data.messageBean.messageDetail,         //メッセージ詳細
                        data.messageBean.msgBtnMode,            //ボタンモード
                        data.messageBean.defaultBtn,            //デフォルトボタン
                        data.messageBean.command,               //コマンド
                        linkInfo,                               //リンク情報
                        false,                                  //オープン時詳細を開くかどうか
                        data.messageBean.messageOkOnClick,      //OKボタン押下時呼び出し関数
                        data.messageBean.messageCancelOnClick,  //Cancelボタン押下呼び出し関数
                        null,                                   //ボタンID
                        ""                                      //DB領域
                );
            }

            // セッションエラー判定
            if (isCheckSessionError == true && data.isSessionError == true) {
                MakeModalAlert($('#msgBoxTitleError').text(), $('#msgServiceSessionErrMessage').text(), $(
                        '#msgBoxBtnNameOk').text());
            }

        },
        'error' : function() {
            MakeModalAlert($('#msgBoxTitleError').text(), $('#msgServiceErrMessage').text(), $('#msgBoxBtnNameOk')
                    .text());
        },
        'complete' : function() {
            // ロックタイマー再開
            // ロックタイマー設定
            setLockTimer();
        },
    };

    var actionWithParam = action;

    // cid
    var cid = $('#cid').val();
    if (cid != undefined) {
        actionWithParam = actionWithParam + "?cid=" + cid;
    }

    if (formData == null) {
        formData = new FormData();
    }

    // 基本情報の設定
    var paramArray = get_common_command_base(command);
    for ( var key in paramArray) {
        formData.append(key, paramArray[key]);
    }

    ajax_submit(actionWithParam, formData, submit_defaults);
}

//===================================================================
/**
* [機能]  userIdを取得する
* [引数]  なし
* [戻値]  usetId
*/
//===================================================================
function getUserId() {
    return $('#userId').text();
}
//===================================================================
/**
* [機能]  screenIdを取得する
* [引数]  なし
* [戻値]  screenId
*/
//===================================================================
function getScreenId() {
    return $('#screenId').text();
}

//フィルタのセレクトボックス押下時の処理
function addFilter(tableId, filterTagId, isPanecon, index, parameters, patternKeys,columnId) {
    var searchMasterInfo = mstSearchInfoMap[tableId];
    var mstConditions = searchMasterInfo.mstConditions;

    if (index === "" || index == null || index == undefined) {
        var selector = $("[name=" + filterTagId + "_filterSelect]");
        columnId = selector.val();
        index = selector.prop("selectedIndex") -1 ;

    }
    if (parameters == undefined) {
        parameters = [];
    }
    if (parameters[0] == null || parameters[0] == undefined) {
        parameters[0] = "";
    }
    if (parameters[1] == null || parameters[1] == undefined) {
        parameters[1] = "";
    }
    if (parameters[2] == null || parameters[2] == undefined) {
        parameters[2] = "";
    }
    if (parameters[3] == null || parameters[3] == undefined) {
        parameters[3] = "";
    }
    if (patternKeys == null || patternKeys == undefined) {
        patternKeys = "";
    }

    //カラム名
    var columnName = filterTagId + "_" + columnId;

    //表示カラム名
    var text = mstConditions[index]["text"];
    //タイプ
    var type = mstConditions[index]["type"];
    //選択肢
    var patterns = mstConditions[index]["patterns"];
    // テキスト入力補助ダイアログ呼出クラス名
    var textDialogClass = "";

    // 数値入力補助ダイアログ呼出クラス名
    var numberDialogClass = "";

    // プルダウン選択一覧ダイアログ呼出処理
    var pulldownDialogOnclick = "";

    // 日付入力補助ダイアログ呼出クラス名
    var dateDialogClass = "datetime";

    // チェックボックスクラス名
    var checkboxClass = "common-dialog-col-checkbox";

    // チェックボックススタイル
    var checkboxStyle = "style=\"width: 1.0em; float: left;\"";

    // オートコンプリートモード
    var auto_mode = "3";

    // JOBで詳細検索の削除ボタンの表示有無の設定の為、Zm001システム定義マスタのCONDITION_REQUIRED+DISP_DELETE_BUTTONの値を取得
    var dispDeleteButton = $("#dispDeleteButton").val();

    //必須入力
    var requiredType = mstConditions[index]["requiredType"];

    if (isPanecon == true) {
        textDialogClass = "common-keyboard-input-char";
        numberDialogClass = "common-keyboard-input-num";
        checkboxClass = "search-dialog-col-checkbox";
        checkboxStyle = "style=\"width: 1.5em; float: left;\"";
    }

    if ($("[name=" + columnName + "_type]").size() == 0 ||
        ($("[name=" + columnName + "_type]").size() > 0 && $("#" +columnName).css("display") == "none")) {
        // 項目を追加する前に、削除する
        if ($("#" +columnName).css("display") == "none") {
            $("#" +columnName).remove();
        }

        //列追加処理
        //チェックボックス列
        var firstTd = '          <div id="' + columnName + '" columnid="' + columnId + '" class="tr">';
        firstTd += '            <input type="hidden" name="' + columnName + '_type" value="' + type + '">';
        var secondTd = '';
        //type=0:文字入力
        switch (type) {
        case 0:

            pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, $('#msgPulldownTableDialogConditionLike')
                    .text());
            secondTd += '            <div class="search-dialog-col-title">' + text + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + textDialogClass + '" name="' + columnName
                    + '_value" value="' + parameters[0] + '">';

            secondTd += '              <select name="' + columnName + '_condition" class="search_condition_display_none" ' + pulldownDialogOnclick + '>';
            for ( var listKey in patterns) {

                secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
            }
            secondTd += '              </select>';
            secondTd += '            </div>';
            break;

        //type=1:文字 プルダウン
        case 1:
            pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text);

            secondTd += '            <div class="search-dialog-col-title">' + text + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <select name="' + columnName + '_value" ' + pulldownDialogOnclick + '>';
            secondTd += '                <option value=""></option>';
            for ( var listKey in patterns) {
                if (patternKeys != "" && patternKeys[0] == listKey) {
                    secondTd += '                <option value="' + listKey + '" selected="selected">'
                            + patterns[listKey] + '</option>';
                }
                else {
                    secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
                }
            }

            secondTd += '              </select>';
            secondTd += '            </div>';
            break;
        //type=2:数値 プルダウン
        case 2:
            pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text);

            secondTd += '            <div class="search-dialog-col-title">' + text + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <select name="' + columnName + '_value" ' + pulldownDialogOnclick + '>';
            secondTd += '                <option value=""></option>';
            for ( var listKey in patterns) {
                if (patternKeys != "" && patternKeys[0] == listKey) {
                    secondTd += '                <option value="' + listKey + '" selected="selected">'
                            + patterns[listKey] + '</option>';
                }
                else {
                    secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
                }
            }
            secondTd += '              </select>';
            secondTd += '            </div>';
            break;
        //type=3:数値入力
        case 3:
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" name="' + columnName
                    + '_from" class="common-text-number gnomes-number gnomes-number-format ' + numberDialogClass
                    + '" value="' + converterNumberFormatComma(parameters[0]) + '">';
            secondTd += '            </div>';

            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp;</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" name="' + columnName
                    + '_to" class="common-text-number gnomes-number gnomes-number-format ' + numberDialogClass
                    + '" value="' + converterNumberFormatComma(parameters[1]) + '">';
            secondTd += '            </div>';
            break;
        //type=4:日付入力
        case 4:
        case 20:
            var day1 = "";
            var day2 = "";
            if (parameters[2] != "") {
                day1 = getSysDateDiff(parameters[2]);
            }
            if (parameters[3] != "") {
                day2 = getSysDateDiff(parameters[3]);
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From ' + day1 + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDD').val() + '\" name="' + columnName + '_from" value="' + parameters[0] + '">';
            secondTd += '            </div>';

            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp; ' + day2
                    + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDD').val() + '\" name="' + columnName + '_to" value="' + parameters[1] + '">';
            secondTd += '            </div>';

            break;
        //type=19:日付入力(月まで)
        case 19:
            var day1 = "";
            var day2 = "";
            if (parameters[2] != "") {
                day1 = getSysDateDiff(parameters[2]);
            }
            if (parameters[3] != "") {
                day2 = getSysDateDiff(parameters[3]);
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From ' + day1 + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
            + '" data-date-format=\"' + $('#datetimeFormat-YYYYMM').val() + '\" name="' + columnName + '_from" value="' + parameters[0] + '">';
            secondTd += '            </div>';

            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp; ' + day2
            + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
            + '" data-date-format=\"' + $('#datetimeFormat-YYYYMM').val() + '\" name="' + columnName + '_to" value="' + parameters[1] + '">';
            secondTd += '            </div>';

            break;
        //type=5:日時入力(分まで）
        case 5:
            var dateTime1 = "";
            var dateTime2 = "";
            if (parameters[2] != "") {
                dateTime1 = getSysDateDiff(parameters[2]);
            }
            if (parameters[3] != "") {
                dateTime2 = getSysDateDiff(parameters[3]);
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From ' + dateTime1
                    + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDDhhmm').val() + '\" name="' + columnName + '_from" value="' + parameters[0]
                    + '">';
            secondTd += '            </div>';

            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp; '
                    + dateTime2 + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDDhhmm').val() + '\" name="' + columnName + '_to" value="' + parameters[1]
                    + '">';
            secondTd += '            </div>';

            break;
        //type=6:日時入力(秒まで）
        case 6:
            var dateTime1 = "";
            var dateTime2 = "";
            if (parameters[2] != "") {
                dateTime1 = getSysDateDiff(parameters[2]);
            }
            if (parameters[3] != "") {
                dateTime2 = getSysDateDiff(parameters[3]);
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From ' + dateTime1
                    + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDDhhmmss').val() + '\" name="' + columnName + '_from" value="'
                    + parameters[0] + '">';
            secondTd += '            </div>';

            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp; '
                    + dateTime2 + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDDhhmmss').val() + '\" name="' + columnName + '_to" value="' + parameters[1]
                    + '">';
            secondTd += '            </div>';

            break;
        //type=21:日時入力(分まで,分は00固定）
        case 21:
            var dateTime1 = "";
            var dateTime2 = "";
            if (parameters[2] != "") {
                dateTime1 = getSysDateDiff(parameters[2]);
            }
            if (parameters[3] != "") {
                dateTime2 = getSysDateDiff(parameters[3]);
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From ' + dateTime1
                    + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDDhh00').val() + '\" name="' + columnName + '_from" value="' + parameters[0]
                    + '">';
            secondTd += '            </div>';

            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp; '
                    + dateTime2 + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDDhh00').val() + '\" name="' + columnName + '_to" value="' + parameters[1]
                    + '">';
            secondTd += '            </div>';

            break;
        //type=22:日時入力(秒まで）
        case 22:
            var dateTime1 = "";
            var dateTime2 = "";
            if (parameters[2] != "") {
                dateTime1 = getSysDateDiff(parameters[2]);
            }
            if (parameters[3] != "") {
                dateTime2 = getSysDateDiff(parameters[3]);
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From ' + dateTime1
                    + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDDhhmm00').val() + '\" name="' + columnName + '_from" value="'
                    + parameters[0] + '">';
            secondTd += '            </div>';

            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp; '
                    + dateTime2 + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <input type="text" class="' + dateDialogClass
                    + '" data-date-format=\"' + $('#datetimeFormat-YYYYMMDDhhmm00').val() + '\" name="' + columnName + '_to" value="' + parameters[1]
                    + '">';
            secondTd += '            </div>';

            break;
        // type=7:プルダウン入力（オートコンプリート有り）
        // type=13プルダウン入力（オートコンプリート有り） mode 1
        // type=14プルダウン入力（オートコンプリート有り） mode 2
        case 7:
        case 13:
        case 14:
            if (type == 13) {
                auto_mode = "1";
            }
            else if (type == 14) {
                auto_mode = "2";
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <select name="' + columnName + '_value" class="gnomes-auto-combo" data-mode="'
                    + auto_mode + '" >';
            secondTd += '                <option value=""></option>';
            for ( var listKey in patterns) {
                if (patternKeys != "" && patternKeys[0] == listKey) {
                    secondTd += '                <option value="' + listKey + '" selected="selected">'
                            + patterns[listKey] + '</option>';
                }
                else {
                    secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
                }
            }
            secondTd += '              </select>';
            secondTd += '            </div>';
            break;
        // type=8:チェックボックス
        case 8:
            var keyIndex = 0;
            for ( var listKey in patterns) {
                if (keyIndex == 0) {
                    secondTd += '            <div class="search-dialog-col-title">' + text + '</div>';
                }
                else {
                    secondTd += '            <div class="search-dialog-col-title"></div>';
                }
                secondTd += '            <div class="search-dialog-col-data">';

                if (patternKeys != "" && patternKeys.indexOf(listKey) != -1) {
                    secondTd += '              <label style=\"vertical-align: middle;\"><input name="' + columnName
                            + '_check" class="' + checkboxClass + '" ' + checkboxStyle + ' type="checkbox" value="'
                            + listKey + '" checked><span>&nbsp;' + patterns[listKey] + '</span></label>';
                }
                else {
                    secondTd += '              <label style=\"vertical-align: middle;\"><input name="' + columnName
                            + '_check" class="' + checkboxClass + '" ' + checkboxStyle + ' type="checkbox" value="'
                            + listKey + '"><span>&nbsp;' + patterns[listKey] + '</span></label>';
                }
                secondTd += '            </div>';
                keyIndex++;
            }

            break;
        // type=9:ラジオボタン
        case 9:

            //          secondTd += '            <div class="search-dialog-col-title">' + text + '</div>';
            //          secondTd += '            <div class="search-dialog-col-data">';
            //          if(parameters[2] != ""){
            //              secondTd += '  <input name="'+columnName+'_check" class="" '+ checkboxStyle +' type="radio" value="0" checked>'+parameters[0];
            //              secondTd += '  <input name="'+columnName+'_check" class="" '+ checkboxStyle +' type="radio" value="1">'+parameters[1];
            //          }
            //          else if(parameters[3] != ""){
            //              secondTd += '  <input name="'+columnName+'_check" class="" '+ checkboxStyle +' type="radio" value="0">'+parameters[0];
            //              secondTd += '  <input name="'+columnName+'_check" class="" '+ checkboxStyle +' type="radio" value="1" checked>'+parameters[1];
            //          }
            //          else{
            //              secondTd += '  <input name="'+columnName+'_check" class="" '+ checkboxStyle +' type="radio" value="0">'+parameters[0];
            //              secondTd += '  <input name="'+columnName+'_check" class="" '+ checkboxStyle +' type="radio" value="1">'+parameters[1];
            //
            //          }
            //          secondTd += '            </div>';
            break;

        // type=10:from (数値 プルダウン) to (数値 プルダウン)
        case 10:
            pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text + '&nbsp;From');
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <select name="' + columnName + '_from" ' + pulldownDialogOnclick + '>';
            secondTd += '                <option value=""></option>';
            for ( var listKey in patterns) {
                if (parameters[0] == listKey) {
                    secondTd += '                <option value="' + listKey + '" selected="selected">'
                            + patterns[listKey] + '</option>';
                }
                else {
                    secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
                }
            }
            secondTd += '              </select>';
            secondTd += '            </div>';

            pulldownDialogOnclick = getPulldownDialogOnclick(isPanecon, text + '&nbsp;To');
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp;</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <select name="' + columnName + '_to" ' + pulldownDialogOnclick + '>';
            secondTd += '                <option value=""></option>';
            for ( var listKey in patterns) {
                if (parameters[1] == listKey) {
                    secondTd += '                <option value="' + listKey + '" selected="selected">'
                            + patterns[listKey] + '</option>';
                }
                else {
                    secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
                }
            }
            secondTd += '              </select>';
            secondTd += '            </div>';
            break;
        //type=11:数値 プルダウン（オートコンプリート有り）
        //type=15:数値 プルダウン（オートコンプリート有り） mode 1
        //type=16:数値 プルダウン（オートコンプリート有り） mode 2
        case 11:
        case 15:
        case 16:
            if (type == 15) {
                auto_mode = "1";
            }
            else if (type == 16) {
                auto_mode = "2";
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <select name="' + columnName + '_value" class="gnomes-auto-combo" data-mode="'
                    + auto_mode + '" >';
            secondTd += '                <option value=""></option>';
            for ( var listKey in patterns) {
                if (patternKeys != "" && patternKeys[0] == listKey) {
                    secondTd += '                <option value="' + listKey + '" selected="selected">'
                            + patterns[listKey] + '</option>';
                }
                else {
                    secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
                }
            }
            secondTd += '              </select>';
            secondTd += '            </div>';
            break;
        // type=12:from (数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り）
        // type=17:from (数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り） mode 1
        // type=18:from (数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り） mode 2
        case 12:
        case 17:
        case 18:
            if (type == 17) {
                auto_mode = "1";
            }
            else if (type == 18) {
                auto_mode = "2";
            }
            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;From</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <select name="' + columnName + '_from" class="gnomes-auto-combo" data-mode="'
                    + auto_mode + '" >';
            secondTd += '                <option value=""></option>';
            for ( var listKey in patterns) {
                if (parameters[0] == listKey) {
                    secondTd += '                <option value="' + listKey + '" selected="selected">'
                            + patterns[listKey] + '</option>';
                }
                else {
                    secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
                }
            }
            secondTd += '              </select>';
            secondTd += '            </div>';

            secondTd += '            <div class="search-dialog-col-title">' + text + '&nbsp;To&nbsp;&nbsp;</div>';
            secondTd += '            <div class="search-dialog-col-data">';
            secondTd += '              <select name="' + columnName + '_to" class="gnomes-auto-combo" data-mode="'
                    + auto_mode + '" >';
            secondTd += '                <option value=""></option>';
            for ( var listKey in patterns) {
                if (parameters[1] == listKey) {
                    secondTd += '                <option value="' + listKey + '" selected="selected">'
                            + patterns[listKey] + '</option>';
                }
                else {
                    secondTd += '                <option value="' + listKey + '">' + patterns[listKey] + '</option>';
                }
            }
            secondTd += '              </select>';
            secondTd += '            </div>';
            break;
        default:
            break;
        }
        secondTd += '            <label class="commmon-dialog-checkbox" style="display: none;"><input type="checkbox" name="' + columnName
                + '_enable" class="' + checkboxClass + '" value="1" checked></label>';
        // JOBで詳細検索の削除ボタンの表示有無の設定の為、Zm001システム定義マスタのCONDITION_REQUIRED+DISP_DELETE_BUTTONの値を取得
        if (dispDeleteButton == null
                || dispDeleteButton == ""
                || (dispDeleteButton == "hide" && requiredType != 1)) {
            secondTd += '            <a href="#" name onclick="hiddenItem(\'' + columnName + '\');"><span class="common-button">' + $('#search-dialog-filter-remove-button-label').val() + '</span></a>';
        }
        secondTd += '          </div>';
        var tr = firstTd + secondTd;
        $('#' + filterTagId).append(tr);

        if (isPanecon == true) {
            $('.common-keyboard-input').keyboard();
            disabledSelectClick();
        }

        // type=7:プルダウン入力（オートコンプリート有り）
        // type=11:数値 プルダウン（オートコンプリート有り）
        // type=13プルダウン入力（オートコンプリート有り） mode 1
        // type=14プルダウン入力（オートコンプリート有り） mode 2
        // type=15:数値 プルダウン（オートコンプリート有り） mode 1
        // type=16:数値 プルダウン（オートコンプリート有り） mode 2
        if (type == 7 || type == 11 || type == 13 || type == 14 || type == 15 || type == 16) {
            $("[name=" + columnName + "_value]").autocombobox();
        }
        // type=12:from (数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り）
        // type=17:from (数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り） mode 1
        // type=18:from (数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り） mode 2
        else if (type == 12 || type == 17 || type == 18) {
            $("[name=" + columnName + "_from]").autocombobox();
            $("[name=" + columnName + "_to]").autocombobox();
        }
    }
}

// 数値から日付情報を取得
function getSysDateDiff(day) {
    var result = "";
    if (day < 0) {
        result = $('#msgSearchDialogPast').text() + Math.abs(day) + $('#msgSearchDialogDay').text();
    }
    else if (day > 0) {
        result = $('#msgSearchDialogFuture').text() + day + $('#msgSearchDialogDay').text();
    }
    return result;

}

function moveUp(index, sortTableTagId) {
    if ($(sortTableTagId + ' #' + index).prev().html() != null) {
        var changeIndex = $(sortTableTagId + ' #' + index).prev().prop('id');
        moveTr(index, changeIndex, sortTableTagId);
    }
}

function moveDown(index, sortTableTagId) {
    if ($(sortTableTagId + ' #' + index).next().html() != null) {
        var changeIndex = $(sortTableTagId + ' #' + index).next().prop('id');
        moveTr(index, changeIndex, sortTableTagId);
    }
}

function moveTr(index, changeIndex, sortTableTagId) {

    //行の取得
    var tr1 = $(sortTableTagId + ' #' + index);
    var tr2 = $(sortTableTagId + ' #' + changeIndex);

    //行の値の取得
    var tr1HTML = $(sortTableTagId + ' #' + index).prop('outerHTML');
    var tr2HTML = $(sortTableTagId + ' #' + changeIndex).prop('outerHTML');

    var tr1Check = $(sortTableTagId + ' [name=' + index + '_disp]').prop('checked');
    var tr2Check = $(sortTableTagId + ' [name=' + changeIndex + '_disp]').prop('checked');

    var tr1Select = $(sortTableTagId + ' [name=' + index + '_orderNum]').val();
    var tr2Select = $(sortTableTagId + ' [name=' + changeIndex + '_orderNum]').val();

    var tr1Type = $(sortTableTagId + ' [name=' + index + '_orderType]:checked').val();
    var tr2Type = $(sortTableTagId + ' [name=' + changeIndex + '_orderType]:checked').val();

    //h行の入れ替え
    tr1.prop('outerHTML', tr2HTML);
    tr2.prop('outerHTML', tr1HTML);

    $(sortTableTagId + ' [name=' + index + '_disp]').prop('checked', tr1Check);
    $(sortTableTagId + ' [name=' + changeIndex + '_disp]').prop('checked', tr2Check);

    $(sortTableTagId + ' [name=' + index + '_orderNum]').val(tr1Select);
    $(sortTableTagId + ' [name=' + changeIndex + '_orderNum]').val(tr2Select);

    $(sortTableTagId + ' [name=' + index + '_orderType]').val([ tr1Type ]);
    $(sortTableTagId + ' [name=' + changeIndex + '_orderType]').val([ tr2Type ]);

    setModalFocusLoop($(sortTableTagId));
}

// プルダウン選択一覧ダイアログ出力処理の取得
function getPulldownDialogOnclick(isPanecon, title) {
    var onclick = "";

    if (isPanecon) {
        onclick = 'onclick="MakePullDownTableModal($(this), \'' + title + '\');"';
    }

    return onclick;
}

function converterNumberFormatComma(value) {
    if (value == null || value == undefined || value == "") {
        return "";
    }
    var val = numeral(value).format('0.[000000000000]');

    if (val > 4503599627370496 || val < -4503599627370496) {
        return "";
    }
    //変換用フォーマット作成
    var format_string = "0,0";
    //フォーマット実行
    var replacedNum = numeral(val).format(format_string);
    return replacedNum;
}

//ソート条件初期表示出力
function outDefaultSortTable(tableId, sortTableTagId, mstOrderings, orderingInfos, fixedColNum) {

    document.getElementById(sortTableTagId).innerHTML = "";

    // ソート対象の総計
    var mstOrderingsSize = 0;
    for (var i = 0; i < mstOrderings.length; i++) {
        var column = mstOrderings[i].columnName;
        if (column != null && column != "" && column != undefined) {
            mstOrderingsSize++;
        }
    }

    // ソート部ボディ
    var tr = '';

    for (var i = 0; i < orderingInfos.length; i++) {

        var index = orderingInfos[i].index;

        // 表示対象カラム
        var dispColumn = mstOrderings[i].columnName;

        // カラム名
        var columnName = sortTableTagId + "_" + dispColumn;

        // 表示カラム名
        var text = mstOrderings[i].text;

        // テーブル内 表示有無
        var isHiddenColumn = orderingInfos[i].hiddenTable;

        // ソート方式  -1:ソート無し 0:昇順 1:降順
        var orderType = orderingInfos[i].orderType;

        // ソート順序
        var orderNum = orderingInfos[i].orderNum;

        // ソート必須
        var sortRequired = orderingInfos[i].sortRequired;
        if(sortRequired == null){
            sortRequired=false;
        }

        //ソート順序プルダウン非表示判定(falseで非表示)
        var pulldownDisplay = true;
        //ソート方式が -1:ソート無し　なら非表示(またはnullやundefinedでも非表示)))
        if(orderType == null || orderType == undefined || orderType == -1){
            pulldownDisplay = false;
        }
        //ソート方式が-1以外でプルダウン非表示でも表示カラム名が入っていなかったらプルダウン非表示にする
        //(または表示カラム名がnullやunidefinedでも非表示になる)
        if( pulldownDisplay && dispColumn == null || dispColumn == undefined || dispColumn == ""){
            pulldownDisplay = false;
        }


        tr += '            <tr  class="tr" id="' + columnName + '" columnid="' + dispColumn + '" sortRequired=' + sortRequired + ' onmouseover="mouseover(' + (i+2) + ');" onmouseout="mouseout(' + (i+2) + ');">';

        // 表示順列
        tr += '              <td class=""><a href="#" class="modal-focus" name="moveUp" onclick="moveUp(' + "'"
                + columnName + "'" + ', ' + "'#" + sortTableTagId + "'"
                + '); return false;"><img src="./images/gnomes/icons/up-chevron-button.png"></a></td>';
        tr += '              <td class=""><a href="#" name="moveDown" onclick="moveDown('
                + "'"
                + columnName
                + "'"
                + ', '
                + "'#"
                + sortTableTagId
                + "'"
                + '); return false;"><img src="./images/gnomes/icons/up-chevron-button.png" class="common-icon-roteto180"></a></td>';

        // 表示有無チェックボックス列
        if (!isHiddenColumn) {
            tr += '              <td class=""><input type="checkbox" name="' + columnName
                    + '_disp" value="1" checked></td>';
        }
        else {
            tr += '              <td class=""><input type="checkbox" name="' + columnName + '_disp" value="1"></td>';
        }

        // カラム名列
        tr += '              <td class="">' + text + '</td>';

        // ソート順序設定可能項目の場合（表示対象カラムが設定されている場合）
        if ( pulldownDisplay ) {
            // ソート順序プルダウンを表示
            if(sortRequired != null && sortRequired == true) {
                tr += '              <td class="orderingInfo_sortrequired">';
            }
            else {
                tr += '              <td class="">';
            }
            tr += '                <select name="' + columnName + '_orderNum">';

            if(sortRequired == null || sortRequired == false) {
                tr += '                  <option></option>';
            }

            for (var j = 1; j <= mstOrderingsSize; j++) {

                if (orderNum != null && j == orderNum) {
                    tr += '                  <option value="' + j + '" selected="selected">' + j + '</option>';
                }
                else {
                    tr += '                  <option value="' + j + '">' + j + '</option>';
                }
            }
            tr += '                </select>';
            tr += '              </td>';

            // ソート方式ラジオボタンを表示
            tr += '              <td class="">';
            if (orderType == 0) {
                tr += '                <input type="radio" name="' + columnName
                        + '_orderType" value="0" checked="checked">' + $('#msgSortDialogAsc').text();
                tr += '                <input type="radio" name="' + columnName + '_orderType" value="1">'
                        + $('#msgSortDialogDesc').text();
            }
            else {
                tr += '                <input type="radio" name="' + columnName + '_orderType" value="0">'
                        + $('#msgSortDialogAsc').text();
                tr += '                <input type="radio" name="' + columnName + '_orderType" value="1" checked>'
                        + $('#msgSortDialogDesc').text();
            }

            tr += '              </td>';

        }
        // ソート順序設定可能項目の場合
        else {
            tr += '              <td class="">';
            tr += '              </td>';
            tr += '              <td class="">';
            tr += '              </td>';
        }
        tr += '            </tr>';
    }
    $('#' + sortTableTagId).append(tr);

    // 列固定
    document.getElementById(sortTableTagId + "_fixedColList").innerHTML = "";

    var fixedColNumList = '';
    fixedColNumList += '          <option value="0">0</option>';
    for (var n = 1; n < mstOrderingsSize; n++) {
        if (n == fixedColNum) {
            fixedColNumList += '          <option value="' + n + '" selected="selected">' + n + '</option>';
        }
        else {
            fixedColNumList += '          <option value="' + n + '">' + n + '</option>';
        }

    }

    $('#' + sortTableTagId + "_fixedColList").append(fixedColNumList);

    setModalFocusLoop($('#' + sortTableTagId));

}

//===================================================================
/**
 * [機能] 検索設定の保存
 * [引数] command     実行コマンド
 *          settingType 設定種別
 *          tableFilterId テーブルフィルターID
 *          tableSortId テーブルソートID
 *
 * [戻値] なし
 */
//===================================================================
function save_table_search_setting(command, tableId, settingType, tableFilterId, tableSortId) {

    var settingObject = makeSearchJSON(tableFilterId, tableSortId);

    if (settingObject == null || settingObject == "") {
        return;
    }

    //禁則文字errorがある場合
    if (settingObject["conditionInfos"][0] == "error") {
        var format = $('[name=dateFormat]').val() + ":ss";
        var dateFormat = comDateFormat(new Date(), format);
        makeMessageModalBase(
                $('[name=mesgCategoryName]').val(),     //メッセージタイトル
                $('[name=mesgIconName]').val(),         //アイコン名
                dateFormat,                             //発生日時
                $(".common-header-userName").html(),    //操作ユーザ名
                $(".common-header-deviceName").html(),  //操作ホスト
                $('[name=mesgBodyName]').val(),         //メッセージ本文
                null,                                   //メッセージ詳細
                null,                                   //ボタンモード
                null,                                   //デフォルトボタン
                null,                                   //コマンド
                null,                                   //リンク情報
                false,                                  //オープン時詳細を開くかどうか
                null,                                   //OKボタン押下時呼び出し関数
                null,                                   //Cancelボタン押下呼び出し関数
                null,                                   //ボタンID
                ""                                      //DB領域
        );
        return;
    }

    var settingString = JSON.stringify(settingObject);

    var formData = new FormData();
    formData.append("tableId", tableId);
    formData.append("settingType", settingType);
    formData.append("setting", settingString);
    formData.append("saveScreenId", getScreenId());

    // コールバック
    var callbacks = {
        'cmdsuccess' : function(commandResponse) {
            MakeModalMessage($('#msgBoxTitleInfo').text(), $('#msgDialogSaveSuccessMessage').text(), $('#msgBoxBtnNameOk').text());
            $('.modal-search-dialog [name="common-dialog-cancel"]').css('pointer-events', 'none');
            $('.modal-search-dialog [name="common-dialog-cancel"]').css('opacity', '0.7');
        }
    };
    ajax_submit_command('rest/TableSearchSettingService', command, formData, callbacks);
}
// カラム項目非表示
function hiddenItem(columnName) {
   $("#" + columnName).css("display", "none");
}

//===================================================================
/**
 * [機能] 検索設定の初期化
 * [引数] command     実行コマンド
 *          settingType 設定種別
 *          tableFilterId テーブルフィルターID
 *          tableSortId テーブルソートID
 *
 * [戻値] なし
 */
//===================================================================
function init_table_search_setting(command, tableId, settingType, tableFilterId, tableSortId, isPanecon) {

    // ダイアログの初期化
    // TODO カスタムタグで表示が正しくなってから作成する

    // サーバロジック実行
    var formData = new FormData();
    formData.append("tableId", tableId);
    formData.append("settingType", settingType);
    formData.append("saveScreenId", getScreenId());

    // コールバック
    var callbacks = {
        'cmdsuccess' : function(commandResponse) {
            MakeModalMessage($('#msgBoxTitleInfo').text(), $('#msgDialogInitSuccessMessage').text(), $('#msgBoxBtnNameOk').text());
            //$('.modal-search-dialog [name="common-dialog-cancel"]').css('pointer-events', 'none');
            //$('.modal-search-dialog [name="common-dialog-cancel"]').css('opacity', '0.7');
        }
    };
    ajax_submit_command('rest/TableSearchSettingService', command, formData, callbacks);

    //$('.'+tableFilterId+'_tr').remove();
    document.getElementById(tableFilterId).innerHTML = "";
    var searchMasterInfo = mstSearchInfoMap[tableId];
    var defaultSearchSetting = searchMasterInfo.defaultSearchSetting;
    var conditionInfos = defaultSearchSetting.conditionInfos;
    var orderingInfos = defaultSearchSetting.orderingInfos;

    for (var i = 0; i < conditionInfos.length; i++) {

        var columnId = conditionInfos[i].columnId;
        var parameters = conditionInfos[i].parameters;
        var patternKeys = conditionInfos[i].patternKeys;
        var index = getIndexFromMstCondition(columnId,tableId);

        addFilter(tableId, tableFilterId, isPanecon, index, parameters, patternKeys,columnId);
    }

    if (isPanecon != true) {

        var mstOrdering = searchMasterInfo.mstOrdering;
        var fixedColNum = defaultSearchSetting.fixedColNum;
        outDefaultSortTable(tableId, tableSortId, mstOrdering, orderingInfos, fixedColNum);
    }

}

//===================================================================
/**
 * [機能] 条件マスターの定義位置を取得
 * [引数] columnName  項目name属性値
 *
 * [戻値] マスターIndex
 */
//===================================================================

function getIndexFromMstCondition(columnName,tableId)
{
    var searchMasterInfo = mstSearchInfoMap[tableId];
    var mstConditions = searchMasterInfo.mstConditions;
    for(var index=0;index<mstConditions.length;index++){
        if(mstConditions[index].columnName === columnName){
            return index;
        }
    }
    return null;
}

//===================================================================
/**
 * [機能] 1カラム分の検索条件を取得
 * [引数] index       マスターIndex
 *          columnName  項目name属性値
 *          type        検索タイプ
 *
 * [戻値] conditionInfo
 */
//===================================================================
function getSearchColumnJson(index, columnName, type) {

    //1行分のJSON
    var columnJson = {};

    //JSONを作成
    //columnJson["index"] = Number(index);
    columnJson["columnId"] = index;
    //default value = false
    columnJson["hiddenItem"] = false;
    //非表示場合
    if($("#" + columnName).css("display") == "none") {
        columnJson["hiddenItem"] = true;
    }

    //入力補助設定（TGC対応で追加、通常は使用しない）
    if ($("[name=" + columnName + "_inputAdvice]").attr("onclick") != undefined
        && $("[name=" + columnName + "_inputAdvice]").attr("onclick") != null
        && $("[name=" + columnName + "_inputAdvice]").attr("onclick") != "") {
        columnJson["inputAdvice"] = $("[name=" + columnName + "_inputAdvice]").attr("onclick");
    }

    switch (type) {
    //type=0:文字入力
    case "0":
        columnJson["patternKeys"] = [ $("[name=" + columnName + "_condition]").val() ];
        columnJson["parameters"] = [ $("[name=" + columnName + "_value]").val() ];
        break;
    //type=1:文字 プルダウン
    case "1":
        columnJson["patternKeys"] = [ $("[name=" + columnName + "_value]").val() ];
        columnJson["parameters"] = [];
        break;
    //type=2:数値 プルダウン
    case "2":
        columnJson["patternKeys"] = [ $("[name=" + columnName + "_value]").val() ];
        columnJson["parameters"] = [];
        break;
    //type=3:数値入力
    case "3":
        columnJson["patternKeys"] = [];
        columnJson["parameters"] = [ $("[name=" + columnName + "_from]").val(), $("[name=" + columnName + "_to]").val() ];
        break;
    //type=4:日付入力
    case "4":
    case "20":
        columnJson["patternKeys"] = [];
        columnJson["parameters"] = [ $("[name=" + columnName + "_from]").val(), $("[name=" + columnName + "_to]").val() ];
        break;
    //type=19:日付入力(年月)
    case "19":
        columnJson["patternKeys"] = [];
        columnJson["parameters"] = [ $("[name=" + columnName + "_from]").val(), $("[name=" + columnName + "_to]").val() ];
        break;
    //type=23:日付入力(年月fromのみ)   
    case "23":
        columnJson["patternKeys"] = [];
        columnJson["parameters"] = [ $("[name=" + columnName + "_from]").val() ];
        break;
    //type=5:日時入力(時分)
    //type=21:日時入力(時分,分は00固定)
    case "5":
    case "21":
        columnJson["patternKeys"] = [];
        columnJson["parameters"] = [ $("[name=" + columnName + "_from]").val(), $("[name=" + columnName + "_to]").val() ];
        break;
    //type=6:日時入力（時分秒）
    //type=22:日時入力（時分秒,秒は00固定）
    case "6":
    case "22":
        columnJson["patternKeys"] = [];
        columnJson["parameters"] = [ $("[name=" + columnName + "_from]").val(), $("[name=" + columnName + "_to]").val() ];
        break;
    //type=7:文字 プルダウン（オートコンプリート有り）
    //type=13:文字 プルダウン（オートコンプリート有り） mode 1
    //type=14:文字 プルダウン（オートコンプリート有り） mode 2
    case "7":
    case "13":
    case "14":
        columnJson["patternKeys"] = [ $("[name=" + columnName + "_value]").val() ];
        columnJson["parameters"] = [];
        break;
    //type=8:チェックボックス
    case "8":
        columnJson["patternKeys"] = [];
        $("[name=" + columnName + "_check]:checked").each(function() {
            columnJson["patternKeys"].push($(this).val());
        });
        //                  columnJson["patternKeys"] =  [$("[name=" + columnName + "_check]:checked").val()];
        columnJson["parameters"] = [];
        // TODO 何のため？
        //                  if($("[name=" + columnName + "_check]:checked").val() != null){
        //                      columnJson["parameters"] = Number($("[name=" + columnName + "_check]:checked").val());
        //                  } else {
        //                      columnJson["parameters"] = [];
        //                  }
        break;
    //type=9:ラジオボタン
    case "9":
        //                  columnJson["patternKeys"] =  [];
        //                  columnJson["parameters"] = !$("[name=" + columnName + "_check]").prop('checked');
        break;
    //type=10:(数値 プルダウン) to (数値 プルダウン)
    case "10":
        columnJson["patternKeys"] = [];
        columnJson["parameters"] = [ $("[name=" + columnName + "_from]").val(), $("[name=" + columnName + "_to]").val() ];
        break;
    //type=11:数値 プルダウン（オートコンプリート有り）
    //type=15:数値 プルダウン（オートコンプリート有り） mode 1
    //type=16:数値 プルダウン（オートコンプリート有り） mode 2
    case "11":
    case "15":
    case "16":
        columnJson["patternKeys"] = [ $("[name=" + columnName + "_value]").val() ];
        columnJson["parameters"] = [];
        break;
    //type=12:(数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り）
    //type=17:(数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り） mode 1
    //type=18:(数値 プルダウン) to (数値 プルダウン)（オートコンプリート有り） mode 2
    case "12":
    case "17":
    case "18":
        columnJson["patternKeys"] = [];
        columnJson["parameters"] = [ $("[name=" + columnName + "_from]").val(), $("[name=" + columnName + "_to]").val() ];
        break;

    default:
        break;
    }

    return columnJson;
}

//===================================================================
/**
 * [機能] 画面内の検索メニュー(common-flexMenu-search-boxクラス)から検索条件を作成
 * [引数] なし
 * [戻値] なし
 */
//===================================================================
function makeSearchMenuMap(isPagingFlag) {

    // 元の検索条件から上書きする
    var json = $('input[name=searchSettingMap]').val();

    if (json === undefined || json.length == 0) {
        return;
    }

    var map = JSON.parse(json);

    $(".common-flexMenu-search-box.common-flexMenu-size").each(function() {

        var filterMenuId = $(this).attr("id");
        var tableId = $(this).data("tableid");
        var indexs = $(this).data("indexs");

        var searchSetting = map[tableId];
        var conditionInfos = [];

        $.each(indexs, function(key, value) {
            // 条件マスター情報Indexの取得
            var index = key.split('.')[1];
            // カラム名
            var columnName = filterMenuId + "_" + index;

            // 1行分のconditionInfo
            var conditionInfo = getSearchColumnJson(index, columnName,value);
            conditionInfo["enable"] = true;

            // 通常は使用しないがTGC用に追加
            if (conditionInfo["inputAdvice"] != undefined && conditionInfo["inputAdvice"] != null && conditionInfo["inputAdvice"] != '') {
                var inputAdvice = conditionInfo["inputAdvice"];
                conditionInfo["inputAdvice"] = inputAdvice;       
            }

            // 1行分を配列に追加
            conditionInfos.push(conditionInfo);
        });

        searchSetting["conditionInfos"] = conditionInfos;
        if (isPagingFlag) {
            searchSetting["pagingFlag"] = 1;
        }
        else {
            searchSetting["pagingFlag"] = 0;
        }
    });

    var jsonString = JSON.stringify(map);

    // hiddenパラメータ書き換え
    $('input[name=searchSettingMap]').val(jsonString);
}

//searchSettingを表示済みのHTMLから作成
function makeSearchJSON(filterTableId, sortTableId) {
    var searchSetting = {};

    //filter部
    var conditionInfos = [];
    var i = 0;
    $("#" + filterTableId + " .tr").each(function() {
      // 検索項目は display: none; なら、要らない
      if ($(this).is(":visible")) {
        //カラム名
        var columnName = $(this).attr("id");

        //カラム値
        var columnValue = $("[name=" + columnName + "_value]").val();
        //禁則文字をチェック
        var valid = checkProhibitionCharacters(columnValue);
        //禁則文字がある場合、
        if (valid == false) {
            //検索条件をクリアー
            conditionInfos = [];
            conditionInfos.push("error");
            return false;
        }

        //カラムID
        var columnid = $(this).attr("columnid");

        //各行のID取得
        var index = columnName.split(filterTableId + "_")[1];

        // チェックなしもサーバに送信するためコメントアウト
        //      if($("[name=" + columnName + "_enable]").prop('checked')){

        //typeを取得・分岐
        var type = $("[name=" + columnName + "_type]").val();

        //1行分のJSON
        var columnJson = getSearchColumnJson(columnid, columnName, type);
        columnJson["enable"] = $("[name=" + columnName + "_enable]").prop('checked');

        //1行分のJSONを配列に追加
        conditionInfos.push(columnJson);
        //      }
        i++;
      }
    });

    //sort部
    var orderingInfos = [];

    var dispCnt = 0;
    var sortFlag = false;
    i = 0;
    $("#" + sortTableId + " .tr").each(
            function() {
                var columnName = $(this).attr("id");
                index = columnName.split(sortTableId + "_")[1];
                var columnid = $(this).attr("columnid");
                var sortRequired = $(this).attr("sortRequired");
                //1行分のJSON
                var sortJson = {};
                sortFlag = true;

                //JSONを作成
                sortJson["columnId"] = columnid;

                sortJson["sortRequired"] = sortRequired;

                if ($("[name=" + columnName + "_orderNum]").val() != ""
                        && $("[name=" + columnName + "_orderNum]").val() != null) {
                    sortJson["orderNum"] = Number($("[name=" + columnName + "_orderNum]").val());
                }

                if ($("[name=" + columnName + "_orderType]:checked").val() != null) {
                    sortJson["orderType"] = Number($("[name=" + columnName + "_orderType]:checked").val());
                }
                else {
                    sortJson["orderType"] = -1;
                }

                if ($("[name=" + columnName + "_disp]").prop('checked')) {
                    dispCnt++;
                }
                sortJson["hiddenTable"] = !$("[name=" + columnName + "_disp]").prop('checked');

                orderingInfos.push(sortJson);
                i++;
            });
    if (sortFlag && dispCnt == 0) {
        MakeModalMessage($('#msgBoxTitleError').text(), $('#msgSortDialogDispCheckMessage').text(), $('#msgBoxBtnNameOk').text());
        return null;
    }

    //配列をJSONに追加
    searchSetting["conditionInfos"] = conditionInfos;
    searchSetting["orderingInfos"] = orderingInfos;

    var fixedColNum = $("#" + sortTableId + "_fixedColList").val();

    //col固定位置
    searchSetting["fixedColNum"] = fixedColNum;

    // 復元
    // 表示タイプ
    //searchSetting["dispType"] = searchDispType;
    // 最大表示可能件数
    //searchSetting["maxDispCount"] = searchMaxDispCount;
    // 現在のページ
    //searchSetting["nowPage"] = searchNowPage;
    // １ページ表示件数
    //searchSetting["onePageDispCount"] = searchOnePageDispCount;
    // 全件件数
    //searchSetting["allDataCount"] = searchAllDataCount;

    return searchSetting;
}

//検索実行
function searchSubmit(commandId, tableId, tableFilterId, tableSortId) {

    //searchSettingを表示済みのHTMLから作成
    var newSearchSetting = makeSearchJSON(tableFilterId, tableSortId);

    //禁則文字errorがある場合
    if (newSearchSetting["conditionInfos"][0] == "error") {
        var format = $('[name=dateFormat]').val() + ":ss";
        var dateFormat = comDateFormat(new Date(), format);
        makeMessageModalBase(
                $('[name=mesgCategoryName]').val(),     //メッセージタイトル
                $('[name=mesgIconName]').val(),         //アイコン名
                dateFormat,                             //発生日時
                $(".common-header-userName").html(),    //操作ユーザ名
                $(".common-header-deviceName").html(),  //操作ホスト
                $('[name=mesgBodyName]').val(),         //メッセージ本文
                null,                                   //メッセージ詳細
                null,                                   //ボタンモード
                null,                                   //デフォルトボタン
                null,                                   //コマンド
                null,                                   //リンク情報
                false,                                  //オープン時詳細を開くかどうか
                null,                                   //OKボタン押下時呼び出し関数
                null,                                   //Cancelボタン押下呼び出し関数
                null,                                   //ボタンID
                ""                                      //DB領域
        );
        return;
    }
    // 処理中にダイアログを表示
    processingDialog();

    // 元の検索条件から上書きする
    var json = $('input[name=searchSettingMap]').val();
    var map = JSON.parse(json);

    // 検索条件以外はそのまま
    var searchSetting = map[tableId];
    searchSetting["conditionInfos"] = newSearchSetting["conditionInfos"];
    searchSetting["pagingFlag"] = 0;

    map[tableId] = searchSetting;

    var jsonString = JSON.stringify(map);

    //hiddenパラメータ書き換え
    $('input[name=searchSettingMap]').val(jsonString);

    // モーダル閉じない

    document.main.command.value = commandId;
    document.main.submit();
}
//検索実行
function sortSubmit(commandId, tableId, tableFilterId, tableSortId) {

    //searchSettingを表示済みのHTMLから作成
    var newSearchSetting = makeSearchJSON(tableFilterId, tableSortId);

    if (newSearchSetting == null || newSearchSetting == "") {
        return;
    }

    // 処理中にダイアログを表示
    processingDialog();

    // 元の検索条件から上書きする
    var json = $('input[name=searchSettingMap]').val();
    var map = JSON.parse(json);

    // ソート系条件以外はそのまま
    var searchSetting = map[tableId];
    searchSetting["orderingInfos"] = newSearchSetting["orderingInfos"];
    searchSetting["fixedColNum"] = newSearchSetting["fixedColNum"];
    searchSetting["pagingFlag"] = 0;

    map[tableId] = searchSetting;

    var jsonString = JSON.stringify(map);

    //hiddenパラメータ書き換え
    $('input[name=searchSettingMap]').val(jsonString);

    document.main.command.value = commandId;
    document.main.submit();
}

//===================================================================
/**
 * [機能] テーブルの1ページ表示件数を設定
 * [引数] tableId:    テーブルID
 *          count:      1ページ表示件数
 * [戻値] なし
 */
//===================================================================
function setTableOnePageDispCount(tableId, count) {

    var json = $('input[name=searchSettingMap]').val();
    var map = JSON.parse(json);

    // ソート系条件以外はそのまま
    var searchSetting = map[tableId];

    searchSetting["onePageDispCount"] = count;

    map[tableId] = searchSetting;

    var jsonString = JSON.stringify(map);

    //hiddenパラメータ書き換え
    $('input[name=searchSettingMap]').val(jsonString);
}

//===================================================================
/**
 * [機能] 別Windowでコマンド実行処理(post)
 * [引数] command     実行コマンド
 *          inArray     送信パラメータ
 *          isPostCid   cid送信フラグ
 * [戻値] なし
 */
//===================================================================
function open_window_submit_command_post(command, inArray, isPostCid) {

    if (isPostCid === undefined)
        isPostCid = false;

    var paramArray = get_common_command_base(command, inArray);
    paramArray['gnomesNewWindowOpen'] = "1";

    var cid = $('#cid').val();

    // about:blankとしてOpen
    var target = 'GNOMES' + (new Date()).getTime();
    window.open("", target);

    // formを生成
    var form = document.createElement("form");
    /*
    form.action = "http://" + location.host + "/fides/gnomes";
    */
    form.action = $(document).attr("location").protocol + "//" + location.host + "/UI/gnomes";
    form.target = target;

    form.method = 'post';

    if (isPostCid == true) {
        paramArray['cid'] = cid;
    }

    // input-hidden生成と設定
    for ( var key in paramArray) {
        var input = document.createElement("input");
        input.setAttribute("type", "hidden");
        input.setAttribute("name", key);
        input.setAttribute("value", paramArray[key]);
        form.appendChild(input);
    }

    // formをbodyに追加して、サブミットする。その後、formを削除
    var body = document.getElementsByTagName("body")[0];
    body.appendChild(form);
    form.submit();
    body.removeChild(form);

}

//===================================================================
/**
* [機能]  windowのcloseサービス実行
* [引数]  command         実行コマンド
*
* [戻値]  なし
*/
//===================================================================
function doCloseWindow(command) {

    var windowId = $('#windowId').val();

    var formData = new FormData();
    formData.append("windowId", windowId);

    // コールバック
    var callbacks = {
        'successfinal' : function(data) {
            $('#myAnyModal').modal('hide');
            $('#myAnyModal').html('');
            window.close();
        }
    };

    ajax_submit_command('rest/Y99002S001/closeWindow', command, formData, callbacks, false);
}

//===================================================================
/**
* [機能]  アップロードサービス実行
* [引数]  inputId         ファイルInputのタグID
*           command         実行コマンド
* [戻値]  なし
*/
//===================================================================
function doUpLoadService(inputId, command) {

    var formData = new FormData();

    var windowId = $('#windowId').val();
    formData.append("windowId", windowId);

    // ファイルの設定
    $.each($('#' + inputId)[0].files, function(i, file) {
        formDataAppendFile(formData, file);
    });

    // コールバック設定
    var callbacks = {
        'successfinal' : function(data) {
            if (data.commandResponse != null && data.commandResponse.isCheckOverwrite == true) {

                // 暫定処置
                // メッセージビーンのメッセージも表示され
                // 上書き確認ができないので
                // nullに設定して表示しないようにする
                data.messageBean.message = null;

                // 上書き確認
                $.confirmDialog(data.commandResponse.message, $('#msgBoxBtnNameOk').text(), // OK
                $('#msgBoxBtnNameCancel').text() // Cancel
                ).done(function() {
                    // OK
                    doUpLoadCompulsion(data.commandResponse.command, '1');
                }).fail(function() {
                    // Cancel
                    doUpLoadCompulsion(data.commandResponse.command, '0');
                });
            }
        }
    };

    ajax_submit_command('rest/Y99002S001/uploadFile', command, formData, callbacks);

}
//===================================================================
/**
 * [機能] 強制アップロードサービス実行
 * [引数] inputId         ファイルInputのタグID
 *          command         実行コマンド
 * [戻値] なし
 */
//===================================================================
function doUpLoadCompulsion(command, isOverwrite) {

    var formData = new FormData();
    formData.append("isOverwrite", isOverwrite);

    var windowId = $('#windowId').val();
    formData.append("windowId", windowId);

    // コールバック
    var callbacks = {
        'cmdsuccess' : function(commandResponse) {
            $('#myAnyModal').modal('hide');
            $('#myAnyModal').html('')
        }
    };

    ajax_submit_command('rest/Y99002S001/uploadFile', command, formData, callbacks);
}

//===================================================================
/**
* [機能]  アップロードサービス実行
* [引数]  inputId         ファイルInputのタグID
*           command         実行コマンド
* [戻値]  なし
*/
//===================================================================
function doFileUpLoadService(inputId, command) {

    var formData = new FormData();

    var windowId = $('input[name=windowId]').val();
    formData.append("windowId", windowId);

    // ファイルの設定
    $.each($('input[name=' + inputId + ']')[0].files, function(i, file) {
        formDataAppendFile(formData, file);
    });

    // コールバック設定
    var callbacks = {};

    ajax_submit_command('rest/Y99002S001/uploadFile', command, formData, callbacks);

}

//===================================================================
/**
 * [機能] localStorageへ保存
 * [引数] key     キー
 *          vakue   値
 * [戻値] なし
 */
//===================================================================
function saveLocalStorage(key, value) {
    localStorage.setItem(key, value);
}

//===================================================================
/**
 * [機能] localStorageから削除
 * [引数] key     キー
 * [戻値] なし
 */
//===================================================================
function deleteLocalStrage(key) {
    localStorage.removeItem(key);
}

//===================================================================
/**
 * [機能] localStorageからkeyで
 *          １つのデータを取得する
 * [引数] key         キー
 * [戻値] 結果      データ
 */
//===================================================================
function getLocalStorage(key) {

    return localStorage.getItem(key);

}

//===================================================================
/**
 * [機能] localStorageからkeyで
 *          前方一致する情報をまとめて連想配列を返す
 * [引数] key         キー
 *          kindName    データ種類名
 * [戻値] 結果      連想配列
 */
//===================================================================
function getLocalStorages(key) {

    var datas = new Object();

    for (var i = 0; i < localStorage.length; i++) {

        var k = localStorage.key(i);
        var cmp = ' ' + k;

        // 前方一致
        if (cmp.indexOf(" " + key) !== -1) {
            datas[k] = localStorage.getItem(k);
        }
    }

    return datas;
}

/**
 * プルダウン選択ダイアログ
 */
function GnomesSelectPullDownDialogBTN(title, message, item_label, item, pulldown_label, pullDownList,
        selectedUserValue, okbotton, onClick) {

    var str = "<div class=\"modal\" id=\"select-pulldown-dialog\" data-keyboard=\"false\" data-backdrop=\"static\" >";
    str += "<div class=\"modal-dialog\">";
    str += "  <div class=\"common-dialog-content modal-dialog\">";

    str += "    <div class=\"common-dialog-header-title clearfix common-dialog-title-style\">" + title + "</div>";
    str += "    <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "      <div class=\"common-dialog-body-column\"> ";
    str += "         <div style=\"font-size: 14.66px;\">" + message + "</div>";
    str += "         <div class=\"common-header-col-title common-client-header-col-data\">" + item_label + "</div>";
    str += "         <div class=\"common-dialog-header-col-data\">";
    str += "           <input name=\"loginUserId\" class=\"common-dialog-dataarea-col-data\" id=\"loginUserId\" type=\"text\" readonly=\"\" value=\""
            + item + "\">";
    str += "         </div>";
    str += "         <br>";
    str += "         <div class=\"common-header-col-title common-client-header-col-data\">" + pulldown_label + "</div>";
    str += "         <div class=\"common-dialog-header-col-data\">";
    str += "           <select id=\"pulldown\" class=\"common-dialog-dataarea-col-data modal-focus\" style=\"height: 20px;\">";
    for (var i = 0; i < pullDownList.length; i++) {
        // ユーザが設定されている値と一致するデータがある場合
        if (pullDownList[i].value == selectedUserValue) {
            str += "<option selected value=\"" + pullDownList[i].value + "\">" + pullDownList[i].name + "</option>";
        }
        else {
            str += "<option value=\"" + pullDownList[i].value + "\">" + pullDownList[i].name + "</option>";
        }
    }
    var strOnclick = onClick;

    str += "           </select>";
    str += "         </div>";
    str += "       </div>";
    str += "     </div>";

    str += "     <div class=\"common-dialog-footer clearfix\">";
    // OK
    str += "      <div class=\"login-dialog-footer-button-left\">";
    str += "        <a class=\"modal-focus\" onclick=\"" + strOnclick + " return false;\" href=\"#\">";
    str += "          <span class=\"common-button common-dialog-button\" style=\"width: 90%;\">" + okbotton + "</span>";
    str += "        </a>";
    str += "      </div>";
    // キャンセル
    str += "      <div class=\"login-dialog-footer-button-right\">";
    str += "        <a class=\"modal-focus\" onclick=\"$('#select-pulldown-dialog').modal('hide');$('#select-pulldown-modal').html(''); return false;\" href=\"#\">";
    str += "          <span class=\"common-button common-dialog-button\" style=\"width: 90%;\">"
            + $('#msgBoxBtnNameCancel').text() + "</span>";
    str += "        </a>";
    str += "      </div>";
    str += "    </div> ";
    str += "  </div>";
    str += "</div>";
    str += "</div>";

    $('#select-pulldown-modal').html(str);

    $('#select-pulldown-dialog').on('shown.bs.modal', function() {
    }).modal();

}

/**
 * ツリーデータ情報の設定
 */
function treeDataSubmit() {

    var numIsClosedTreeChild = $(".expandable").length;
    var isClosedTreeChildId = new Array();

    if ($('input[name=treeClosedId]').val() != "") {
        var json = JSON.parse($('input[name=treeClosedId]').val());
    }
    else {
        var json = {};
    }

    // 閉じた状態のツリー項目IDを取得
    if (numIsClosedTreeChild != 0) {
        for (var i = 0; i < numIsClosedTreeChild; i++) {
            isClosedTreeChildId[i] = $(
                    ".expandable:eq(" + i + ") span.client-treeview-column, .expandable:eq(" + i
                            + ") span.panecon-treeview-label").attr('id');
        }
    }
    else {
        isClosedTreeChildId[0] = "";
    }

    json.treeClosedIdArray = isClosedTreeChildId;

    // ツリー項目IDリスト(閉じた状態)書き換え
    $('input[name=treeClosedId]').val(JSON.stringify(json));

    // コマンドが設定されている場合はコマンドを実行する。
    var commandId = $('input[name=command]').val();
    if (commandId != undefined && commandId != null && commandId != "") {
        document.main.submit();
    }

}

/**
 * ツリー項目クリック時の処理
 */
function treeCommandSubmit(id, treeNodeInfoName) {

    // 選択・開閉項目情報Beanが設定されている場合、選択・開閉状態を設定
    if (treeNodeInfoName != null && treeNodeInfoName != "" && treeNodeInfoName != undefined) {
        var treeNodeInfoNameVal = $('input[name=' + treeNodeInfoName + ']').val();

        if (treeNodeInfoNameVal != null && treeNodeInfoNameVal != "" && treeNodeInfoNameVal != undefined
                && treeNodeInfoNameVal != "{") {

            var json = JSON.parse(treeNodeInfoNameVal);
        }
        else {
            var json = {};
        }
    }
    else {
        var json = {};
    }

    //選択したツリー項目のIDの格納
    json.treeExecuteId = id;

    var numIsClosedTreeChild = $(".expandable").length;
    var isClosedTreeChildId = new Array();
    // 閉じた状態のツリー項目IDを取得
    if (numIsClosedTreeChild != 0) {
        for (var i = 0; i < numIsClosedTreeChild; i++) {
            isClosedTreeChildId[i] = $(
                    ".expandable:eq(" + i + ") span.client-treeview-column, .expandable:eq(" + i
                            + ") span.panecon-treeview-label").attr('id');
        }
    }
    else {
        isClosedTreeChildId[0] = "";
    }

    json.treeClosedIdArray = isClosedTreeChildId;

    // 選択・開閉状態を選択・開閉項目情報Beanに設定
    $('input[name=' + treeNodeInfoName + ']').val(JSON.stringify(json));

    var commandId = document.main.command.value;
    if (commandId != null && commandId != "" && commandId != undefined) {
        // コマンド実行
        commandSubmit(commandId);
    }
}

/**
 * チェックボックスがチェック状態の行をテーブルの先頭にスクロール
 */
function scrollTableForFirstChecked(checkboxTagName) {

    // チェック対象行
    var checkedFirstRow;
    // 対象テーブル先頭行
    var tableFirstRow;

    // 対象チェックボックスを確認
    $("input[name='" + checkboxTagName + "']").each(function() {
        // 対象チェックボックスがチェック状態の場合
        if ($(this)[0].checked) {
            // その行をチェック対象行として設定
            checkedFirstRow = $(this).parents('tr');
            // 対象テーブルtbody
            tableFirstRow = checkedFirstRow.parent();
            // break
            return false;

        }
    });

    // チェック対象行が存在する場合
    if (checkedFirstRow != null && checkedFirstRow != undefined) {
        // チェック対象行の初期位置を取得
        var checkedRowTop = checkedFirstRow[0].offsetTop;
        // 対象テーブルの先頭行の初期位置を取得
        var firstRowTop = tableFirstRow[0].offsetTop;

        // 対象テーブルをチェック対象行が先頭になるようスクロールさせる。
        tableFirstRow.parent().parent()[0].scrollTop = checkedRowTop - firstRowTop;

    }

}

/**
 * inputのreadonlyをクリック時、disabledを一時的に付加
 */
$(document).ready(function() {
    $('input[readonly], textarea[readonly]').on('mousedown keydown', function() {
        var $target = $(this);
        $target.attr('disabled', 'disabled');
        setTimeout(function() {
            $target.removeAttr('disabled');
        }, 1);
    });
});

/***
 * オートコンプリートのコンボボックス
 */
$(function() {
    $
            .widget(
                    "gnomes.autocombobox",
                    {

                        _create : function() {
                            this.wrapper = $("<span>").addClass(this.element.attr('class')).insertAfter(this.element);

                            this.element.hide();
                            this._createAutocomplete();
                            //        this._createShowAllButton();
                        },
                        _createAutocomplete : function() {
                            var selected = this.element.children(":selected");

                            var mode = this.element.data("mode");
                            if (mode == null) {
                                mode = 3;
                            }
                            var value = "";
                            if (mode == 1) {
                                value = selected.val() ? selected.val() : "";
                            }
                            else if (mode == 2) {
                                value = selected.val() ? selected.text() : "";
                            }
                            else {
                                value = selected.val() ? selected.val() : "";
                                value = selected.text() ? value + " " + selected.text() : value;
                            }

                            var e = this.element;
                            var isdisabled = this.element.is(':disabled')
                            var self = this;

                            // 選択後にonchangeなどでフォーカスが移動した場合、inputのvalueで比較しないようにするフラグ
                            self.changeFlag = false;

                            this.input = $("<input>").appendTo(this.wrapper).val(value).attr("title", "").prop(
                                    'disabled', isdisabled).addClass("gnomes-auto-combobox-input ui-widget").focusin(
                                    function(e) {
                                        self.changeFlag = false;
                                    }).autocomplete({
                                delay : 0,
                                minLength : 0,
                                source : $.proxy(this, "_source"),
                                select : function(event, ui) {
                                    $(this).val(ui.item.value);
                                    self.changeFlag = true;
                                    e.change();
                                    return false;
                                }
                            }).focus(function() {
                                jQuery(this).autocomplete("search", "");
                                //プルダウン上向きと下向き切り替え処理
                                changeTopOrDownPulldownPosition();
                            }).tooltip({
                                classes : {
                                    "ui-tooltip" : "ui-state-highlight"
                                }
                            });

                            this.input.data("ui-autocomplete")._renderMenu = function(ul, items) {
                                ul.addClass("gnomes-threecolumnautocomplete");

                                if ($("select").hasClass("gnomes-combo-3line")) {
                                    ul.addClass("gnomes-combo-3line");
                                }
                                var self = this;
                                $.each(items, function(index, item) {
                                    self._renderItemData(ul, item);
                                });
                            };

                            this.input.data("ui-autocomplete")._renderItem = function(ul, item) {
                                if (item.value2 == "") {
                                    return $("<li>")
                                            .attr("data-value", item.value2)
                                            .append(
                                                    "<a><div class='col'>&nbsp;</div><div class='col'>&nbsp</div><div class='col'>&nbsp;</div></a>")
                                            .appendTo(ul);
                                }
                                else {
                                    return $("<li>").attr("data-value", item.value2).append(
                                            "<a><div class='col'>" + item.value2
                                                    + "</div><div class='col'>&nbsp</div><div class='col'>"
                                                    + item.label2 + "</div></a>").appendTo(ul);
                                }
                            };
                            this._on(this.input, {
                                autocompleteselect : function(event, ui) {
                                    ui.item.option.selected = true;
                                    this._trigger("select", event, {
                                        item : ui.item.option
                                    });

                                },
                                autocompletechange : "_removeIfInvalid"
                            });
                        },
                        _source : function(request, response) {
                            var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");

                            var mode = this.element.data("mode");
                            if (mode == null) {
                                mode = 3;
                            }

                            response(this.element.children("option").map(
                                    function() {
                                        var text = $(this).text();
                                        var value = $(this).val();
                                        var text2 = "";

                                        if (text == null || text == "null") {
                                            text = "";
                                        }

                                        if (mode == 1) {
                                            text2 = value;
                                        }
                                        else if (mode == 2) {
                                            text2 = text;
                                        }
                                        else {
                                            if (value != "") {
                                                text2 = value;
                                                if (text != "") {
                                                    text2 = text2 + ' ' + text;
                                                }
                                            }
                                        }

                                        if ( /* this.value && */value == ""
                                                || (!request.term || matcher.test(text) || matcher.test(value)))
                                            return {
                                                label : text,
                                                value : text2,
                                                label2 : text,
                                                value2 : value,
                                                option : this
                                            };
                                    }));
                        },
                        _removeIfInvalid : function(event, ui) {
                            // Selected an item, nothing to do
                            if (ui.item) {
                                return;
                            }

                            // if (this.changeFlag) {
                            //     this.changeFlag = false;
                            //     return;
                            // }

                            var mode = this.element.data("mode");
                            if (mode == null) {
                                mode = 3;
                            }

                            // Search for a match (case-insensitive)
                            var value = this.input.val(), valueLowerCase = value.toLowerCase(), valid = false;
                            var newtext = "";

                            this.element.children("option").each(
                                    function() {
                                        if ($(this).text().toLowerCase() === valueLowerCase
                                                || $(this).val().toLowerCase() === valueLowerCase) {
                                            this.selected = valid = true;
                                            if (mode == 1) {
                                                newtext = $(this).val();
                                            }
                                            else if (mode == 2) {
                                                newtext = $(this).text();
                                            }
                                            else {
                                                if ($(this).val() != "") {
                                                    newtext = $(this).val();
                                                    if ($(this).text() != "") {
                                                        newtext = newtext + " " + $(this).text();
                                                    }
                                                }
                                            }
                                            return false;
                                        }
                                    });

                            // Found a match, nothing to do
                            if (valid) {
                                this.input.val(newtext);
                                this.element.change();
                                return;
                            }

                            // Remove invalid value
                            this.input.val("");

                            this.element.val("");

                            this.input.autocomplete("instance").term = "";
                        },
                        _destroy : function() {
                            this.wrapper.remove();
                            this.element.show();
                        }
                    });

    $(".gnomes-auto-combo").each(function() {
        $(this).autocombobox();
    });

});

// バーコードカスタムタグ用コマンド実行クラス
// バーコードボタン押下後、ENTERキーを押下した場合、コマンドを実行する。
function Key_on(key, commandId) {
    // Enter=13
    if (key == 13) {
        commandSubmit(commandId);
    }
}

$(document).ready(function() {
    // 工程端末表示の場合
    if (document.body.className.indexOf('common-font-panecon') != -1) {
        // selectをクリック時、disabledを一時的に付加（プルダウンを開閉させないようにする）
        disabledSelectClick();
        // selectタグのタッチイベントにonclickを設定（Chromeのタッチ操作に対応するために実行）
        $(function() {
          $('select').each(function(index, element){
            var onclick = $(element).get(0).onclick;
            $(element).bind('touchstart', onclick);
          });
        });
    }
    //gnomes-auto-combobox-inputに表示内容を削除の場合、
    $(".gnomes-auto-combo .gnomes-auto-combobox-input").on("keyup", function() {
        //プルダウン上向きと下向き切り替え処理
        changeTopOrDownPulldownPosition();
    });

    // 一覧画面の詳細検索をクリックする、入力した値を検索ダイアログに表示する
    $('a.search-detail-link').click(function() {
        var inputColumns = $('.common-flexMenu-search-box.common-flexMenu-size li :input');
        // 一覧画面の検索条件リスト
        inputColumns.each(function(e) {
            var checkValue = false;
            // 検索条件のcolumn名
            var columnName = $(this).attr('name');
            // 検索条件の値
            var columnValue = $(this).val();
            if (columnName) {
                columnName = columnName.replace('_FilterTableMenu', '_FilterTable');
            }
            // プルダウン入力（オートコンプリート)
            if ($(this).next('.gnomes-auto-combo').children('.gnomes-auto-combobox-input').length > 0) {
                var comboValue = $(this).next('.gnomes-auto-combo').children('.gnomes-auto-combobox-input').val();
            }
            // チェックボックス,ラジオボタン
            if($(this).prop("checked") == true){
                checkValue = true;
            }
            // 検索ダイアログの条件リスト
            var dialogColumns =   $(".common-flexMenu-search-box.search-dialog-box-size .tr .search-dialog-col-data :input");
            // 工程端末検索ダイアログの条件リスト
            if (isPanecon()) {
                dialogColumns =   $(".common-flexMenu-search-box.panecon-searchDialog-box-size .tr .search-dialog-col-data :input");
            }
            // 検索ダイアログのリスト
            dialogColumns.each(function(e) {
                // 検索ダイアログの条件column名
                var dialogColName = $(this).attr('name');
                if (typeof columnName !== 'undefined' && typeof dialogColName !== 'undefined' && columnName === dialogColName) {
                    // チェックボックス,ラジオボタンの場合、
                    if ($(this).attr('type') === "checkbox" || $(this).attr('type') === "radio") {
                        if (columnValue === $(this).val()) {
                            $(this).prop("checked", checkValue);
                        }
                       return true;
                    } else {
                        $(this).val(columnValue);
                        // プルダウン（オートコンプリート)
                        if ($(this).next('.gnomes-auto-combo').children('.gnomes-auto-combobox-input').length > 0) {
                            $(this).next('.gnomes-auto-combo').children('.gnomes-auto-combobox-input').val(comboValue);
                        }
                    }
                }
            });
        });
    });

    // ページング上限処理
    var overSizeMesBody = $('[name=overSizeMesBody]').val();
    if(typeof overSizeMesBody !== 'undefined') {
        var format = $('[name=dateFormat]').val() + ":ss";
        var dateFormat = comDateFormat(new Date(), format);
        makeMessageModalBase(
                $('[name=overSizeMesCategory]').val(),  //メッセージタイトル
                $('[name=overSizeMesIconName]').val(),  //アイコン名
                dateFormat,                             //発生日時
                $(".common-header-userName").html(),    //操作ユーザ名
                $(".common-header-deviceName").html(),  //操作ホスト
                $('[name=overSizeMesBody]').val(),      //メッセージ本文
                null,                                   //メッセージ詳細
                null,                                   //ボタンモード
                null,                                   //デフォルトボタン
                null,                                   //コマンド
                null,                                   //リンク情報
                false,                                  //オープン時詳細を開くかどうか
                null,                                   //OKボタン押下時呼び出し関数
                null,                                   //Cancelボタン押下呼び出し関数
                null,                                   //ボタンID
                ""                                      //DB領域
                );
    }
});

/**
 * プルダウン上向きと下向き切り替え処理
 */
function changeTopOrDownPulldownPosition() {
    // id of gnomes-threecolumnautocomplete active
    var activeAutoCompleteId = $(".gnomes-threecolumnautocomplete:visible").attr("id");
    // window height
    var windowHeight = parseInt($(window).height());
    // top value of autocomplete pulldown
    var originaltop = parseFloat($("#"+activeAutoCompleteId).css("top"));
    // height of autocomplete pulldown
    var pulldownHeight = $("#"+activeAutoCompleteId ).height();

    var totalHeight = originaltop + pulldownHeight;

    if(totalHeight > windowHeight) {
        var upperTop = parseFloat(originaltop) - pulldownHeight - $(".ui-autocomplete-input").outerHeight();// height of ui-autocomplete-input
        $("#"+activeAutoCompleteId).css("top", upperTop);
    }
}

/**
 * selectをクリック時、disabledを一時的に付加
 */
function disabledSelectClick() {
    $('select').on('mousedown keydown', function() {
        var $target = $(this);
        $target.attr('disabled', 'disabled');
        setTimeout(function() {
            $target.removeAttr('disabled');
        }, 1);
    });
    $('select').on('mouseup', function() {
        var $target = $(this);
        setTimeout(function() {
            $target.attr('disabled', 'disabled');
        }, 1);
        setTimeout(function() {
            $target.removeAttr('disabled');
        }, 1);
    });
}

/**
 * プルダウン選択テーブルダイアログ（工程端末プルダウン押下時）
 */
function MakePullDownTableModal(tagInfo, title, onchange) {

    var selectTagInfo = tagInfo[0];
    MakePullDownTableModalButton(selectTagInfo, title, onchange, '');

}

/**
 * プルダウン選択テーブルダイアログ選択値反映処理（プルダウン選択状態の変更）
 */
function selectedChange(selectTagInfo, value) {
    for (var i = 0; i < selectTagInfo.length; i++) {

        if (selectTagInfo[i].value == value) {
            selectTagInfo[i].selected = true;
            //選択イベント発火
            var jo = jQuery(selectTagInfo);
            jo.change();
        }
        else {
            selectTagInfo[i].selected = false;
        }
    }
}

/**
 * プルダウン選択テーブルダイアログ
 */
function MakePullDownTableModalButton(selectTagInfo, title, onchange, inputName1, inputName2) {

    var index = $('[name=' + selectTagInfo.name + ']').index(selectTagInfo);

    if (title == null || title == undefined) {
        title = "";
    }
    if (onchange == null || onchange == undefined) {
        onchange = " setWarningFlag(); ";
    } else {
        onchange = " setWarningFlag(); " + onchange;
    }

    var str = "<div class=\"modal\" id=\"pulldown-table-dialog\" data-keyboard=\"false\" data-backdrop=\"static\">";
    str += "  <div class=\"modal-dialog\">";
    str += "    <div class=\"common-dialog-content modal-dialog\">";
    str += "      <div class=\"common-dialog-header-title clearfix\">" + title + " "
            + $('#msgPulldownTableDialogSelect').text() + "</div>";
    str += "      <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "        <div class=\"pulldown-table-dialog-body-column common-table-ttl-fix\">";
    str += "        <table class=\"common-table\" _fixedhead=\"rows:1 cols:1 div-full-mode=no;\">";
    str += "          <thead class=\"common-header-table\">";
    str += "            <tr>";
    str += "              <th style=\"width: 80px;\"></th>";
    str += "              <th>" + $('#msgPulldownTableDialogName').text() + "</th>";
    str += "            </tr>";
    str += "          </thead>";
    str += "          <tbody>";

    // 選択名
    var name;
    // 選択値
    var value;
    // タグName
    var tagName = selectTagInfo.name;
    // onclick
    var onclick;
    // 選択値反映処理
    var selectScript = "";

    for (var i = 0; i < selectTagInfo.length; i++) {

        name = selectTagInfo[i].text;
        value = selectTagInfo[i].value;

        // 反映先1が設定されている場合、反映先に選択値を設定
        if (inputName1 != null && inputName1 != undefined && inputName1 != "") {
            selectScript = "setValueFromDialog('" + inputName1 + "', '" + value + "');";
        }
        // 反映先2が設定されている場合、反映先2に選択名を設定
        if (inputName2 != null && inputName2 != undefined && inputName2 != "") {
            selectScript += "setValueFromDialog('" + inputName2 + "', '" + name + "');";
        }

        // 反映先が設定されていない場合、呼び元のプルダウンの選択状態を変更
        if (selectScript == "") {
            selectScript = "selectedChange($('[name=" + tagName + "]')[" + index + "], '" + value + "');";
        }

        onclick = selectScript + "$('#pulldown-table-dialog').modal('hide');" + onchange + " return false;"

        str += "            <tr class=\"tr\">";
        str += "              <td><a class=\"modal-focus\" href=\"#\" onclick=\"" + onclick
                + "\"><span class=\"common-button\">" + $('#msgPulldownTableDialogSelect').text() + "</span></a></td>";
        str += "              <td>" + name + "</td>";
        str += "            </tr>";

        selectScript = "";
    }

    str += "          </tbody>";
    str += "        </table>";
    str += "        </div>";
    str += "      </div>";
    str += "      <div class=\"common-dialog-footer clearfix\">";
    str += "        <div class=\"common-dialog-footer-button-right\"><a href=\"#\" class=\"modal-focus\" name=\"common-dialog-cancel\" onclick=\"$('#pulldown-table-dialog').modal('hide'); return false;\"><span class=\"common-button common-dialog-footer-button\" style=\"width: 90%;\">"
            + $('#msgBoxBtnNameCancel').text() + "</span></a></div>";
    str += "      </div>";
    str += "    </div>";
    str += "  </div>";
    str += "</div>";

    $('#pulldown-table-modal').html(str);

    $('#pulldown-table-dialog').on('shown.bs.modal', function() {
    }).modal();

}

/**
 * プルダウン選択テーブルダイアログ選択値反映処理（ラベルやテキストボックスへの反映）
 */
function setValueFromDialog(inputTagName, value) {

    // タグ名が設定されている、タグが存在している場合
    if (inputTagName != null && $('[name=' + inputTagName + ']').length != 0) {
        // 格納先のタグが<input>の場合、valueに値を設定する。
        if ($('[name=' + inputTagName + ']')[0].tagName == 'INPUT') {
            $('[name=' + inputTagName + ']').val(value);
        }
        // 格納先のタグが<input>以外の場合、textに値を設定する。
        else {
            $('[name=' + inputTagName + ']').text(value);
        }
    }
}

/**
 * 指定したタグに値を設定する。
 */
function setInputValue(inputTagName, value) {

    // タグ名が設定されている、タグが存在している場合
    if (inputTagName != null && $('[name=' + inputTagName + ']').length != 0) {
        // 格納先のタグが<input>の場合、valueに値を設定する。
        if ($('[name=' + inputTagName + ']')[0].tagName == 'INPUT') {
            $('[name=' + inputTagName + ']').val(value);
        }
        // 格納先のタグが<input>以外の場合、textに値を設定する。
        else {
            $('[name=' + inputTagName + ']').text(value);
        }
    }

}

//===================================================================
/**
* ---------------------------------------------------------------
*
* [2019/01/16 浜本記載] ここからgnomes_commonからの移植
*
* ---------------------------------------------------------------
**/

//===================================================================

//===================================================================
/**
 * [機能] アップロードサービス(後から登録処理）用の非表示Inputタグで
 *          ファイル選択を行う
 * [引数] inputId         非表示InputタグID
 *          cntId           ファイル選択件数を表示するタグID
 *          filetableId     ファイル情報を表示するTableのタグID
 * [戻値] なし
 */
//===================================================================
function doHideUpLoadFileSelect(inputId, cntId, filetableId) {

    // ファイル情報表示を削除
    $('#' + filetableId).find("tr").remove();

    $('#' + inputId).change(function() {
        var files = $(this).prop('files');
        var num = files.length;
        // ファイル選択件数を表示
        $('#' + cntId).text(num + "ファイル");
    });

    $('#' + inputId).click();
}

//===================================================================
/**
 * [機能] インポートファイルサービス実行
 * [引数] inputId         ファイルInputのタグID
 *          errLinkId       エラーリンクのタグID
 *          command         実行コマンド
 * [戻値] なし
 */
//===================================================================
function doImportFileService(inputId, errLinkId, command, inCallbacks) {
    var formData = new FormData();
    $.each($('#' + inputId)[0].files, function(i, file) {
        formDataAppendFile(formData, file);
    });

    var windowId = $('#windowId').val();
    formData.append("windowId", windowId);

    var callbacks = {
        'begin' : function() {
            $('#' + errLinkId).hide();
            $("button[id$=ErrLnk]").hide();
        },
        'cmdsuccess' : function(commandResponse) {
            if (commandResponse.importValidateError) {
                $('#' + errLinkId).show();
            }
            else {
                $('#' + errLinkId).hide();
            }

            if (inCallbacks != undefined && 'cmdsuccess' in inCallbacks) {
                inCallbacks['cmdsuccess'](commandResponse);
            }
        }
    };

    ajax_submit_command('rest/Y99002S001/importDataFile', command, formData, callbacks);
}

//===================================================================
/**
 * [機能] アップロードサービス実行(後から登録処理）
 * [引数] inputId         ファイルInputのタグID
 *          filetableId     ファイル情報を表示するTableのタグID
 *          command         実行コマンド
 * [戻値] なし
 */
//===================================================================
function doUpLoadServiceAfterSubmit(inputId, filetableId, command) {

    var formData = new FormData();

    var windowId = $('#windowId').val();
    formData.append("windowId", windowId);

    // ファイルの設定
    $.each($('#' + inputId)[0].files, function(i, file) {
        formDataAppendFile(formData, file);
    });

    // コールバック
    var callbacks = {
        'cmdsuccess' : function(commandResponse) {
            if (commandResponse.isSuccess == true) {
                // ファイル情報表示を削除
                $('#' + filetableId).find("tr").remove();

                // ファイル情報を表示
                for (var i = 0; i < commandResponse.filenames.length; i++) {
                    $('#' + filetableId).append('<tr><td>' + commandResponse.filenames[i] + '</td></tr>');
                }
            }
        }
    };

    ajax_submit_command('rest/Y99002S001/uploadFile', command, formData, callbacks);

}

//===================================================================
/**
 * [機能] フォームデータにファイルを追加
 * [引数] formData    フォームデータ
 *          file        追加ファイル
 * [戻値] なし
 */
//===================================================================
function formDataAppendFile(formData, file) {
    formData.append('file', file);
}

//===================================================================
/**
 * [機能] インポートエラーダウンロード
 * [引数] なし
 *
 * [戻値] なし
 */
//===================================================================
function importErrorDownload() {

    var params = {};
    var windowId = $('#windowId').val();

    params['screenId'] = getScreenId();
    params['screenName'] = getScreenName();
    params['windowId'] = windowId;

    open_window_submit_command('Y99002C001', params);

}
//===================================================================
/**
 * [機能] 別Windowでコマンド実行処理
 * [引数] command     実行コマンド
 *          inArray     送信パラメータ
 * [戻値] なし
 */
//===================================================================
function open_window_submit_command(command, inArray) {

    var paramArray = get_common_command_base(command, inArray);
    var cid = $('#cid').val();

    var target = '_blank';
    var paramStr = '';

    for ( var key in paramArray) {
        paramStr = paramStr + "&" + key + "=" + decodeURIComponent(paramArray[key]);
    }

    window.open($(document).attr("location").protocol + "//" + location.host + "/UI/gnomes?command=" + command + "&cid=" + cid + paramStr, target);

}

//===================================================================
/**
* ---------------------------------------------------------------
*
* [2019/01/16 浜本記載] ここまでgnomes_commonからの移植
*
* ---------------------------------------------------------------
**/

// 固定ヘッダにダブルクリックのソートダイアログ出力イベントを追加
function setDblClickEvent() {
  // FixedMidashiを使用している"～_Sort_tr"のみピックアップ
  var commonTableList = $("[id$='_Sort_tr']");
  var addedIdArray = [];

  for(var commonTable of commonTableList){
    var commonTableId = commonTable.id;                

    // 1IDにつき1度だけunbindとdblclick追加を実施
    if( addedIdArray.includes(commonTableId) ){
      continue;
    }
    else{
      $("[name=" + commonTableId + "]").unbind('dblclick');
      $("[name=" + commonTableId + "]").dblclick( function(e){
        setSortDblClick(e);
      });
      
      addedIdArray.push(commonTableId);
    }
    
  }
}

// ソートダイアログを出力する（テーブルヘッダーダブルクリック時）
function setSortDblClick(e) {
  if ($(e.target)[0].type != "checkbox") {
    // sortDialogId = ～_Sort
    var sortDialogId = (e.currentTarget.id).replace("_tr", "");
    $('#'+ sortDialogId).on('shown.bs.modal').modal();
  }
}

// 半角英数字のみ入力を許可する
function checkInputKana($this)
{
    var str=$this.value;
    while(str.match(/[^A-Z^a-z\d\-]/))
    {
        str=str.replace(/[^A-Z^a-z\d\-]/,"");
    }
    $this.value=str;
}

// 処理中にダイアログを表示
function processingDialog() {

    var str = "<div class=\"modal\" id=\"process-dialog\" data-keyboard=\"false\" data-backdrop=\"static\" >";
    str += "<div class=\"modal-dialog\">";
    str += "<div class=\"common-dialog-content\" style=\"width: 600px;\">";
    str += "  <div class=\"common-dialog-header-title clearfix\">" + $('#processDialogTitle').text() + "</div>";
    str += "  <div class=\"common-dialog-header-wrapper clearfix\">";
    str += "    <div class=\"common-dialog-body-column\" style=\"-ms-word-break: break-all;text-align: center;font-size: 20px\">" + $('#processDialogTitle').text() + "</div>";
    str += "  </div>";
    str += "</div>";
    str += "</div>";
    str += "</div>";

    $('#processModal').html(str);
    $('#process-dialog').on('shown.bs.modal', function() {
    }).modal();
}

//クリアデータの処理
$(function() {
    $ ('.modal .clr-after-click' ). on ('click' , function () {
        $('.modal').find('input:text').val('');
    });

    $('#CommentInputDialog').on('shown.bs.modal', function(e) {
        $(this).find('input:text').val('');
    });
});

// キャンセルボタンをクリックしたあとで、モーダルをオリジナルに設定する
function originalSearchModal(tableId, tableFilterId) {
    var json = $('input[name=searchSettingMap]').val();
    var map = JSON.parse(json);

    document.getElementById(tableFilterId).innerHTML = "";
    var searchMasterInfo = map[tableId];
    var conditionInfos = searchMasterInfo.conditionInfos;

    for (var i = 0; i < conditionInfos.length; i++) {
        if(!conditionInfos[i].hiddenItem) {
            var columnId = conditionInfos[i].columnId;
            var parameters = conditionInfos[i].parameters;
            var patternKeys = conditionInfos[i].patternKeys;
            var index = getIndexFromMstCondition(columnId,tableId);
            addFilter(tableId, tableFilterId, isPanecon(), index, parameters, patternKeys,columnId);
        }
    }
}

// 変更理由非活性
$(function() {
    $('.change-reason-disabled input').prop('readonly', true);
    $('.change-reason-disabled input').prop('disabled', true);
    $('.change-reason-disabled input').attr('tabindex', '-1');
});

//禁則文字列チェック
function checkProhibitionCharacters(columnValue) {
    // 禁則文字列を取得
    var prohibitCharString = $("[name='prohibitString']").val();
    prohibitCharString = prohibitCharString.substring(1, prohibitCharString.length - 1);
    for (var i = 0; i < prohibitCharString.length; i++) {
        // 禁則文字がある場合
        if (typeof columnValue !== 'undefined' && columnValue.indexOf(prohibitCharString.charAt(i)) != -1) {
            return false;
        }
    }
    return true;
}

//一覧画面の検索ボタンを押す場合、禁則文字列チェック
function checkInvalidValSearchCondition() {
    var validFlag = true;
    // 一覧画面の検索条件情報
    $(".common-flexMenu-search-box.common-flexMenu-size").each(function() {
        var filterMenuId = $(this).attr("id");
        var tableId = $(this).data("tableid");
        var indexs = $(this).data("indexs");
        // 条件情報リスト
        $.each(indexs, function(key, value) {
            // 条件マスター情報Indexの取得
            var index = key.split('.')[1];
            // カラム名
            var columnName = filterMenuId + "_" + index;
            //カラム値
            var columnValue = $("[name=" + columnName + "_value]").val();
            //禁則文字をチェック
            var valid = checkProhibitionCharacters(columnValue);

            //禁則文字がある場合、
            if (valid == false) {
                validFlag = false;
                return false;
            }
        });
    });
    return validFlag;
}

//ユーザ認証チェックする処理
function addAuthenticationProcess() {
    // ロックアウト解除ダイアログのonclickの値を取得
    var onclick = $('#unlock-check-btn').attr('onclick');
    // 認証をチェックするため、
    var authenticationCheck = $('[name=authenticationCheckValue]').val();
    // button id 取得
    var unlockBtnId = $('[name=unlockBtnId]').val();
    if(authenticationCheck != undefined && authenticationCheck != 0) {
        if (authenticationCheck == 1) {
            // 認証ダイアログを表示する
            var newOnclick = "IsnecessaryPassword('" + $('#msgUnlockDialogTitle').text() +"','" + $('#userId').text() + "','" + onclick + "','" + unlockBtnId + "', false);";
        } else {
            // ダブル認証ダイアログを表示する
            var newOnclick = "IsnecessaryPassword('" + $('#msgUnlockDialogTitle').text() +"','" + $('#userId').text() + "','" + onclick + "','" + unlockBtnId + "', true);";
        }
        //認証ダイアログの為、onclickの値を置き換える
        $('#unlock-check-btn').attr("onclick", newOnclick);
    }
}

//パスワード変更の為、ユーザ認証チェックする処理
function addAuthenticationProcessForUpdPsw() {
    // パスワード変更ダイアログのonclickの値を取得
    var updateOnclick = $('#update_psw_btn').attr('onclick');
      // パスワード変更ダイアログのonclickの値を取得
    var initialPswOnclick = $('#update_initial_psw_btn').attr('onclick');
    // 認証をチェックするため、
    var authenticationCheckPassword = $('[name=authenticationCheckPassword]').val();
    // パスワード変更ボタン 取得
    var updatePasswordBtnId = $('[name=passwordBtnId]').val();

    if(authenticationCheckPassword != undefined && authenticationCheckPassword != 0) {
        if (authenticationCheckPassword == 1) {
            // 認証ダイアログを表示する
            var newOnclick = "IsnecessaryPassword('" + $('#msgPasswordChangeDialogTitle').text() +"','" + $('#userId').text() + "','" + updateOnclick + "','" + updatePasswordBtnId + "', false);";
            // 認証ダイアログを表示する
            var newInitialOnclick = "IsnecessaryPassword('" + $('#msgPasswordChangeDialogTitle').text() +"','" + $('#userId').text() + "','" + initialPswOnclick + "','" + updatePasswordBtnId + "', false);";
        } else {
            // ダブル認証ダイアログを表示する
            var newOnclick = "IsnecessaryPassword('" + $('#msgPasswordChangeDialogTitle').text() +"','" + $('#userId').text() + "','" + updateOnclick + "','" + updatePasswordBtnId + "', true);";
            // ダブル認証ダイアログを表示する
            var newInitialOnclick = "IsnecessaryPassword('" + $('#msgPasswordChangeDialogTitle').text() +"','" + $('#userId').text() + "','" + initialPswOnclick + "','" + updatePasswordBtnId + "', true);";
        }
        //　パスワード変更の為、onclickの値を置き換える
        $('#update_psw_btn').attr("onclick", newOnclick);
        //　パスワード初期化の為、onclickの値を置き換える
        $('#update_initial_psw_btn').attr("onclick", newInitialOnclick);
    }
}

//テキストエリアの内容がMaxLengthを超えている場合、改行コードの数だけ削除を行う処理
//（改行コードの文字数を1として扱うがDBでは2と認識する為）
function delTextareaValMaxlengthOver(textareaName) {
  // テキストエリアの内容を取得
  var textareaString = $('[name=' + textareaName + ']').val();
  // 内容の文字数を取得
  var textareaStringCount = textareaString.length;
  // 改行コード「\n」の数を取得
  var lineFeedCodeCount = ( textareaString.match( /\n/g ) || [] ).length;
  // 内容の文字数に改行コード「\n」の数を足す
  textareaStringCount = textareaStringCount + lineFeedCodeCount;
  // テキストエリアのmaxlengthを取得
  var maxLength = $('[name=' + textareaName + ']').attr('maxlength');

  // maxlengthと文字数を比較
  if (maxLength != null && maxLength != undefined) {
      // maxLengthと文字数を比較
      var charactersExceedCount = maxLength - textareaStringCount;
      if (charactersExceedCount < 0) {
          // maxlengthを超えた文字数分、文字列から削除
          $('[name=' + textareaName + ']').val(textareaString.slice(0, charactersExceedCount));
      }
  }
}

//警告チェック設定
function setWarningFlag() {
    //集中データがある場合、
    $('[name=inputChangeFlag]').val('1');
}

//警告ダイアログ表示処理
function warmingAlertProcess(changeFlagVal,okcmd) {
    // 押すボタンのデータ入力確認ダイアログ表示フラグが "１" 場合
    if (changeFlagVal == "1") {
        // 編集中データがある場合
        if ($('[name=inputChangeFlag]').val() == "1") {
            var format = $('[name=dateFormat]').val() + ":ss";
            var dateFormat = comDateFormat(new Date(), format);
            var decOkCmd = decodeURIComponent(okcmd);
            makeMessageModalBase(
                    $('[name=mesgWarningCategoryName]').val(), //メッセージタイトル
                    $('[name=mesgWarningIconName]').val(),     //アイコン名
                    dateFormat,                                //発生日時
                    $(".common-header-userName").html(),       //操作ユーザ名
                    $(".common-header-deviceName").html(),     //操作ホスト
                    $('[name=mesWarningBody]').val(),          //メッセージ本文
                    null,                                      //メッセージ詳細
                    MesBtnMode_OKCancel,                       //ボタンモード
                    null,                                      //デフォルトボタン
                    null,                                      //コマンド
                    null,                                      //リンク情報
                    false,                                     //オープン時詳細を開くかどうか
                    decOkCmd,                                  //OKボタン押下時呼び出し関数
                    null,                                      //Cancelボタン押下呼び出し関数
                    null,                                      //ボタンID
                    ""                                         //DB領域
            );
            return 0;
        } else {
            return 1;
        }
    } else {
        return 1;
    }
}

// FunctionBean延命措置用タイマー
var timerArrivalFunctionBeanId=0;

// FunctionBean延命措置用タイマー周期時間(ms)
var timerArrivalFunctionBeanTime=300*1000;

$(document).ready(function(){
    setTimerArrivalFunctionBean();
});

// FunctionBean延命措置用タイマー処理
function timerArrivalFunctionBeanTimeOut(){
    clearTimerArrivalFunctionBean();

    if(submitFlag != true){
      //ログインしていない場合は確認しない
      if(getLocalStorage("LOGINED")==="1"){
        commandSubmit("WATCHDOG_CONVERSION",false,true);
      }
    }

    setTimerArrivalFunctionBean();
}

// FunctionBean延命措置用タイマー設定
function setTimerArrivalFunctionBean() {
    timerArrivalFunctionBeanId = setTimeout("timerArrivalFunctionBeanTimeOut()", timerArrivalFunctionBeanTime);
}

// FunctionBean延命措置用タイマークリア
function clearTimerArrivalFunctionBean() {
    if(timerArrivalFunctionBeanId != 0){
        clearTimeout(timerArrivalFunctionBeanId);
        timerArrivalFunctionBeanId=0;
    }
}

//===================================================================
/**
* [機能]  getPassWordSalt:AjaxでSalt値を返す共通関数
* [引数]  formToCertifyHashJSON:certInfoを作るコールバック関数
*         callSuccessFunction:呼び出しで正常になったら返すコールバック関数
* [戻値]  data（certInfoの中身)
*/
//===================================================================
function getPassWordSalt(jsonCertInfo) {
  var result = $.ajax({
      type: 'POST',
      contentType: 'application/json',
//      url: "rest-pharms/Y01001S001/GetSalt",
      url: "rest/A01001S001/GetSalt",
      async: false,
      dataType: "json",
      data: jsonCertInfo,
  }).responseJSON;
  return result;
}

//===================================================================
/**
* [機能] パスワードをHash化する
* [引数]  crtInfoの内容（各自内容を詰めておく)
* [戻値]  Hash化パスワード
*/
//===================================================================
function getHashPasswd(jsonCertInfo) {

    var salttString = getPassWordSalt(jsonCertInfo).password;
    var jsonCertInfo = JSON.parse(jsonCertInfo);
    var shatxt = getHash(jsonCertInfo.password + salttString);

    return salttString + "$" + shatxt;
}

//ハッシュ化処理
    function getHash(text) {

        var sha256 = function sha256(ascii) {
            function rightRotate(value, amount) {
                return (value>>>amount) | (value<<(32 - amount));
            };

            var mathPow = Math.pow;
            var maxWord = mathPow(2, 32);
            var lengthProperty = 'length'
            var i, j; // Used as a counter across the whole file
            var result = ''

            var words = [];
            var asciiBitLength = ascii[lengthProperty]*8;

            //* caching results is optional - remove/add slash from front of this line to toggle
            // Initial hash value: first 32 bits of the fractional parts of the square roots of the first 8 primes
            // (we actually calculate the first 64, but extra values are just ignored)
            var hash = sha256.h = sha256.h || [];
            // Round constants: first 32 bits of the fractional parts of the cube roots of the first 64 primes
            var k = sha256.k = sha256.k || [];
            var primeCounter = k[lengthProperty];
            /*/
            var hash = [], k = [];
            var primeCounter = 0;
            //*/

            var isComposite = {};
            for (var candidate = 2; primeCounter < 64; candidate++) {
                if (!isComposite[candidate]) {
                    for (i = 0; i < 313; i += candidate) {
                        isComposite[i] = candidate;
                    }
                    hash[primeCounter] = (mathPow(candidate, .5)*maxWord)|0;
                    k[primeCounter++] = (mathPow(candidate, 1/3)*maxWord)|0;
                }
            }

            ascii += '\x80' // Append' bit (plus zero padding)
            while (ascii[lengthProperty]%64 - 56) ascii += '\x00' // More zero padding
            for (i = 0; i < ascii[lengthProperty]; i++) {
                j = ascii.charCodeAt(i);
                if (j>>8) return; // ASCII check: only accept characters in range 0-255
                words[i>>2] |= j << ((3 - i)%4)*8;
            }
            words[words[lengthProperty]] = ((asciiBitLength/maxWord)|0);
            words[words[lengthProperty]] = (asciiBitLength)

            // process each chunk
            for (j = 0; j < words[lengthProperty];) {
                var w = words.slice(j, j += 16); // The message is expanded into 64 words as part of the iteration
                var oldHash = hash;
                // This is now the undefinedworking hash", often labelled as variables a...g
                // (we have to truncate as well, otherwise extra entries at the end accumulate
                hash = hash.slice(0, 8);

                for (i = 0; i < 64; i++) {
                    var i2 = i + j;
                    // Expand the message into 64 words
                    // Used below if
                    var w15 = w[i - 15], w2 = w[i - 2];

                    // Iterate
                    var a = hash[0], e = hash[4];
                    var temp1 = hash[7]
                        + (rightRotate(e, 6) ^ rightRotate(e, 11) ^ rightRotate(e, 25)) // S1
                        + ((e&hash[5])^((~e)&hash[6])) // ch
                        + k[i]
                        // Expand the message schedule if needed
                        + (w[i] = (i < 16) ? w[i] : (
                                w[i - 16]
                                + (rightRotate(w15, 7) ^ rightRotate(w15, 18) ^ (w15>>>3)) // s0
                                + w[i - 7]
                                + (rightRotate(w2, 17) ^ rightRotate(w2, 19) ^ (w2>>>10)) // s1
                            )|0
                        );
                    // This is only used once, so *could* be moved below, but it only saves 4 bytes and makes things unreadble
                    var temp2 = (rightRotate(a, 2) ^ rightRotate(a, 13) ^ rightRotate(a, 22)) // S0
                        + ((a&hash[1])^(a&hash[2])^(hash[1]&hash[2])); // maj

                    hash = [(temp1 + temp2)|0].concat(hash); // We don't bother trimming off the extra ones, they're harmless as long as we're truncating when we do the slice()
                    hash[4] = (hash[4] + temp1)|0;
                }

                for (i = 0; i < 8; i++) {
                    hash[i] = (hash[i] + oldHash[i])|0;
                }
            }

            for (i = 0; i < 8; i++) {
                for (j = 3; j + 1; j--) {
                    var b = (hash[i]>>(j*8))&255;
                    result += ((b < 16) ? 0 : '') + b.toString(16);
                }
            }
            return result;
        };

        return sha256(text)

    }

//===================================================================
/**
* [機能]  HTMエスケープする
* [引数]  unsafeText シングルクォーテーションが含まれる文字列
* [戻値]  エスケープ後の文字列
*/
//===================================================================
function getStringEscapeSpecialChars(unsafeText) {

  if(typeof unsafeText !== 'string'){
      return unsafeText;        
  }

  return unsafeText
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
}
