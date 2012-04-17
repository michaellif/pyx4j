/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.EnumSet;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.dto.LeaseDTO;

public class LeaseLister extends ListerBase<LeaseDTO> {

    private final static I18n i18n = I18n.get(LeaseLister.class);

    public LeaseLister() {
        super(LeaseDTO.class, false, true);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).build(),
            new Builder(proto().type()).build(),

            new Builder(proto().unit().belongsTo().propertyCode()).build(),
            new Builder(proto().unit()).build(),
            
            new Builder(proto().version().status()).build(),
            new Builder(proto().version().completion()).build(),
            
            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),
            
            new Builder(proto().version().expectedMoveIn()).build(),
            new Builder(proto().version().expectedMoveOut(), false).build(),
            new Builder(proto().version().actualMoveIn(), false).build(),
            new Builder(proto().version().actualMoveOut(), false).build(),
            new Builder(proto().version().moveOutNotice(), false).build(),
            
            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().createDate(), false).build(),
            
            new Builder(proto().version().tenants()).build()
        );//@formatter:on
    }

    @Override
    protected EntityListCriteria<LeaseDTO> updateCriteria(EntityListCriteria<LeaseDTO> criteria) {
        criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        criteria.add(PropertyCriterion.in(criteria.proto().version().status(), Lease.Status.current()));
        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        new SelectLeaseTypeDialog().show();
    }

    private LeaseDTO createNewLease(Service.Type leaseType) {
        LeaseDTO newLease = EntityFactory.create(LeaseDTO.class);
        newLease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        newLease.version().status().setValue(Lease.Status.Created);
        newLease.type().setValue(leaseType);
        return newLease;
    }

    private class SelectLeaseTypeDialog extends SelectEnumDialog<Service.Type> implements OkCancelOption {

        public SelectLeaseTypeDialog() {
            super(i18n.tr("Select Lease Type"), EnumSet.allOf(Service.Type.class));
        }

        @Override
        public boolean onClickOk() {
            getPresenter().editNew(getItemOpenPlaceClass(), createNewLease(getSelectedType()));
            return true;
        }

        @Override
        public boolean onClickCancel() {
            return true;
        };

        @Override
        public String defineHeight() {
            return "100px";
        };

        @Override
        public String defineWidth() {
            return "300px";
        }

    }
}
