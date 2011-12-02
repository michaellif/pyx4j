/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionEditorForm;
import com.propertyvista.domain.financial.offering.Concession;

class ServiceConcessionFolder extends VistaBoxFolder<Concession> {

    private final IListerView<Concession> concessionListerVeiw;

    public ServiceConcessionFolder(boolean modifyable, IListerView<Concession> concessionListerVeiw) {
        super(Concession.class, modifyable);
        this.concessionListerVeiw = concessionListerVeiw;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Concession) {
            return new ConcessionEditorForm(new CrmViewersComponentFactory());
        }
        return super.create(member);

    }

    @Override
    public IFolderItemDecorator<Concession> createItemDecorator() {
        BoxFolderItemDecorator<Concession> decor = (BoxFolderItemDecorator<Concession>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected void addItem() {
        new ShowPopUpBox<SelectConcessionBox>(new SelectConcessionBox()) {
            @Override
            protected void onClose(SelectConcessionBox box) {
                if (box.getSelectedConcessions() != null) {
                    for (Concession item : box.getSelectedConcessions()) {
                        addItem(item);
                    }
                }
            }
        };
    }

    private class SelectConcessionBox extends OkCancelBox {

        private List<Concession> selectedConcessions;

        public SelectConcessionBox() {
            super(i18n.tr("Select Concessions"));
            concessionListerVeiw.getLister().releaseSelection();
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);
            concessionListerVeiw.getLister().addItemSelectionHandler(new ItemSelectionHandler<Concession>() {
                @Override
                public void onSelect(Concession selectedItem) {
                    okButton.setEnabled(!concessionListerVeiw.getLister().getSelectedItems().isEmpty());
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(concessionListerVeiw.asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("700px", "200px");
        }

        @Override
        protected boolean onOk() {
            selectedConcessions = concessionListerVeiw.getLister().getSelectedItems();
            return true;
        }

        @Override
        protected void onCancel() {
            selectedConcessions = null;
        }

        protected List<Concession> getSelectedConcessions() {
            return selectedConcessions;
        }
    }
}