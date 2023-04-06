package com.gnomes.common.util;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.gnomes.common.data.GnomesSystemBean;
import com.gnomes.common.logging.LogHelper;


/**
 * Ethernetストア
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2018/04/23 YJP/S.Hosokawa              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@ApplicationScoped
public class EthernetStore
{
	/**
	 * HashMapでEthernetのオブジェクトを秤量器IDに格納
	 */
	private HashMap<String,Ethernet> ethernetMap = new HashMap<>();

	private int workingcount = 0;

    /** アプリケーションBean */
    @Inject
    protected GnomesSystemBean gnomesSystemBean;

    @Inject
    LogHelper loghelper;

    @Inject
    Logger logger;

    /**
     * コンストラクタ（特に何もしない）
     */
    public EthernetStore(){


    }

    /**
     * 仕事状態
     */
    public void setWorking(){
    	synchronized(this){
    		this.workingcount++;
    	}
    }

    /**
     * 仕事状態リセット
     */
    public void resetWorking(){
    	synchronized(this){
    		this.workingcount--;
    	}
    }

    /**
     * 格納されている秤量器IDのEthernetオブジェクトを格納
     * 無かったらnullを返す
     * @param weighMachineId 秤量器ID
     * @return Ethernetのオブジェクト
     */
    public Ethernet getEthernet(String weighMachineId) {
    	//削除と取得が重ならないようにするため
    	synchronized(this){
	    	if(ethernetMap.containsKey(weighMachineId)){
	    		Ethernet ethernet = ethernetMap.get(weighMachineId);
	    		return ethernet;
	    	}
	    	else {
	    		return null;
	    	}
    	}
    }

    /**
     * Ethernetオブジェクトを登録（一生登録）
     * @param weighMachineId ポート番号("COM1"など）
     * @param ethernet Ethernetオブジェクト
     */
    public void AddEthernet(String weighMachineId,Ethernet ethernet){
    	//登録と削除が重ならないようにするため
    	synchronized(this){
    		ethernetMap.put(weighMachineId,ethernet);
    	}
    }

    public void watchdog() {
    	//HTML呼出中はカウントしない
		if (workingcount != 0) {
			loghelper.fine(logger, null, null, "watchdog count=" + workingcount);
			return;
		}
    	//削除と取得が重ならないようにするため
    	synchronized(this){
	    	try {
		    	for (String key : ethernetMap.keySet()) {
		    		Ethernet current = ethernetMap.get(key);
		    	    boolean isDelete = current.monitorAndEliminateLeisure(gnomesSystemBean.getWeighCloseTimeout());
		    	    if(isDelete){
		    	    	loghelper.fine(logger, null, null, "Close start");
		    	    	current.close();
		    	    	ethernetMap.remove(key);
		    	    	loghelper.fine(logger, null, null, "Close and removed");
		    	    } else {
		    	    	loghelper.fine(logger, null, null, "Monitor WeighMachine:" + current.getWeighMachineId() + " wait for " + current.getSleepingTimes());
		    	    }
		    	}
	    	}
	    	catch(Exception ex){
	    	}
    	}
    }
}
