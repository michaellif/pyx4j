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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.widgets.client.CheckGroup;
import com.pyx4j.widgets.client.OptionGroup.Layout;
import com.pyx4j.widgets.client.dialog.Dialog;

public class FilterItemAddDialog extends Dialog {

    private final CheckGroup<ColumnDescriptor> checkGroup;

    public FilterItemAddDialog(FilterPanel parent) {
        super("Select Filter Items");

        checkGroup = new CheckGroup<>(Layout.VERTICAL);

        checkGroup.setHeight("400px");
        checkGroup.setWidth("100%");

        checkGroup.setOptions(parent.getColumnDescriptors());

        List<ColumnDescriptor> descriptors = new ArrayList<>();
        for (FilterItem item : parent.getValue()) {
            descriptors.add(item.getColumnDescriptor());
        }
        checkGroup.setValue(descriptors);

        setDialogPixelWidth(500);
        setBody(checkGroup);
    }

    public Collection<ColumnDescriptor> getSelectedItems() {
        return checkGroup.getValue();
    }

}
