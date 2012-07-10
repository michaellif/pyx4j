/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.HtmlContent;

public class RichTextContentFolder extends VistaBoxFolder<HtmlContent> {
    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public RichTextContentFolder(boolean modifiable) {
        super(HtmlContent.class, modifiable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<HtmlContent>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<HtmlContent>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (HtmlContent item : getValue()) {
            usedLocales.add(item.locale());
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
                HtmlContent item = EntityFactory.create(HtmlContent.class);
                item.locale().set(event.getValue());
                RichTextContentFolder.super.addItem(item);
            }
        }).show();
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof HtmlContent) {
            return new RichTextContentEditor();
        }
        return super.create(member);
    }
}
