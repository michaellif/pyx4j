/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.rpc.services.selections.SelectParkingListService;
import com.propertyvista.domain.property.asset.Parking;

public abstract class ParkingSelectorDialog extends EntitySelectorTableVisorController<Parking> {

    private static final I18n i18n = I18n.get(ParkingSelectorDialog.class);

    public ParkingSelectorDialog(IPane parentView) {
        this(parentView, null);
    }

    public ParkingSelectorDialog(IPane parentView, boolean isMultiselect) {
        this(parentView, isMultiselect, Collections.<Parking> emptySet());
    }

    public ParkingSelectorDialog(IPane parentView, Set<Parking> alreadySelected) {
        this(parentView, alreadySelected != null, alreadySelected, i18n.tr("Select Parking"));
    }

    public ParkingSelectorDialog(IPane parentView, boolean isMultiselect, Set<Parking> alreadySelected) {
        this(parentView, isMultiselect, alreadySelected, i18n.tr("Select Parking"));
    }

    public ParkingSelectorDialog(IPane parentView, boolean isMultiselect, Set<Parking> alreadySelected, String caption) {
        super(parentView, Parking.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                // unit data
                new MemberColumnDescriptor.Builder(proto().name()).build(),
                new MemberColumnDescriptor.Builder(proto().type()).build(),
                new MemberColumnDescriptor.Builder(proto().levels()).build(),
                new MemberColumnDescriptor.Builder(proto().totalSpaces()).build(),
                new MemberColumnDescriptor.Builder(proto().regularSpaces()).build(),
                new MemberColumnDescriptor.Builder(proto().disabledSpaces()).build(),
                new MemberColumnDescriptor.Builder(proto().wideSpaces()).build(),
                new MemberColumnDescriptor.Builder(proto().narrowSpaces()).build()
                ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().building().propertyCode(), false), new Sort(proto().type(), false));
    }

    @Override
    protected AbstractListCrudService<Parking> getSelectService() {
        return GWT.<AbstractListCrudService<Parking>> create(SelectParkingListService.class);
    }
}