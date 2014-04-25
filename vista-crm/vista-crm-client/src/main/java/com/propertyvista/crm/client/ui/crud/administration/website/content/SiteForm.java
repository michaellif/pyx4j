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
package com.propertyvista.crm.client.ui.crud.administration.website.content;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.administration.website.RichTextContentFolder;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(SiteForm.class);

    public SiteForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        TwoColumnFlexFormPanel content;

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().siteTitles(), new SiteTitlesFolder(isEditable())));
        selectTab(addTab(content, proto().siteTitles().getMeta().getCaption()));

        content = new TwoColumnFlexFormPanel();
        content.setH4(0, 0, 2, i18n.tr("Recommended Logo size not to exceed: small - {0}, large - {1} pixels", "160x80", "300x90"));
        content.setWidget(1, 0, 2, inject(proto().logo(), new SiteImageResourceFolder(isEditable())));
        addTab(content, i18n.tr("Site Logos"));

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().slogan(), new RichTextContentFolder(isEditable())));
        addTab(content, proto().slogan().getMeta().getCaption());

        SiteImageSetFolder imageFolder = new SiteImageSetFolder(isEditable());
        imageFolder.setImageSize(690, 300);
        imageFolder.setThumbSize(230, 100);
        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().banner(), imageFolder));
        addTab(content, proto().banner().getMeta().getCaption());

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().metaTags(), new MetaTagsFolder(isEditable())));
        addTab(content, proto().metaTags().getMeta().getCaption());

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().childPages(), new PageDescriptorFolder(this)));
        selectTab(addTab(content, proto().childPages().getMeta().getCaption()));

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, createGadgetPanel());
        addTab(content, i18n.tr("Home Page Gadgets"));

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().cityIntroPages(), new CityIntroPageFolder(this)));
        addTab(content, proto().cityIntroPages().getMeta().getCaption());
    }

    private Widget createGadgetPanel() {
        TwoColumnFlexFormPanel gadgetPanel = new TwoColumnFlexFormPanel();
        int row = 0;

        gadgetPanel.setH4(row++, 0, 2, i18n.tr("Narrow Page Gadgets:"));
        gadgetPanel.setWidget(row++, 0, 2, inject(proto().homePageGadgetsNarrow(), new HomePageGadgetFolder(isEditable())));

        gadgetPanel.setBR(row++, 0, 2);

        gadgetPanel.setH4(row++, 0, 2, i18n.tr("Wide Page Gadgets:"));
        gadgetPanel.setWidget(row++, 0, 2, inject(proto().homePageGadgetsWide(), new HomePageGadgetFolder(isEditable())));

        return gadgetPanel;
    }
}