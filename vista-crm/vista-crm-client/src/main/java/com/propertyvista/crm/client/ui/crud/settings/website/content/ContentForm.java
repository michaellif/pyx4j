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

import java.util.EnumSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.settings.website.PortalImageResourceFolder.SiteImageThumbnail;
import com.propertyvista.crm.client.ui.crud.settings.website.general.HomePageGadgetFolder;
import com.propertyvista.domain.File;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.dto.SiteDescriptorDTO;

public class ContentForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(ContentForm.class);

    private final SiteImageThumbnail thumb = new SiteImageThumbnail();

    public ContentForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        FormFlexPanel content;

        content = new FormFlexPanel(proto().childPages().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().childPages(), new PageDescriptorFolder(this)));
        selectTab(addTab(content));

        content = new FormFlexPanel(i18n.tr("Home Page Gadgets"));
        content.setWidget(0, 0, createGadgetPanel());
        addTab(content);

        content = new FormFlexPanel(proto().cityIntroPages().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().cityIntroPages(), new CityIntroPageFolder(this)));
        addTab(content);

        content = new FormFlexPanel(proto().crmLogo().getMeta().getCaption());
        HorizontalPanel imageLinkContainer = new HorizontalPanel();
        imageLinkContainer.setWidth("100%");
        content.setWidget(0, 0, imageLinkContainer);
        imageLinkContainer.add(inject(proto().crmLogo(), new CFile<File>(new Command() {
            @Override
            public void execute() {
                OkDialog dialog = new OkDialog(getValue().crmLogo().fileName().getValue()) {
                    @Override
                    public boolean onClickOk() {
                        return true;
                    }
                };
                dialog.setBody(new Image(MediaUtils.createSiteImageResourceUrl(getValue().crmLogo())));
                dialog.center();
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
                        setValue(rc);
                        thumb.setUrl(MediaUtils.createSiteImageResourceUrl(rc));
                    }
                });
            }
        }));
        imageLinkContainer.add(thumb);
        imageLinkContainer.setCellVerticalAlignment(imageLinkContainer.getWidget(0), HasVerticalAlignment.ALIGN_MIDDLE);
        imageLinkContainer.getWidget(0).setWidth("400px");
        imageLinkContainer.setCellWidth(thumb, "200px");
        addTab(content);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        thumb.setUrl(MediaUtils.createSiteImageResourceUrl(getValue().crmLogo()));
    }

    class GadgetSelectorDialog extends SelectEnumDialog<HomePageGadget.GadgetType> {
        public GadgetSelectorDialog() {
            super(i18n.tr("Select Gadget Type"), EnumSet.allOf(HomePageGadget.GadgetType.class));
        }

        @Override
        public boolean onClickOk() {
            HomePageGadget.GadgetType type = getSelectedType();
            if (type == null) {
                return false;
            }

            HomePageGadget newItem = EntityFactory.create(HomePageGadget.class);
            newItem.type().setValue(type);
            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(HomePageGadget.class).formNewItemPlace(newItem));
            return true;
        }
    }

    private Widget createGadgetPanel() {
        FormFlexPanel gadgetPanel = new FormFlexPanel();
        int row = 0;

        Widget addNewItem = null;
        if (isEditable()) {
            addNewItem = new HTML();
        } else {
            Anchor addNewItemLink = new Anchor(i18n.tr("Add New Gadget"));
            addNewItemLink.getElement().getStyle().setProperty("lineHeight", "2em");
            addNewItemLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new GadgetSelectorDialog().show();
                }
            });
            addNewItem = addNewItemLink;
        }

        gadgetPanel.setH1(row++, 0, 2, i18n.tr("Home Page Gadgets"), addNewItem);
        gadgetPanel.setWidget(row, 0, inject(proto().homePageGadgetsNarrow(), new HomePageGadgetFolder(isEditable())));
        gadgetPanel.setWidget(row, 1, inject(proto().homePageGadgetsWide(), new HomePageGadgetFolder(isEditable())));

        return gadgetPanel;
    }
}