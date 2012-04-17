/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.steps.welcomewizard;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.person.Name.Prefix;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.person.Person.Sex;

public class WelcomeWizardDemoData {

    public static Person applicantsPerson() {
        Person person = EntityFactory.create(Person.class);
        person.name().namePrefix().setValue(Prefix.Mr);
        person.name().firstName().setValue("Frodo");
        person.name().lastName().setValue("Baggins");
        person.email().setValue("frodob@shire.net");
        person.birthDate().setValue(new LogicalDate(1997 - 1900, 1, 1));
        person.sex().setValue(Sex.Male);
        return person;
    }
}
