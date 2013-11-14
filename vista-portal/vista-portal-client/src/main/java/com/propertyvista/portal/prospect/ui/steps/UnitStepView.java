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

import com.propertyvista.portal.rpc.portal.prospect.dto.steps.UnitStepDTO;
import com.propertyvista.portal.shared.ui.IWizardStepView;

public interface UnitStepView extends IWizardStepView<UnitStepDTO> {

    interface UnitStepPresenter extends IWizardStepPresenter<UnitStepDTO> {

    }
}
