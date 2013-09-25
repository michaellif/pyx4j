/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.lease.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.billing.InvoiceChargeTax;
import com.propertyvista.domain.financial.billing.InvoiceDebit;

@Transient
@ExtendsBO(InvoiceDebit.class)
public interface InvoiceDebitDTO extends IEntity {

    IPrimitive<LogicalDate> date();

    /** item description */
    IPrimitive<String> item();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalAmount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> outstandingDebit();

    IPrimitive<LogicalDate> dueDate();

    @Owned
    IList<InvoiceChargeTax> taxes();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> taxTotal();

    IList<DebitLinkDTO> debitCreditLinks();

}
