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
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectLockerAreaListService;
import com.propertyvista.domain.property.asset.LockerArea;

public abstract class LockerAreaSelectionDialog extends EntitySelectorTableDialog<LockerArea> {

    private static final I18n i18n = I18n.get(LockerAreaSelectionDialog.class);

    public LockerAreaSelectionDialog() {
        this(Collections.<LockerArea> emptySet());
    }

    public LockerAreaSelectionDialog(Set<LockerArea> alreadySelected) {
        this(false, alreadySelected, i18n.tr("Select LockerArea"));
    }

    public LockerAreaSelectionDialog(boolean isMultiselect, Set<LockerArea> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select LockerArea"));
    }

    public LockerAreaSelectionDialog(boolean isMultiselect, Set<LockerArea> alreadySelected, String caption) {
        super(LockerArea.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                // unit data
                new ColumnDescriptor.Builder(proto().name()).filterAlwaysShown(true).build(),
                new ColumnDescriptor.Builder(proto().levels()).build(),
                new ColumnDescriptor.Builder(proto().totalLockers()).build(),
                new ColumnDescriptor.Builder(proto().largeLockers()).build(),
                new ColumnDescriptor.Builder(proto().regularLockers()).build(),
                new ColumnDescriptor.Builder(proto().smallLockers()).build()
                ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().building().propertyCode(), false), new Sort(proto().name(), false));
    }

    @Override
    protected AbstractListCrudService<LockerArea> getSelectService() {
        return GWT.<AbstractListCrudService<LockerArea>> create(SelectLockerAreaListService.class);
    }
}