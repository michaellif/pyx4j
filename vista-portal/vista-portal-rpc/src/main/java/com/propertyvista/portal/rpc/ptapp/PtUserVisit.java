/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp;

import com.pyx4j.security.shared.UserVisit;

public class PtUserVisit extends UserVisit {

    private static final long serialVersionUID = -7071001051604793204L;

    private String applicationPrimaryKey;

    protected PtUserVisit() {

    }

    public PtUserVisit(String principalPrimaryKey, String name) {
        super(principalPrimaryKey, name);
    }

    public String getApplicationPrimaryKey() {
        return applicationPrimaryKey;
    }

    public void setApplicationPrimaryKey(String applicationPrimaryKey) {
        this.applicationPrimaryKey = applicationPrimaryKey;
    }

}
