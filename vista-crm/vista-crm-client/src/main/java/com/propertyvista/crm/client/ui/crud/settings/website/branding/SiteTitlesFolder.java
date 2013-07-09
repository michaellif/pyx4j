/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.website.branding;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.settings.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.SiteTitles;

class SiteTitlesFolder extends VistaBoxFolder<SiteTitles> {
    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public SiteTitlesFolder(boolean modifyable) {
        super(SiteTitles.class, modifyable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<SiteTitles>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<SiteTitles>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (SiteTitles titles : getValue()) {
            usedLocales.add(titles.locale());
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        updateUsedLocales();
    }

    @Override
    protected void addItem() {
        new AvailableLocaleSelectorDialog(usedLocales, new ValueChangeHandler<AvailableLocale>() {
            @Override
            public void onValueChange(ValueChangeEvent<AvailableLocale> event) {
                SiteTitles titles = EntityFactory.create(SiteTitles.class);
                titles.locale().set(event.getValue());
                SiteTitlesFolder.super.addItem(titles);
            }
        }).show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof SiteTitles) {
            return new SiteTitlesEditor();
        }
        return super.create(member);
    }

    class SiteTitlesEditor extends CEntityDecoratableForm<SiteTitles> {

        public SiteTitlesEditor() {
            super(SiteTitles.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().locale(), locale), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().crmHeader()), 35).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().prospectPortalTitle()), 35).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().residentPortalTitle()), 35).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().residentPortalPromotions()), 35).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().copyright()), 35).build());
            return main;
        }
    }
}