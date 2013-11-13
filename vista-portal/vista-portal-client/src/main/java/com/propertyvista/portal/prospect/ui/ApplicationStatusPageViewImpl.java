/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui;

import com.propertyvista.portal.rpc.portal.prospect.dto.ApplicationStatusDTO;
import com.propertyvista.portal.shared.ui.AbstractFormView;

public class ApplicationStatusPageViewImpl extends AbstractFormView<ApplicationStatusDTO> implements ApplicationStatusPageView {

    public ApplicationStatusPageViewImpl() {
        setForm(new ApplicationStatusPage(this));
    }

}
