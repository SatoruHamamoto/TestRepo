/**
 *
 *
 * @version 0.01
 * @author  03501213 S.Hamamoto
 *
 * ============================ MODIFICATION HISTORY ============================
 * Release  Date       Comment
 * ------------------------------------------------------------------------------
 *
 * [END OF MODIFICATION HISTORY]
 * ==============================================================================
 */

//===================================================================
/**
 *
 */
//===================================================================


(function($){

    $(function(){
        // スクロールが発生した時に処理を行う
        $(window).scroll(function () {

            var $table = $(".table");          // テーブルの要素を取得
            var $thead = $table.children("thead");  // thead取得
            var toffset = $table.offset();          // テーブルの位置情報取得
            // テーブル位置+テーブル縦幅 < スクロール位置 < テーブル位置
            if(toffset.top + $table.height()< $(window).scrollTop()
              || toffset.top > $(window).scrollTop()){
                // クローンテーブルが存在する場合は消す
                var $clone = $("#clonetable");
                if($clone.length > 0){
                    $clone.css("display", "none");
                }
            }
            // テーブル位置 < スクロール位置 < テーブル位置+テーブル縦幅
            else if(toffset.top < $(window).scrollTop()){
                // クローンテーブルが存在するか確認
                var $clone = $("#clonetable");
                if($clone.length == 0){
                    // 存在しない場合は、theadのクローンを作成
                    $clone= $thead.clone(true);
                    // idをclonetableとする
                    $clone.attr("id", "clonetable");
                    // body部に要素を追加
                    $clone.appendTo("body");
                    // theadのCSSをコピーする
                    StyleCopy($clone, $thead);
                    // theadの子要素(tr)分ループさせる
                    for(var i = 0; i < $thead.children("tr").length; i++)
                    {
                        // i番目のtrを取得
                        var $theadtr = $thead.children("tr").eq(i);
                        var $clonetr = $clone.children("tr").eq(i);
                        // trの子要素(th)分ループさせる
                        for (var j = 0; j < $theadtr.eq(i).children("th").length; j++){
                            // j番目のthを取得
                            var $theadth = $theadtr.eq(i).children("th").eq(j);
                            var $cloneth = $clonetr.eq(i).children("th").eq(j);
                            // thのCSSをコピーする
                            StyleCopy($cloneth, $theadth);
                        }
                    }
                }

                // コピーしたtheadの表示形式をtableに変更
                $clone.css("display", "table");
                // positionをブラウザに対し絶対値とする
                $clone.css("position", "fixed");
                $clone.css("border-collapse", "collapse");
                // positionの位置を設定(left = 元のテーブルのleftとする)
                $clone.css("left", toffset.left - $(window).scrollLeft());
                // positionの位置を設定(topをブラウザの一番上とする)
                $clone.css("top", "100px");
                // 表示順番を一番優先させる
                $clone.css("z-index", 99);

                $clone.css("table-layout", "fixed");
            }
        });

        // CSSのコピー
        function StyleCopy($copyTo, $copyFrom){
            $copyTo.css("width",
                        $copyFrom.css("width"));
            $copyTo.css("height",
                        $copyFrom.css("height"));

            $copyTo.css("padding-top",
                        $copyFrom.css("padding-top"));
            $copyTo.css("padding-left",
                        $copyFrom.css("padding-left"));
            $copyTo.css("padding-bottom",
                        $copyFrom.css("padding-bottom"));
            $copyTo.css("padding-right",
                        $copyFrom.css("padding-right"));

            $copyTo.css("background",
                        $copyFrom.css("background"));
            $copyTo.css("background-color",
                        $copyFrom.css("background-color"));
            $copyTo.css("vertical-align",
                        $copyFrom.css("vertical-align"));

            $copyTo.css("border-top-width",
                        $copyFrom.css("border-top-width"));
            $copyTo.css("border-top-color",
                        $copyFrom.css("border-top-color"));
            $copyTo.css("border-top-style",
                        $copyFrom.css("border-top-style"));

            $copyTo.css("border-left-width",
                        $copyFrom.css("border-left-width"));
            $copyTo.css("border-left-color",
                        $copyFrom.css("border-left-color"));
            $copyTo.css("border-left-style",
                        $copyFrom.css("border-left-style"));

            $copyTo.css("border-right-width",
                        $copyFrom.css("border-right-width"));
            $copyTo.css("border-right-color",
                        $copyFrom.css("border-right-color"));
            $copyTo.css("border-right-style",
                        $copyFrom.css("border-right-style"));

            $copyTo.css("border-bottom-width",
                        $copyFrom.css("border-bottom-width"));
            $copyTo.css("border-bottom-color",
                        $copyFrom.css("border-bottom-color"));
            $copyTo.css("border-bottom-style",
                        $copyFrom.css("border-bottom-style"));
        }
    });
})(jQuery);
