/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 21, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.legal;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.ui.crud.legal.TermsAndConditionsEditorView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.domain.legal.TermsAndConditions;
import com.propertyvista.admin.rpc.services.TermsAndConditionsCrudService;

public class TermsAndConditionsEditorActivity extends EditorActivityBase<TermsAndConditions> {

    public TermsAndConditionsEditorActivity(CrudAppPlace place) {
        super(place, AdministrationVeiwFactory.instance(TermsAndConditionsEditorView.class), GWT
                .<TermsAndConditionsCrudService> create(TermsAndConditionsCrudService.class), TermsAndConditions.class);
    }

}
