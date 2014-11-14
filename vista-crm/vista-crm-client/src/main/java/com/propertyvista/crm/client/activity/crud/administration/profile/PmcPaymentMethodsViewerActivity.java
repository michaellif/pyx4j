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
package com.propertyvista.crm.client.activity.crud.administration.profile;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.administration.profile.paymentmethods.PmcPaymentMethodsViewerView;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.crm.rpc.services.admin.PmcPaymentMethodsCrudService;

public class PmcPaymentMethodsViewerActivity extends CrmViewerActivity<PmcPaymentMethodsDTO> implements PmcPaymentMethodsViewerView.IPrimeViewerPresenter {

    public PmcPaymentMethodsViewerActivity(CrudAppPlace place) {
        super(PmcPaymentMethodsDTO.class, place, CrmSite.getViewFactory().getView(PmcPaymentMethodsViewerView.class), GWT
                .<PmcPaymentMethodsCrudService> create(PmcPaymentMethodsCrudService.class));
    }

}
