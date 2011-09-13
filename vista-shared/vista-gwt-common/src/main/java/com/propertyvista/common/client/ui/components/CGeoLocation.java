/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.domain.GeoLocation;

public class CGeoLocation extends CEditableComponent<GeoLocation, NativeGeoLocation> {

    public CGeoLocation() {
    }

    public CGeoLocation(String title) {
        super(title);
    }

    @Override
    protected NativeGeoLocation createWidget() {
        NativeGeoLocation w = new NativeGeoLocation(this);
        return w;
    }

    @Override
    public void onEditingStop() {
        super.onEditingStop();
        if (isValid()) {
            setNativeValue(getValue());
        }
    }
}
