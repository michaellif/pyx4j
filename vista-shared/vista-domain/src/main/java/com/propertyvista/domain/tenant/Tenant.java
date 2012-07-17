/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

@ToStringFormat("{0}, {1} - {2}{3,choice,null#|!null#, {3}}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@DiscriminatorValue("Tenant")
public interface Tenant extends LeaseParticipant {

    @NotNull
    @ToString(index = 3)
    @Caption(description = "Relation to the Main Applicant")
    IPrimitive<PersonRelationship> relationship();

    @Caption(name = "Take Ownership", description = "By checking the box TAKE OWNERSHIP you are agreeing that the MAIN APPLICANT will have access to your personal information and that you are present during the Application Process. The MAIN APPLICANT account will be the USERNAME for future communications with the Property Manager. This Box is only recommended for Family Members. Should you wish to have a separate and secure Login Access please leave the check box blank and an e-mail alert with individual username and passwords will be automatically be sent to all Tenants and Guarantors")
    IPrimitive<Boolean> takeOwnership();

    /**
     * Tenant's payment share:
     */
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> percentage();

    PaymentMethod preauthorizedPayment();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<MaintenanceRequest> _MaintenanceRequests();

}
