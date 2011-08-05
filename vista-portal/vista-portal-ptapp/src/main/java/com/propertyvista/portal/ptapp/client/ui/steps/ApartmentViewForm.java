/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;

public class ApartmentViewForm extends CEntityForm<UnitSelection> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentViewForm.class);

    private ApartmentViewPresenter presenter;

    private final AptUnitDTO selectedUnit = EntityFactory.create(AptUnitDTO.class);

    public ApartmentViewForm() {
        super(UnitSelection.class, new VistaEditorsComponentFactory());
    }

    public void setPresenter(ApartmentViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        // last step - add building picture on the right:
        HorizontalPanel content = new HorizontalPanel();
        content.add(new BuildingPicture());
        return content;
    }

    @Override
    public void populate(UnitSelection value) {
        super.populate(value);
    }

}
