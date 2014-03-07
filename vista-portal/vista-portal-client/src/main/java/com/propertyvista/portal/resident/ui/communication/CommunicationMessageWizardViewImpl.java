/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.communication;

import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardFormView;

public class CommunicationMessageWizardViewImpl extends AbstractWizardFormView<CommunicationMessageDTO> implements CommunicationMessageWizardView {

    public CommunicationMessageWizardViewImpl() {
        setWizard(new CommunicationMessageWizard(this));
    }

}
