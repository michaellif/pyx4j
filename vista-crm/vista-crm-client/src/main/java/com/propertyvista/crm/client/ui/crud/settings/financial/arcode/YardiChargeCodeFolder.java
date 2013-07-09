/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.financial.arcode;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.offering.YardiChargeCode;

public class YardiChargeCodeFolder extends VistaTableFolder<YardiChargeCode> {

    public static List<EntityFolderColumnDescriptor> COLUMNS;
    static {
        YardiChargeCode proto = EntityFactory.getEntityPrototype(YardiChargeCode.class);
        COLUMNS = Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto.yardiChargeCode(), "100%")
        );//@formatter:on
    }

    public YardiChargeCodeFolder() {
        super(YardiChargeCode.class);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof YardiChargeCode) {
            return new YardiChargeCodeForm();
        }
        return super.create(member);
    }

    public static class YardiChargeCodeForm extends CEntityFolderRowEditor<YardiChargeCode> {

        public YardiChargeCodeForm() {
            super(YardiChargeCode.class, COLUMNS);
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column == proto().yardiChargeCode()) {
                // TODO do we actually have to override the default CComponent?
                return new CTextField();
            } else {
                return super.createCell(column);
            }
        }

    }
}
