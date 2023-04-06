//キーボード表示
$(document).ready(function() {

	$('input').attr('autocomplete', 'off');


    $(document).on("focus", ".common-keyboard-input-char", function(){
        $(this).keyboard({
            userClosed: true,
            css: {
                // add new class(noaccept) into default state
                buttonDefault: 'ui-state-default ui-corner-all noaccept'
            }
        })
        .addTyping({
        showTyping: false
        });
    });

    $("#login_username").focus();

    //文字キーボード
    $(document).on("focus", "#lock-dialog .common-keyboard-input-char", function(){
        $(this).keyboard({
            userClosed: true,
            css: {
                // add new class(noaccept) into default state
                buttonDefault: 'ui-state-default ui-corner-all noaccept'
            }
        })
    	.addTyping({
	    showTyping: false
    	});
    });

    //文字キーボード
    $(document).on("focus", "#change-pass-dialog .common-keyboard-input-char", function(){
        $(this).keyboard({
            userClosed: true,
            css: {
                // add new class(noaccept) into default state
                buttonDefault: 'ui-state-default ui-corner-all noaccept'
            }
        })
        .addTyping({
        showTyping: false
        });
    });

    //文字数オーバー時の消去処理
    $(document).on("change", ".common-keyboard-input-char", function () {
        if($(this).val().length > $(this).attr('maxlength')){
            $(this).val("");
        }
    });


    //数字キーボード
    $(document).on("focus", ".common-keyboard-input-num", function () {
        $(this).keyboard({
            layout: 'custom',
            userClosed: true,
            css: {
                // add new class for number keyboard
                buttonDefault: 'ui-state-default ui-corner-all ui-num-keyboard'
            },
            customLayout: {
                'normal' : [
                    '7 8 9 {bksp}',
                    '4 5 6 {del}',
                    '1 2 3 {cancel}',
                    '0 . - {accept}'
                ]
            },
            display: {
                'accept' : ' Enter ',
                'del' : 'Del',
                'cancel' : 'Cancel'
            },
            visible: function(e, keyboard, el) {
                keyboard.$preview[0].select();
            }
        })
    	.addTyping({
	    showTyping: false
    	});

        $(this).removeClass( "ui-widget-content" );
        $(this).removeClass( "ui-keyboard-input" );
        $(this).removeClass( "ui-corner-all" );
    });

    //キーボードのリターンでacceptにする
    $.keyboard.keyaction.enter = function( kb ) {
        // same as $.keyboard.keyaction.accept();
        kb.close( true );
        return false;     // return false prevents further processing
    };
});
