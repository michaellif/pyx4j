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
 */
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.MiscUtils;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.boxes.CustomerSelectionDialog;
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

    protected CrmEntityForm<?> getParentForm() {
        return parentForm;
    }

    @Override
    public VistaBoxFolderItemDecorator<E> createItemDecorator() {
        VistaBoxFolderItemDecorator<E> decor = super.createItemDecorator();
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

        new AddParticipantBox() {
            @Override
            public boolean onClickOk() {
                switch (getSelection()) {
                case Existing:
                    new CustomerSelectionDialog(retrieveExistingCustomers()) {
                        @Override
                        public boolean onClickOk() {
                            if (!getSelectedItems().isEmpty()) {
                                addParticipants(new ArrayList<>(getSelectedItems()));
                            }
                            return true;
                        }
                    }.show();
                    break;
                case New:
                    addParticipant();
                    break;
                }

                return true;
            };
        }.show();
    }

    protected abstract String getAddItemDialogCaption();

    protected abstract String getAddItemDialogSelectionText();

    protected abstract void addParticipants(List<Customer> customers);

    protected abstract void addParticipant();

    private Set<Customer> retrieveExistingCustomers() {
        HashSet<Customer> customers = new HashSet<Customer>();
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

    protected enum AddParticipantSelection {
        New, Existing
    }

    private abstract class AddParticipantBox extends OkCancelDialog {

        private final CRadioGroupEnum<AddParticipantSelection> selection = new CRadioGroupEnum<>(AddParticipantSelection.class, RadioGroup.Layout.HORIZONTAL);

        public AddParticipantBox() {
            super(getAddItemDialogCaption());

            selection.populate(AddParticipantSelection.New);

            setBody(createBody());
            setDialogPixelWidth(300);
        }

        protected Widget createBody() {
            VerticalPanel content = new VerticalPanel();
            content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            selection.asWidget().getElement().getStyle().setTextAlign(TextAlign.CENTER);

            content.add(new HTML(getAddItemDialogSelectionText()));
            content.add(selection);

            MiscUtils.setPanelSpacing(content, 8);
            content.setWidth("100%");
            return content.asWidget();
        }

        public AddParticipantSelection getSelection() {
            return selection.getValue();
        }
    }
}
