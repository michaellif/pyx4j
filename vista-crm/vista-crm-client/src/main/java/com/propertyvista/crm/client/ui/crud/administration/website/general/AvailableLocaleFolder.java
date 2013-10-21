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
package com.propertyvista.crm.client.ui.crud.administration.website.general;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog_v2;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.shared.i18n.CompiledLocale;

class AvailableLocaleFolder extends VistaTableFolder<AvailableLocale> {

    private static final I18n i18n = I18n.get(AvailableLocaleFolder.class);

    private final Set<CompiledLocale> usedLocales = new HashSet<CompiledLocale>();

    public AvailableLocaleFolder(boolean modifyable) {
        super(AvailableLocale.class, modifyable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<AvailableLocale>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<AvailableLocale>> event) {
                updateUsedLocales();
            }
        });
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().lang(), "20em"));
        return columns;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (proto().lang().getPath().equals(member.getPath())) {
            return new CLabel();
        }
        return super.create(member);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        updateUsedLocales();
    }

    @Override
    protected IFolderDecorator<AvailableLocale> createFolderDecorator() {
        TableFolderDecorator<AvailableLocale> decor = (TableFolderDecorator<AvailableLocale>) super.createFolderDecorator();
        decor.setShowHeader(false);
        return decor;
    }

    @Override
    protected void addItem() {
        new LocaleSelectorDialog() {
            @Override
            public boolean onClickCancel() {
                return true;
            }
        }.show();
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (AvailableLocale al : getValue()) {
            usedLocales.add(al.lang().getValue());
        }
    }

    abstract class LocaleSelectorDialog extends Dialog_v2 implements CancelOption, ClickHandler {

        private final String LocaleRadioGroup = "LocaleSelector";

        private final VerticalPanel panel = new VerticalPanel();

        public LocaleSelectorDialog() {
            super("Select Locale");
            setDialogPixelWidth(400);
            setDialogOptions(this);

            EnumSet<CompiledLocale> availLocales = CompiledLocale.getSupportedLocales();
            availLocales.removeAll(usedLocales);
            if (availLocales.size() == 0) {
                panel.add(new Label(i18n.tr("Sorry, no more items to choose from.")));
            } else {
                for (CompiledLocale locale : availLocales) {
                    RadioButton radio = new RadioButton(LocaleRadioGroup, locale.toString());
                    radio.setFormValue(locale.name());
                    radio.addClickHandler(this);
                    panel.add(radio);
                }
            }
            setBody(panel);
        }

        @Override
        public void onClick(ClickEvent event) {
            RadioButton radio = null;
            Iterator<Widget> widgets = panel.iterator();
            while ((radio = (RadioButton) widgets.next()) != null) {
                if (event.getSource().equals(radio)) {
                    AvailableLocale locale = EntityFactory.create(AvailableLocale.class);
                    locale.lang().setValue(CompiledLocale.valueOf(radio.getFormValue()));
                    AvailableLocaleFolder.super.addItem(locale);
                    hide(false);
                    break;
                }
            }
        }
    }
}