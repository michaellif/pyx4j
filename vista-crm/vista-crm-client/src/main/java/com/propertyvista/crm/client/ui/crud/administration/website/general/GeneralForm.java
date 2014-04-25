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
package com.propertyvista.crm.client.ui.crud.administration.website.general;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
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

    public GeneralForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = 0;

        content.setH1(row++, 0, 2, i18n.tr("Web Skin"));

        CComboBox<Skin> skinComp = new CComboBox<Skin>();
        skinComp.setOptions(EnumSet.of(Skin.skin2, Skin.skin3, Skin.skin4, Skin.skin5, Skin.skin6));
        content.setWidget(row++, 0, injectAndDecorate(proto().skin(), skinComp, 10));
        content.setWidget(row++, 0, injectAndDecorate(proto().sitePalette().object1(), 10));
        content.setWidget(row++, 0, injectAndDecorate(proto().sitePalette().object2(), 10));
        content.setWidget(row++, 0, injectAndDecorate(proto().sitePalette().contrast1(), 10));
        content.setWidget(row++, 0, injectAndDecorate(proto().sitePalette().contrast2(), 10));

        // ---------------------------------------------------------------------------------------------------------------

        content.setH1(row++, 0, 2, i18n.tr("Website"));
        content.setWidget(row++, 0, injectAndDecorate(proto().enabled(), publicPortalSwitch, 10));
        content.setWidget(row++, 0, injectAndDecorate(proto().disableMapView(), 10));
        content.setWidget(row++, 0, injectAndDecorate(proto().disableBuildingDetails(), 10));

        // --------------------------------------------------------------------------------------------------------------------

        content.setH1(row++, 0, 2, i18n.tr("Resident Portal"));
        content.setWidget(row++, 0, injectAndDecorate(proto().residentPortalEnabled(), 10));

        selectTab(addTab(content, i18n.tr("General")));

        // =====================================================================================================================

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().locales(), new AvailableLocaleFolder(isEditable())));
        addTab(content, proto().locales().getMeta().getCaption());

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().pmcInfo(), new RichTextContentFolder(isEditable())));
        addTab(content, proto().pmcInfo().getMeta().getCaption());

        addTab(createCrmLogoTab(), proto().crmLogo().getMeta().getCaption());

        content = new TwoColumnFlexFormPanel();
        content.setWidget(0, 0, 2, inject(proto().socialLinks(), new SocialLinkFolder(isEditable())));
        addTab(content, proto().socialLinks().getMeta().getCaption());
    }

    private TwoColumnFlexFormPanel createCrmLogoTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        CImage file = new CImage(GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class), new SiteImageResourceFileURLBuilder());
        file.setImageSize(150, 100);
        file.setScaleMode(ScaleMode.Contain);

        content.setWidget(0, 0, injectAndDecorate(proto().crmLogo().file(), file, 20));

        return content;
    }
}