/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertvista.generator.III;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;

public class Test {

    public void test() {
        PersistableDataFactory factory = new PersistableDataFactory(new BuildingGenerator());

        // Lease test
        {
            Building buildingA = factory.get(Building.class);

            AptUnit unit = factory.create(AptUnit.class, buildingA);

            AptUnit unit2 = factory.get(AptUnit.class);
            //unit2 === unit;

            AptUnit unit3 = factory.create(AptUnit.class);
            //units3 != unit2

            // New building with new units,
            factory.create(Building.class);
            factory.get(AptUnit.class);

        }

        {
            AptUnit unit = factory.getPersisted(AptUnit.class);
            AptUnit unit2 = factory.create(AptUnit.class);

            Customer tenant1 = factory.getPersisted(Customer.class);
            Customer tenant2 = factory.getPersisted(Customer.class);

            Lease lease = factory.create(Lease.class, unit, tenant1, tenant2);
            // laseCrudService.create(lease,,,, );
        }
    }

}
