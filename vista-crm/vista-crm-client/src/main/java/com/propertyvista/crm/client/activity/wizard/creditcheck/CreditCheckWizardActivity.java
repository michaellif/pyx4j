/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.wizard.creditcheck;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.activity.crud.CrmWizardActivity;
import com.propertyvista.crm.client.ui.crud.viewfactories.WizardViewFactory;
import com.propertyvista.crm.client.ui.wizard.creditcheck.CreditCheckWizardView;
import com.propertyvista.crm.rpc.services.CreditCheckWizardService;
import com.propertyvista.dto.CreditCheckWizardDTO;

public class CreditCheckWizardActivity extends CrmWizardActivity<CreditCheckWizardDTO> implements CreditCheckWizardView.Presenter {

    public CreditCheckWizardActivity(AppPlace place) {
        super(place, WizardViewFactory.instance(CreditCheckWizardView.class), GWT.<CreditCheckWizardService> create(CreditCheckWizardService.class),
                CreditCheckWizardDTO.class);
    }

}
