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
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.ArrayList;
import java.util.Collection;

import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.dto.CommunicationEndpointDTO;

public class CommunicationEndpointSelectorAddDialog extends Dialog implements OkCancelOption {

    private final SelectRecipientsDialogForm selectForm;

    private final Collection<CommunicationEndpointDTO> alreadySelected;

    private final CommunicationEndpointSelector parent;

    public CommunicationEndpointSelectorAddDialog(CommunicationEndpointSelector parent) {
        super("Select recipients");
        this.parent = parent;
        alreadySelected = (parent.getValue() != null ? parent.getValue() : new ArrayList<CommunicationEndpointDTO>());
        selectForm = new SelectRecipientsDialogForm(alreadySelected);
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

    private void setSelectedItems(Collection<CommunicationEndpointDTO> eps) {
        if (alreadySelected != null) {
            alreadySelected.clear();
        }
        if (eps != null && eps.size() > 0) {
            alreadySelected.addAll(eps);
        }
        updateSelector(parent, alreadySelected);
    }

    private void updateSelector(CommunicationEndpointSelector selector, Collection<CommunicationEndpointDTO> value) {
        selector.setValue(value);
        selector.refresh(true);
    }
}
