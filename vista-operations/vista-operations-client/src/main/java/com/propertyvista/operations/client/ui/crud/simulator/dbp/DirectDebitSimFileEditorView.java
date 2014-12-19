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
 */
package com.propertyvista.operations.client.ui.crud.simulator.dbp;

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;

import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;

public interface DirectDebitSimFileEditorView extends IPrimeEditorView<DirectDebitSimFile> {

    interface Presenter extends IPrimeEditorView.IPrimeEditorPresenter {

    }
}
