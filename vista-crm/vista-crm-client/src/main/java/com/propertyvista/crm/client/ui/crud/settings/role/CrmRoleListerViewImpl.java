/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.role;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.domain.security.CrmRole;

public class CrmRoleListerViewImpl extends CrmListerViewImplBase<CrmRole> implements CrmRoleListerView {

    public CrmRoleListerViewImpl() {
        setLister(new CrmRoleLister());
    }

    public static class CrmRoleLister extends AbstractLister<CrmRole> {

        public CrmRoleLister() {
            super(CrmRole.class, true);

            setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().name()).build(),
                new MemberColumnDescriptor.Builder(proto().description()).build(),
                new MemberColumnDescriptor.Builder(proto().behaviors()).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }
    }
}
