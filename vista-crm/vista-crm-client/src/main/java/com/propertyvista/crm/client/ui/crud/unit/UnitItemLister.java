/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemLister extends AbstractLister<AptUnitItem> {

    public UnitItemLister() {
        super(AptUnitItem.class, true);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor.Builder(proto().description()).build(),
            new MemberColumnDescriptor.Builder(proto().flooringType()).build(),
            new MemberColumnDescriptor.Builder(proto().cabinetsType()).build(),
            new MemberColumnDescriptor.Builder(proto().counterTopType()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().type().getPath().toString(), false));
    }
}
