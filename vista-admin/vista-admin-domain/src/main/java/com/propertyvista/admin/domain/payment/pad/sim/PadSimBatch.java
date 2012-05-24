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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PadSimBatch extends IEntity {

    @Override
    @Indexed
    @OrderColumn
    IPrimitive<Key> id();

    @Owner
    @JoinColumn
    PadSimFile padFile();

    IPrimitive<Integer> batchNumber();

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

    @Caption(description = "'1001' - Invalid Count\n'1002' - Batch out of balance\n'1003' - Invalid Terminal ID \n'1004' - Invalid Bank ID \n'1005' - Invalid Transit Number \n'1006' - Invalid Bank Account Number \n'1007' - Bank Information Mismatch")
    IPrimitive<String> acknowledgmentStatusCode();

}
