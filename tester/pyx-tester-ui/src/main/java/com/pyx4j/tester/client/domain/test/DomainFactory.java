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

    public static EntityI createEntityI() {
        EntityI retVal = EntityFactory.create(EntityI.class);
        retVal.optionalTextI().setValue("Optional String Member");
        retVal.mandatoryTextI().setValue("Mandatory String Member");
        retVal.optionalInteger().setValue(counter++);
        retVal.optionalEnum().setValue(EntityI.Enum1.Value0);

        retVal.entityIIList().add(createEntityII());
        retVal.entityIIList().add(createEntityII());
        retVal.entityIIList().add(createEntityII());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());
        retVal.entityIVList().add(createEntityIV());
        return retVal;
    }

    public static EntityII createEntityII() {
        EntityII retVal = EntityFactory.create(EntityII.class);
        retVal.stringMember().setValue("== EntityII Value ==");
        retVal.integerMember().setValue(counter++);
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
}
