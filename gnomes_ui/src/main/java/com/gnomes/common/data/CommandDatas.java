package com.gnomes.common.data;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class CommandDatas {

    /**
     * コマンドデータ
     */
    @XmlElementWrapper
    @XmlElement(name = "commandData")
    List<CommandData> commandDataList;


    /**
     * 画面データ
     */
    @XmlElementWrapper
    @XmlElement(name = "screenData")
    List<CommandScreenData> screenDataList;


	/**
	 * @return commandDataList
	 */
	public List<CommandData> getCommandDataList() {
		return commandDataList;
	}


	/**
	 * @param commandDataList セットする commandDataList
	 */
	public void setCommandDataList(List<CommandData> commandDataList) {
		this.commandDataList = commandDataList;
	}


	/**
	 * @return screenDataList
	 */
	public List<CommandScreenData> getScreenDataList() {
		return screenDataList;
	}


	/**
	 * @param screenDataList セットする screenDataList
	 */
	public void setScreenDataList(List<CommandScreenData> screenDataList) {
		this.screenDataList = screenDataList;
	}




}
