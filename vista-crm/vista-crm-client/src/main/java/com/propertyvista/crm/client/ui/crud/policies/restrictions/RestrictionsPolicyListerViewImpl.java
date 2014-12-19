/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.policies.restrictions;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.services.policies.policy.RestrictionsPolicyCrudService;
import com.propertyvista.domain.policy.dto.RestrictionsPolicyDTO;

public class RestrictionsPolicyListerViewImpl extends AbstractListerView<RestrictionsPolicyDTO> implements RestrictionsPolicyListerView {

    public RestrictionsPolicyListerViewImpl() {
        setDataTablePanel(new RestrictionsPolicyLister());
    }

    public static class RestrictionsPolicyLister extends PolicyListerBase<RestrictionsPolicyDTO> {

        public RestrictionsPolicyLister() {
            super(RestrictionsPolicyDTO.class, GWT.<RestrictionsPolicyCrudService> create(RestrictionsPolicyCrudService.class));

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().nodeType()).sortable(false).build(), //
                    new ColumnDescriptor.Builder(proto().nodeRepresentation()).sortable(false).build(), //
                    new ColumnDescriptor.Builder(proto().occupantsPerBedRoom()).build(), //
                    new ColumnDescriptor.Builder(proto().maxParkingSpots()).build(), //
                    new ColumnDescriptor.Builder(proto().maxLockers()).build(), //
                    new ColumnDescriptor.Builder(proto().maxPets()).build(), //
                    new ColumnDescriptor.Builder(proto().ageOfMajority()).build(), //
                    new ColumnDescriptor.Builder(proto().enforceAgeOfMajority()).build(), // 
                    new ColumnDescriptor.Builder(proto().maturedOccupantsAreApplicants()).build(), //
                    new ColumnDescriptor.Builder(proto().noNeedGuarantors()).build(), //
                    new ColumnDescriptor.Builder(proto().yearsToForcingPreviousAddress()).build());

            setDataTableModel(new DataTableModel<RestrictionsPolicyDTO>());
        }
    }

}
