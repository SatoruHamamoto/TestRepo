package com.gnomes.uiservice;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

import com.gnomes.common.data.MessageData;
import com.gnomes.common.data.MessageInfo;
import com.gnomes.common.exception.GnomesAppException;

/**
 * 非同期処理
 */
@Stateless
public class AsynchronousProcess {

    /**
     * メッセージ情報登録(非同期)
     * @param messageInfo メッセージ情報
     * @param request ContainerRequest
     * @throws GnomesAppException
     */
    @Asynchronous
    public void insertMessage(MessageInfo messageInfo, ContainerRequest request) throws GnomesAppException {

        request.insertMessage(messageInfo);

    }

    @Asynchronous
    public void insertMessage(MessageInfo messageInfo, MessageData[] childDatas, ContainerRequest request) throws GnomesAppException {

        request.insertMessage(messageInfo, childDatas);

    }

}
