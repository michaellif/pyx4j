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
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.boxes.CustomerSelectorDialog;
import com.propertyvista.crm.client.ui.crud.lease.common.YesNoCancelDialog;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public abstract class LeaseTermParticipantFolder<E extends LeaseTermParticipant<?>> extends VistaBoxFolder<E> {

    static final I18n i18n = I18n.get(LeaseTermParticipantFolder.class);

    private Integer ageOfMajority;

    public LeaseTermParticipantFolder(Class<E> clazz, boolean modifiable) {
        super(clazz, modifiable);
        setOrderable(false);
    }

    @Override
    public IFolderItemDecorator<E> createItemDecorator() {
        BoxFolderItemDecorator<E> decor = (BoxFolderItemDecorator<E>) super.createItemDecorator();
        decor.setExpended(isEditable());
        return decor;
    }

    /** Sets age of majority validation, pass <code>null</code> to disable */
    public void setAgeOfMajority(Integer ageOfMajority) {
        this.ageOfMajority = ageOfMajority;
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
                addParticipant();
                return true;
            }
        }.show();
    }

    protected abstract String getAddItemDialogCaption();

    protected abstract String getAddItemDialogBody();

    protected abstract void addParticipants(List<Customer> customers);

    protected abstract void addParticipant();

    /**
     * override in order to supply parent on new Item creation
     * 
     * @return - parent (LeaseTerm) primary Key
     */
    protected Key getParentKey() {
        return null;
    }

    private List<Customer> retrieveExistingCustomers() {
        List<Customer> customers = new ArrayList<Customer>(getValue().size());
        for (LeaseTermParticipant<?> wrapper : getValue()) {
            customers.add(wrapper.leaseParticipant().customer());
        }
        return customers;
    }
}
