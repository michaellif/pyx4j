/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-19
 * @author Amer Sohail
 */
package com.propertyvista.crm.rpc.dto.reports;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.tenant.CustomerCreditCheck;

public class CustomerCreditCheckReportDataDTO implements Serializable {

    private static final long serialVersionUID = 3975713068991763562L;

    public LogicalDate minCreditCheckDate;

    public LogicalDate maxCreditCheckDate;

    public BigDecimal minAmountChecked;

    public BigDecimal maxAmountChecked;

    public Vector<CustomerCreditCheck> unitStatuses;

}
