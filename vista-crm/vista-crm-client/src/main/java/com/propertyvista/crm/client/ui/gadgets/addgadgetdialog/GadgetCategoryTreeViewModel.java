/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 7, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.addgadgetdialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IGadgetDirectory;

class GadgetCategoryTreeViewModel implements TreeViewModel {
    private static final I18n i18n = I18n.get(GadgetCategoryTreeViewModel.class);

    private final IGadgetDirectory directory;

    private final SingleSelectionModel<GadgetCategoryWrapper> selectionModel;

    public GadgetCategoryTreeViewModel(SingleSelectionModel<GadgetCategoryWrapper> selectionModel, IGadgetDirectory directory) {
        assert selectionModel != null : "no selection model provided";
        this.directory = directory;
        this.selectionModel = selectionModel;
    }

    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        GadgetCategoryProvider provider = null;
        if (value == null) {
            // Root node                
            List<IGadgetFactory> supportedGadgets = new ArrayList<IGadgetFactory>(directory.getAvailableGadgets());
            provider = new GadgetCategoryProvider(Arrays.asList(new GadgetCategoryWrapper(i18n.tr("All"), supportedGadgets)));
        } else {
            provider = new GadgetCategoryProvider(((GadgetCategoryWrapper) value).partition());
        }
        return new DefaultNodeInfo<GadgetCategoryWrapper>(provider, new GadgetCategoryCell(), selectionModel, null);
    }

    @Override
    public boolean isLeaf(Object value) {
        return (value instanceof GadgetCategoryWrapper) && !((GadgetCategoryWrapper) value).hasSubCategories();
    }

}