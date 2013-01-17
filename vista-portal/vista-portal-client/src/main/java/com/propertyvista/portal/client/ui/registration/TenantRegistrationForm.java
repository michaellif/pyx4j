/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.registration;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.EntityDataSource;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationDTO;

public class TenantRegistrationForm extends CEntityDecoratableForm<SelfRegistrationDTO> {

    private CEntityComboBox<SelfRegistrationBuildingDTO> buildingOptions;

    public TenantRegistrationForm() {
        super(SelfRegistrationDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        int row = -1;

        buildingOptions = new CEntityComboBox<SelfRegistrationBuildingDTO>(SelfRegistrationBuildingDTO.class);
        buildingOptions.setOptionsDataSource(new EntityDataSource<SelfRegistrationBuildingDTO>() {
            @Override
            public void obtain(EntityQueryCriteria<SelfRegistrationBuildingDTO> criteria,
                    AsyncCallback<EntitySearchResult<SelfRegistrationBuildingDTO>> handlingCallback) {
                // TODO Auto-generated method stub

            }
        });

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().building(), buildingOptions)).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().firstName())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lastName())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().secuirtyCode())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email())).build());
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password(), new CPasswordTextField())).build());

        return contentPanel;
    }

    public void setBuildingOptions(List<SelfRegistrationBuildingDTO> buildings) {
        buildingOptions.setOptions(buildings);
    }
}
