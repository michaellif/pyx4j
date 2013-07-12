/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@DiscriminatorValue("Tenant")
public interface Tenant extends LeaseParticipant<LeaseTermTenant> {

    //TODO remove
    @Deprecated
    @Transient
    LeasePaymentMethod preauthorizedPayment();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    @OrderBy(PrimaryKey.class)
    @Caption(name = "Pre-Authorized Payments")
    IList<PreauthorizedPayment> preauthorizedPayments();

    // ----------------------------------------------------
    // parent <-> child relationship:

    //TODO move
    @Override
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = LeaseTermTenant.class, mappedBy = LeaseTermParticipant.LeaseParticipantHolderId.class)
    ISet<LeaseTermTenant> leaseTermParticipants();
}
