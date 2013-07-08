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
package com.propertyvista.crm.client.ui.crud.settings.website.branding;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.settings.website.RichTextContentFolder;
import com.propertyvista.dto.SiteDescriptorDTO;

public class BrandingForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(BrandingForm.class);

    public BrandingForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        FormFlexPanel content;

        content = new FormFlexPanel(proto().siteTitles().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().siteTitles(), new SiteTitlesFolder(isEditable())));
        selectTab(addTab(content));

        content = new FormFlexPanel(i18n.tr("Site Logos"));
        content.setWidget(0, 0, inject(proto().logo(), new PortalImageResourceFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(proto().slogan().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().slogan(), new RichTextContentFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(proto().banner().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().banner(), new PortalImageSetFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(proto().metaTags().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().metaTags(), new MetaTagsFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(proto().socialLinks().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().socialLinks(), new SocialLinkFolder(isEditable())));
        addTab(content);
    }
}