/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease2;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.boxes.CustomerSelectorDialog;
import com.propertyvista.crm.client.ui.crud.lease.common.YesNoCancelDialog;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseParticipant_2;

public abstract class LeaseParticipantFolder<E extends LeaseParticipant_2> extends VistaBoxFolder<E> {

    static final I18n i18n = I18n.get(LeaseParticipantFolder.class);

    public LeaseParticipantFolder(Class<E> clazz, boolean modifiable) {
        super(clazz, modifiable);
        setOrderable(false);
    }

    @Override
    protected void addItem() {
        new YesNoCancelDialog(getAddItemDialogCaption(), getAddItemDialogBody()) {
            @Override
            public boolean onClickYes() {
                new CustomerSelectorDialog(retrieveExistingCustomers()) {
                    @Override
                    public boolean onClickOk() {
                        if (getSelectedItems().isEmpty()) {
                            return false;
                        } else {
                            addParticipants(getSelectedItems());
                            return true;
                        }
                    }
                }.show();
                return true;
            }

            @Override
            public boolean onClickNo() {
                LeaseParticipantFolder.super.addItem();
                return true;
            }
        }.show();
    }

    protected abstract String getAddItemDialogCaption();

    protected abstract String getAddItemDialogBody();

    protected abstract void addParticipants(List<Customer> customers);

    private List<Customer> retrieveExistingCustomers() {
        List<Customer> customers = new ArrayList<Customer>(getValue().size());
        for (LeaseParticipant_2 wrapper : getValue()) {
            customers.add(wrapper.customer());
        }
        return customers;
    }
}
