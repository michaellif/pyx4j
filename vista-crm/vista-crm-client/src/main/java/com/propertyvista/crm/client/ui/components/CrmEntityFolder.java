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

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderItemEditorDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrudAppPlace;

public abstract class CrmEntityFolder<E extends IEntity> extends CEntityFolderEditor<E> {
    protected static I18n i18n = I18nFactory.getI18n(CrmEntityFolder.class);

    private final Class<E> clazz;

    private final String itemName;

    private final boolean editable;

    private final Class<? extends CrudAppPlace> placeClass;

    private final CEntityForm<?> parent;

    public CrmEntityFolder(Class<E> clazz, String itemName, boolean editable) {
        this(clazz, itemName, editable, null, null);
    }

    public CrmEntityFolder(Class<E> clazz, String itemName, boolean editable, Class<? extends CrudAppPlace> placeClass, CEntityForm<?> parent) {
        super(clazz);
        this.clazz = clazz;
        this.itemName = itemName;
        this.editable = editable;
        this.placeClass = placeClass;
        this.parent = parent;
    }

    protected abstract List<EntityFolderColumnDescriptor> columns();

    @Override
    protected CEntityFolderItemEditor<E> createItem() {
        return new CEntityFolderRowEditor<E>(clazz, columns()) {

            @Override
            public IFolderItemEditorDecorator<E> createFolderItemDecorator() {
                IFolderItemEditorDecorator<E> decor;
                if (placeClass != null) {
                    decor = new CrmFolderItemDecorator<E>(i18n.tr("Remove ") + itemName, editable);
                    decor.addItemClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            CrudAppPlace place = null;
                            if (editable) {
                                place = CrudAppPlace.formEditorPlace(AppSite.getHistoryMapper().createPlace(placeClass), getValue().getPrimaryKey());
                            } else {
                                place = CrudAppPlace.formViewerPlace(AppSite.getHistoryMapper().createPlace(placeClass), getValue().getPrimaryKey());
                            }

                            AppSite.getPlaceController().goTo(place);
                        }
                    });
                } else {
                    decor = new TableFolderItemEditorDecorator<E>(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove ") + itemName,
                            editable);
                }
                return decor;
            }
        };
    }

    @Override
    protected IFolderEditorDecorator<E> createFolderDecorator() {
        if (placeClass != null && parent != null) {
            CrmTableFolderDecorator<E> decor = new CrmTableFolderDecorator<E>(columns(), i18n.tr("Add new ") + itemName, editable);
            decor.addNewItemClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (parent.getValue().getPrimaryKey() != null) { // parent shouldn't be new unsaved value!..
                        AppSite.getPlaceController().goTo(
                                CrudAppPlace.formEditorPlace(AppSite.getHistoryMapper().createPlace(placeClass), parent.getValue().getPrimaryKey()));
                    }
                }
            });
            return decor;
        } else {
            return new TableFolderEditorDecorator<E>(columns(), CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add new ") + itemName,
                    editable);
        }
    }
}