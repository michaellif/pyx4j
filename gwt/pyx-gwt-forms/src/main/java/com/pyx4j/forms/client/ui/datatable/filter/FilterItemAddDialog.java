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

import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

public class FilterItemAddDialog extends Dialog implements OkCancelOption {

    private final SelectFilterItemDialogForm selectForm;

    private final Collection<FilterItem> alreadySelected;

    private final FilterPanel parent;

    public FilterItemAddDialog(FilterPanel parent, DataTablePanel<?> dataTablePanel) {
        super("Select Filter Items");
        this.parent = parent;
        alreadySelected = parent.getValue() != null ? parent.getValue() : new ArrayList<FilterItem>();
        selectForm = new SelectFilterItemDialogForm(alreadySelected, dataTablePanel);
        setDialogOptions(this);
        setDialogPixelWidth(1000);
        setBody(selectForm);
    }

    @Override
    public boolean onClickCancel() {
        this.hide(true);
        return true;
    }

    @Override
    public boolean onClickOk() {
        setSelectedItems(selectForm.getSelectedItems());
        this.hide(true);
        return true;
    }

    private void setSelectedItems(Collection<FilterItem> eps) {
        if (alreadySelected != null) {
            alreadySelected.clear();
        }
        if (eps != null && eps.size() > 0) {
            alreadySelected.addAll(eps);
        }
        updateSelector(parent, alreadySelected);
    }

    private void updateSelector(FilterPanel parent, Collection<FilterItem> value) {
        parent.setValue(value);
    }
}
