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

import org.junit.Test;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.framework.domain.EntityA;
import com.propertyvista.biz.validation.framework.domain.EntityB;
import com.propertyvista.biz.validation.framework.domain.EntityC;

public class CompositeEntityValidatorTest {

    @Test
    public void testCompositeEntityValidator() {

        CompositeEntityValidator<EntityA> compositeValidator = new CompositeEntityValidator<EntityA>(EntityA.class) {
            @Override
            protected void init() {
                bind(proto().str(), new NotNullValidator());
                bind(proto().child().value(), new NotNullValidator());
                bind(proto().children(), new NotEmptyValidator<EntityB>());
                bind(proto().children(), new TestEntityValidator());
            }
        };

        EntityB b = EntityFactory.create(EntityB.class);
        EntityA a = EntityFactory.create(EntityA.class);

        a.child().set(b);

        EntityB b1 = EntityFactory.create(EntityB.class);
        b1.value().setValue(0);

        a.children().add(b1);
        {
            Set<ValidationFailure> failures = compositeValidator.validate(a);
            // expect a.str() fails because it's null
            // expect a.child().value() fails because it's null           
            Assert.assertEquals(2, failures.size());
        }

        a.child().value().setValue(1);
        {
            Set<ValidationFailure> failures = compositeValidator.validate(a);
            Assert.assertEquals(1, failures.size());

            ValidationFailure failure = failures.iterator().next();
            // expect a.str() fails because it's null
            Assert.assertEquals(a.str(), failure.getProperty());
        }

        a.str().setValue("some value");
        b1.value().setValue(1);
        {
            Set<ValidationFailure> failures = compositeValidator.validate(a);
            Assert.assertEquals(1, failures.size());

            ValidationFailure failure = failures.iterator().next();

            // expect b1 to fail because it's value is not zero
            Assert.assertEquals(b1, failure.getProperty());
        }

        a.children().clear();
        {
            Set<ValidationFailure> failures = compositeValidator.validate(a);
            Assert.assertEquals(1, failures.size());

            ValidationFailure failure = failures.iterator().next();

            // expect a.children fails because it's empty
            Assert.assertEquals(a.children(), failure.getProperty());
        }
    }

    @Test
    public void testCompositeOfComposites() {
        final CompositeEntityValidator<EntityC> compositeChildValidator = new CompositeEntityValidator<EntityC>(EntityC.class) {
            @Override
            protected void init() {
                bind(proto().memberA().value(), new NotNullValidator());
                bind(proto().memberB(), new TestEntityValidator());
            }
        };
        CompositeEntityValidator<EntityA> compositeParentValidator = new CompositeEntityValidator<EntityA>(EntityA.class) {

            @Override
            protected void init() {
                bind(proto().anotherChild(), compositeChildValidator);
            }

        };

        EntityB memberA = EntityFactory.create(EntityB.class);
        memberA.value().setValue(null);

        EntityB memberB = EntityFactory.create(EntityB.class);
        memberB.value().setValue(1);

        EntityC child = EntityFactory.create(EntityC.class);
        child.memberA().set(memberA);
        child.memberB().set(memberB);

        EntityA parent = EntityFactory.create(EntityA.class);
        parent.anotherChild().set(child);

        // TEST
        {
            Set<ValidationFailure> validationFailures = compositeParentValidator.validate(parent);
            Assert.assertEquals(2, validationFailures.size());
        }

        // make it pass Test validation
        memberB.value().setValue(0);
        {
            Set<ValidationFailure> validationFailures = compositeParentValidator.validate(parent);
            Assert.assertEquals(1, validationFailures.size());
        }

        // make it pass NotNull validation
        memberA.value().setValue(1);
        {
            Set<ValidationFailure> validationFailures = compositeParentValidator.validate(parent);
            Assert.assertEquals(0, validationFailures.size());
        }
    }
}
