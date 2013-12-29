/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.communication.mail.template.model;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface EmailTemplateContext extends IEntity {

    IPrimitive<String> accessToken();

    AbstractUser user();

    Lease lease();

    LeaseParticipant<?> leaseParticipant();

    LeaseTermParticipant<?> leaseTermParticipant();

    MaintenanceRequest maintenanceRequest();

    AutopayAgreement preauthorizedPayment();

    PaymentRecord paymentRecord();
}
