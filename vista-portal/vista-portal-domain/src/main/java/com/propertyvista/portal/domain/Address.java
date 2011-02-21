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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@EmbeddedEntity
@ToStringFormat("{0} {1} {2} {3}, {4} {5}")
public interface Address extends IEntity {

    public enum AddressType {

        property,

        mailing,

        shipping,

        billing,

        current,

        previous,

        forwarding, // on move out this is the address

        legal_notice,

        termination_notice,

        other
    }

    /**
     * (max 100 char)
     */
    @Caption(name = "Address Line 1")
    @ToString(index = 0)
    IPrimitive<String> addressLine1();

    /**
     * (max 100 char)
     */
    @Caption(name = "Address Line 2")
    @ToString(index = 1)
    IPrimitive<String> addressLine2();

    /**
     * (max 60 char)
     */
    @ToString(index = 2)
    IPrimitive<String> city();

    @Caption(name = "Province/State")
    @ToString(index = 3)
    IPrimitive<String> state();

    /**
     * (max 12 char)
     */
    @Caption(name = "Postal/Zip")
    @ToString(index = 4)
    IPrimitive<String> zip();

    @ToString(index = 5)
    Country country();

    IPrimitive<String> countyName();

    //    IPrimitive<GeoPoint> location(); for now the database complains about this unknown type

    IPrimitive<AddressType> addressType();

}
