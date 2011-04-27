/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberPrimitiveColumnDescriptor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.client.crud.EntityListPanel;

public abstract class ListerGadgetBase<E extends IEntity> extends GadgetBase {

    protected final EntityListPanel<E> listPanel;

    public ListerGadgetBase(GadgetMetadata gmd, Class<E> clazz) {
        super(gmd);

        listPanel = new EntityListPanel<E>(clazz) {
            @Override
            public List<ColumnDescriptor<E>> getColumnDescriptors() {
                ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
                ListerGadgetBase.this.fillColumnDescriptors(columnDescriptors, proto());
                return columnDescriptors;
            }
        };
    }

    // EntityListPanel access:
    protected EntityListPanel<E> getListPanel() {
        return listPanel;
    }

    /*
     * Implement in derived class to set desired table structure.
     */
    protected abstract void fillColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto);

    // IGadget:
    @Override
    public Widget getWidget() {
        ScrollPanel scroll = new ScrollPanel(listPanel.asWidget());
        scroll.setWidth("100%");
        return scroll;
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public ISetup getSetup() {
        class Setup implements ISetup {
            private final ListBox columns = new ListBox(true);

            @Override
            public Widget getWidget() {
                suspend();

                for (String name : getListPanel().proto().getEntityMeta().getMemberNames()) {
                    MemberMeta meta = getListPanel().proto().getEntityMeta().getMemberMeta(name);
                    if (meta.getObjectClassType() == ObjectClassType.Primitive) {
                        columns.addItem(meta.getCaption());
                        columns.setValue(columns.getItemCount() - 1, name);
                    }
                }

                FlowPanel setupPanel = new FlowPanel();
                setupPanel.add(new Label("Select columns to show:"));

                columns.setVisibleItemCount(8);
                setupPanel.add(columns);

                setupPanel.getElement().getStyle().setPadding(10, Unit.PX);
                setupPanel.getElement().getStyle().setPaddingBottom(0, Unit.PX);
                return setupPanel;
            }

            @Override
            public boolean onOk() {
                ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
                for (int i = 0; i < columns.getItemCount(); ++i) {
                    if (columns.isItemSelected(i)) {
                        columnDescriptors.add(new MemberPrimitiveColumnDescriptor<E>(getListPanel().proto().getMember(columns.getValue(i)).getPath(), columns
                                .getItemText(i)));
                    }
                }

                if (!columnDescriptors.isEmpty()) {
                    stop();
                    getListPanel().getDataTable().getDataTableModel().setColumnDescriptors(columnDescriptors);
                    start();
                } else {
                    resume();
                }

                return true;
            }

            @Override
            public void onCancel() {
                resume();
            }
        }

        return new Setup();
    }
}
