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
package com.propertyvista.operations.domain.payment.pad.simulator;

import java.util.Date;

import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.domain.payment.pad.TransactionReconciliationStatus;

@RequireFeature(ApplicationDevelopmentFeature.class)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PadSimDebitRecord extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed
    PadSimBatch padBatch();

    @OrderColumn
    IPrimitive<Integer> odr();

    // A unique value to represent the client/cardholder
    @Length(29)
    IPrimitive<String> clientId();

    IPrimitive<String> amount();

    @Length(3)
    IPrimitive<String> bankId();

    @Length(5)
    IPrimitive<String> branchTransitNumber();

    @Length(12)
    IPrimitive<String> accountNumber();

    //A unique value to represent the transaction/payment
    @Length(15)
    IPrimitive<String> transactionId();

    @Caption(name = "Acknowledgment Code", description = "'2001' - Invalid Amount\n '2002' - Invalid Bank ID\n '2003' - Invalid Bank Transit Number\n '2004' - Invalid Bank Account Number\n '2005' - Invalid Reference Number")
    IPrimitive<String> acknowledgmentStatusCode();

    // --- reconciliation

    @Caption(description = "YYYYMMDD")
    IPrimitive<String> paymentDate();

    IPrimitive<TransactionReconciliationStatus> reconciliationStatus();

    @Caption(description = "--reject codes--\n'900' - EDIT REJECT\n;--return codes--\n'901' - NSF (DEBIT ONLY);\n'902' - ACCOUNT NOT FOUND;\n'912' INVALID/INCORRECT ACCOUNT NO")
    IPrimitive<String> reasonCode();

    IPrimitive<String> reasonText();

    @Caption(description = "Reject/Return Item Fee; This field will contain the fee for the reject or returned item.")
    IPrimitive<String> fee();

    @Timestamp(Timestamp.Update.Updated)
    @Format("yyyy-MM-dd HH:mm")
    @Editor(type = Editor.EditorType.label)
    IPrimitive<Date> updated();
}
