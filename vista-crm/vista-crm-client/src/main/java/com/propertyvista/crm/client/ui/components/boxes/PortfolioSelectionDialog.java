/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2014
 * @author smolka
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
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;

public abstract class PortfolioSelectionDialog extends EntitySelectorTableDialog<Portfolio> {
    private static final I18n i18n = I18n.get(PortfolioSelectionDialog.class);

    public PortfolioSelectionDialog() {
        this(Collections.<Portfolio> emptySet());
    }

    public PortfolioSelectionDialog(Set<Portfolio> alreadySelected) {
        this(false, alreadySelected);
    }

    public PortfolioSelectionDialog(boolean isMultiselect, Set<Portfolio> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Portfolio"));
    }

    public PortfolioSelectionDialog(boolean isMultiselect, Set<Portfolio> alreadySelected, String caption) {
        super(Portfolio.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().name()).build(),
                new MemberColumnDescriptor.Builder(proto().description()).build()
        ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }

    @Override
    protected AbstractListCrudService<Portfolio> getSelectService() {
        return GWT.<AbstractListCrudService<Portfolio>> create(SelectPortfolioListService.class);
    }
}
