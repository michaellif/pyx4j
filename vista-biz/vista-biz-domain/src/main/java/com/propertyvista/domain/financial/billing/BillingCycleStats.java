/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Table(prefix = "billing")
public interface BillingCycleStats extends IEntity {

    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @Detached
    @JoinColumn
    @Indexed(uniqueConstraint = true)
    @XmlTransient
    BillingCycle billingCycle();

    // Statistics:
    //TODO 1 add stats

    @Caption(name = "Failed Bills")
    IPrimitive<Long> failed();

    @Caption(name = "Rejected Bills")
    IPrimitive<Long> rejected();

    @Caption(name = "Non Confirmed Bills")
    IPrimitive<Long> notConfirmed();

    @Caption(name = "Confirmed Bills")
    IPrimitive<Long> confirmed();

}
