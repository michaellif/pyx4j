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
package com.propertyvista.crm.client.activity.profile;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.profile.paymentmethods.PmcPaymentMethodsEditorView;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.crm.rpc.services.admin.PmcPaymentMethodsCrudService;

public class PmcPaymentMethodsEditorActivity extends CrmEditorActivity<PmcPaymentMethodsDTO> implements PmcPaymentMethodsEditorView.Presenter {

    public PmcPaymentMethodsEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(PmcPaymentMethodsEditorView.class), GWT
                .<PmcPaymentMethodsCrudService> create(PmcPaymentMethodsCrudService.class), PmcPaymentMethodsDTO.class);
    }

}
