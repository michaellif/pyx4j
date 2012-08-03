/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseNewFormBase;
import com.propertyvista.dto.LeaseDTO2;

public class LeaseNewForm extends LeaseNewFormBase<LeaseDTO2> {

    public LeaseNewForm() {
        super(LeaseDTO2.class);
    }

    @Override
    public void createTabs() {
        createCommonContent();
    }
}