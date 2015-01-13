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
package com.propertyvista.crm.client.ui.crud.administration.website.general;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;

import com.propertyvista.common.client.SiteImageResourceFileURLBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.administration.website.RichTextContentFolder;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.dto.SiteDescriptorDTO;

public class GeneralForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(GeneralForm.class);

    private final CCheckBox publicPortalSwitch = new CCheckBox();

    public GeneralForm(IPrimeFormView<SiteDescriptorDTO, ?> view) {
        super(SiteDescriptorDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Web Skin"));

        CComboBox<Skin> skinComp = new CComboBox<Skin>();
        skinComp.setOptions(EnumSet.of(Skin.skin2, Skin.skin3, Skin.skin4, Skin.skin5, Skin.skin6));
        formPanel.append(Location.Left, proto().skin(), skinComp).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().sitePalette().object1()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().sitePalette().object2()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().sitePalette().contrast1()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().sitePalette().contrast2()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().sitePalette().contrast3()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().sitePalette().contrast4()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().sitePalette().contrast5()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().sitePalette().contrast6()).decorate().componentWidth(120);

        // ---------------------------------------------------------------------------------------------------------------

        formPanel.h1(i18n.tr("Website"));

        formPanel.append(Location.Left, proto().enabled(), publicPortalSwitch).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().disableMapView()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().disableBuildingDetails()).decorate().componentWidth(120);

        // --------------------------------------------------------------------------------------------------------------------

        formPanel.h1(i18n.tr("Resident Portal"));
        formPanel.append(Location.Left, proto().residentPortalEnabled()).decorate().componentWidth(120);

        selectTab(addTab(formPanel, i18n.tr("General")));

        // =====================================================================================================================

        formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().locales(), new AvailableLocaleFolder(isEditable()));
        addTab(formPanel, proto().locales().getMeta().getCaption());

        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().pmcInfo(), new RichTextContentFolder(isEditable()));
        addTab(formPanel, proto().pmcInfo().getMeta().getCaption());

        addTab(createCrmLogoTab(), proto().crmLogo().getMeta().getCaption());

        formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().socialLinks(), new SocialLinkFolder(isEditable()));
        addTab(formPanel, proto().socialLinks().getMeta().getCaption());
    }

    private FormPanel createCrmLogoTab() {
        FormPanel formPanel = new FormPanel(this);

        CImage file = new CImage(GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class), new SiteImageResourceFileURLBuilder());
        file.setImageSize(150, 100);
        file.setScaleMode(ScaleMode.Contain);

        formPanel.append(Location.Left, proto().crmLogo().file(), file).decorate();

        return formPanel;
    }
}