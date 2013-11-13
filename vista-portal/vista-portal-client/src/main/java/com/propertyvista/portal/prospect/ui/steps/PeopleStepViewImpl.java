/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.steps;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.prospect.dto.PeopleStepDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardStepView;

public class PeopleStepViewImpl extends AbstractWizardStepView<PeopleStepDTO> implements PeopleStepView {

    private static final I18n i18n = I18n.get(PeopleStepViewImpl.class);

    public PeopleStepViewImpl() {
        super();
        setWizardStep(new PeopleStep(this));
    }

}
