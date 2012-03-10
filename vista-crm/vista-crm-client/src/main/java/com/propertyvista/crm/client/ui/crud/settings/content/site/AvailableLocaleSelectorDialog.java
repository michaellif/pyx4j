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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.shared.CompiledLocale;

public class AvailableLocaleSelectorDialog extends Dialog implements CancelOption {
    private final VerticalPanel panel = new VerticalPanel();

    public AvailableLocaleSelectorDialog(final Set<AvailableLocale> usedLocales, final ValueChangeHandler<AvailableLocale> selectHandler) {
        super("Select Locale");
        setDialogOptions(this);

        CEntityComboBox<AvailableLocale> localeSelector = new CEntityComboBox<AvailableLocale>(AvailableLocale.class);
        localeSelector.setNoSelectionText("Select Locale");
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
        localeSelector.setValueByString("");
        localeSelector.addValueChangeHandler(new ValueChangeHandler<AvailableLocale>() {
            @Override
            public void onValueChange(ValueChangeEvent<AvailableLocale> event) {
                selectHandler.onValueChange(event);
                hide();
            }
        });

        int optSize = localeSelector.getOptions().size();
        if (optSize == 0) {
            panel.add(new Label("Sorry, no more items to choose from."));
        } else {
            panel.add(localeSelector);
            localeSelector.getWidget().getEditor().setVisibleItemCount(optSize + 1);
        }

        setBody(panel);
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

}
