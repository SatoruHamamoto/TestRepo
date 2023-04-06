package com.gnomes.common.data;

import java.util.List;
import java.util.Map;

public class ProcessTableInfo {

	/***/
	private Map<String, Integer> processTableInfo;

	/***/
	private List<ProcessTableBean> processTableBeanList;

	public Map<String, Integer> getProcessTableInfo() {
		return processTableInfo;
	}

	public void setProcessTableInfo(Map<String, Integer> processTableInfo) {
		this.processTableInfo = processTableInfo;
	}

	public List<ProcessTableBean> getProcessTableBeanList() {
		return processTableBeanList;
	}

	public void setProcessTableBeanList(List<ProcessTableBean> processTableBeanList) {
		this.processTableBeanList = processTableBeanList;
	}

}
