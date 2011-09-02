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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmTableFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageDescriptor.Type;
import com.propertyvista.domain.site.SiteLocale;
import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteEditorForm extends CrmEntityForm<SiteDescriptorDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public SiteEditorForm(IFormView<SiteDescriptorDTO> parentView) {
        this(parentView, new CrmEditorsComponentFactory());
    }

    public SiteEditorForm(IFormView<SiteDescriptorDTO> parentView, IEditableComponentFactory factory) {
        super(SiteDescriptorDTO.class, factory);
        setParentView(parentView);
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

    public IsWidget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().skin()), 10);
        main.add(inject(proto().baseColor()), 10);
        main.add(inject(proto().copyright()), 25);

        main.add(new CrmSectionSeparator(proto().locales().getMeta().getCaption()));
        main.add(inject(proto().locales(), createLocalesList()));

// TODO: image lists uploaders:
//        main.add(inject(proto().logo(), new CFileUploader()), 60);
//        main.add(inject(proto().slogan(), new CFileUploader()), 60);
//        main.add(inject(proto().images(), new CFileUploader()), 60);

        main.add(new CrmSectionSeparator(proto().childPages().getMeta().getCaption()));
        main.add(inject(proto().childPages(), createChildPagesList()));

        return new CrmScrollPanel(main);
    }

    private CEntityFolderEditor<SiteLocale> createLocalesList() {
        return new CrmEntityFolder<SiteLocale>(SiteLocale.class, i18n.tr("SiteLocale"), isEditable()) {
            private final CrmEntityFolder<SiteLocale> parent = this;

            private final ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            {
                columns.add(new EntityFolderColumnDescriptor(proto().locale(), "10em"));
            }

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return columns;
            }

            @Override
            public IFolderEditorDecorator<SiteLocale> createFolderDecorator() {
                TableFolderEditorDecorator<SiteLocale> decor = (TableFolderEditorDecorator<SiteLocale>) super.createFolderDecorator();
                decor.setShowHeader(false);
                return decor;
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
                                        ((SiteViewer) getParentView()).viewChild(getValue().getPrimaryKey());
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
                        if (SiteEditorForm.this.getValue().getPrimaryKey() != null) { // parent shouldn't be new unsaved value!..
                            ((SiteViewer) getParentView()).newChild(SiteEditorForm.this.getValue().getPrimaryKey());
                        }
                    }
                });
                decor.setShowHeader(false);
                return decor;
            }

            @Override
            protected boolean isFolderItemAllowed(PageDescriptor item) {
                return !(Type.findApartment.equals(item.type().getValue()) || Type.residents.equals(item.type().getValue()));
            }
        };
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
                return new CEntityFolderItemEditor<Testimonial>(Testimonial.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());
                        main.add(inject(proto().locale()), 10);
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
                return new CEntityFolderItemEditor<News>(News.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());
                        main.add(inject(proto().locale()), 10);
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

//    private class ColorPickerDialog extends DialogBox {
//
//        private final ColorPicker picker;
//
//        private Integer colorselected;
//
//        public ColorPickerDialog() {
//            setText("Choose a color");
//
//            // Define the panels
//            VerticalPanel panel = new VerticalPanel();
//            FlowPanel okcancel = new FlowPanel();
//            picker = new ColorPicker();
//
//            // Define the buttons
//            Button preview = new Button("Preview");
//            preview.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent sender) {
//                    colorPreview(picker.getHexColor());
//                }
//            });
//
//            Button ok = new Button("Ok"); // ok button
//            ok.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent sender) {
//                    colorSelected(picker.getHexColor());
//                    ColorPickerDialog.this.hide();
//                }
//            });
//
//            Button cancel = new Button("Cancel"); // cancel button
//            cancel.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent sender) {
//                    cancel();
//                    ColorPickerDialog.this.hide();
//                }
//            });
//            okcancel.add(preview);
//            okcancel.add(ok);
//            okcancel.add(cancel);
//
//            // Put it together
//            panel.add(picker);
//            panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//            panel.add(okcancel);
//
//            setWidget(panel);
//        }
//
//        public void showNear(Widget sender) {
//            int left = sender.getAbsoluteLeft() + 30;
//            int top = sender.getAbsoluteTop() + 10;
//            this.setPopupPosition(left, top);
//            this.show();
//        }
//
//        public Integer colorSelected() {
//            return colorselected;
//        }
//
//        private void colorSelected(Integer color) {
//            colorselected = color;
//        }
//
//        private void cancel() {
//            colorselected = -1;
//        }
//    }
}