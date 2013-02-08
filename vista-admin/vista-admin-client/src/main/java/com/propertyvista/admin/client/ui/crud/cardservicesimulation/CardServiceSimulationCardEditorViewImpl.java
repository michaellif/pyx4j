/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.cardservicesimulation;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.admin.client.ui.crud.AdminEditorViewImplBase;
import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.dev.CardServiceSimulationCard;
import com.propertyvista.admin.domain.dev.CardServiceSimulationToken;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;

public class CardServiceSimulationCardEditorViewImpl extends AdminEditorViewImplBase<CardServiceSimulationCard> implements CardServiceSimulationCardEditorView {

    private static class CardServiceSimulationTokenTableFolder extends VistaTableFolder<CardServiceSimulationToken> {

        private static List<EntityFolderColumnDescriptor> COLUMNS;
        static {
            CardServiceSimulationToken p = EntityFactory.getEntityPrototype(CardServiceSimulationToken.class);
            COLUMNS = Arrays.asList(new EntityFolderColumnDescriptor(p.token(), "25em"));
        }

        public CardServiceSimulationTokenTableFolder() {
            super(CardServiceSimulationToken.class);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return COLUMNS;
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof CardServiceSimulationToken) {
                return new CEntityFolderRowEditor<CardServiceSimulationToken>(CardServiceSimulationToken.class, COLUMNS) {
                    @Override
                    protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                        if (column == proto().token()) {
                            return inject(proto().token());
                        } else {
                            return super.createCell(column);
                        }
                    }
                };
            }
            return super.create(member);
        }

    }

    private static class CardServiceSimulationForm extends AdminEntityForm<CardServiceSimulationCard> {

        public CardServiceSimulationForm(IFormView<CardServiceSimulationCard> view) {
            super(CardServiceSimulationCard.class, view);

            FormFlexPanel contentPanel = new FormFlexPanel("Cards Simulation");

            int row = -1;
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().merchant())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cardType())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().number())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expiryDate())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().balance())).build());
            contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().responseCode())).build());
            contentPanel.setWidget(++row, 0, new Label("Tokens"));
            contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
            contentPanel.setWidget(++row, 0, inject(proto().tokens(), new CardServiceSimulationTokenTableFolder()));
            contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

            selectTab(addTab(contentPanel));

        }
    }

    public CardServiceSimulationCardEditorViewImpl() {
        super(AdminSiteMap.Administration.CardServiceSimulation.class);
        setForm(new CardServiceSimulationForm(this));
    }

}
