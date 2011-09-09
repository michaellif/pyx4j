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
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmTableFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.domain.site.PageDescriptor;

public class PageEditorForm extends CrmEntityForm<PageDescriptor> {

    public PageEditorForm(IFormView<PageDescriptor> parentView) {
        this(parentView, new CrmEditorsComponentFactory());
    }

    public PageEditorForm(IFormView<PageDescriptor> parentView, IEditableComponentFactory factory) {
        super(PageDescriptor.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().name()), 15);

        main.add(new CrmSectionSeparator(proto().content().getMeta().getCaption()));
        main.add(inject(proto().content(), createPageContentsList()));

        main.add(new CrmSectionSeparator(proto().childPages().getMeta().getCaption()));
        main.add(inject(proto().childPages(), createChildPagesList()));

        return new CrmScrollPanel(main);
    }

    private CEntityFolderEditor<PageContent> createPageContentsList() {
        return new CrmEntityFolder<PageContent>(PageContent.class, i18n.tr("PageContent"), isEditable()) {
            private final CrmEntityFolder<PageContent> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return null;
            }

            @Override
            protected IFolderEditorDecorator<PageContent> createFolderDecorator() {
                return new CrmBoxFolderDecorator<PageContent>(parent);
            }

            @Override
            protected CEntityFolderItemEditor<PageContent> createItem() {
                return new CEntityFolderItemEditor<PageContent>(PageContent.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());

                        main.add(inject(proto().locale()), 10);

                        main.add(inject(proto()._caption().caption()), 20);

                        if (parent.isEditable()) {
                            main.add(inject(proto().content()), 60);
                        } else {
                            CLabel content = new CLabel();
                            content.setAllowHtml(true);
                            main.add(inject(proto().content(), content), 60);
                        }

                        // TODO
                        // main.add(inject(proto().image(), new CFileUploader()), 60);
                        return main;
                    }

                    @Override
                    public IFolderItemEditorDecorator<PageContent> createFolderItemDecorator() {
                        return new CrmBoxFolderItemDecorator<PageContent>(parent);
                    }
                };
            }

            @Override
            protected void createNewEntity(PageContent newEntity, AsyncCallback<PageContent> callback) {
                newEntity.descriptor().set(PageEditorForm.this.getValue());
                callback.onSuccess(newEntity);
            }
        };
    }

    private CEntityFolderEditor<PageDescriptor> createChildPagesList() {
        return new CrmEntityFolder<PageDescriptor>(PageDescriptor.class, i18n.tr("Page"), !isEditable()) {
            private final CrmEntityFolder<PageDescriptor> parent = this;

            private final ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            {
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "25em"));
            }

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return columns;
            }

            @Override
            protected CEntityFolderItemEditor<PageDescriptor> createItem() {
                return new CEntityFolderRowEditor<PageDescriptor>(PageDescriptor.class, columns()) {
                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        if (column.getObject().equals(proto().name())) {
                            CComponent<?> comp = null;
                            if (!parent.isEditable()) {
                                comp = inject(column.getObject(), new CLabel());
                            } else {
                                comp = inject(column.getObject(), new CHyperlink(new Command() {
                                    @Override
                                    public void execute() {
                                        ((PageViewer) getParentView()).viewChild(getValue().getPrimaryKey());
                                    }
                                }));
                            }
                            return comp;
                        }
                        return super.createCell(column);
                    }

                    @Override
                    public IFolderItemEditorDecorator<PageDescriptor> createFolderItemDecorator() {
                        return new CrmTableFolderItemDecorator<PageDescriptor>(parent, !parent.isEditable());
                    }
                };
            }

            @Override
            protected IFolderEditorDecorator<PageDescriptor> createFolderDecorator() {
                CrmTableFolderDecorator<PageDescriptor> decor = new CrmTableFolderDecorator<PageDescriptor>(columns(), parent);
                decor.addItemAddClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (PageEditorForm.this.getValue().getPrimaryKey() != null) { // parent shouldn't be new unsaved value!..
                            ((PageViewer) getParentView()).newChild(PageEditorForm.this.getValue().getPrimaryKey());
                        }
                    }
                });
                decor.setShowHeader(false);
                return decor;
            }
        };
    }
}