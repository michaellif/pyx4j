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
package com.propertyvista.crm.client.ui.crud.administration.website.branding;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.SiteDescriptorDTO;

public class PortalForm extends CrmEntityForm<SiteDescriptorDTO> {

    public PortalForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        TwoColumnFlexFormPanel content;

        PortalBannerImageFolder imageFolder = new PortalBannerImageFolder(isEditable());
        imageFolder.setImageSize(600, 100);
        content = new TwoColumnFlexFormPanel(proto().portalBanner().getMeta().getCaption());
        content.setWidget(0, 0, 2, inject(proto().portalBanner(), imageFolder));
        selectTab(addTab(content));
    }
}