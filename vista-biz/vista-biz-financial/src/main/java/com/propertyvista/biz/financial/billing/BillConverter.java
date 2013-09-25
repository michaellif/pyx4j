/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.dto.BillDTO;

public class BillConverter extends EntityBinder<Bill, BillDTO> {

    public BillConverter() {
        super(Bill.class, BillDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }
}