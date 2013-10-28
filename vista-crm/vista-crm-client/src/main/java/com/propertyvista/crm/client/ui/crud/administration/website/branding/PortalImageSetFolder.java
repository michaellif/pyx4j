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

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PortalImageSet;
import com.propertyvista.domain.site.SiteImageResource;

public class PortalImageSetFolder extends VistaBoxFolder<PortalImageSet> {
    private static final I18n i18n = I18n.get(PortalImageSetFolder.class);

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    private Dimension imageSize;

    private Dimension thumbSize;

    public PortalImageSetFolder(boolean editable) {
        super(PortalImageSet.class, editable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<PortalImageSet>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<PortalImageSet>> event) {
                updateUsedLocales();
            }
        });
    }

    public void setImageSize(int width, int height) {
        imageSize = new Dimension(width, height);
        for (CComponent<?> comp : getComponents()) {
            if (comp instanceof PortalImageSetEditor) {
                ((PortalImageSetEditor) comp).setImageSize(width, height);
            }
        }
    }

    public void setThumbSize(int width, int height) {
        thumbSize = new Dimension(width, height);
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (PortalImageSet items : getValue()) {
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
                    PortalImageSet item = EntityFactory.create(PortalImageSet.class);
                    item.locale().set(locale);
                    PortalImageSetFolder.super.addItem(item);
                }
                return true;
            }
        }.show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PortalImageSet) {
            PortalImageSetEditor editor = new PortalImageSetEditor();
            if (imageSize != null) {
                editor.setImageSize(imageSize.width, imageSize.height);
            }
            if (thumbSize != null) {
                editor.setThumbSize(thumbSize.width, thumbSize.height);
            }
            return editor;
        }
        return super.create(member);
    }

    class PortalImageSetEditor extends CEntityForm<PortalImageSet> {
        private final CImageSlider<SiteImageResource> imageHolder;

        public PortalImageSetEditor() {
            super(PortalImageSet.class);
            imageHolder = new CImageSlider<SiteImageResource>(SiteImageResource.class,
                    GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class), new ImageFileURLBuilder()) {
                @Override
                protected EntityFolderImages getFolderIcons() {
                    return VistaImages.INSTANCE;
                }

                @Override
                public Widget getImageEntryView(CEntityForm<SiteImageResource> entryForm) {
                    VerticalPanel infoPanel = new VerticalPanel();
                    infoPanel.add(new FormDecoratorBuilder(entryForm.inject(entryForm.proto().fileName(), new CLabel<String>())).build());
                    infoPanel.add(new FormDecoratorBuilder(entryForm.inject(entryForm.proto().caption())).build());
                    infoPanel.add(new FormDecoratorBuilder(entryForm.inject(entryForm.proto().description())).build());
                    return infoPanel;
                }
            };
        }

        public void setImageSize(int width, int height) {
            imageHolder.setImageSize(width, height);
        }

        public void setThumbSize(int width, int height) {
            imageHolder.setThumbSize(width, height);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().locale(), locale), 10, true).build());
            main.setWidget(++row, 0, 2, inject(proto().imageSet(), imageHolder));

            return main;
        }
    }

    class ImageFileURLBuilder implements FileURLBuilder<SiteImageResource> {
        @Override
        public String getUrl(SiteImageResource file) {
            return MediaUtils.createSiteImageResourceUrl(file);
        }
    }
}
