/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.domain.dto.AptUnitDTO;

public class UnitDetailsViewImpl extends SimplePanel implements UnitDetailsView {

    private Presenter presenter;

    private final HTML label;

    public UnitDetailsViewImpl() {
        label = new HTML("Unit Details");
        setWidget(label);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(AptUnitDTO unit) {
        label.setText(unit.getStringView());
    }
}
