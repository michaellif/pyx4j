/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.website.branding;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PortalLogoImageResource;
import com.propertyvista.domain.site.SiteImageResource;

public class PortalImageResourceFolder extends VistaBoxFolder<PortalLogoImageResource> {
    private static final I18n i18n = I18n.get(PortalImageResourceFolder.class);

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public PortalImageResourceFolder(boolean editable) {
        super(PortalLogoImageResource.class, editable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<PortalLogoImageResource>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<PortalLogoImageResource>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (PortalLogoImageResource items : getValue()) {
            usedLocales.add(items.locale());
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        updateUsedLocales();
    }

    @Override
    protected void addItem() {
        new AvailableLocaleSelectorDialog(usedLocales) {
            @Override
            public boolean onClickOk() {
                AvailableLocale locale = getSelectedLocale();
                if (locale != null) {
                    PortalLogoImageResource item = EntityFactory.create(PortalLogoImageResource.class);
                    item.locale().set(locale);
                    PortalImageResourceFolder.super.addItem(item);
                }
                return true;
            }
        }.show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PortalLogoImageResource) {
            return new PortalImageResourceEditor();
        }
        return super.create(member);
    }

    class PortalImageResourceEditor extends CEntityForm<PortalLogoImageResource> {

        private final CImage<SiteImageResource> smallLogo;

        private final CImage<SiteImageResource> largeLogo;

        public PortalImageResourceEditor() {
            super(PortalLogoImageResource.class);

            smallLogo = new CImage<SiteImageResource>(GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class)) {
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
            smallLogo.setFileUrlBuilder(new FileURLBuilder<SiteImageResource>() {
                @Override
                public String getUrl(SiteImageResource file) {
                    return MediaUtils.createSiteImageResourceUrl(file);
                }
            });
            smallLogo.setImageSize(150, 100);

            largeLogo = new CImage<SiteImageResource>(GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class)) {
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
            largeLogo.setFileUrlBuilder(new FileURLBuilder<SiteImageResource>() {
                @Override
                public String getUrl(SiteImageResource file) {
                    return MediaUtils.createSiteImageResourceUrl(file);
                }
            });
            largeLogo.setImageSize(300, 150);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().locale(), locale), 5, 20, 20).build());
            main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().large(), largeLogo), 5, 20, 20).build());
            main.getFlexCellFormatter().setRowSpan(row, 1, 2);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().small(), smallLogo), 5, 20, 20).build());

            return main;
        }
    }
}
