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

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.media.Media;

public interface Company extends IEntity {

    @ToString
    IPrimitive<String> name();

    IList<AddressStructured> addresses();

    IList<Phone> phones();

    IPrimitive<String> website();

    IList<Email> emails();

    //This can't be exported imported now
    @XmlTransient
    IList<OrganizationContacts> contacts();

    @EmbeddedEntity
    @XmlTransient
    Media logo();
}
