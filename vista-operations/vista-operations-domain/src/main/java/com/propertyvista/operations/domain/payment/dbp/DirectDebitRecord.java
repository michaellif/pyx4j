/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.payment.dbp;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface DirectDebitRecord extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed
    DirectDebitFile file();

    @NotNull
    @Length(14)
    @Indexed
    IPrimitive<String> accountNumber();

    @ReadOnly(allowOverrideNull = true)
    @Detached
    @Indexed(group = { "m,1" })
    Pmc pmc();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    //Trace number unique to the payer's transaction provided by the incoming source location code telebanking operator
    @Length(30)
    IPrimitive<String> paymentReferenceNumber();

    //Name of the payer if provided by the source location code telebanking operator
    @Length(35)
    IPrimitive<String> customerName();

    @Timestamp(Timestamp.Update.Created)
    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> receivedDate();

    @NotNull
    IPrimitive<DirectDebitRecordProcessingStatus> processingStatus();

    DirectDebitRecordTrace trace();

}
