/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.tenant;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;

public class CustomerCreditCheckLongReportDTOMockup {

    public static CustomerCreditCheckLongReportDTO createLongReport() {
        CustomerCreditCheckLongReportDTO report = EntityFactory.create(CustomerCreditCheckLongReportDTO.class);

        report.percentOfRentCovered().setValue(new BigDecimal(1.23456));

        return report;
    }
}
