/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc;

import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.shared.VistaUserVisit;

@SuppressWarnings("serial")
public class CrmUserVisit extends VistaUserVisit<CrmUser> {

    // to make it GWT Serializable ?
    public CrmUserVisit() {
        super();
    }

    public CrmUserVisit(VistaApplication application, CrmUser user) {
        super(application, user);
    }

    @Override
    public String toString() {
        return "CRM " + super.toString();
    }
}
