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

    enum Status {
        noInsurance, hasTenantSure, hasOtherInsurance
    }

    IPrimitive<Status> status();

    IPrimitive<LogicalDate> coverageExpiryDate();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> minimumRequiredLiability();

    IList<InsuranceCertificateSummaryDTO> certificates();

}
