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
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.shared.CompiledLocale;

class AvailableLocaleFolder extends VistaTableFolder<AvailableLocale> {

    public AvailableLocaleFolder(boolean modifyable) {
        super(AvailableLocale.class, modifyable);
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
            CComboBox<CompiledLocale> langCombo = new CComboBox<CompiledLocale>();
            langCombo.setOptions(EnumSet.allOf(CompiledLocale.class));
            return langCombo;
        }
        return super.create(member);
    }

    @Override
    protected IFolderDecorator<AvailableLocale> createDecorator() {
        TableFolderDecorator<AvailableLocale> decor = (TableFolderDecorator<AvailableLocale>) super.createDecorator();
        decor.setShowHeader(false);
        return decor;
    }
}