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
 */
package com.propertyvista.crm.client.ui.crud.administration.website.general;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

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
    public List<FolderColumnDescriptor> columns() {
        ArrayList<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
        columns.add(new FolderColumnDescriptor(proto().lang(), "20em"));
        return columns;
    }

    @Override
    public CField<?, ?> create(IObject<?> member) {
        if (proto().lang().getPath().equals(member.getPath())) {
            return new CLabel<>();
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
            public boolean onClickOk() {
                CompiledLocale compiledLocale = getSelectedLocale();
                if (compiledLocale != null) {
                    AvailableLocale locale = EntityFactory.create(AvailableLocale.class);
                    locale.lang().setValue(compiledLocale);
                    AvailableLocaleFolder.super.addItem(locale);
                }
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

    private abstract class LocaleSelectorDialog extends Dialog implements OkCancelOption {

        private CompiledLocale selectedLocale;

        public LocaleSelectorDialog() {
            super(i18n.tr("Locale Selection"));
            setDialogPixelWidth(400);
            setDialogOptions(this);

            SimplePanel panel = new SimplePanel();

            EnumSet<CompiledLocale> availLocales = CompiledLocale.getSupportedLocales();
            availLocales.removeAll(usedLocales);
            if (availLocales.isEmpty()) {
                panel.setWidget(new Label(i18n.tr("Sorry, no more Locales to choose from.")));
                getOkButton().setVisible(false);
            } else {
                CComboBox<CompiledLocale> localeSelector = new CComboBox<>();
                localeSelector.setMandatory(true);
                localeSelector.populate(null);
                localeSelector.setOptions(availLocales);
                localeSelector.addValueChangeHandler(new ValueChangeHandler<CompiledLocale>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<CompiledLocale> event) {
                        selectedLocale = event.getValue();
                        getOkButton().setEnabled(true);
                    }
                });
                getOkButton().setEnabled(false);
                localeSelector.asWidget().setWidth("100%");
                panel.setWidget(localeSelector);
            }
            panel.getElement().getStyle().setPadding(1, Unit.EM);
            setBody(panel);
        }

        @Override
        public boolean onClickCancel() {
            return true;
        }

        public CompiledLocale getSelectedLocale() {
            return selectedLocale;
        }
    }
}