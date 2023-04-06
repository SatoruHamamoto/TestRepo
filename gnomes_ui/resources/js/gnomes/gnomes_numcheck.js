//数値入力チェック
$(function(){

  if ($("input[name=screenId]").val() !== "MGY001" && $("input[name=screenId]").val() !== "OPY001") {
    //ロケールを取得
    var localeId = $('#loginUserLocaleId').val().split('_')[0];
    //ロケール設定
    numeral.locale(localeId);
  }

  // 小数点のため、ピリオドを使うロケールリスト
  var localeListWithDottDeli = new Array("ja","th", "en");

  //入力キー制御
  $(document).on("keydown", ".gnomes-number",function (event) {
    //入力キーのチェック
    var keyCode = event.keyCode;
    if(event.ctrlKey){
        //カット、コピー、ペーストの有効化
        if(keyCode == 67 || keyCode == 86 || keyCode == 88){
           return true;
        }
    }
    if(event.shiftKey){
        //shift押下時はカーソルキー、タブ以外は無効
        if((keyCode >= 37 && keyCode <= 40) || keyCode == 9 ){
            return true;
        } else {
            return false;
        }

    }
    //数値キー（テンキー含む）、カーソルキー、ハイフン、ピリオド、バックスペース、タブ、デリートの有効化
    if((keyCode >= 48 && keyCode <= 57) || (keyCode >= 96 && keyCode <= 105) || (keyCode >= 37 && keyCode <= 40) || keyCode == 109 || keyCode == 110 || keyCode == 189 || keyCode == 188 || keyCode == 190 || keyCode == 8 || keyCode == 9 || keyCode == 46){
      // タイ、日本、アメリカ、イギリス　（コンマは無効）
      if($.inArray(localeId, localeListWithDottDeli) !== -1) {
        if(keyCode == 188) {
          return false;
        }
      } else {　
        // タイ、日本、アメリカ、イギリス以外は　（ピリオドは無効）
        if(keyCode == 190) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  });

  //数値チェック
  $(document).on("blur", ".gnomes-number",function (event) {
    var before_val;
    //入力値の保持
    before_val = $(this).val();

    if($.inArray(localeId, localeListWithDottDeli) !== -1) {
      var after_replace_val = before_val.replace(/,/g, '');
    } else {
      var after_replace_val = before_val.replace(/,/g, '.')
    }

    if($.isNumeric(after_replace_val)){
      if($.inArray(localeId, localeListWithDottDeli) !== -1) {
         $(this).val(after_replace_val);
      } else {
         $(this).val(before_val);
      }
      return true;
    } else {
      $(this).val("");
    }
  });

  //フォーカスアウト時の桁数チェック
  $(document).on("blur", ".number-digit-check",function (event) {
     
    var id = $(this).attr("id");
    var nlist = null
    if(id == null){
      nlist = $(this);
    } else {
      var nlist = $(".number-digit-check#" + selectorEscape(id));
    }    
    
    //画面内の同じIDの数値テキストボックス（IDがない場合は自要素のみ）に実施
    nlist.each(function(i, numBox){
        //データチェック
        if($(numBox).val() != ""){
          var integer_digits = "";
          var decimal_digits = "";

          //許容桁数の取得
          var hidden = $(numBox).data("hidden-digits-id");

          if(hidden){
              integer_digits = $("#" + hidden).data("integer-digits");
              decimal_digits = $("#" + hidden).data("decimal-digits");
          } else {
              integer_digits = $(numBox).data("integer-digits");
              decimal_digits = $(numBox).data("decimal-digits");
          }

          //入力値の桁数判断
          var input_string_format = "0,0";
          var count = 0;
          var dot_split = $(numBox).val().split('.');
          var comman_split = $(numBox).val().split(',');

          // 入力値に小数点がある場合、小数点の桁数を取得する
          if (dot_split[1] != null || comman_split[1] != null) {

            if(dot_split[1] != null){
              count = dot_split[1].length;
            } else {
              count = comman_split[1].length;
            }
            if (count < decimal_digits) {
              count = decimal_digits
            }
          } else {
            // 入力値に小数点がない場合、ディフォルトを設定する
            count = decimal_digits
          }
          //入力値の桁数判断用フォーマット作成
          input_string_format = input_string_format + ".[";
          for (var i = count; --i >= 0;) {
            input_string_format = input_string_format + '0';
          }
          input_string_format = input_string_format + "]";
          //入力値のフォーマット
          var input_val_after_digit = numeral($(numBox).val()).format(input_string_format);

          //入力値の保持
          var val_before = numeral($(numBox).val()).value();

          //変換用フォーマット作成
          var format_string = "0,0";
          if(!$.isNumeric(decimal_digits)){
            format_string = format_string + ".[000000000000]";
          } else if(decimal_digits != 0){
              format_string = format_string + ".";
              for (var i = decimal_digits; --i >= 0;) {
                  format_string = format_string + '0';
              }
          }

          //桁数判断用フォーマット作成
          var format_string_compare = "0,0";
          if(decimal_digits != 0){
            format_string_compare = format_string_compare + ".[";
              for (var i = decimal_digits; --i >= 0;) {
                format_string_compare = format_string_compare + '0';
              }
              format_string_compare = format_string_compare + "]";
          }

          //小数の判定
          //フォーマット文字列生成
          if($.isNumeric(decimal_digits)){

              //変換実行
              var val_after_digit = numeral($(numBox).val()).format(format_string_compare);
              //フォーマット後に値が変わった場合は消去
              if(String(input_val_after_digit) == String(val_after_digit)){

              } else {

                  $(numBox).val("");
                  return true;
              }
          } else {

          }

          //整数の判定
          if($.isNumeric(integer_digits)){
              var val_after_integer = val_before;
              if(val_after_integer < 0){
                  val_after_integer = -val_after_integer;
              }
              val_after_integer = Math.floor(val_after_integer);
              if(integer_digits != 0){
                  if(String(val_after_integer).length > integer_digits){
                      $(numBox).val("");
                      return true;
                  }
              } else {
                  if(val_after_integer != 0){
                      $(numBox).val("");
                      return true;
                  }
              }
          }

          var val_result = numeral($(numBox).val()).format(format_string);
          $(numBox).val(val_result);

        } else {
            $(numBox).val("");
        }
    });
  });

  //フォーカスアウト時のフォーマット実行処理
  $(document).on("blur", ".gnomes-number-format",function (event) {
    
    var id = $(this).attr("id");
    var nlist = null
    if(id == null){
      nlist = $(this);
    } else {
      var nlist = $(".gnomes-number-format#" + selectorEscape(id));
    }    
    
    //画面内の同じIDの数値テキストボックス（IDがない場合は自要素のみ）に実施
    nlist.each(function(i, numBox){
          //数値チェック
        if($.isNumeric(numeral($(numBox).val()).value())){
          var val = numeral($(numBox).val()).format('0.[000000000000]');

          if(val > 4503599627370496 || val < -4503599627370496){
            $(numBox).val("");
              return false;
          }

        var integer_digits = "";
        var decimal_digits = "";

        //許容桁数の取得
        if($(numBox).hasClass("number-digit-check")){
            var hidden = $(numBox).data("hidden-digits-id");

            if(hidden){
                integer_digits = $("#" + hidden).data("integer-digits");
                decimal_digits = $("#" + hidden).data("decimal-digits");
            } else {
                integer_digits = $(numBox).data("integer-digits");
                decimal_digits = $(numBox).data("decimal-digits");
            }
        }

        //変換用フォーマット作成
        var format_string = "0,0";
        if(!$.isNumeric(decimal_digits)){
            format_string = format_string + ".[000000000000]";
        } else if(decimal_digits != 0){
            format_string = format_string + ".";
            for (var i = decimal_digits; --i >= 0;) {
                format_string = format_string + '0';
            }
        }

        //フォーマット実行
        var replacedNum = numeral(val).format(format_string);
        $(numBox).val(replacedNum);

        } else {
            $(numBox).val("");
        }
      
    });
  });

  //最後のインデックスにカーソルを移動する
  $.fn.putCursorAtEnd = function() {
    return this.each(function() {
      // Cache references
      var $el = $(this),
          el = this;

      // Only focus if input isn't already
      if (!$el.is(":focus")) {
       $el.focus();
      }
      // If this function exists... (IE 9+)
      if (el.setSelectionRange) {
        // Double the length because Opera is inconsistent about whether a carriage return is one character or two.
        var len = $el.val().length * 2;

        // Timeout seems to be required for Blink
        setTimeout(function() {
          el.setSelectionRange(len, len);
        }, 1);
      } else {
        // As a fallback, replace the contents with itself
        // Doesn't work in Chrome, but Chrome supports setSelectionRange
        $el.val($el.val());
      }
      // Scroll to the bottom, in case we're in a tall textarea
      // (Necessary for Firefox and Chrome)
      this.scrollTop = 999999;
    });
  };

  //フォーカスイン時のフォーマット解除処理
  $(document).on("focus", ".gnomes-number-format",function (event) {

    //最初にドットをクリックするときは、0.を設定
    if($(this).val() == "null.") {
      $(this).val("0.");
      $(this).putCursorAtEnd();
      return;
    }
    if($.inArray(localeId, localeListWithDottDeli) !== -1) {
      var split = $(this).val().split('.');
    } else {
      var split = $(this).val().split(',');
    }

    if(split[1] == null || split[1] == undefined){
      //フォーマット解除
        $(this).val(numeral($(this).val()).value());
        return;
    }

    if(numeral(split[0]).value() != null){
      if($.inArray(localeId, localeListWithDottDeli) !== -1) {
        $(this).val(numeral(split[0]).value() + "." + split[1]);
      } else {
        $(this).val(numeral(split[0]).value() + "," + split[1]);
      }
    }
  });
});

function selectorEscape(val){
    return val.replace(/[ !"#$%&'()*+,.\/:;<=>?@\[\\\]^`{|}~]/g, '\\$&');
};