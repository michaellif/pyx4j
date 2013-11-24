/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.CustomerUserVisit;

@SuppressWarnings("serial")
public class ProspectUserVisit extends CustomerUserVisit {

    private OnlineApplication onlineApplication;

    // to make it GWT Serializable ?
    public ProspectUserVisit() {
        super();
    }

    public ProspectUserVisit(VistaApplication application, CustomerUser user) {
        super(application, user);
    }

    public OnlineApplication getOnlineApplication() {
        return onlineApplication;
    }

    public void setOnlineApplication(OnlineApplication onlineApplication) {
        if (onlineApplication != null) {
            this.onlineApplication = onlineApplication.createIdentityStub();
        } else {
            this.onlineApplication = null;
        }
        setChanged();
    }

}
