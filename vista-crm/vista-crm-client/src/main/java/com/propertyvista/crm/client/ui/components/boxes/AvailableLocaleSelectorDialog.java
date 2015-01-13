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
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.events.AsyncValueChangeEvent;
import com.pyx4j.forms.client.events.AsyncValueChangeHandler;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.OptionsFilter;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.shared.i18n.CompiledLocale;

public abstract class AvailableLocaleSelectorDialog extends Dialog implements OkCancelOption {
    private final static I18n i18n = I18n.get(AvailableLocaleSelectorDialog.class);

    private final static String title = i18n.tr("Select Locale");

    private final SimplePanel panel = new SimplePanel();

    private final Set<CompiledLocale> usedLocales;

    private AvailableLocale selectedLocale;

    private static Set<CompiledLocale> toCompiledLocale(Collection<AvailableLocale> locales) {
        Set<CompiledLocale> result = new HashSet<>();
        for (AvailableLocale al : locales) {
            result.add(al.lang().getValue());
        }
        return result;
    }

    public AvailableLocaleSelectorDialog() {
        this((Set<CompiledLocale>) null);
    }

    public AvailableLocaleSelectorDialog(final Collection<AvailableLocale> usedLocales) {
        this(toCompiledLocale(usedLocales));
    }

    public AvailableLocaleSelectorDialog(final Set<CompiledLocale> usedLocales) {
        super(title);
        setDialogPixelWidth(400);
        setDialogOptions(this);

        this.usedLocales = usedLocales;

        final CEntityComboBox<AvailableLocale> localeSelector = new CEntityComboBox<AvailableLocale>(AvailableLocale.class);

        // this triggers option load
        localeSelector.populate(null);
        localeSelector.setValueByString("");
        if (localeSelector.isOptionsLoaded()) {
            setContentPanel(localeSelector);
        } else {
            // this handler will fire when options loaded
            localeSelector.addAsyncValueChangeHandler(new AsyncValueChangeHandler<AvailableLocale>() {
                @Override
                public void onAsyncChange(AsyncValueChangeEvent<AvailableLocale> event) {
                    setContentPanel(localeSelector);
                }
            });
        }

        localeSelector.addValueChangeHandler(new ValueChangeHandler<AvailableLocale>() {
            @Override
            public void onValueChange(ValueChangeEvent<AvailableLocale> event) {
                selectedLocale = event.getValue();
            }
        });

        localeSelector.setFormat(new IFormatter<AvailableLocale, String>() {
            @Override
            public String format(AvailableLocale value) {
                return (value != null ? value.toString() : title);
            }
        });

        localeSelector.asWidget().setWidth("100%");
        panel.getElement().getStyle().setPadding(1, Unit.EM);
        setBody(panel);
    }

    private void setContentPanel(CEntityComboBox<AvailableLocale> localeSelector) {
        if (usedLocales != null) {
            localeSelector.setOptionsFilter(new OptionsFilter<AvailableLocale>() {
                @Override
                public boolean acceptOption(AvailableLocale al) {
                    return !usedLocales.contains(al.lang().getValue());
                }
            });
        }

        int optSize = localeSelector.getOptions().size();
        if (optSize == 0) {
            panel.setWidget(new Label("Sorry, no more Locales to choose from."));
            getOkButton().setVisible(false);
        } else {
            panel.setWidget(localeSelector);
        }
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

    public AvailableLocale getSelectedLocale() {
        return selectedLocale;
    }
}
