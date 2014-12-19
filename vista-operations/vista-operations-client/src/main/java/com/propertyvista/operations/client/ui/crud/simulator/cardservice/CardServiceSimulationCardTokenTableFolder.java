/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 25, 2014
 * @author vlads
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationToken;

class CardServiceSimulationCardTokenTableFolder extends VistaTableFolder<CardServiceSimulationToken> {

    private static List<FolderColumnDescriptor> COLUMNS;

    static {
        CardServiceSimulationToken p = EntityFactory.getEntityPrototype(CardServiceSimulationToken.class);
        COLUMNS = Arrays.asList(new FolderColumnDescriptor(p.token(), "25em"));
    }

    public CardServiceSimulationCardTokenTableFolder() {
        super(CardServiceSimulationToken.class);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    protected CForm<? extends CardServiceSimulationToken> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<CardServiceSimulationToken>(CardServiceSimulationToken.class, COLUMNS) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (column == proto().token()) {
                    return inject(proto().token());
                } else {
                    return super.createCell(column);
                }
            }
        };
    }

}