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
package com.propertyvista.crm.client.activity.tools.legal.l1;

import com.google.gwt.core.shared.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmWizardActivity;
import com.propertyvista.crm.client.ui.tools.legal.l1.L1FormDataReviewWizardView;
import com.propertyvista.crm.rpc.dto.legal.l1.L1FormDataReviewWizardDTO;
import com.propertyvista.crm.rpc.services.legal.L1FormDataReviewWizardService;

public class L1FormDataReviewWizardActivity extends CrmWizardActivity<L1FormDataReviewWizardDTO> implements L1FormDataReviewWizardView.Presenter {

    public L1FormDataReviewWizardActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(L1FormDataReviewWizardView.class), GWT
                .<L1FormDataReviewWizardService> create(L1FormDataReviewWizardService.class), L1FormDataReviewWizardDTO.class);
    }

}
