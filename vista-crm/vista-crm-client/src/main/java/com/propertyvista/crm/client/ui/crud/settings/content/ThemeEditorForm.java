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

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.Testimonial;

public class ThemeEditorForm extends CrmEntityForm<SiteDescriptor> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public ThemeEditorForm() {
        super(SiteDescriptor.class, new CrmEditorsComponentFactory());
    }

    public ThemeEditorForm(IEditableComponentFactory factory) {
        super(SiteDescriptor.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createTestimonialsTab(), i18n.tr("Testimonials"));
        tabPanel.add(createNewsTab(), i18n.tr("News"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    public IsWidget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().skin()), 10);
        main.add(inject(proto().baseColor()), 10);

        main.add(inject(proto().copyright()), 25);

        return new CrmScrollPanel(main);
    }

    private Widget createTestimonialsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().testimonials(), createTestimonialsList()));
        return new ScrollPanel(main);
    }

    private Widget createNewsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().news(), createNewsList()));
        return new ScrollPanel(main);
    }

    private CEntityFolderEditor<Testimonial> createTestimonialsList() {
        return new CrmEntityFolder<Testimonial>(Testimonial.class, i18n.tr("Testimonial"), isEditable()) {
            private final CrmEntityFolder<Testimonial> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected IFolderEditorDecorator<Testimonial> createFolderDecorator() {
                return new CrmBoxFolderDecorator<Testimonial>(parent);
            }

            @Override
            protected CEntityFolderItemEditor<Testimonial> createItem() {
                return createGuarantorRowEditor();
            }

            private CEntityFolderItemEditor<Testimonial> createGuarantorRowEditor() {
                return new CEntityFolderItemEditor<Testimonial>(Testimonial.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());
                        main.add(inject(proto().content()), 50);
                        main.add(inject(proto().author()), 20);
                        return main;
                    }

                    @Override
                    public IFolderItemEditorDecorator<Testimonial> createFolderItemDecorator() {
                        return new CrmBoxFolderItemDecorator<Testimonial>(parent);
                    }
                };
            }
        };
    }

    private CEntityFolderEditor<News> createNewsList() {
        return new CrmEntityFolder<News>(News.class, i18n.tr("News"), isEditable()) {
            private final CrmEntityFolder<News> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected IFolderEditorDecorator<News> createFolderDecorator() {
                return new CrmBoxFolderDecorator<News>(parent);
            }

            @Override
            protected CEntityFolderItemEditor<News> createItem() {
                return createGuarantorRowEditor();
            }

            private CEntityFolderItemEditor<News> createGuarantorRowEditor() {
                return new CEntityFolderItemEditor<News>(News.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());
                        main.add(inject(proto().caption()), 20);
                        main.add(inject(proto().content()), 50);
                        main.add(inject(proto().date()), 8.2);
                        return main;
                    }

                    @Override
                    public IFolderItemEditorDecorator<News> createFolderItemDecorator() {
                        return new CrmBoxFolderItemDecorator<News>(parent);
                    }
                };
            }
        };
    }
}