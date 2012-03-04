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
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.shared.CompiledLocale;

class AvailableLocaleFolder extends VistaTableFolder<AvailableLocale> {
    private final Set<CompiledLocale> usedLocales = new HashSet<CompiledLocale>();

    public AvailableLocaleFolder(boolean modifyable) {
        super(AvailableLocale.class, modifyable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<AvailableLocale>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<AvailableLocale>> event) {
                usedLocales.clear();
                for (AvailableLocale al : event.getValue()) {
                    usedLocales.add(al.lang().getValue());
                }
                applyEditabilityRules();
            }
        });
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().lang(), "20em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (proto().lang().getPath().equals(member.getPath())) {
            final CComboBox<CompiledLocale> langCombo = new CComboBox<CompiledLocale>() {
                @Override
                public void applyEditabilityRules() {
                    super.applyEditabilityRules();
                    if (isEditable()) {
                        EnumSet<CompiledLocale> opts = EnumSet.allOf(CompiledLocale.class);
                        opts.removeAll(usedLocales);
                        if (getValue() != null) {
                            opts.add(getValue());
                        }
                        setOptions(opts);
                    }
                }
            };
            return langCombo;
        }
        return super.create(member);
    }

    @Override
    protected void onPopulate() {
        usedLocales.clear();
        for (AvailableLocale al : getValue()) {
            usedLocales.add(al.lang().getValue());
        }
    }

    @Override
    protected IFolderDecorator<AvailableLocale> createDecorator() {
        TableFolderDecorator<AvailableLocale> decor = (TableFolderDecorator<AvailableLocale>) super.createDecorator();
        decor.setShowHeader(false);
        return decor;
    }
}