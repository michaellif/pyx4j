/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-1-21
 * @author VladL
 */
package com.propertyvista.crm.client.ui.crud.customer.creditcheck;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;

public class CustomerCreditCheckLongReportViewerViewImpl extends CrmViewerViewImplBase<CustomerCreditCheckLongReportDTO> implements
        CustomerCreditCheckLongReportViewerView {

    public CustomerCreditCheckLongReportViewerViewImpl() {
        super(true);
        setForm(new CustomerCreditCheckLongReportForm(this));
    }

    @Override
    protected void populateBreadcrumbs(CustomerCreditCheckLongReportDTO value) {
        // DO NOTHING
    }
}
