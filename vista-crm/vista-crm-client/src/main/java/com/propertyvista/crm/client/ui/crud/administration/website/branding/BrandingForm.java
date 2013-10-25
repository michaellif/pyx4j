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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.administration.website.RichTextContentFolder;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.dto.SiteDescriptorDTO;

public class BrandingForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(BrandingForm.class);

    public BrandingForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        TwoColumnFlexFormPanel content;

        content = new TwoColumnFlexFormPanel(proto().siteTitles().getMeta().getCaption());
        content.setWidget(0, 0, 2, inject(proto().siteTitles(), new SiteTitlesFolder(isEditable())));
        selectTab(addTab(content));

        content = new TwoColumnFlexFormPanel(i18n.tr("Site Logos"));
        content.setWidget(0, 0, 2, inject(proto().logo(), new PortalImageResourceFolder(isEditable())));
        addTab(content);

        content = new TwoColumnFlexFormPanel(proto().slogan().getMeta().getCaption());
        content.setWidget(0, 0, 2, inject(proto().slogan(), new RichTextContentFolder(isEditable())));
        addTab(content);

        content = new TwoColumnFlexFormPanel(proto().pmcInfo().getMeta().getCaption());
        content.setWidget(0, 0, 2, inject(proto().pmcInfo(), new RichTextContentFolder(isEditable())));
        addTab(content);

        PortalImageSetFolder imageFolder = new PortalImageSetFolder(isEditable());
        imageFolder.setImageSize(690, 300);
        imageFolder.setThumbSize(230, 100);
        content = new TwoColumnFlexFormPanel(proto().banner().getMeta().getCaption());
        content.setWidget(0, 0, 2, inject(proto().banner(), imageFolder));
        addTab(content);

        content = new TwoColumnFlexFormPanel(proto().metaTags().getMeta().getCaption());
        content.setWidget(0, 0, 2, inject(proto().metaTags(), new MetaTagsFolder(isEditable())));
        addTab(content);

        content = new TwoColumnFlexFormPanel(proto().socialLinks().getMeta().getCaption());
        content.setWidget(0, 0, 2, inject(proto().socialLinks(), new SocialLinkFolder(isEditable())));
        addTab(content);

        addTab(createCrmLogoTab());
    }

    private TwoColumnFlexFormPanel createCrmLogoTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(proto().crmLogo().getMeta().getCaption());

        CImage<SiteImageResource> file = new CImage<SiteImageResource>(GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class)) {
            @Override
            public Widget getImageEntryView(CEntityForm<SiteImageResource> entryForm) {
                SimplePanel main = new SimplePanel();
                main.setWidget(new FormDecoratorBuilder(entryForm.inject(entryForm.proto().caption()), 8, 15, 16).build());
                return main;
            }

            @Override
            protected EntityFolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }
        };
        file.setFileUrlBuilder(new FileURLBuilder<SiteImageResource>() {
            @Override
            public String getUrl(SiteImageResource file) {
                return MediaUtils.createSiteImageResourceUrl(file);
            }
        });
        file.setImageSize(150, 100);

        content.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().crmLogo(), file), 20).build());

        return content;
    }

}