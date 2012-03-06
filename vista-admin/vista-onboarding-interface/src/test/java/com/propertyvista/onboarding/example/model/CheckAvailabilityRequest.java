/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding.example.model;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

public class CheckAvailabilityRequest extends Request {

    @Size(max = 64)
    @Pattern(regexp = "[A-Za-z0-9]+")
    @XmlElement
    public String pmcId;

}
