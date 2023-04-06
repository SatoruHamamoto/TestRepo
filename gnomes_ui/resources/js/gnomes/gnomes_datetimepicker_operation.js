//日時入力の設定(Bootstrap datetimepicker)
$(document).on("focus", ".datetime", function () {

    // ロケールを取得する
    var userLocale = $('#loginUserLocaleId').val().split('_');

    $(this).datetimepicker({
        locale: userLocale[0],
        dayViewHeaderFormat: $('#calenderHeaderFormat-YYYYMM').val(), // カレンダーのヘーダフォーマットを取得する
        format : $(this).data("date-format"),
        showClose : true,
        showClear : true,
        showTodayButton : true,
        useCurrent : false,
        debug : true,
        widgetParent : '.common-main-area',
        parseInputDate: function(inputDate) {

          var parseDateBySlash=null;
          var parseDateByHyphen=null;

          var parseDateTime = inputDate.split(' ');
          if(parseDateTime.length > 1){
            parseDateBySlash = parseDateTime[0].split('/');
            parseDateByHyphen = parseDateTime[0].split('-');
          }
          else {
            parseDateBySlash = inputDate.split('/');
            parseDateByHyphen = inputDate.split('-');
          }

          //日付と時刻がある場合は処理除外する
          if(parseDateTime.length == 1){

            // 入力する日付のフォーマットを変更する(4-1==>2020/04/01)
            if (parseDateByHyphen.length == 2) {
              // 今年の値を取得
              var year = new Date().getFullYear();
              // 今年と入力する日付を連結
              parseDateByHyphen = year + "-" + inputDate;
              return moment(parseDateByHyphen, $('#datetimeFormat-YYYYMMDD').val());
            } else if (parseDateByHyphen.length == 3) {
              // (20-4-1==>2020/04/01)
              return moment(inputDate, $('#datetimeFormat-YYYYMMDD').val());
            }

            // 入力する日付のフォーマットを変更する(4/1==>2020/04/01)
            if (parseDateBySlash.length == 2) {
              // 今年の値を取得
              var year = new Date().getFullYear();
              // 今年と入力する日付を連結
              parseDateBySlash = year + "/" + inputDate;
              return moment(parseDateBySlash, $('#datetimeFormat-YYYYMMDD').val());
            } else if (parseDateBySlash.length == 3) {
              // (20/4/1==>2020/04/01)
              return moment(inputDate, $('#datetimeFormat-YYYYMMDD').val());
            }
          }
          return moment(inputDate);
        },
     });

    $(this).data("DateTimePicker").viewMode("days");
    $(this).data("inputValue",$(this).val());

    var gnomes_datetime_change = false;

    $(this).off("dp.change");
    $(this).off("dp.show");
    $(this).off("dp.hide");

    $(this)
        .on('dp.change', function(e){
//            console.log("dp.change oldDate=" + e.oldDate + " date=" + e.date );
            if (e.oldDate == null) {
                gnomes_datetime_change = true;
            } else {
                var strFormat = $(this).data("date-format");
                var strOldDate = e.oldDate.format(strFormat)
                var momOldDate = moment(strOldDate, strFormat);

                if (e.date == false) {
                 e.date = e.oldDate;
                }
                var strDate = e.date.format(strFormat)
                var momDate = moment(strDate, strFormat);

//                console.log("dp.change strOldDate=" + strOldDate + " strDate=" + strDate);
//                console.log("dp.change momOldDate=" + momOldDate + " momDate=" + momDate);

                if (strOldDate != strDate) {
//                  console.log("dp.change change true --------");
                    gnomes_datetime_change = true;
                } else {
                  $(this).val(strDate);
                }
            }
        });

    $(this)
        .on('dp.show', function(e){
//            console.log("dp.show");
        });

    $(this)
        .on('dp.hide', function(e){
            var strFormat = $(this).data("date-format");
            var strDate = e.date.format(strFormat);
            var momDate = moment(strDate, strFormat);

            if (gnomes_datetime_change == true) {
//                console.log("dp.hide date=" + e.date + " momDate=" + momDate + " str=" + strDate);
//               $(this).change();
                var f = $(this);
                var hoge = setInterval( function() {
                             f.change();
                             clearInterval(hoge);
                           }, 1);
            } else {
//                console.log("dp.hide no change date=" + e.date + " momDate=" + momDate + " str=" + strDate);
            }

            gnomes_datetime_change = false;
        });

});


