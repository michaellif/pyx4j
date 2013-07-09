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
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;

public class LegalTermsDialog extends OkDialog {

    private boolean isLoaded;

    public LegalTermsDialog(AbstractLegalTermsService service, String width, String height, String caption) {
        super(caption);
        final LegalTermsContentViewer termsViewer = new LegalTermsContentViewer(height);
        setBody(termsViewer);
        setSize(width, height);
        service.retrieveLegalTerms(new DefaultAsyncCallback<LegalTermsContent>() {

            @Override
            public void onSuccess(LegalTermsContent result) {
                termsViewer.populate(result);
                isLoaded = true;
            }

            @Override
            public void onFailure(Throwable caught) {
                LegalTermsDialog.this.hide();
                super.onFailure(caught);
            }

        });
    }

    @Override
    public boolean onClickOk() {
        return isLoaded;
    }

}
