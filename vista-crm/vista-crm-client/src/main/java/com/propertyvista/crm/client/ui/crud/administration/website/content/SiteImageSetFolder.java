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
package com.propertyvista.crm.client.ui.crud.administration.website.content;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.SiteImageResourceFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.SiteImageSet;

public class SiteImageSetFolder extends VistaBoxFolder<SiteImageSet> {

    private static final I18n i18n = I18n.get(SiteImageSetFolder.class);

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    private Dimension imageSize;

    private Dimension thumbSize;

    public SiteImageSetFolder(boolean editable) {
        super(SiteImageSet.class, editable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<SiteImageSet>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<SiteImageSet>> event) {
                updateUsedLocales();
            }
        });
    }

    public void setImageSize(int width, int height) {
        imageSize = new Dimension(width, height);
        for (CComponent<?, ?, ?, ?> comp : getComponents()) {
            ((PortalImageSetEditor) ((CFolderItem<?>) comp).getComponents().iterator().next()).setImageSize(width, height);
        }
    }

    public void setThumbSize(int width, int height) {
        thumbSize = new Dimension(width, height);
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (SiteImageSet items : getValue()) {
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
                    SiteImageSet item = EntityFactory.create(SiteImageSet.class);
                    item.locale().set(locale);
                    SiteImageSetFolder.super.addItem(item);
                }
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<SiteImageSet> createItemForm(IObject<?> member) {
        PortalImageSetEditor editor = new PortalImageSetEditor();
        if (imageSize != null) {
            editor.setImageSize(imageSize.width, imageSize.height);
        }
        if (thumbSize != null) {
            editor.setThumbSize(thumbSize.width, thumbSize.height);
        }
        return editor;
    }

    class PortalImageSetEditor extends CForm<SiteImageSet> {

        private final CImageSlider<SiteImageResource> imageHolder;

        public PortalImageSetEditor() {
            super(SiteImageSet.class);
            imageHolder = new CImageSlider<SiteImageResource>(SiteImageResource.class,
                    GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class), new SiteImageResourceFileURLBuilder()) {
                @Override
                protected FolderImages getFolderIcons() {
                    return VistaImages.INSTANCE;
                }

                @Override
                public Widget getImageEntryView(CForm<SiteImageResource> entryForm) {
                    VerticalPanel infoPanel = new VerticalPanel();
                    infoPanel.add(entryForm.inject(entryForm.proto().file().fileName(), new CLabel<String>(), new FieldDecoratorBuilder().build()));
                    infoPanel.add(entryForm.inject(entryForm.proto().caption(), new FieldDecoratorBuilder().build()));
                    infoPanel.add(entryForm.inject(entryForm.proto().description(), new FieldDecoratorBuilder().build()));
                    return infoPanel;
                }
            };
            imageHolder.setOrganizerWidth(800);
            imageHolder.setNote(i18n.tr("Recommended banner size is {0}", "920x375"));
        }

        public void setImageSize(int width, int height) {
            imageHolder.setImageSize(width, height);
        }

        public void setThumbSize(int width, int height) {
            imageHolder.setThumbSize(width, height);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            formPanel.append(Location.Left, proto().locale(), locale).decorate();
            formPanel.append(Location.Left, proto().imageSet(), imageHolder).decorate();

            return formPanel;
        }
    }

}
