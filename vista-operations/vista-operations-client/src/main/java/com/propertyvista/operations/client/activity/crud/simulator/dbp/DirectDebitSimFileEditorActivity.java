/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.simulator.dbp;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimFileEditorView;
import com.propertyvista.operations.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimFileCrudService;

public class DirectDebitSimFileEditorActivity extends AbstractEditorActivity<DirectDebitSimFile> implements DirectDebitSimFileEditorView.Presenter {

    public DirectDebitSimFileEditorActivity(CrudAppPlace place) {
        super(place, AdministrationVeiwFactory.instance(DirectDebitSimFileEditorView.class), GWT
                .<DirectDebitSimFileCrudService> create(DirectDebitSimFileCrudService.class), DirectDebitSimFile.class);
    }
}
