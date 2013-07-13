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
package com.propertyvista.crm.client.ui.crud.settings.website.general;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.HtmlContent;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class ResidentCustomContentFolder extends VistaBoxFolder<HtmlContent> {
    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public ResidentCustomContentFolder(boolean modifiable) {
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
                ResidentCustomContentFolder.super.addItem(item);
            }
        }).show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof HtmlContent) {
            return new ResidentCustomContentEditor();
        }
        return super.create(member);
    }

    class ResidentCustomContentEditor extends CEntityDecoratableForm<HtmlContent> {
        public ResidentCustomContentEditor() {
            super(HtmlContent.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            CEntityComboBox<AvailableLocale> locale = new CEntityComboBox<AvailableLocale>(AvailableLocale.class);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().locale(), locale), 10).build());
            if (isEditable()) {
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().html(), new CTextArea()), 60).build());
            } else {
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().html(), new CLabel<String>()), 60).build());
            }
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().updated()), 10).build());
            return main;
        }

        // This is to fix problems with css injection when full custom resident page html is rendered inside Label viewer.
        @Override
        protected void onValuePropagation(HtmlContent value, boolean fireEvent, boolean populate) {
            if (!isEditable()) {
                String html = value.html().getValue();
                String status = html.contains(DeploymentConsts.RESIDENT_CONTENT_ID) && html.contains(DeploymentConsts.RESIDENT_LOGIN_ID) ? "OK" : "INCOMPLETE";
                value.html().setValue(status);
            }
            super.onValuePropagation(value, fireEvent, populate);
        }
    }
}
