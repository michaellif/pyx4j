/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.company;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;

public interface Company extends IEntity {

    @ToString
    @Length(128)
    IPrimitive<String> name();

    //TODO Make it work in next version. For now we don't add this to DB structure
    @Transient
    IList<AddressStructured> addresses();

    IList<CompanyPhone> phones();

    IPrimitive<String> website();

    IList<CompanyEmail> emails();

    //This can't be exported imported now
    @XmlTransient
    IList<OrganizationContacts> contacts();

    @XmlTransient
    @Owned
    CompanyLogo logo();
}
