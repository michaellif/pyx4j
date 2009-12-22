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
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IOwnedMember;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

public interface Customer extends IObject<Customer> {

    IPrimitive<String, Customer> firstName();

    interface HomeAddress extends Address, IOwnedMember<Customer> {
    }

    HomeAddress homeAddress();

    interface OfficeAddress extends Address, IMember<Customer> {
    }

    OfficeAddress officeAddress();

    ISet<Address, Customer> otherAdresses();

}
