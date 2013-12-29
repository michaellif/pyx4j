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
package com.propertyvista.crm.client.ui.crud.administration.financial.glcode;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.financial.GlCodeCategory;

public class GlCodeCategoryLister extends AbstractLister<GlCodeCategory> {

    private static final I18n i18n = I18n.get(GlCodeCategoryLister.class);

    public GlCodeCategoryLister() {
        super(GlCodeCategory.class, true, true);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().categoryId()).build(),
            new MemberColumnDescriptor.Builder(proto().description()).build(),
            new MemberColumnDescriptor.Builder(proto().glCodes()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().categoryId(), false));
    }
}
