package com.gnomes.common.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.gnomes.common.constants.SystemDefConstants;
import com.gnomes.common.dto.PullDownDto;
import com.gnomes.common.exception.GnomesAppException;
import com.gnomes.system.dao.MstrSystemDefineDao;
import com.gnomes.system.entity.MstrSystemDefine;

/**
 * ページングの表示件数リスト取得ロジック クラス
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2019/07/23 YJP/tada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class PagingPulldownGetLogic extends BaseLogic {

	@Inject
    MstrSystemDefineDao mstrSystemDefineDao;

	/**
	 * デフォルトのプルダウン項目リストを生成して返却する。
	 * @return プルダウン項目リスト
	 * @throws GnomesAppException
	 */
	public List<PullDownDto> getDefaultPulldownList()throws GnomesAppException {
		//システム定義を取得
		//TODO: CTAG_TABLE_DEFAULT、DISPLAY_COUNT_PULLDOWN_ITEMをSystemDefConstantsクラスに追加。
		 MstrSystemDefine mstrSystemDefine = this.mstrSystemDefineDao.getMstrSystemDefine(
				 SystemDefConstants.TYPE_CTAG_TABLE_DEFAULT, SystemDefConstants.CODE_DISPLAY_COUNT_PULLDOWN_ITEM);

		 //表示件数情報がない場合は空のリストを返却
		 if(mstrSystemDefine == null || StringUtils.isBlank(mstrSystemDefine.getChar1())){
			 return new ArrayList<PullDownDto>();
		 }

	     return Arrays.asList(mstrSystemDefine.getChar1().split(","))
	    		 .stream()
	    		 .map(e -> e.toString().trim())
	    		 .map(e -> new PullDownDto(e, e))
	    		 .collect(Collectors.toList());
	}
}
