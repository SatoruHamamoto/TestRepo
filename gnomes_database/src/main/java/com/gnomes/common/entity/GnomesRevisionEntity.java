package com.gnomes.common.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@RevisionEntity
@Table(name = "revinfo")
public class GnomesRevisionEntity implements Serializable {

    /** 履歴番号 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long rev;

    /** 履歴タイムスタンプ */
    @RevisionTimestamp
    private Long revtstmp;

}
