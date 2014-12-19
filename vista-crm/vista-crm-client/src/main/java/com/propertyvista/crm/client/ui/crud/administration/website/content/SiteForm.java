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
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.administration.website.RichTextContentFolder;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(SiteForm.class);

    public SiteForm(IPrimeFormView<SiteDescriptorDTO, ?> view) {
        super(SiteDescriptorDTO.class, view);

        FormPanel formPanel;

        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().siteTitles(), new SiteTitlesFolder(isEditable()));
        selectTab(addTab(formPanel, proto().siteTitles().getMeta().getCaption()));

        formPanel = new FormPanel(this);
        formPanel.h4(i18n.tr("Recommended Logo size not to exceed: small - {0}, large - {1} pixels", "160x80", "300x90"));
        formPanel.append(Location.Dual, proto().logo(), new SiteImageResourceFolder(isEditable()));
        addTab(formPanel, i18n.tr("Site Logos"));

        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().slogan(), new RichTextContentFolder(isEditable()));
        addTab(formPanel, proto().slogan().getMeta().getCaption());

        SiteImageSetFolder imageFolder = new SiteImageSetFolder(isEditable());
        imageFolder.setImageSize(690, 300);
        imageFolder.setThumbSize(230, 100);
        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().banner(), imageFolder);
        addTab(formPanel, proto().banner().getMeta().getCaption());

        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().metaTags(), new MetaTagsFolder(isEditable()));
        addTab(formPanel, proto().metaTags().getMeta().getCaption());

        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().childPages(), new PageDescriptorFolder(this));
        selectTab(addTab(formPanel, proto().childPages().getMeta().getCaption()));

        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, createGadgetPanel());
        addTab(formPanel, i18n.tr("Home Page Gadgets"));

        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().cityIntroPages(), new CityIntroPageFolder(this));
        addTab(formPanel, proto().cityIntroPages().getMeta().getCaption());
    }

    private IsWidget createGadgetPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h4(i18n.tr("Narrow Page Gadgets:"));
        formPanel.append(Location.Dual, proto().homePageGadgetsNarrow(), new HomePageGadgetFolder(isEditable()));

        formPanel.br();

        formPanel.h4(i18n.tr("Wide Page Gadgets:"));
        formPanel.append(Location.Dual, proto().homePageGadgetsWide(), new HomePageGadgetFolder(isEditable()));

        return formPanel;
    }
}