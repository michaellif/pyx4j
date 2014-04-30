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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.administration.website.RichTextContentFolder;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(SiteForm.class);

    public SiteForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        BasicCFormPanel formPanel;

        formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Full, proto().siteTitles(), new SiteTitlesFolder(isEditable()));
        selectTab(addTab(formPanel, proto().siteTitles().getMeta().getCaption()));

        formPanel = new BasicCFormPanel(this);
        formPanel.h4(i18n.tr("Recommended Logo size not to exceed: small - {0}, large - {1} pixels", "160x80", "300x90"));
        formPanel.append(Location.Full, proto().logo(), new SiteImageResourceFolder(isEditable()));
        addTab(formPanel, i18n.tr("Site Logos"));

        formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Full, proto().slogan(), new RichTextContentFolder(isEditable()));
        addTab(formPanel, proto().slogan().getMeta().getCaption());

        SiteImageSetFolder imageFolder = new SiteImageSetFolder(isEditable());
        imageFolder.setImageSize(690, 300);
        imageFolder.setThumbSize(230, 100);
        formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Full, proto().banner(), imageFolder);
        addTab(formPanel, proto().banner().getMeta().getCaption());

        formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Full, proto().metaTags(), new MetaTagsFolder(isEditable()));
        addTab(formPanel, proto().metaTags().getMeta().getCaption());

        formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Full, proto().childPages(), new PageDescriptorFolder(this));
        selectTab(addTab(formPanel, proto().childPages().getMeta().getCaption()));

        formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Full, createGadgetPanel());
        addTab(formPanel, i18n.tr("Home Page Gadgets"));

        formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Full, proto().cityIntroPages(), new CityIntroPageFolder(this));
        addTab(formPanel, proto().cityIntroPages().getMeta().getCaption());
    }

    private IsWidget createGadgetPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.h4(i18n.tr("Narrow Page Gadgets:"));
        formPanel.append(Location.Full, proto().homePageGadgetsNarrow(), new HomePageGadgetFolder(isEditable()));

        formPanel.br();

        formPanel.h4(i18n.tr("Wide Page Gadgets:"));
        formPanel.append(Location.Full, proto().homePageGadgetsWide(), new HomePageGadgetFolder(isEditable()));

        return formPanel;
    }
}