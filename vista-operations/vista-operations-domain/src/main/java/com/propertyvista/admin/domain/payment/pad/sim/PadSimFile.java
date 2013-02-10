/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.payment.pad.sim;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

/**
 * e-Cheque or PAD (pre-authorized debit transactions) / EFT Batch payments
 * 
 * @see <a href="http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Caledon"/>
 * @see <a href="http://jira.birchwoodsoftwaregroup.com/wiki/download/attachments/4587553/CCS-PAD-File-Specifications.pdf"/>
 */
@Table(namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PadSimFile extends IEntity {

    public enum PadSimFileStatus {

        Loaded,

        Acknowledged,

        ReconciliationSent;

    };

    @ToString
    IPrimitive<String> fileName();

    @ToString
    IPrimitive<PadSimFileStatus> status();

    IPrimitive<String> companyId();

    IPrimitive<String> fileCreationNumber();

    IPrimitive<String> fileCreationDate();

    IPrimitive<String> fileType();

    IPrimitive<String> fileVersion();

    IPrimitive<Integer> batchRecordsCount();

    IPrimitive<Integer> recordsCount();

    IPrimitive<String> fileAmount();

    @Caption(name = "Acknowledgment Code", description = "If Empty Calculated base on batches.\n  '0000' - Accepted\n '0001' - File out of balance\n '0002' - Batch level reject\n '0003' - Transaction reject '0004' - Batch and transaction reject\n '0005' Detail Record Count out of balance\n '0006' - Invalid File Format\n '0007' - Invalid File Header")
    IPrimitive<String> acknowledgmentStatusCode();

    IPrimitive<String> acknowledgmentRejectReasonMessage();

    @Timestamp(Timestamp.Update.Created)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> received();

    @Timestamp(Timestamp.Update.Updated)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> updated();

    IPrimitive<Date> acknowledged();

    IPrimitive<Date> reconciliationSent();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    IList<PadSimBatch> batches();

}
