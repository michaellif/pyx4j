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
package com.propertyvista.admin.client.ui.crud.scheduler.trigger;

import com.propertyvista.admin.client.ui.crud.AdminListerViewImplBase;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class TriggerListerViewImpl extends AdminListerViewImplBase<Trigger> implements TriggerListerView {

    public TriggerListerViewImpl() {
        super(AdminSiteMap.Management.Trigger.class);
        setLister(new TriggerLister());
    }
}
