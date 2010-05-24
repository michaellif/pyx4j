/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Mar 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.domain.crm;

import java.io.Serializable;

public enum Province implements Serializable {

    ON("Ontario"),

    QC("Quebec"),

    NS("Nova Scotia"),

    NB("New Brunswick"),

    MB("Manitoba"),

    BC("British Columbia"),

    PE("Prince Edward Island"),

    SK("Saskatchewan"),

    AB("Alberta"),

    NL("Newfoundland and Labrador"),

    NT("Northwest Territories"),

    YT("Yukon"),

    NU("Nunavut");

    private String str;

    Province() {
    }

    Province(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }

}
