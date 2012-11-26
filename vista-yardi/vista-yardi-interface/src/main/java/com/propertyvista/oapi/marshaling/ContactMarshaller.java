/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.oapi.model.ContactIO;
import com.propertyvista.oapi.xml.StringIO;

public class ContactMarshaller implements Marshaller<PropertyContact, ContactIO> {

    private static class SingletonHolder {
        public static final ContactMarshaller INSTANCE = new ContactMarshaller();
    }

    private ContactMarshaller() {
    }

    public static ContactMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public ContactIO unmarshal(PropertyContact contact) {
        ContactIO contactIO = new ContactIO();
        contactIO.email = new StringIO(contact.email().getValue());
        contactIO.name = contact.name().getValue();
        contactIO.phone = new StringIO(contact.phone().getValue());
        return contactIO;
    }

    @Override
    public PropertyContact marshal(ContactIO contactIO) throws Exception {
        return null;
    }
}
