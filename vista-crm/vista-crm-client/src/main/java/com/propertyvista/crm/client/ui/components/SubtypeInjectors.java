/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-27
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.financial.offering.Concession;

public class SubtypeInjectors {

    protected static I18n i18n = I18n.get(SubtypeInjectors.class);

    public static VistaTableFolder<Concession> injectConcessions(boolean isEditable) {
        return new VistaTableFolder<Concession>(Concession.class, "Concession", isEditable) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().approvedBy(), "20em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().expirationDate(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().expirationDate(), "8.2em"));
                return columns;
            }
        };
    }
}
