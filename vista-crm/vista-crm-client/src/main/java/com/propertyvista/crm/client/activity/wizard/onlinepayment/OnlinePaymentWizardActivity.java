/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.wizard.onlinepayment;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmWizardActivity;
import com.propertyvista.crm.client.ui.wizard.onlinepayment.OnlinePaymentWizardView;
import com.propertyvista.crm.rpc.services.vista2pmc.OnlinePaymentWizardService;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO;

public class OnlinePaymentWizardActivity extends CrmWizardActivity<OnlinePaymentSetupDTO> implements OnlinePaymentWizardView.Persenter {

    public OnlinePaymentWizardActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(OnlinePaymentWizardView.class), GWT
                .<OnlinePaymentWizardService> create(OnlinePaymentWizardService.class), OnlinePaymentSetupDTO.class);
    }

}
