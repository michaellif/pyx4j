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
package com.propertyvista.operations.domain.payment.pad;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@ToStringFormat("Id:{1} Account:{0} Status:{2}")
//TODO rename to FundsTransferRecord
public interface PadDebitRecord extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed
    PadBatch padBatch();

    // A unique value to represent the client/cardholder
    @Length(29)
    @ToString
    IPrimitive<String> clientId();

    IPrimitive<BigDecimal> amount();

    @Length(3)
    IPrimitive<String> bankId();

    @Length(5)
    IPrimitive<String> branchTransitNumber();

    @Length(12)
    IPrimitive<String> accountNumber();

    interface TransactionId extends ColumnId {
    }

    //A unique value to represent the transaction/payment
    @Length(15)
    @Indexed
    @ToString
    @JoinColumn(TransactionId.class)
    IPrimitive<String> transactionId();

    IPrimitive<String> acknowledgmentStatusCode();

    // Not coming from Caledon, Record processing status
    IPrimitive<Boolean> processed();

    // Not coming from Caledon, this is our processing flag
    @ToString
    IPrimitive<PadDebitRecordProcessingStatus> processingStatus();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    @OrderBy(PrimaryKey.class)
    IList<PadDebitRecordTransaction> transactionRecords();

}
