/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.common.domain.ref.City;
import com.propertyvista.common.domain.ref.Province;

import com.pyx4j.entity.rpc.GeoCriteria;

public interface SearchApartmentView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {

        public void goToCityMap(Province province);

        public void goToPropertyMap(City city);

        public void goToPropertyMap(GeoCriteria geoCriteria);

    }

}
