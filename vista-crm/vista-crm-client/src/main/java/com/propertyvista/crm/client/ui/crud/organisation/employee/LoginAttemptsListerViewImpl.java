/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.dto.account.LoginAttemptDTO;

public class LoginAttemptsListerViewImpl extends CrmListerViewImplBase<LoginAttemptDTO> implements LoginAttemptsListerView {

    public LoginAttemptsListerViewImpl() {
        setLister(new LoginAttemptsLister());
    }

    public static class LoginAttemptsLister extends AbstractLister<LoginAttemptDTO> {

        public LoginAttemptsLister() {
            super(LoginAttemptDTO.class);
            setColumnDescriptors(asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().time()).build(),
                    new MemberColumnDescriptor.Builder(proto().remoteAddress()).build(),
                    new MemberColumnDescriptor.Builder(proto().outcome()).build()
            ));//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().time(), true));
        }
    }
}
