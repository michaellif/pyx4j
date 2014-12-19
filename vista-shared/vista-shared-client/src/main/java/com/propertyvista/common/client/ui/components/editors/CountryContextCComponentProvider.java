/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-11
 * @author artyom
 */
package com.propertyvista.common.client.ui.components.editors;

import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.domain.ref.ISOCountry;

public class CountryContextCComponentProvider implements PostalCodeFormatter.ICountryContextProvider {

    private final CComponent<?, ISOCountry, ?, ?> component;

    public CountryContextCComponentProvider(CComponent<?, ISOCountry, ?, ?> component) {
        assert component != null;
        this.component = component;
    }

    @Override
    public ISOCountry getCountry() {
        return component.getValue();
    }
}
