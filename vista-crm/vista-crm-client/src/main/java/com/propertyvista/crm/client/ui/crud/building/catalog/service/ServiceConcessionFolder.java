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

import com.google.gwt.user.client.Command;
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
        final SelectConcessionBox box = new SelectConcessionBox();
        box.run(new Command() {
            @Override
            public void execute() {
                for (Concession item : box.getSelectedItems()) {
                    addItem(item);
                }
            }
        });
    }

    private class SelectConcessionBox extends OkCancelBox {

        private List<Concession> selectedConcessions;

        public SelectConcessionBox() {
            super(i18n.tr("Select Concessions"));
            concessionListerVeiw.getLister().releaseSelection();
            setContent(createContent());
            setSize("700px", "200px");
        }

        protected Widget createContent() {
            getOkButton().setEnabled(false);
            concessionListerVeiw.getLister().addItemSelectionHandler(new ItemSelectionHandler<Concession>() {
                @Override
                public void onSelect(Concession selectedItem) {
                    getOkButton().setEnabled(!concessionListerVeiw.getLister().getSelectedItems().isEmpty());
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(concessionListerVeiw.asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        public boolean onClickOk() {
            selectedConcessions = concessionListerVeiw.getLister().getSelectedItems();
            return super.onClickOk();
        }

        protected List<Concession> getSelectedItems() {
            return selectedConcessions;
        }
    }
}