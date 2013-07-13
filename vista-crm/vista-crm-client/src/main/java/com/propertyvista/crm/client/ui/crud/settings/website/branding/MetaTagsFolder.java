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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.settings.website.general.AvailableLocaleSelectorDialog;
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
        new AvailableLocaleSelectorDialog(usedLocales, new ValueChangeHandler<AvailableLocale>() {
            @Override
            public void onValueChange(ValueChangeEvent<AvailableLocale> event) {
                PageMetaTags titles = EntityFactory.create(PageMetaTags.class);
                titles.locale().set(event.getValue());
                MetaTagsFolder.super.addItem(titles);
            }
        }).show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PageMetaTags) {
            return new PageMetaTagsEditor();
        }
        return super.create(member);
    }

    class PageMetaTagsEditor extends CEntityDecoratableForm<PageMetaTags> {

        public PageMetaTagsEditor() {
            super(PageMetaTags.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().locale(), locale), 10).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().title()), 35).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 35).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().keywords()), 35).build());
            return main;
        }
    }
}