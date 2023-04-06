/**
 *
 *
 * @version 0.01
 * @author  30018232 S.Hosokawa
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
// ===================================================================
var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C",
		"C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy",
		"Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python",
		"Ruby", "Scala", "Scheme" ];

$(function() {

	//パネコンの場合キーボード表示をON
	if (true) {
		$('.NMLTXT_NORMAL').keyboard({
			layout : 'qwerty',
			usePreview : false,
			css : {
				// input & preview
				input : '',
				// keyboard container
				container : 'center-block dropdown-menu', // jumbotron
				// default state
				buttonDefault : 'btn btn-default btn-lg keyboardButton',
				// hovered button
				buttonHover : 'btn-primary',
				// Action keys (e.g. Accept, Cancel, Tab, etc);
				// this replaces "actionClass" option
				buttonAction : 'active',
				// used when disabling the decimal button {dec}
				// when a decimal exists in the input area
				buttonDisabled : 'disabled'
			}

		});
	}
});