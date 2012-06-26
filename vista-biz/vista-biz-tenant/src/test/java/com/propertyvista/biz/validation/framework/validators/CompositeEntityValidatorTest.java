/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.framework.validators;

import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.validation.framework.CompositeEntityValidator;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.framework.domain.EntityA;
import com.propertyvista.biz.validation.framework.domain.EntityB;

public class CompositeEntityValidatorTest extends TestCase {

    public void testCompositeEntityValidator() {
        CompositeEntityValidator<EntityA> compositeValidator = new CompositeEntityValidator<EntityA>(EntityA.class) {
            @Override
            protected void init() {
                bind(proto().str(), new NotNullValidator());
                bind(proto().child().value(), new NotNullValidator());
            }
        };

        EntityB b = EntityFactory.create(EntityB.class);
        EntityA a = EntityFactory.create(EntityA.class);

        a.child().set(b);

        {
            Set<ValidationFailure<?>> failures = compositeValidator.validate(a);
            Assert.assertEquals(2, failures.size());
        }

        a.child().value().setValue(1);

        {
            Set<ValidationFailure<?>> failures = compositeValidator.validate(a);
            Assert.assertEquals(1, failures.size());

            ValidationFailure<?> failure = failures.iterator().next();
            Assert.assertEquals(a.str(), failure.getProperty());
        }
    }
}
