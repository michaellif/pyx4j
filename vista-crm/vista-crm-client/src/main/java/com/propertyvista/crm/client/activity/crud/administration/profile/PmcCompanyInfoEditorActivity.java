/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.administration.profile;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.administration.profile.companyinfo.PmcCompanyInfoEditorView;
import com.propertyvista.crm.rpc.dto.admin.PmcCompanyInfoDTO;
import com.propertyvista.crm.rpc.services.admin.PmcCompanyInfoCrudService;

public class PmcCompanyInfoEditorActivity extends CrmEditorActivity<PmcCompanyInfoDTO> implements PmcCompanyInfoEditorView.Presenter {

    public PmcCompanyInfoEditorActivity(CrudAppPlace place) {
        super(PmcCompanyInfoDTO.class, place, CrmSite.getViewFactory().getView(PmcCompanyInfoEditorView.class),
                GWT.<PmcCompanyInfoCrudService> create(PmcCompanyInfoCrudService.class));
    }
}
