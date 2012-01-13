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
 * Created on Oct 3, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.domain.test;

import com.pyx4j.entity.shared.EntityFactory;

public class DomainFactory {

    private static int counter = 0;

    private static int poscounter = 0;

    private static int intvalues[] = { 0, 1, 2, 3 };

    private static String optstrvalues[] = { "Optional String 0", "Optional String 1", "Optional String 2", "Optional String 3" };

    private static String mndstrvalues[] = { "Mandatory String 0", "Mandatory String 1", "Mandatory String 2", "Mandatory String 3" };

    private static String opttxtvalues[] = { "Optional Text Area 0", "Optional Text Area 1", "Optional Text Area 2", "Optional Text Area 3" };

    private static String mndtxtvalues[] = { "Mandatory Text Area 0", "Mandatory Text Area 1", "Mandatory Text Area 2", "Mandatory Text Area 3" };

    private static EntityI.Enum1 enumvalues[] = { EntityI.Enum1.Value0, EntityI.Enum1.Value1, EntityI.Enum1.Value2, EntityI.Enum1.Value3 };

    public static EntityI createEntityI() {
        poscounter = 0;
        return createEntityI(0);
    }

    public static EntityI createEntityINext() {

        if (++poscounter >= intvalues.length)
            poscounter = 0;

        return createEntityI(poscounter);
    }

    public static EntityII createEntityII() {
        EntityII retVal = EntityFactory.create(EntityII.class);
        retVal.optionalTextI().setValue("== EntityII Value ==");
        retVal.optionalInteger().setValue(counter++);
        return retVal;
    }

    public static EntityIII createEntityIII() {
        EntityIII retVal = EntityFactory.create(EntityIII.class);
        retVal.stringMember().setValue("== EntityII Value ==");
        retVal.integerMember().setValue(counter++);
        return retVal;
    }

    public static EntityIV createEntityIV() {
        EntityIV retVal = EntityFactory.create(EntityIV.class);
        retVal.stringMember().setValue("== EntityII Value ==");
        retVal.integerMember().setValue(counter++);
        return retVal;
    }

    private static EntityI createEntityI(int pos) {
        EntityI retVal = EntityFactory.create(EntityI.class);

        retVal.optionalTextI().setValue(optstrvalues[pos]);
        retVal.mandatoryTextI().setValue(mndstrvalues[pos]);
        retVal.optionalInteger().setValue(intvalues[pos]);
        retVal.optionalEnum().setValue(enumvalues[pos]);

        retVal.optionalTextAreaII().setValue(opttxtvalues[pos]);
        retVal.mandatoryTextAreaII().setValue(mndtxtvalues[pos]);

        retVal.entityIIList().add(createEntityII());
        retVal.entityIIList().add(createEntityII());
        retVal.entityIIList().add(createEntityII());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());
        return retVal;
    }
}
