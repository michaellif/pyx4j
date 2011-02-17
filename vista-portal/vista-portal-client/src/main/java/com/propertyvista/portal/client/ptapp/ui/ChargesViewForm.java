/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.Charges;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

@Singleton
public class ChargesViewForm extends CEntityForm<Charges> {

    public ChargesViewForm() {
        super(Charges.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(create(proto().applicationCharges(), this));
        main.add(create(proto().rentCharges(), this));
        main.add(create(proto().upgradeCharges(), this));
        setWidget(main);
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member == proto().applicationCharges() || member == proto().rentCharges() || member == proto().upgradeCharges()) {
            return createChargesEditorColumns();
        } else {
            return super.createMemberFolderEditor(member);
        }
    }

    private CEntityFolder<ChargeLine> createChargesEditorColumns() {
        return new CEntityFolder<ChargeLine>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                ChargeLine proto = EntityFactory.getEntityPrototype(ChargeLine.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.type(), "60px"));
                //                columns.add(new EntityFolderColumnDescriptor(proto.name(), "120px"));
                //                columns.add(new EntityFolderColumnDescriptor(proto.color(), "120px"));
                //                columns.add(new EntityFolderColumnDescriptor(proto.breed(), "120px"));
                //                columns.add(new EntityFolderColumnDescriptor(proto.weight(), "120px"));
                //                columns.add(new EntityFolderColumnDescriptor(proto.weightUnit(), "120px"));
                //                columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "120px"));
                //                columns.add(new EntityFolderColumnDescriptor(proto.charge(), "120px"));
            }

            @Override
            protected FolderDecorator<ChargeLine> createFolderDecorator() {
                return new TableFolderDecorator<ChargeLine>(columns, SiteImages.INSTANCE.addRow(), "Add a ChargeLine");
            }

            @Override
            protected CEntityFolderItem<ChargeLine> createItem() {
                return createChargeLineRowEditor(columns);
            }

            private CEntityFolderItem<ChargeLine> createChargeLineRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<ChargeLine>(ChargeLine.class, columns, ChargesViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator(int index) {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove ChargeLine");
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        //                        if (column.getObject() == proto().)
                        CComponent<?> comp = super.createCell(column);
                        //                        comp.setEnabled(column.getObject() != proto().charge());
                        return comp;
                    }
                };
            }

        };

    }
}
