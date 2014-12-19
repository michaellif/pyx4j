/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.policies.n4;

import java.util.List;

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;

public interface N4PolicyEditorView extends IPrimeEditorView<N4PolicyDTO> {

    void setARCodeOptions(List<ARCode> arCodeOptions);
}
