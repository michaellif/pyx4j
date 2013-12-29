/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.propertyvista.integration.yardi.AllYardiTestsSuite;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@RunWith(Categories.class)
@IncludeCategory(FunctionalTests.class)
@Suite.SuiteClasses({ AllYardiTestsSuite.class })
public class FunctionalSuite {

}
