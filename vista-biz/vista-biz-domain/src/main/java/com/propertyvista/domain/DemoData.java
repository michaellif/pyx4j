/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain;

import com.pyx4j.commons.CommonsStringUtils;

import com.propertyvista.domain.security.VistaOperationsBehavior;
import com.propertyvista.shared.config.VistaDemo;

public class DemoData {

    public static final boolean vistaDemo = VistaDemo.isDemo();

    public static enum DemoPmc {
        vista, redridge, rockville, star, gondor, demo;
    }

    //We need E-mail delivery during tests. All E-mails goes to us!
    public final static String USERS_DOMAIN = vistaDemo ? "@propertyvista.com" : "@pyx4j.com";

    public static enum UserType {

        PTENANT("p", 6),

        PCOAPPLICANT("cp", 6),

        PGUARANTOR("gp", 4),

        TENANT("t", 4),

        COAPPLICANT("ct", 6),

        GUARANTOR("gt", 4),

        NEW_TENANT("n", 10),

        PM("m", 3),

        EMP("e", 10),

        ADMIN("a", VistaOperationsBehavior.values().length);

        private final String namePrefix;

        private final int defaultMax;

        UserType(String namePrefix, int defaultMax) {
            this.namePrefix = namePrefix;
            this.defaultMax = defaultMax;
        }

        public String getNamePrefix() {
            return namePrefix;
        }

        public int getDefaultMax() {
            return defaultMax;
        }

        public String getEmail(int number) {
            return getNamePrefix() + CommonsStringUtils.d000(number) + DemoData.USERS_DOMAIN;
        }

        public String getEmail(int number, String namespace) {
            return getNamePrefix() + CommonsStringUtils.d000(number) + "." + namespace + DemoData.USERS_DOMAIN;
        }

    }

    public static String applicationModeName() {
        return vistaDemo ? "DEMO" : "DEVELOPMENT";
    }
}
