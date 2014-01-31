/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.crm.rpc.services.AbstractLegalTermsService;
import com.propertyvista.shared.rpc.LegalTermTO;

public class LegalTermsDialog extends OkDialog {

    private boolean isLoaded;

    public LegalTermsDialog(AbstractLegalTermsService service, String caption) {
        super(caption);
        final LegalTermsContentViewer termsViewer = new LegalTermsContentViewer("400px");
        setBody(termsViewer);
        setDialogPixelWidth(500);
        service.retrieveLegalTerms(new DefaultAsyncCallback<LegalTermTO>() {

            @Override
            public void onSuccess(LegalTermTO result) {
                termsViewer.populate(result);
                isLoaded = true;
            }

            @Override
            public void onFailure(Throwable caught) {
                LegalTermsDialog.this.hide(false);
                super.onFailure(caught);
            }

        });
    }

    @Override
    public boolean onClickOk() {
        return isLoaded;
    }

}
