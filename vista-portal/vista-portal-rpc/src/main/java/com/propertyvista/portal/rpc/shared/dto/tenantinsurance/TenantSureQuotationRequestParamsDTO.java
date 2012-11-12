/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitiveSet;

@Transient
public interface TenantSureQuotationRequestParamsDTO extends IEntity {

    IPrimitiveSet<BigDecimal> generalLiabilityCoverageOptions();

    IPrimitiveSet<BigDecimal> contentsCoverageOptions();

    IPrimitiveSet<BigDecimal> deductibleOptions();

}
