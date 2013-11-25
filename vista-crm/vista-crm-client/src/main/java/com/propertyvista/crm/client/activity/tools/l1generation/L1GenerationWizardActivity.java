/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.l1generation;

import com.google.gwt.core.shared.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmWizardActivity;
import com.propertyvista.crm.client.ui.tools.l1generation.L1GenerationWizardView;
import com.propertyvista.crm.rpc.dto.legal.l1.L1GenerationWizardDTO;
import com.propertyvista.crm.rpc.services.legal.L1GenerationWizardService;

public class L1GenerationWizardActivity extends CrmWizardActivity<L1GenerationWizardDTO> implements L1GenerationWizardView.Presenter {

    public L1GenerationWizardActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(L1GenerationWizardView.class), GWT.<L1GenerationWizardService> create(L1GenerationWizardService.class),
                L1GenerationWizardDTO.class);
    }

}
