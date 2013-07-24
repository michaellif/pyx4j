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
package com.propertyvista.operations.client.ui.crud.simulator.dbp;

import com.pyx4j.site.client.ui.prime.form.IEditor;

import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;

public interface DirectDebitSimFileEditorView extends IEditor<DirectDebitSimFile> {

    interface Presenter extends IEditor.Presenter {

    }
}
