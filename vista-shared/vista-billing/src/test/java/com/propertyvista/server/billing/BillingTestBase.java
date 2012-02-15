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
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.server.billing.preload.BuildingGenerator;
import com.propertyvista.server.billing.preload.ProductItemTypesGenerator;
import com.propertyvista.server.billing.preload.RDBDataModel;

public class BillingTestBase extends VistaDBTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RDBDataModel model = new RDBDataModel();

        model.persist(ProductItemTypesGenerator.generate(model));

        model.persist(BuildingGenerator.generate(model));

//        model.persist(TenantGenerator.generate(model));

//        model.persist(LeaseGenerator.generate(model));

    }
}
