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
package com.propertyvista.admin.domain.payment.pad;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.GwtBlacklist;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

/**
 * e-Cheque or PAD (pre-authorized debit transactions) / EFT Batch payments
 * 
 * @see http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Caledon
 * @see http://jira.birchwoodsoftwaregroup.com/wiki/download/attachments/4587553/CCS-PAD-File-Specifications.pdf
 */
@Table(namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@GwtBlacklist
public interface PadFile extends IEntity {

    public enum PadFileStatus {

        Creating,

        Sending,

        SendindError,

        Sent,

        Invalid,

        Acknowledged,

        Received,

        Procesed;

    };

    public enum FileAcknowledgmentStatus {

        Accepted("0000"),

        FileOutOfBalance("0001"),

        BatchLevelReject("0002"),

        TransactionReject("0003"),

        BatchAndTransactionReject("0004"),

        DetailRecordCountOutOfBalance("0005"),

        InvalidFileFormat("0006"),

        InvalidFileHeader("0007");

        private final String statusCode;

        FileAcknowledgmentStatus(String code) {
            statusCode = code;
        }

        public String getStatusCode() {
            return statusCode;
        }
    }

    IPrimitive<String> fileCreationNumber();

    IPrimitive<String> fileName();

    IPrimitive<PadFileStatus> status();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    IList<PadBatch> batches();

    IPrimitive<Date> sent();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    IPrimitive<Date> acknowledged();

    // Updated when batch is sent to Caledon

    IPrimitive<Integer> recordsCount();

    IPrimitive<BigDecimal> fileAmount();

    IPrimitive<String> acknowledgmentStatusCode();

    IPrimitive<String> acknowledgmentRejectReasonMessage();

    IPrimitive<FileAcknowledgmentStatus> acknowledgmentStatus();

}
