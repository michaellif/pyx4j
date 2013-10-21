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
package com.propertyvista.crm.client.ui.crud.administration.financial.arcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.shared.config.VistaFeatures;

public class ARCodeLister extends AbstractLister<ARCode> {

    private static final I18n i18n = I18n.get(ARCodeLister.class);

    public ARCodeLister() {
        super(ARCode.class, true, true);

        List<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>(Arrays.asList(
        //@formatter:off
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor(new Builder(proto().type()).searchable(false).sortable(false).title(i18n.tr("Debit/Credit"))) {
                @Override
                public String convert(IEntity entity) {
                    ARCode.Type type = (ARCode.Type) entity.getMember(getColumnPath()).getValue();
                    if (type != null) {
                    return type.getActionType().toString();
                    } else {
                        return null;
                    }
                }
            },
            new MemberColumnDescriptor.Builder(proto().glCode()).build(),
            new MemberColumnDescriptor.Builder(proto().reserved()).build()
        ));//@formatter:on

        if (VistaFeatures.instance().yardiIntegration()) {
            columnDescriptors.add(new MemberColumnDescriptor.Builder(proto().yardiChargeCodes()).build());
        }

        setColumnDescriptors(columnDescriptors);
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
