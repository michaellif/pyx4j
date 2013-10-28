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
package com.propertyvista.crm.client.activity.crud.administration.website.content;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.administration.website.content.SiteEditor;
import com.propertyvista.crm.rpc.services.admin.SiteContentCrudService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteEditorActivity extends CrmEditorActivity<SiteDescriptorDTO> implements SiteEditor.Presenter {

    public SiteEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(SiteEditor.class), GWT.<SiteContentCrudService> create(SiteContentCrudService.class),
                SiteDescriptorDTO.class);
    }

    @Override
    protected void onSaved(Key result) {
        ReferenceDataManager.invalidate(AvailableLocale.class);
    }
}
