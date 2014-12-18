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
package com.propertyvista.crm.client.ui.crud.administration.financial.glcode;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.financial.GlCodeCategory;

public class GlCodeCategoryEditorViewImpl extends CrmEditorViewImplBase<GlCodeCategory> implements GlCodeCategoryEditorView {
    public GlCodeCategoryEditorViewImpl() {
        setForm(new GlCodeCategoryForm(this));
    }
}
