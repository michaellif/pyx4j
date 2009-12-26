/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.domain;

import com.pyx4j.entity.shared.IMember;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Address extends IEntity<Address> {

    interface AddressCountry extends Country, IMember<Address> {
    }

    AddressCountry country();

    IPrimitive<String, Address> streetName();

}
