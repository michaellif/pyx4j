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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.SiteImageResourceFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PortalBannerImage;

public class PortalBannerImageFolder extends VistaBoxFolder<PortalBannerImage> {
    private static final I18n i18n = I18n.get(PortalBannerImageFolder.class);

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    private Dimension imageSize;

    public PortalBannerImageFolder(boolean editable) {
        super(PortalBannerImage.class, editable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<PortalBannerImage>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<PortalBannerImage>> event) {
                updateUsedLocales();
            }
        });
    }

    public void setImageSize(int width, int height) {
        imageSize = new Dimension(width, height);
        for (CComponent<?, ?, ?, ?> comp : getComponents()) {
            ((PortalBannerImageEditor) ((CFolderItem<?>) comp).getComponents().iterator().next()).setImageSize(width, height);
        }
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (PortalBannerImage items : getValue()) {
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
                    PortalBannerImage item = EntityFactory.create(PortalBannerImage.class);
                    item.locale().set(locale);
                    PortalBannerImageFolder.super.addItem(item);
                }
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<PortalBannerImage> createItemForm(IObject<?> member) {
        PortalBannerImageEditor editor = new PortalBannerImageEditor();
        if (imageSize != null) {
            editor.setImageSize(imageSize.width, imageSize.height);
        }
        return editor;
    }

    class PortalBannerImageEditor extends CForm<PortalBannerImage> {
        private final CImage imageHolder;

        public PortalBannerImageEditor() {
            super(PortalBannerImage.class);
            imageHolder = new CImage(GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class), new SiteImageResourceFileURLBuilder());
            imageHolder.setNote(i18n.tr("Recommended banner size is {0}", "1200x200"));
        }

        public void setImageSize(int width, int height) {
            imageHolder.setImageSize(width, height);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            formPanel.append(Location.Dual, proto().locale(), locale).decorate();
            formPanel.append(Location.Dual, proto().image().file(), imageHolder).decorate();

            return formPanel;
        }
    }

}
