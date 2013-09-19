/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.dto.insurance.status;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18n;

@Transient
@AbstractEntity
public interface InsuranceStatusDTO extends IEntity {

    static final I18n i18n = I18n.get(InsuranceStatusDTO.class);

    public static final String noInsuranceStatusMessage = i18n.tr("According to our records you do not have valid tenant insurance!");

    public static final String hasInsuranceStatusMessage = i18n.tr("According to our records you have valid tenant insurance which expire on {0}.");

    public static final String noInsuranceTenantSureInvitation = i18n
            .tr("As per your Lease Agreement, you must obtain and provide the landlord with proof of tenant insurance. We have teamed up with Highcourt Partners Limited, a licensed broker, to assist you in obtaining your tenant insurance.");

    public static final String otherInsuranceTenantSureInvitation = i18n
            .tr("If you wish you can get next insurance policy here. We have teamed up with Highcourt Partners Limited, a licensed broker, to assist you in obtaining your tenant insurance.");

    enum Status {
        noInsurance, hasTenantSure, hasOtherInsurance
    }

    IPrimitive<Status> status();

    IPrimitive<LogicalDate> coverageExpiryDate();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> minimumRequiredLiability();

    IList<InsuranceCertificateSummaryDTO> certificates();

}
