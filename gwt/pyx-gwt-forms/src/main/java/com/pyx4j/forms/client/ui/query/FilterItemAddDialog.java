/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2014
 * @author arminea
 */
package com.pyx4j.forms.client.ui.query;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.pyx4j.gwt.commons.ui.ScrollPanel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.OptionGroup.Layout;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.Dialog;

public class FilterItemAddDialog extends Dialog {

    private static final I18n i18n = I18n.get(FilterItemAddDialog.class);

    private final RadioGroup<FilterItem> checkGroup;

    public FilterItemAddDialog(QueryComposer<?> parent) {
        super(i18n.tr("Select Additional Filter Items"));

        checkGroup = new RadioGroup<>(Layout.VERTICAL);
        checkGroup.setFormatter(new IFormatter<FilterItem, SafeHtml>() {

            @Override
            public SafeHtml format(FilterItem value) {
                return SafeHtmlUtils.fromTrustedString(value.toString());
            }
        });

        checkGroup.setHeight("200px");
        checkGroup.setWidth("100%");

        List<FilterItem> options = new ArrayList<>(parent.getOptions());
        options.removeAll(parent.getValue());

        checkGroup.setOptions(options);

        setDialogPixelWidth(300);
        setBody(new ScrollPanel(checkGroup));
    }

    public FilterItem getSelectedItem() {
        return checkGroup.getValue();
    }

}
