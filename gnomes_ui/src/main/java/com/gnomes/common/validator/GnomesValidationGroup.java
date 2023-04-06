package com.gnomes.common.validator;

/**
 * バリデータアノテーション順定義
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R4.11.01 2022/11/02 YJP/A.Okada               初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class GnomesValidationGroup{
	
	public static enum GnomesValidationOrder{
		/** GnomesNotBlank */
		MV01_0002(0),
		/** GnomesNotEmpty */
		MV01_0001(1),
		/** GnomesNotNull */
		MV01_0004(2),
		/** GnomesSize */
		MV01_0010(3),  // 
		/** ByteSize */
		MV01_0012(4),
		/** GnomesDigits */
		MV01_0003(5),
		/** GnomesDigitsVariable */
		MV01_0031(6),
		/** GnomesMax, GnomesDecimalMax*/
		MV01_0007(7),
		/** GnomesMin, GnomesDecimalMin */
		MV01_0008(9),
		/** GnomesPositive */
		MV01_0022(11),
		/** GnomesPositiveOrZero */
		MV01_0023(12),
		/** GnomesNegative */
		MV01_0020(13),
		/** GnomesNegativeOrZero */
		MV01_0021(14),
		/** GnomesFurute */
		MV01_0005(15),
		/** GnomesPast */
		MV01_0006(16),
		/** ComparePast */
		MV01_0011(17),
		/** GnomesPattern, GnomesProhibitionString */
		MV01_0009(18),
		/** GnomesEmail */
		MV01_0024(20),
		/** GnomesUrl */
		MV01_0025(21);
		
		// フィールド
	    private final int order;

	    // コンストラクタ
	    private GnomesValidationOrder(int order) {
	        this.order = order;
	    }

	    // メソッド
	    public static int getOrder(String messageId) {
	        return GnomesValidationOrder.valueOf(messageId).order;
	    }
	}
}