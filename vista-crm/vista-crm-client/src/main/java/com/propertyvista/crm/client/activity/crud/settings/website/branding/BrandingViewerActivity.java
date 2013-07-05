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
package com.propertyvista.crm.client.activity.crud.settings.website.branding;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.settings.website.branding.BrandingViewer;
import com.propertyvista.crm.client.ui.crud.viewfactories.WebsiteViewFactory;
import com.propertyvista.crm.rpc.services.admin.SiteBrandingCrudService;
import com.propertyvista.dto.SiteDescriptorDTO;

public class BrandingViewerActivity extends CrmViewerActivity<SiteDescriptorDTO> implements BrandingViewer.Presenter {

    public BrandingViewerActivity(CrudAppPlace place) {
        super(place, WebsiteViewFactory.instance(BrandingViewer.class), GWT.<SiteBrandingCrudService> create(SiteBrandingCrudService.class));
    }
}
