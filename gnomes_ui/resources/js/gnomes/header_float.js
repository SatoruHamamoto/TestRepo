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
        // �X�N���[���������������ɏ������s��
        $(window).scroll(function () {

            var $table = $(".table");          // �e�[�u���̗v�f���擾
            var $thead = $table.children("thead");  // thead�擾
            var toffset = $table.offset();          // �e�[�u���̈ʒu���擾
            // �e�[�u���ʒu+�e�[�u���c�� < �X�N���[���ʒu < �e�[�u���ʒu
            if(toffset.top + $table.height()< $(window).scrollTop()
              || toffset.top > $(window).scrollTop()){
                // �N���[���e�[�u�������݂���ꍇ�͏���
                var $clone = $("#clonetable");
                if($clone.length > 0){
                    $clone.css("display", "none");
                }
            }
            // �e�[�u���ʒu < �X�N���[���ʒu < �e�[�u���ʒu+�e�[�u���c��
            else if(toffset.top < $(window).scrollTop()){
                // �N���[���e�[�u�������݂��邩�m�F
                var $clone = $("#clonetable");
                if($clone.length == 0){
                    // ���݂��Ȃ��ꍇ�́Athead�̃N���[�����쐬
                    $clone= $thead.clone(true);
                    // id��clonetable�Ƃ���
                    $clone.attr("id", "clonetable");
                    // body���ɗv�f��ǉ�
                    $clone.appendTo("body");
                    // thead��CSS���R�s�[����
                    StyleCopy($clone, $thead);
                    // thead�̎q�v�f(tr)�����[�v������
                    for(var i = 0; i < $thead.children("tr").length; i++)
                    {
                        // i�Ԗڂ�tr���擾
                        var $theadtr = $thead.children("tr").eq(i);
                        var $clonetr = $clone.children("tr").eq(i);
                        // tr�̎q�v�f(th)�����[�v������
                        for (var j = 0; j < $theadtr.eq(i).children("th").length; j++){
                            // j�Ԗڂ�th���擾
                            var $theadth = $theadtr.eq(i).children("th").eq(j);
                            var $cloneth = $clonetr.eq(i).children("th").eq(j);
                            // th��CSS���R�s�[����
                            StyleCopy($cloneth, $theadth);
                        }
                    }
                }

                // �R�s�[����thead�̕\���`����table�ɕύX
                $clone.css("display", "table");
                // position���u���E�U�ɑ΂���Βl�Ƃ���
                $clone.css("position", "fixed");
                $clone.css("border-collapse", "collapse");
                // position�̈ʒu��ݒ�(left = ���̃e�[�u����left�Ƃ���)
                $clone.css("left", toffset.left - $(window).scrollLeft());
                // position�̈ʒu��ݒ�(top���u���E�U�̈�ԏ�Ƃ���)
                $clone.css("top", "100px");
                // �\�����Ԃ���ԗD�悳����
                $clone.css("z-index", 99);

                $clone.css("table-layout", "fixed");
            }
        });

        // CSS�̃R�s�[
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
