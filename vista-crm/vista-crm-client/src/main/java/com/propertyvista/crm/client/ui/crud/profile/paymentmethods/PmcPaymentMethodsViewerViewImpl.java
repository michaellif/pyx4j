/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.profile.paymentmethods;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;

public class PmcPaymentMethodsViewerViewImpl extends CrmViewerViewImplBase<PmcPaymentMethodsDTO> implements PmcPaymentMethodsViewerView {

    public PmcPaymentMethodsViewerViewImpl() {
        super(CrmSiteMap.Administration.Profile.PaymentMethods.class);
        setForm(new PmcPaymentMethodsForm(this));
    }

    @Override
    protected void populateBreadcrumbs(PmcPaymentMethodsDTO value) {
        // DO NOTHING
    }
}
