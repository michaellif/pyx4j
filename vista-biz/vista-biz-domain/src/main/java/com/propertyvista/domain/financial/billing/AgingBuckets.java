/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Inheritance.InheritanceStrategy;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.ARCode;

@AbstractEntity
@Inheritance(strategy = InheritanceStrategy.SINGLE_TABLE)
public interface AgingBuckets<SNAPSHOT extends ArrearsSnapshot<?>> extends IEntity {

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    @Indexed
    SNAPSHOT arrearsSnapshot();

    IPrimitive<ARCode.Type> arCode();

    /** sum of money that is owed but which time is not yet overdue */
    @Caption(name = "Current")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> bucketCurrent();

    /** sum of payments that is due from 1 day to the day when the month and were not payed */
    @Caption(name = "This Month")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> bucketThisMonth();

    /** sum of payments that haven't been received from client 1 to 30 days ago */
    @Caption(name = "1 to 30")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> bucket30();

    /** sum of payments that haven't been received from client 31 to 60 days ago */
    @Caption(name = "31 to 60")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> bucket60();

    /** sum of payments that haven't been received from client 61 to 90 days ago */
    @Caption(name = "61 to 90")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> bucket90();

    /** sum of payments that haven't been received from client 91 and more days ago */
    @Caption(name = "91 and more")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> bucketOver90();

    /**
     * sum of all the money that is owed by client (arrears + not arrears)
     */
    @Caption(name = "AR Balance")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    // TODO rename column arrearsAmount to 'AR Balance' (this is AR Balance not just arrears)
    IPrimitive<BigDecimal> arrearsAmount();

    @Caption(name = "Prepayments")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    // TODO rename column to prepayments
    IPrimitive<BigDecimal> creditAmount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    /**
     *  {@link #arrearsAmount} - {@link #creditAmount}
     */
    IPrimitive<BigDecimal> totalBalance();

}
