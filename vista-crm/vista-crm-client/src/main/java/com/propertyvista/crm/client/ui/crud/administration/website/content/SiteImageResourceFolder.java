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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;

import com.propertyvista.common.client.SiteImageResourceFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.SiteLogoImageResource;

public class SiteImageResourceFolder extends VistaBoxFolder<SiteLogoImageResource> {

    private static final I18n i18n = I18n.get(SiteImageResourceFolder.class);

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public SiteImageResourceFolder(boolean editable) {
        super(SiteLogoImageResource.class, editable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<SiteLogoImageResource>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<SiteLogoImageResource>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (SiteLogoImageResource items : getValue()) {
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
                    SiteLogoImageResource item = EntityFactory.create(SiteLogoImageResource.class);
                    item.locale().set(locale);
                    SiteImageResourceFolder.super.addItem(item);
                }
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<SiteLogoImageResource> createItemForm(IObject<?> member) {
        return new PortalImageResourceEditor();
    }

    class PortalImageResourceEditor extends CForm<SiteLogoImageResource> {

        private final CImage smallLogo;

        private final CImage largeLogo;

        public PortalImageResourceEditor() {
            super(SiteLogoImageResource.class);

            smallLogo = new CImage(GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class), new SiteImageResourceFileURLBuilder());
            smallLogo.setImageSize(150, 100);
            smallLogo.setScaleMode(ScaleMode.Contain);

            largeLogo = new CImage(GWT.<SiteImageResourceUploadService> create(SiteImageResourceUploadService.class), new SiteImageResourceFileURLBuilder());
            largeLogo.setImageSize(300, 150);
            largeLogo.setScaleMode(ScaleMode.Contain);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            formPanel.append(Location.Left, proto().locale()).decorate();
            formPanel.append(Location.Left, proto().small().file(), smallLogo).decorate().customLabel(i18n.tr("Small Logo"));
            formPanel.append(Location.Dual, proto().large().file(), largeLogo).decorate().customLabel(i18n.tr("Large Logo"));

            return formPanel;
        }

    }
}
