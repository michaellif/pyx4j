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
package com.propertyvista.operations.domain.eft.caledoneft.to;

import java.util.Date;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.financial.FundsTransferType;

/**
 * e-Cheque or PAD (pre-authorized debit transactions) / EFT Batch payments
 * 
 * @see <a href="http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Caledon"/>
 * @see <a href="http://jira.birchwoodsoftwaregroup.com/wiki/download/attachments/4587553/FUNDS_TRANSFER_FILE_SPECIFICATIONS_V1.8_20130716.pdf"/>
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface FundsTransferAckFile extends IEntity {

    // YYYYMMDDhhmmss.COMPANYID_pad_acknowledgement.csv
    public static String FileNameSufix = "_acknowledgement.csv";

    IPrimitive<String> fileName();

    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> remoteFileDate();

    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> fileNameDate();

    IPrimitive<FundsTransferType> fundsTransferType();

    IPrimitive<Integer> version();

    @Length(20)
    @NotNull
    IPrimitive<String> companyId();

    @Length(6)
    @NotNull
    IPrimitive<String> fileCreationNumber();

    @Length(8)
    @NotNull
    IPrimitive<String> fileCreationDate();

    @Length(12)
    @NotNull
    IPrimitive<String> batcheCount();

    @Length(12)
    @NotNull
    IPrimitive<String> recordsCount();

    @Length(14)
    @NotNull
    IPrimitive<String> fileAmount();

    @Length(4)
    @NotNull
    IPrimitive<String> acknowledgmentStatusCode();

    IPrimitive<String> acknowledgmentRejectReasonMessage();

    IList<FundsTransferAckBatch> batches();

    IList<FundsTransferAckRecord> records();
}
