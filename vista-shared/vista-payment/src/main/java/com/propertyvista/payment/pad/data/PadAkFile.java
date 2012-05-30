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
package com.propertyvista.payment.pad.data;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * e-Cheque or PAD (pre-authorized debit transactions) / EFT Batch payments
 * 
 * @see http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Caledon
 * @see http://jira.birchwoodsoftwaregroup.com/wiki/download/attachments/4587553/CCS-PAD-File-Specifications.pdf
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PadAkFile extends IEntity {

    IPrimitive<Integer> version();

    @Length(15)
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

    IList<PadAkBatch> batches();

    IList<PadAkDebitRecord> records();
}
