/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.payments;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;

@Transient
public interface PaymentRecordForReportDTO extends IEntity {

    IPrimitive<String> merchantAccount();

    IPrimitive<String> building();

    IPrimitive<String> lease();

    Customer tenant();

    IPrimitive<PaymentType> method();

    IPrimitive<PaymentRecord.PaymentStatus> status();

    IPrimitive<LogicalDate> lastStatusChangeDate();

    IPrimitive<Date> created();

    IPrimitive<LogicalDate> received();

    IPrimitive<LogicalDate> finalized();

    IPrimitive<LogicalDate> target();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    Building buildingFilterAnchor();

}
