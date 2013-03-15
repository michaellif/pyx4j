/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.onboardingusers;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.AbstractLister;

import com.propertyvista.operations.client.ui.crud.OperationsListerViewImplBase;
import com.propertyvista.operations.rpc.OnboardingUserDTO;

public class OnboardingUserListerViewImpl extends OperationsListerViewImplBase<OnboardingUserDTO> implements OnboardingUserListerView {

    public OnboardingUserListerViewImpl() {
        setLister(new OnboardingUserLister());
    }

    private static class OnboardingUserLister extends AbstractLister<OnboardingUserDTO> {

        public OnboardingUserLister() {
            super(OnboardingUserDTO.class, true);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().email()).build(),
                    new MemberColumnDescriptor.Builder(proto().enabled()).build(),
                    new MemberColumnDescriptor.Builder(proto().onboardingAccountId()).build(),
                    new MemberColumnDescriptor.Builder(proto().pmc().namespace()).title("Pmc").build(),
                    new MemberColumnDescriptor.Builder(proto().pmcStatus()).build(),
                    new MemberColumnDescriptor.Builder(proto().role()).build(),
                    new MemberColumnDescriptor.Builder(proto().created()).build(),
                    new MemberColumnDescriptor.Builder(proto().credentialUpdated()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().passwordUpdated()).visible(false).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().pmc().namespace().getPath().toString(), false), new Sort(proto().name().getPath().toString(), false));
        }
    }
}
