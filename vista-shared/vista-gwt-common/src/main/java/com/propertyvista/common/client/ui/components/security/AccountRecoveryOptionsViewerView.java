/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import com.pyx4j.site.client.ui.crud.form.IViewerView;

import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public interface AccountRecoveryOptionsViewerView extends IViewerView<AccountRecoveryOptionsDTO> {

    public static final String ARG_PASSWORD = "password";

    void setSecurityQuestionRequired(boolean isSecurityQuestionEntiryRequired);

}
