/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.gadgets;

import junit.framework.TestCase;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.server.common.gadgets.testdomain.Bar;
import com.propertyvista.server.common.gadgets.testdomain.Foo;

public class GadgetMetadataDiffTest extends TestCase {

    public void testPrimitiveMemberDiff() {

        Foo orig = EntityFactory.create(Foo.class);
        orig.intValue().setValue(5);
        orig.strValue().setValue("original");

        Foo version = orig.duplicate();
        version.strValue().setValue("override");

        Foo diff = GadgetMetadataDiffCalculator.diff(orig, version);

        assertEquals("override", diff.strValue().getValue());
        assertTrue(diff.intValue().isNull());

    }

    public void testChildEntityMemberDiff() {
        Foo orig = EntityFactory.create(Foo.class);
        Bar bar = EntityFactory.create(Bar.class);
        bar.barStrValue().setValue("original");
        orig.bar().set(bar);

        Foo version = orig.duplicate();
        version.bar().barStrValue().setValue("override");

        Foo diff = GadgetMetadataDiffCalculator.diff(orig, version);

        assertEquals("override", diff.bar().barStrValue().getValue());
    }
}
