/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.dashboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public abstract class DashboardSelectorDialog extends EntitySelectorTableDialog<DashboardMetadata> {

    private static final I18n i18n = I18n.get(DashboardSelectorDialog.class);

    public DashboardSelectorDialog() {
        super(DashboardMetadata.class, false, Collections.<DashboardMetadata> emptyList(), i18n.tr("Select Dashboard..."));
        setFilters(Arrays.<Criterion> asList(PropertyCriterion.eq(proto().type(), DashboardMetadata.DashboardType.building)));
    }

    @Override
    protected AbstractListService<DashboardMetadata> getSelectService() {
        return GWT.create(DashboardMetadataCrudService.class);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().name()).build(),
                new MemberColumnDescriptor.Builder(proto().description()).build(),
                new MemberColumnDescriptor.Builder(proto().type()).build(),
                new MemberColumnDescriptor.Builder(proto().isFavorite()).build(),
                new MemberColumnDescriptor.Builder(proto().isShared()).build()
        );//@formatter:on
    }
}
