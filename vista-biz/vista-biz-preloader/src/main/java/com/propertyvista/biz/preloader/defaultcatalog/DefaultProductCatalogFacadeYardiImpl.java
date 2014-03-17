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
 * Created on 2014-03-17
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.biz.preloader.defaultcatalog;

import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class DefaultProductCatalogFacadeYardiImpl implements DefaultProductCatalogFacade {

    @Override
    public void createFor(Building building) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateFor(Building buildingId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void persistFor(Building building) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addUnit(Building building, AptUnit unit) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateUnit(Building buildingId, AptUnit unit) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillDefaultDeposits(Product<?> entity) {
        DefaultDepositManager.fillDefaultDeposits(entity);
    }
}
