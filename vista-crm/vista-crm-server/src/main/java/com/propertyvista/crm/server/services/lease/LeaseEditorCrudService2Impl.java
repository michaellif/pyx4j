/*
 *
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.lease.LeaseEditorCrudService2;
import com.propertyvista.crm.server.services.lease.common.LeaseEditorCrudServiceBase2Impl;
import com.propertyvista.dto.LeaseDTO2;

public class LeaseEditorCrudService2Impl extends LeaseEditorCrudServiceBase2Impl<LeaseDTO2> implements LeaseEditorCrudService2 {

    private final static I18n i18n = I18n.get(LeaseEditorCrudService2Impl.class);

    public LeaseEditorCrudService2Impl() {
        super(LeaseDTO2.class);
    }
}