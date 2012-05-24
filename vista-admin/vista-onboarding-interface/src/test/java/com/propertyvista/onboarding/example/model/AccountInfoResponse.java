/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.onboarding.example.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

public class AccountInfoResponse extends Response {

    @XmlElement
    public String accountStatus_TBD;

    @XmlElement(required = true)
    @NotNull
    public String vistaCrmUrl;

    @XmlElement(required = true)
    @NotNull
    public String residentPortalUrl;

    @XmlElement(required = true)
    @NotNull
    public String prospectPortalUrl;
}
