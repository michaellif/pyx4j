/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 5, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.administration.website.general;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.administration.website.general.GeneralViewer;
import com.propertyvista.crm.rpc.services.admin.SiteGeneralCrudService;
import com.propertyvista.dto.SiteDescriptorDTO;

public class GeneralViewerActivity extends CrmViewerActivity<SiteDescriptorDTO> implements GeneralViewer.Presenter {

    public GeneralViewerActivity(CrudAppPlace place) {
        super(SiteDescriptorDTO.class, place, CrmSite.getViewFactory().getView(GeneralViewer.class), GWT
                .<SiteGeneralCrudService> create(SiteGeneralCrudService.class));
    }
}
