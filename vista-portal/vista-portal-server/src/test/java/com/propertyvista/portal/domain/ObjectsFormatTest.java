/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain;

import junit.framework.TestCase;

import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.util.DomainUtil;

import com.pyx4j.entity.shared.EntityFactory;

public class ObjectsFormatTest extends TestCase {

    public void testMoneyFormat() {
        assertEquals("10", "$10.00", DomainUtil.createMoney(10).getStringView());
    }

    public void testChargeLineFormat() {
        ChargeLine cl = EntityFactory.create(ChargeLine.class);
        cl.charge().set(DomainUtil.createMoney(10));
        cl.label().setValue("Monthly");
        assertEquals("10", "$10.00 Monthly", cl.getStringView());
    }
}
