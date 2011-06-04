/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 3, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.domain.dto.PropertyDTO;

import com.pyx4j.entity.client.ui.flex.CEntityForm;

public class PropertyForm extends CEntityForm<PropertyDTO> implements PropertyMapView {

    PropertyMapView.Presenter presenter;

    public PropertyForm(Class<PropertyDTO> clazz) {
        super(PropertyDTO.class);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void populate(List<PropertyDTO> properties) {
        // super.populate(properties);

    }

    @Override
    public IsWidget createContent() {
        // TODO Auto-generated method stub
        return null;
    }

}
