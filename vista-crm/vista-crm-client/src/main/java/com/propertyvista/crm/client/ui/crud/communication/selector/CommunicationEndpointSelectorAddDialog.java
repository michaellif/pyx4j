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
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.Collection;
import java.util.Collections;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.CommunicationEndpointDTO;

public class CommunicationEndpointSelectorAddDialog extends Dialog implements OkCancelOption {

    private final SelectRecipientsDialogForm selectForm;

    private final Collection<CommunicationEndpointDTO> alreadySelected;

    private final CommunicationEndpointSelector parent;

    public CommunicationEndpointSelectorAddDialog(CommunicationEndpointSelector parent, Collection<CommunicationEndpointDTO> alreadySelected) {
        super("Select recipients");
        this.setDialogOptions(this);
        this.parent = parent;
        this.alreadySelected = (alreadySelected != null ? alreadySelected : Collections.<CommunicationEndpointDTO> emptyList());
        selectForm = new SelectRecipientsDialogForm();
        setDialogPixelWidth(1000);
        setBody(selectForm);

    }

    public CommunicationEndpointSelectorAddDialog(Collection<CommunicationEndpointDTO> alreadySelected) {
        this(null, (alreadySelected != null ? alreadySelected : Collections.<CommunicationEndpointDTO> emptyList()));

    }

    public CommunicationEndpointSelectorAddDialog() {
        this(null, Collections.<CommunicationEndpointDTO> emptyList());

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

    private void setSelectedItems(Collection<? extends IEntity> eps) {
        if (eps != null && eps.size() > 0) {
            for (IEntity selected : eps) {
                if (!ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(selected.getPrimaryKey())) {
                    addRecipient(selected);
                }
            }
        }
    }

    private void addRecipient(IEntity selected) {
        CommunicationEndpointDTO proto = EntityFactory.create(CommunicationEndpointDTO.class);
        Class<? extends IEntity> epType = selected.getInstanceValueClass();
        if (epType.equals(Building.class)) {
            proto.name().set(((Building) selected).propertyCode());
            proto.type().setValue(ContactType.Building);
            CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
            cg.building().set(selected);
            proto.endpoint().set(cg);
        } else if (epType.equals(Portfolio.class)) {
            proto.name().set(((Portfolio) selected).name());
            proto.type().setValue(ContactType.Portfolio);
            CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
            cg.portfolio().set(selected);
            proto.endpoint().set(cg);
        } else if (epType.equals(Employee.class)) {
            proto.name().setValue(((Employee) selected).name().getStringView());
            proto.type().setValue(ContactType.Employee);
            proto.endpoint().set(selected);
        } else if (epType.equals(Tenant.class)) {
            proto.name().setValue(((Tenant) selected).customer().person().name().getStringView());
            proto.type().setValue(ContactType.Tenant);
            proto.endpoint().set(selected);
        }

        alreadySelected.add(proto);
        updateSelector(parent, alreadySelected);
    }

    private void updateSelector(CommunicationEndpointSelector selector, Collection<CommunicationEndpointDTO> value) {
        selector.setValue(value);
        selector.refresh(true);
    }
}
