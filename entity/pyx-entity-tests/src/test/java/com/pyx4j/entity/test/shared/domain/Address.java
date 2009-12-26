/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain;

import com.pyx4j.entity.shared.IMember;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Address extends IEntity<Address> {

    interface AddressCountry extends Country, IMember<Address> {
    }

    AddressCountry country();

    IPrimitive<String, Address> streetName();
}
