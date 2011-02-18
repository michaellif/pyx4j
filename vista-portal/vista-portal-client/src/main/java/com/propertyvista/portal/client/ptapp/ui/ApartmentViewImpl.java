/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.inject.Singleton;
import com.propertyvista.portal.domain.pt.UnitSelection;

@Singleton
public class ApartmentViewImpl extends WizardStepViewImpl<UnitSelection, ApartmentViewPresenter> implements ApartmentView {

    public ApartmentViewImpl() {
        super(new ApartmentViewForm());
    }

}
