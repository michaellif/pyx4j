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
import com.propertyvista.domain.site.PageMetaTags;

class MetaTagsFolder extends VistaBoxFolder<PageMetaTags> {
    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public MetaTagsFolder(boolean modifyable) {
        super(PageMetaTags.class, modifyable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<PageMetaTags>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<PageMetaTags>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (PageMetaTags titles : getValue()) {
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
                    PageMetaTags titles = EntityFactory.create(PageMetaTags.class);
                    titles.locale().set(locale);
                    MetaTagsFolder.super.addItem(titles);
                }
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<PageMetaTags> createItemForm(IObject<?> member) {
        return new PageMetaTagsEditor();
    }

    class PageMetaTagsEditor extends AccessoryEntityForm<PageMetaTags> {

        public PageMetaTagsEditor() {
            super(PageMetaTags.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, injectAndDecorate(proto().locale(), locale, 10));
            main.setWidget(++row, 0, injectAndDecorate(proto().title(), 35));
            main.setWidget(++row, 0, injectAndDecorate(proto().description(), 35));
            main.setWidget(++row, 0, injectAndDecorate(proto().keywords(), 35));
            return main;
        }
    }
}