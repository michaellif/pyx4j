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
package com.propertyvista.crm.client.ui.crud.administration.website.content;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.AccessoryEntityForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.administration.website.general.AvailableLocaleSelectorDialog;
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
        new AvailableLocaleSelectorDialog(usedLocales) {
            @Override
            public boolean onClickOk() {
                AvailableLocale locale = getSelectedLocale();
                if (locale != null) {
                    SiteTitles titles = EntityFactory.create(SiteTitles.class);
                    titles.locale().set(locale);
                    SiteTitlesFolder.super.addItem(titles);
                }
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<SiteTitles> createItemForm(IObject<?> member) {
        return new SiteTitlesEditor();
    }

    class SiteTitlesEditor extends AccessoryEntityForm<SiteTitles> {

        public SiteTitlesEditor() {
            super(SiteTitles.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, injectAndDecorate(proto().locale(), locale, 10));
            main.setWidget(++row, 0, injectAndDecorate(proto().crmHeader(), 35));
            main.setWidget(++row, 0, injectAndDecorate(proto().prospectPortalTitle(), 35));
            main.setWidget(++row, 0, injectAndDecorate(proto().residentPortalTitle(), 35));
            main.setWidget(++row, 0, injectAndDecorate(proto().residentPortalPromotions(), 35));
            main.setWidget(++row, 0, injectAndDecorate(proto().copyright(), 35));
            return main;
        }
    }
}