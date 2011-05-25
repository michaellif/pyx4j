/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap;

public abstract class CrmEntityFolder<E extends IEntity> extends CEntityFolder<E> {
    protected static I18n i18n = I18nFactory.getI18n(CrmEntityFolder.class);

    private final Class<E> clazz;

    private final String itemName;

    private final boolean editable;

    private final AppPlace place;

    public CrmEntityFolder(Class<E> clazz, String itemName, boolean editable) {
        this(clazz, itemName, editable, null);
    }

    public CrmEntityFolder(Class<E> clazz, String itemName, boolean editable, AppPlace place) {
        super(clazz);
        this.clazz = clazz;
        this.itemName = itemName;
        this.editable = editable;
        this.place = place;
    }

    protected abstract List<EntityFolderColumnDescriptor> columns();

    @Override
    protected CEntityFolderItem<E> createItem() {
        return new CEntityFolderRow<E>(clazz, columns()) {

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                FolderItemDecorator decor;
                if (place != null) {
                    decor = new CrmFolderItemDecorator(i18n.tr("Remove " + itemName), editable);
                    decor.addItemClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            AppSite.getPlaceController().goTo(CrmSiteMap.formItemPlace(place, getValue().getPrimaryKey()));
                        }
                    });
                } else {
                    decor = new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove " + itemName), editable);
                }
                return decor;
            }
        };
    }

    @Override
    protected FolderDecorator<E> createFolderDecorator() {
        return new CrmTableFolderDecorator<E>(columns(), i18n.tr("Add" + itemName), editable);
    }
}