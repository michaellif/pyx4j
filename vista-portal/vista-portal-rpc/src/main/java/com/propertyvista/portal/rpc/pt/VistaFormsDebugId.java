/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.pt;

import com.pyx4j.commons.IDebugId;

public enum VistaFormsDebugId implements IDebugId {

    MainNavigation_Prefix,

    SecondNavigation_Prefix,

    UserMessage_Prefix,

    Available_Units_Change,

    Available_Units_ViewPlan;

    @Override
    public String getDebugIdString() {
        return this.name();
    }

}
