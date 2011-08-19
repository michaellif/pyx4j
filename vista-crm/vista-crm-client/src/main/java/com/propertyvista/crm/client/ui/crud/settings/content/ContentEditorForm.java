/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderItemEditorDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmTableFolderDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.settings.content.ContentEditor.Presenter;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.site.PageDescriptor;

public class ContentEditorForm extends CrmEntityForm<PageDescriptor> {

    Presenter presenter;

    public ContentEditorForm() {
        super(PageDescriptor.class, new CrmEditorsComponentFactory());
    }

    public ContentEditorForm(IEditableComponentFactory factory) {
        super(PageDescriptor.class, factory);
    }

    void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
//        main.setShowMandatory(false);

        main.add(inject(proto().caption()), 15);
        main.add(inject(proto().content().content()), 60);

        main.add(new CrmHeader2Decorator(proto().childPages().getMeta().getCaption()));
        main.add(inject(proto().childPages(), createChildPagesList()));

        return new CrmScrollPanel(main);
    }

    private CEntityFolderEditor<PageDescriptor> createChildPagesList() {
        return new CEntityFolderEditor<PageDescriptor>(PageDescriptor.class) {

            private final ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            {
                columns.add(new EntityFolderColumnDescriptor(proto().caption(), "25em"));
            }

            @Override
            protected CEntityFolderItemEditor<PageDescriptor> createItem() {
                return new CEntityFolderRowEditor<PageDescriptor>(PageDescriptor.class, columns) {
                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        if (column.getObject().equals(proto().caption())) {
                            CComponent<?> comp = null;
                            if (ContentEditorForm.this.isEditable()) {
                                comp = inject(column.getObject(), new CLabel());
                            } else {
                                comp = inject(column.getObject(), new CHyperlink(new Command() {
                                    @Override
                                    public void execute() {
                                        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(CrmSiteMap.Settings.Content.class);
                                        place.formViewerPlace(getValue().getPrimaryKey());
                                        AppSite.getPlaceController().goTo(place);
                                    }
                                }));
                            }
                            return comp;
                        }
                        return super.createCell(column);
                    }

                    @Override
                    public IFolderItemEditorDecorator<PageDescriptor> createFolderItemDecorator() {
                        IFolderItemEditorDecorator<PageDescriptor> decor = new TableFolderItemEditorDecorator<PageDescriptor>(CrmImages.INSTANCE.del(),
                                CrmImages.INSTANCE.delHover(), i18n.tr("Remove Page"), ContentEditorForm.this.isEditable());
                        decor.addItemRemoveClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                assert (presenter != null);
                                presenter.deleteChildPage(getValue());
                            }
                        });
                        return decor;
                    }
                };
            }

            @Override
            protected IFolderEditorDecorator<PageDescriptor> createFolderDecorator() {
                CrmTableFolderDecorator<PageDescriptor> decor = new CrmTableFolderDecorator<PageDescriptor>(columns, i18n.tr("Add new Page"),
                        !ContentEditorForm.this.isEditable());
                decor.addItemAddClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (ContentEditorForm.this.getValue().getPrimaryKey() != null) { // parent shouldn't be new unsaved value!..
                            CrudAppPlace place = AppSite.getHistoryMapper().createPlace(CrmSiteMap.Settings.Content.class);
                            place.formNewItemPlace(ContentEditorForm.this.getValue().getPrimaryKey());
                            AppSite.getPlaceController().goTo(place);
                        }
                    }
                });
                decor.setShowHeader(false);
                return decor;
            }
        };
    }
}