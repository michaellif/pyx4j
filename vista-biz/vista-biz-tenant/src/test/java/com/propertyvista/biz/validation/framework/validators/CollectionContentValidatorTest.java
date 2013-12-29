/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.framework.validators;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.framework.domain.EntityA;
import com.propertyvista.biz.validation.framework.domain.EntityB;

public class CollectionContentValidatorTest {

    @Test
    public void testCollectionsContetnValidator() {
        CollectionContentValidator<EntityB> contentValidator = new CollectionContentValidator<EntityB>(new TestEntityValidator());
        EntityA a = EntityFactory.create(EntityA.class);
        EntityB b1 = a.children().$();
        EntityB b2 = a.children().$();
        EntityB b3 = a.children().$();
        EntityB b4 = a.children().$();

        b1.value().setValue(0);
        b2.value().setValue(1);
        b3.value().setValue(0);
        b4.value().setValue(2);

        a.children().add(b1);
        a.children().add(b2);
        a.children().add(b3);
        a.children().add(b4);

        Set<ValidationFailure> failures = contentValidator.validate(a.children());
        Assert.assertEquals(2, failures.size());
        boolean failureB2HasBeenDiscovered = false;
        boolean failureB4HasBeenDiscovered = false;
        for (ValidationFailure failure : failures) {
            if (b4.equals(failure.getProperty())) {
                failureB2HasBeenDiscovered = true;
            }
            if (b4.equals(failure.getProperty())) {
                failureB4HasBeenDiscovered = true;
            }
        }
        Assert.assertTrue(failureB2HasBeenDiscovered);
        Assert.assertTrue(failureB4HasBeenDiscovered);
    }
}
