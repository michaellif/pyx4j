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
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.boxes.CustomerSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public abstract class LeaseTermParticipantFolder<E extends LeaseTermParticipant<?>> extends VistaBoxFolder<E> {

    static final I18n i18n = I18n.get(LeaseTermParticipantFolder.class);

    private Integer ageOfMajority = 18;

    private Boolean enforceAgeOfMajority = false;

    private final CrmEntityForm<?> parentForm;

    public LeaseTermParticipantFolder(Class<E> clazz, CrmEntityForm<?> parentForm) {
        super(clazz, parentForm.isEditable());
        this.parentForm = parentForm;
        setOrderable(false);
    }

    @Override
    public IFolderItemDecorator<E> createItemDecorator() {
        BoxFolderItemDecorator<E> decor = (BoxFolderItemDecorator<E>) super.createItemDecorator();
        decor.setExpended(isEditable());
        return decor;
    }

    public Integer getAgeOfMajority() {
        return this.ageOfMajority;
    }

    /** enables age of majority validation, pass <code>null</code> to disable validation */
    public void setAgeOfMajority(Integer ageOfMajority) {
        this.ageOfMajority = ageOfMajority;
    }

    public Boolean getEnforceAgeOfMajority() {
        return enforceAgeOfMajority;
    }

    public void setEnforceAgeOfMajority(Boolean enforceAgeOfMajority) {
        this.enforceAgeOfMajority = enforceAgeOfMajority;
    }

    @Override
    protected void addItem() {
        MessageDialog.confirm(getAddItemDialogCaption(), getAddItemDialogBody(), new Command() {
            @Override
            public void execute() {
                new CustomerSelectorDialog(parentForm.getParentView(), retrieveExistingCustomers()) {
                    @Override
                    public void onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            addParticipants(getSelectedItems());
                        }
                    }
                }.show();
            }
        }, new Command() {
            @Override
            public void execute() {
                addParticipant();
            }
        }, null);
    }

    protected abstract String getAddItemDialogCaption();

    protected abstract String getAddItemDialogBody();

    protected abstract void addParticipants(List<Customer> customers);

    protected abstract void addParticipant();

    private List<Customer> retrieveExistingCustomers() {
        List<Customer> customers = new ArrayList<Customer>();
        customers.addAll(retrieveCurrentCustomers());
        customers.addAll(retrieveConcurrentCustomers());
        return customers;
    }

    public List<Customer> retrieveCurrentCustomers() {
        List<Customer> customers = new ArrayList<Customer>(getValue().size());
        for (LeaseTermParticipant<?> wrapper : getValue()) {
            customers.add(wrapper.leaseParticipant().customer());
        }
        return customers;
    }

    protected List<Customer> retrieveConcurrentCustomers() {
        return Collections.emptyList();
    }
}
