/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface AggregatedTransferNonVistaTransaction extends IEntity {

    @Owner
    @ReadOnly(allowOverrideNull = true)
    @Detached
    @Indexed
    AggregatedTransfer aggregatedTransfer();

    IPrimitive<Key> cardsClearanceRecordKey();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @Editor(type = EditorType.label)
    @Format("MM/dd/yyyy HH:mm:ss")
    IPrimitive<Date> transactionDate();

    @Editor(type = EditorType.label)
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> reconciliationDate();

    IPrimitive<String> details();
}
