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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.feature;

import java.util.EnumSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.common.client.ui.components.dialogs.SelectTypeDialog;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureEditorViewImpl extends CrmEditorViewImplBase<Feature> implements FeatureEditorView {

    public FeatureEditorViewImpl() {
        super(CrmSiteMap.Properties.Feature.class, new FeatureEditorForm());
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Feature.Type> callback) {
        new SelectTypeDialog<Feature.Type>(i18n.tr("Select Feature Type"), EnumSet.allOf(Feature.Type.class)) {
            @Override
            public boolean onClickOk() {
                defaultCaption = getSelectedType().toString();
                callback.onSuccess(getSelectedType());
                return true;
            }
            
            @Override
            public String defineWidth() {return "250px";}
            
            @Override
            public String defineHeight() {return "100px";}
        }.show();
    }
}
