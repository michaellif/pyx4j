/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectLeaseAdjustmentReasonListService;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public abstract class LeaseAdjustmentReasonSelectorDialog extends EntitySelectorTableDialog<LeaseAdjustmentReason> {

    private final static I18n i18n = I18n.get(LeaseAdjustmentReasonSelectorDialog.class);

    public LeaseAdjustmentReasonSelectorDialog() {
        this(Collections.<LeaseAdjustmentReason> emptyList());
    }

    public LeaseAdjustmentReasonSelectorDialog(boolean isMultiselect) {
        this(isMultiselect, Collections.<LeaseAdjustmentReason> emptyList());
    }

    public LeaseAdjustmentReasonSelectorDialog(List<LeaseAdjustmentReason> alreadySelected) {
        this(false, alreadySelected);
    }

    public LeaseAdjustmentReasonSelectorDialog(boolean isMultiselect, List<LeaseAdjustmentReason> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Reason"));
    }

    public LeaseAdjustmentReasonSelectorDialog(boolean isMultiselect, List<LeaseAdjustmentReason> alreadySelected, String caption) {
        super(LeaseAdjustmentReason.class, isMultiselect, alreadySelected, caption);
        setWidth("700px");
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().name(), true).build(),
                new MemberColumnDescriptor.Builder(proto().glCode(), true).build(),
                new MemberColumnDescriptor.Builder(proto().actionType(), true).build(),
                new MemberColumnDescriptor.Builder(proto().precalculatedTax(), true).build()

        ); //@formatter:on
    }

    @Override
    protected AbstractListService<LeaseAdjustmentReason> getSelectService() {
        return GWT.<AbstractListService<LeaseAdjustmentReason>> create(SelectLeaseAdjustmentReasonListService.class);
    }
}