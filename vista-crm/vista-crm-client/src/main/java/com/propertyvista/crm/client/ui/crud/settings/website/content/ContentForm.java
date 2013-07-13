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
package com.propertyvista.crm.client.ui.crud.settings.website.content;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.SiteDescriptorDTO;

public class ContentForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(ContentForm.class);

    public ContentForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        TwoColumnFlexFormPanel content;

        content = new TwoColumnFlexFormPanel(proto().childPages().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().childPages(), new PageDescriptorFolder(this)));
        selectTab(addTab(content));

        content = new TwoColumnFlexFormPanel(i18n.tr("Home Page Gadgets"));
        content.setWidget(0, 0, createGadgetPanel());
        addTab(content);

        content = new TwoColumnFlexFormPanel(proto().cityIntroPages().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().cityIntroPages(), new CityIntroPageFolder(this)));
        addTab(content);
    }

    private Widget createGadgetPanel() {
        TwoColumnFlexFormPanel gadgetPanel = new TwoColumnFlexFormPanel();
        int row = 0;

        gadgetPanel.setH4(row++, 0, 1, i18n.tr("Narrow Page Gadgets:"));
        gadgetPanel.setWidget(row++, 0, inject(proto().homePageGadgetsNarrow(), new HomePageGadgetFolder(isEditable())));

        gadgetPanel.setBR(row++, 0, 1);

        gadgetPanel.setH4(row++, 0, 1, i18n.tr("Wide Page Gadgets:"));
        gadgetPanel.setWidget(row++, 0, inject(proto().homePageGadgetsWide(), new HomePageGadgetFolder(isEditable())));

        return gadgetPanel;
    }
}