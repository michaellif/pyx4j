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
package com.propertyvista.crm.client.ui.crud.administration.website.general;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.events.AsyncValueChangeEvent;
import com.pyx4j.forms.client.events.AsyncValueChangeHandler;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.IFormat;
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

    private final Set<AvailableLocale> usedLocales;

    private AvailableLocale selectedLocale;

    public AvailableLocaleSelectorDialog(final Set<AvailableLocale> usedLocales) {
        super(title);
        setDialogPixelWidth(400);
        setDialogOptions(this);

        this.usedLocales = usedLocales;

        final CEntityComboBox<AvailableLocale> localeSelector = new CEntityComboBox<AvailableLocale>(AvailableLocale.class);
        localeSelector.asWidget().setWidth("100%");
        localeSelector.setFormat(new IFormat<AvailableLocale>() {

            @Override
            public AvailableLocale parse(String string) throws ParseException {
                return null;
            }

            @Override
            public String format(AvailableLocale value) {
                return value != null ? value.toString() : title;
            }
        });

        // this triggers option load
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

        setBody(panel);
    }

    private void setContentPanel(CEntityComboBox<AvailableLocale> localeSelector) {
        if (usedLocales != null) {
            final Set<CompiledLocale> clSet = new HashSet<CompiledLocale>();
            for (AvailableLocale al : usedLocales) {
                clSet.add(al.lang().getValue());
            }
            localeSelector.setOptionsFilter(new OptionsFilter<AvailableLocale>() {
                @Override
                public boolean acceptOption(AvailableLocale al) {
                    return !clSet.contains(al.lang().getValue());
                }
            });
        }

        int optSize = localeSelector.getOptions().size();
        if (optSize == 0) {
            panel.setWidget(new Label("Sorry, no more items to choose from."));
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
