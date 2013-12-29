/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface LeadsAndRentalsGadgetDataDTO extends IEntity {

    @Caption(name = "Leads This Month")
    IPrimitive<Integer> leads();

    IPrimitive<Integer> appointments();

    IPrimitive<Integer> rentals();

    IPrimitive<Double> leadToAppoinmentsRatio();

    IPrimitive<Double> rentalsToAppointmetsRatio();

    /** this is for presentation part only: filled on client side */
    @Caption(name = "Appointments")
    IPrimitive<String> appointmentsLabel();

    /** this is for presentation part only: filled on client side */
    @Caption(name = "Rentals")
    IPrimitive<String> rentalsLabel();

}
