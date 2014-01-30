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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;

import com.propertyvista.common.client.SiteImageResourceFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
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
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof SiteLogoImageResource) {
            return new PortalImageResourceEditor();
        }
        return super.create(member);
    }

    class PortalImageResourceEditor extends CEntityForm<SiteLogoImageResource> {

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
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().locale(), locale), 5, 20, 20).build());
            main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().large().file(), largeLogo), 5, 20, 20).customLabel(i18n.tr("Large Logo")).build());
            main.getFlexCellFormatter().setRowSpan(row, 1, 2);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().small().file(), smallLogo), 5, 20, 20).customLabel(i18n.tr("Small Logo")).build());

            return main;
        }

    }
}
