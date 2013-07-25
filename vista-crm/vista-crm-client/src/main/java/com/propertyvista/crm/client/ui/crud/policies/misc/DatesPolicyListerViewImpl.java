/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.misc;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.domain.policy.dto.DatesPolicyDTO;

public class DatesPolicyListerViewImpl extends CrmListerViewImplBase<DatesPolicyDTO> implements DatesPolicyListerView {

    public DatesPolicyListerViewImpl() {
        setLister(new DatesPolicyLister());
    }

    public static class DatesPolicyLister extends PolicyListerBase<DatesPolicyDTO> {

        public DatesPolicyLister() {
            super(DatesPolicyDTO.class);
            setColumnDescriptors( // @formatter:off
                    new MemberColumnDescriptor.Builder(proto().yearRangeFutureSpan()).build(), 
                    new MemberColumnDescriptor.Builder(proto().yearRangeStart()).build() 
            ); // @formatter:on

        }
    }
}
