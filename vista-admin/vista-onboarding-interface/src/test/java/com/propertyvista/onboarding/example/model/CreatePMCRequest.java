/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding.example.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

public class CreatePMCRequest extends Request {

    @NotNull
    @XmlElement(required = true)
    public String name;

    /**
     * May contain up to 63 characters. The characters allowed in a label are a subset of the ASCII character set, and includes the characters a through z, A
     * through Z, digits 0 through 9, and
     * the hyphen.
     */
    @XmlElement(required = true, name = "dnsName")
    // name="" is  hack for jaxb  fields ending with Id
    @Size(max = 64)
    @Pattern(regexp = "[A-Za-z0-9]+")
    public String dnsName;

    @XmlElementWrapper
    @XmlElements({ @XmlElement(name = "item") })
    public List<String> dnsNameAliases;
}
