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

$(document).ready(function() {
	$('.dropdown-submenu a.test').on("click", function(e) {
		$(this).next('ul').toggle();
		e.stopPropagation();
		e.preventDefault();
	});
});
