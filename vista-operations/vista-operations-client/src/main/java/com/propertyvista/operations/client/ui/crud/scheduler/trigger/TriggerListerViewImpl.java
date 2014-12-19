/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.operations.rpc.dto.TriggerDTO;

public class TriggerListerViewImpl extends AbstractListerView<TriggerDTO> implements TriggerListerView {

    public TriggerListerViewImpl() {
        setDataTablePanel(new TriggerLister());
    }
}
