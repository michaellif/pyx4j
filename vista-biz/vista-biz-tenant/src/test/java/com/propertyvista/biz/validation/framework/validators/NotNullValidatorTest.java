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

import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.framework.domain.EntityA;

public class NotNullValidatorTest extends TestCase {

    public void testNotNullValidator() {

        EntityA a = EntityFactory.create(EntityA.class);

        NotNullValidator notNullValidator = new NotNullValidator();

        {
            Set<ValidationFailure<?>> failures = notNullValidator.validate(a.str());
            Assert.assertEquals("must have one failure", 1, failures.size());
        }

        a.str().setValue("test");

        {
            Set<ValidationFailure<?>> failures = notNullValidator.validate(a.str());
            Assert.assertEquals(0, failures.size());
        }

    }
}
