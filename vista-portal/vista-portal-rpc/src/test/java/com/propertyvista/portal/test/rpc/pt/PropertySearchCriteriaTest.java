/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 15, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.test.rpc.pt;

import junit.framework.TestCase;

import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.BedroomRange;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.PriceRange;

public class PropertySearchCriteriaTest extends TestCase {

    public void testPriceRange() {
        assertEquals("PriceRange.Any", "Any", PriceRange.Any.toString());
        assertEquals("PriceRange.lt600", "Less than $599", PriceRange.lt600.toString());
        assertEquals("PriceRange.gt600", "$600 - $799", PriceRange.gt600.toString());
        assertEquals("PriceRange.gt800", "$800 - $999", PriceRange.gt800.toString());
        assertEquals("PriceRange.gt1000", "$1000 - $1199", PriceRange.gt1000.toString());
        assertEquals("PriceRange.gt1200", "Over $1200", PriceRange.gt1200.toString());
    }

    public void testBedroomRange() {
        assertEquals("BedroomRange.Any", "Any", BedroomRange.Any.toString());
        assertEquals("BedroomRange.One", "1", BedroomRange.One.toString());
        assertEquals("BedroomRange.OneOrMore", "1 and more", BedroomRange.OneOrMore.toString());
        assertEquals("BedroomRange.Two", "2", BedroomRange.Two.toString());
        assertEquals("BedroomRange.TwoOrMore", "2 and more", BedroomRange.TwoOrMore.toString());
        assertEquals("BedroomRange.Three", "3", BedroomRange.Three.toString());
        assertEquals("BedroomRange.ThreeOrMore", "3 and more", BedroomRange.ThreeOrMore.toString());
        assertEquals("BedroomRange.Four", "4", BedroomRange.Four.toString());
        assertEquals("BedroomRange.FourOrMore", "4 and more", BedroomRange.FourOrMore.toString());
    }
}
