/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;

import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public interface IGadgetInstancePresenter {

    void save(GadgetMetadata gadgetMetadata);

    void retrieve(Key gadgetId, AsyncCallback<GadgetMetadata> callback);
}
