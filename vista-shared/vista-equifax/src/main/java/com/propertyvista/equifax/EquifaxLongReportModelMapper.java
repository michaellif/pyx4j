/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-31
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax;

import ca.equifax.uat.from.EfxTransmit;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.dto.CustomerCreditCheckLongReportDTO;
import com.propertyvista.domain.tenant.CustomerCreditCheck;

public class EquifaxLongReportModelMapper {

    public static CustomerCreditCheckLongReportDTO createLongReport(EfxTransmit efxResponse, CustomerCreditCheck ccc) {
        CustomerCreditCheckLongReportDTO dto = EntityFactory.create(CustomerCreditCheckLongReportDTO.class);
        // TODO Add all business mapping here
        return dto;
    }
}
