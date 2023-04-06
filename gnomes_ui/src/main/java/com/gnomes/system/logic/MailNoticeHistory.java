package com.gnomes.system.logic;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.UUID;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import com.gnomes.common.util.ConverterUtils;
import com.gnomes.common.util.CurrentTimeStamp;
import com.gnomes.system.data.MailNoticeHistoryInfo;
import com.gnomes.system.entity.HistoryMailNotice;
import com.gnomes.uiservice.ContainerRequest;

/**
 * メール通知履歴
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/12/13 YJP/H.Yamada              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
@Dependent
public class MailNoticeHistory {

    /** リクエストコンテナ */
    @Inject
    protected ContainerRequest containerRequest;


    /**
     * メール通知履歴登録.
     * <pre>
     * メール通知履歴の登録を行う。
     * </pre>
     * @param historyInfo メール通知履歴情報
     * @param em エンティティマネージャ
     */
    public void registration(MailNoticeHistoryInfo historyInfo, EntityManager em) {

        // メール通知履歴編集
        HistoryMailNotice historyMailNotice = this.setHistoryMailNotice(historyInfo);
        // 登録
        em.persist(historyMailNotice);
        em.flush();

    }

    /**
     * メール通知履歴登録情報設定.
     * <pre>
     * メール通知履歴登録情報の設定を行う。
     * </pre>
     * @param historyInfo メール通知履歴情報
     * @return メール通知履歴 エンティティ
     */
    private HistoryMailNotice setHistoryMailNotice(MailNoticeHistoryInfo historyInfo) {

        HistoryMailNotice historyMailNotice = new HistoryMailNotice();
        historyMailNotice.setReq(this.containerRequest);

        Timestamp systemDate = ConverterUtils.utcToTimestamp(
                ConverterUtils.dateToLocalDateTime(
                        CurrentTimeStamp.getSystemCurrentTimeStamp()).atZone(ZoneId.systemDefault()));

        // メール通知履歴キー
        historyMailNotice.setHistory_mail_notice_key(UUID.randomUUID().toString());
        // メッセージキー
        historyMailNotice.setMessage_key(historyInfo.getMessageKey());
        // 発生日時
        historyMailNotice.setOccur_date(historyInfo.getOccurdate());
        // メッセージNo
        historyMailNotice.setMessage_no(historyInfo.getMessageno());
        // 通知日時
        historyMailNotice.setNotice_date(systemDate);
        // メール通知状況
        historyMailNotice.setMail_notice_status(historyInfo.getMailNoticeStatus().getValue());
        // 失敗理由
        historyMailNotice.setFailure_reason(historyInfo.getFailureReason());

        return historyMailNotice;

    }

}
