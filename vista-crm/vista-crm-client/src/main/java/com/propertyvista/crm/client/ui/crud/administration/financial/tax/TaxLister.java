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
package com.propertyvista.crm.client.ui.crud.administration.financial.tax;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.lister.EntityDataTablePanel;

import com.propertyvista.domain.financial.tax.Tax;

public class TaxLister extends EntityDataTablePanel<Tax> {

    public TaxLister() {
        super(Tax.class, true, true);

        setDataTableModel(new DataTableModel<Tax>(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().authority()).build(),
            new MemberColumnDescriptor.Builder(proto().rate()).build(),
            new MemberColumnDescriptor.Builder(proto().compound()).build()
        ));//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
