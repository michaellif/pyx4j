/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect;

import java.io.Serializable;

import com.pyx4j.commons.Key;

public class ProspectPortalAttributes implements Serializable {

    private static final long serialVersionUID = 1L;

    private Key applicationPrimaryKey;

    protected ProspectPortalAttributes() {

    }

    public Key getApplicationPrimaryKey() {
        return applicationPrimaryKey;
    }

    public void setApplicationPrimaryKey(Key applicationPrimaryKey) {
        this.applicationPrimaryKey = applicationPrimaryKey;
    }
}
