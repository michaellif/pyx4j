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
package com.propertyvista.operations.domain.eft.caledoneft.simulator;

import java.util.Date;

import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.domain.eft.caledoneft.MerchantReconciliationStatus;

@RequireFeature(ApplicationDevelopmentFeature.class)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PadSimBatch extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed
    PadSimFile padFile();

    @Length(3)
    IPrimitive<String> batchNumber();

    @Owned
    IList<PadSimDebitRecord> records();

    /**
     * TBD Copy of merchantAccount at the time of Batch creation
     */
    @Length(8)
    IPrimitive<String> terminalId();

    @Length(3)
    @ToString
    IPrimitive<String> bankId();

    @Length(5)
    @ToString
    IPrimitive<String> branchTransitNumber();

    @Length(12)
    @ToString
    IPrimitive<String> accountNumber();

    // filed editable by CRM

    /**
     * Caledon: Description to appear on client's statement. Typically a merchant's business name.
     */
    @Length(60)
    IPrimitive<String> chargeDescription();

    IPrimitive<Integer> recordsCount();

    IPrimitive<String> batchAmount();

    @Caption(name = "Acknowledgment Code", description = "'1001' - Invalid Count\n'1002' - Batch out of balance\n'1003' - Invalid Terminal ID \n'1004' - Invalid Bank ID \n'1005' - Invalid Transit Number \n'1006' - Invalid Bank Account Number \n'1007' - Bank Information Mismatch")
    IPrimitive<String> acknowledgmentStatusCode();

    // --- reconciliation --------------

    IPrimitive<MerchantReconciliationStatus> reconciliationStatus();

    IPrimitive<String> grossPaymentAmount();

    IPrimitive<String> grossPaymentFee();

    IPrimitive<String> grossPaymentCount();

    IPrimitive<String> rejectItemsAmount();

    IPrimitive<String> rejectItemsFee();

    IPrimitive<String> rejectItemsCount();

    IPrimitive<String> returnItemsAmount();

    IPrimitive<String> returnItemsFee();

    IPrimitive<String> returnItemsCount();

    IPrimitive<String> netAmount();

    IPrimitive<String> adjustments();

    IPrimitive<String> previousBalance();

    IPrimitive<String> merchantBalance();

    IPrimitive<String> fundsReleased();

    @Timestamp(Timestamp.Update.Updated)
    @Format("yyyy-MM-dd HH:mm")
    @Editor(type = Editor.EditorType.label)
    IPrimitive<Date> updated();

}
