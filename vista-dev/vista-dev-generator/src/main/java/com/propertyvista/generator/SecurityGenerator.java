/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.generator;

import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Pair;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.security.AbstractUserCredential;

public class SecurityGenerator {

    private static List<Pair<String, String>> qa;

    public static void assignSecurityQuestion(AbstractUserCredential<?> credential) {
        if (qa == null) {
            qa = new Vector<Pair<String, String>>();
            qa.add(new Pair<String, String>("property", "vista"));
            qa.add(new Pair<String, String>("birch", "wood"));
            qa.add(new Pair<String, String>("star", "light"));
            qa.add(new Pair<String, String>("1+1", "2"));
            qa.add(new Pair<String, String>("2x2", "4"));
        }
        Pair<String, String> q = DataGenerator.random(qa);
        credential.securityQuestion().setValue(q.getA());
        credential.securityAnswer().setValue(q.getB());
    }
}
