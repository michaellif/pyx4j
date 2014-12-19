/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseTermEditorViewImpl extends CrmEditorViewImplBase<LeaseTermDTO> implements LeaseTermEditorView {

    private static final I18n i18n = I18n.get(LeaseTermEditorViewImpl.class);

    public LeaseTermEditorViewImpl() {
        setForm(new LeaseTermForm(this));
    }

    @Override
    public void populate(LeaseTermDTO value) {
        super.populate(value);

        if (EditMode.newItem.equals(mode)) {
            if (value.lease().status().getValue() == Lease.Status.NewLease) {
                setCaption(i18n.tr("New Lease..."));
            } else if (value.lease().status().getValue() == Lease.Status.ExistingLease) {
                setCaption(i18n.tr("New Current Lease..."));
            } else if (value.lease().status().getValue() == Lease.Status.Application) {
                setCaption(i18n.tr("New Lease Application..."));
            }
        } else if (EditMode.existingItem.equals(mode)) {
//            if (value.lease().status().getValue() == Lease.Status.NewLease) {
//                setCaption(i18n.tr("Lease"));
//            } else if (value.lease().status().getValue() == Lease.Status.ExistingLease) {
//                setCaption(i18n.tr("Current Lease"));
//            } else
            if (value.lease().status().getValue() == Lease.Status.Application) {
                setCaption(i18n.tr("Lease Application"));
            }
        }
    }

    @Override
    public void updateBuildingValue(Building value) {
        LeaseTermForm form = (LeaseTermForm) getForm();

        form.get(form.proto().building()).setValue(value);

        // just clear all unit-related data:
        form.get(form.proto().unit()).clear();
        form.get(form.proto().version().utilities()).clear();

        form.getValue().selectedServiceItems().clear();

        resetServiceData();
    }

    @Override
    public void updateUnitValue(LeaseTermDTO value) {
        LeaseTermForm form = (LeaseTermForm) getForm();

        form.get(form.proto().unit()).setValue(value.unit());
        form.get(form.proto().building()).setValue(value.unit().building());
        form.get(form.proto().version().utilities()).setValue(value.version().utilities());

        form.setUnitNote(value.unitMoveOutNote().getValue());
        form.setRestrictions(value, true);

        this.getValue().lease().billingAccount().set(value.lease().billingAccount());

        form.getValue().selectedServiceItems().clear();
        form.getValue().selectedServiceItems().addAll(value.selectedServiceItems());
        if (form.getValue().selectedServiceItems().size() > 1) {
            // in case of multiple services available - clear all service-related data
            // and allow user to select the service he/she wants to use:
            resetServiceData();
        } else {
            updateServiceValue(value);
        }
    }

    @Override
    public void updateServiceValue(LeaseTermDTO value) {
        LeaseTermForm form = (LeaseTermForm) getForm();

        // update non-editable runtime data:
        form.getValue().selectedFeatureItems().clear();
        form.getValue().selectedConcessions().clear();

        form.getValue().selectedFeatureItems().addAll(value.selectedFeatureItems());
        form.getValue().selectedConcessions().addAll(value.selectedConcessions());

        form.get(form.proto().version().leaseProducts().serviceItem()).setValue(value.version().leaseProducts().serviceItem());
        form.get(form.proto().version().leaseProducts().featureItems()).setValue(value.version().leaseProducts().featureItems());
        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            form.get(form.proto().version().leaseProducts().concessions()).setValue(value.version().leaseProducts().concessions());
        }
    }

    private void resetServiceData() {
        LeaseTermForm form = (LeaseTermForm) getForm();

        form.getValue().selectedFeatureItems().clear();
        form.getValue().selectedConcessions().clear();

        form.get(form.proto().version().leaseProducts().serviceItem()).clear();
        form.get(form.proto().version().leaseProducts().featureItems()).clear();
        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            form.get(form.proto().version().leaseProducts().concessions()).clear();
        }
    }
}
