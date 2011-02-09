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
 * Created on Feb 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import java.util.List;

import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.essentials.client.crud.EntityListWithCriteriaWidget;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.gwt.geo.MapUtils;

public class CustomerListWidget extends EntityListWithCriteriaWidget<Customer> {

    public CustomerListWidget() {
        super(Customer.class, ExamplesSiteMap.Crm.Customers.class, ExamplesSiteMap.Crm.Customers.Edit.class, new CustomerSearchCriteriaPanel(),
                new CustomerSearchResultsPanel());
    }

    @Override
    protected void populateData(List<Customer> entities, EntitySearchCriteria<Customer> criteria, int pageNumber, boolean hasMoreData) {
        super.populateData(entities, criteria, pageNumber, hasMoreData);

        GeoCriteria geoCriteria = (GeoCriteria) criteria.getValue(new PathSearch(criteria.proto().locationCriteria()));
        Integer areaRadius = (Integer) criteria.getValue(new PathSearch(criteria.proto().locationCriteria().radius()));
        if ((areaRadius != null) && (geoCriteria != null) && (!geoCriteria.geoPoint().isNull())) {
            ((CustomerSearchResultsPanel) getSearchResultsPanel()).setDistanceOverlay(MapUtils.newLatLngInstance(geoCriteria.geoPoint().getValue()),
                    areaRadius.intValue());
        } else {
            ((CustomerSearchResultsPanel) getSearchResultsPanel()).setDistanceOverlay(null, 0);
        }
    }

}
