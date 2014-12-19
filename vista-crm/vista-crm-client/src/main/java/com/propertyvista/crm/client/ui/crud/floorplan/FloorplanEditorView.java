/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;

import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.FloorplanDTO;

public interface FloorplanEditorView extends IPrimeEditorView<FloorplanDTO> {

    interface Presenter extends IPrimeEditorView.IPrimeEditorPresenter {
        void getILSVendors(AsyncCallback<Vector<ILSVendor>> callback, Key buildingId);
    }
}
