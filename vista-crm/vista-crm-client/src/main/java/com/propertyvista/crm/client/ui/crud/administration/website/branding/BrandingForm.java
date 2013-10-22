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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.administration.website.RichTextContentFolder;
import com.propertyvista.crm.client.ui.crud.administration.website.SiteImageThumbnail;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.dto.SiteDescriptorDTO;

public class BrandingForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(BrandingForm.class);

    private final SiteImageThumbnail thumb = new SiteImageThumbnail();

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

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        thumb.setUrl(MediaUtils.createSiteImageResourceUrl(getValue().crmLogo()));
    }

    private TwoColumnFlexFormPanel createCrmLogoTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(proto().crmLogo().getMeta().getCaption());

        content.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().crmLogo(), new CFile<SiteImageResource>(new Command() {
            @Override
            public void execute() {
                OkDialog dialog = new OkDialog(getValue().crmLogo().fileName().getValue()) {
                    @Override
                    public boolean onClickOk() {
                        return true;
                    }
                };
                dialog.setBody(new Image(MediaUtils.createSiteImageResourceUrl(getValue().crmLogo())));
                dialog.layout();
            }
        }) {
            @Override
            public void showFileSelectionDialog() {
                SiteImageResourceProvider provider = new SiteImageResourceProvider();
                provider.selectResource(new AsyncCallback<SiteImageResource>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        MessageDialog.error(i18n.tr("Action Failed"), caught.getMessage());
                    }

                    @Override
                    public void onSuccess(SiteImageResource rc) {
                        if (rc != null) {
                            setValue(rc);
                            thumb.setUrl(MediaUtils.createSiteImageResourceUrl(rc));
                        }
                    }
                });
            }
        }), 20).build());

        content.setWidget(0, 1, thumb);

        return content;
    }

}