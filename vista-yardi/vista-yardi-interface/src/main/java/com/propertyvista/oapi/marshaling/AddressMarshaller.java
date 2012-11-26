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

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.xml.StringIO;

public class AddressMarshaller implements Marshaller<AddressStructured, AddressIO> {

    private static class SingletonHolder {
        public static final AddressMarshaller INSTANCE = new AddressMarshaller();
    }

    private AddressMarshaller() {
    }

    public static AddressMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public AddressIO unmarshal(AddressStructured address) {
        AddressIO addressIO = new AddressIO();
        addressIO.country = new StringIO(address.country().name().getValue());
        addressIO.province = new StringIO(address.province().name().getValue());
        addressIO.city = new StringIO(address.city().getValue());
        addressIO.postalCode = new StringIO(address.postalCode().getValue());
        addressIO.streetNumber = new StringIO(address.streetNumber().getValue());
        addressIO.streetName = new StringIO(address.streetName().getValue());
        addressIO.streetType = new StringIO(address.streetType().getValue().toString());
        return addressIO;
    }

    @Override
    public AddressStructured marshal(AddressIO addressIO) throws Exception {
        return null;
    }
}
