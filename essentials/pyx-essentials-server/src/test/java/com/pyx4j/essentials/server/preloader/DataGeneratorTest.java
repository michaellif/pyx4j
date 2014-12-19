/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-05-04
 * @author vlads
 */
package com.pyx4j.essentials.server.preloader;

import junit.framework.TestCase;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.pyx4j.commons.LogicalDate;

public class DataGeneratorTest extends TestCase {

    static class RandomDataSet {

        String postalCode;

        String phone;

        LogicalDate date;

        String lastName1;

        String lastName2;

        String streetName;

        String municipality;

        Double latitude;

        void generate() {
            postalCode = DataGenerator.randomPostalCode();
            phone = DataGenerator.randomPhone();
            date = DataGenerator.randomDateInLastYearMonthShifted(100);
            lastName1 = DataGenerator.randomLastName();

            AddressInfo address = DataGenerator.randomAddressInfo();
            streetName = address.streetName().getValue();
            municipality = address.municipality().getValue();
            latitude = address.latitude().getValue();

            lastName2 = DataGenerator.randomLastName();
        }

        @Override
        public boolean equals(Object other) {
            return EqualsBuilder.reflectionEquals(this, other);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public void testConsistentDataCreation() {

        final long seed1 = 250;
        DataGenerator.setRandomSeed(seed1);
        RandomDataSet ds1 = new RandomDataSet();
        ds1.generate();

        // To some other data generation
        DataGenerator.setRandomSeed(System.currentTimeMillis());
        new RandomDataSet().generate();

        // Repeat the first data generation
        DataGenerator.setRandomSeed(seed1);
        RandomDataSet ds2 = new RandomDataSet();
        ds2.generate();

        assertEquals("Same data expected", ds1, ds2);

    }
}
