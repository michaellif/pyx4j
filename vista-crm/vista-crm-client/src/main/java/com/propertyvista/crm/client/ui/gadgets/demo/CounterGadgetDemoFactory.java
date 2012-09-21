/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.demo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewFolder;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewFolder.IZoomableRowEditorFactory;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewFolder.ZoomableViewEntityRowEditor;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.CounterGadgetDemoDTO;
import com.propertyvista.crm.rpc.dto.gadgets.CounterGadgetDemoSubDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AbstractCounterGadgetBaseService;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.CounterGadgetDemoMetadata;

public class CounterGadgetDemoFactory extends AbstractGadget<CounterGadgetDemoMetadata> {

    public class CounterGadgetDemo extends CounterGadgetInstanceBase<CounterGadgetDemoDTO, VoidSerializable, CounterGadgetDemoMetadata> {

        public CounterGadgetDemo(GadgetMetadata metadata) {
            super(CounterGadgetDemoDTO.class, new AbstractCounterGadgetBaseService<CounterGadgetDemoDTO, VoidSerializable>() {

                @Override
                public void countData(AsyncCallback<CounterGadgetDemoDTO> callback, VoidSerializable queryParams) {
                    CounterGadgetDemoDTO dto = EntityFactory.create(CounterGadgetDemoDTO.class);
                    dto.counterValue().setValue(55);
                    dto.strValue().setValue("123");
                    dto.doubleValue().setValue(35.5d);
                    dto.moneyValue().setValue(new BigDecimal("99.99"));
                    CounterGadgetDemoSubDataDTO foo = dto.xs().$();
                    foo.a().setValue(5);
                    foo.b().setValue(6);
                    dto.xs().add(foo);

                    foo = dto.xs().$();
                    foo.a().setValue(55);
                    foo.b().setValue(11);
                    dto.xs().add(foo);

                    callback.onSuccess(dto);
                }
            }, new ZoomableViewForm<CounterGadgetDemoDTO>(CounterGadgetDemoDTO.class) {

                @Override
                public IsWidget createContent() {
                    FlowPanel content = new FlowPanel();
                    content.add(new DecoratorBuilder(inject(proto().counterValue())).build());
                    content.add(new DecoratorBuilder(inject(proto().strValue())).build());
                    content.add(new DecoratorBuilder(inject(proto().doubleValue())).build());
                    content.add(new DecoratorBuilder(inject(proto().moneyValue())).build());
                    IZoomableRowEditorFactory<CounterGadgetDemoSubDataDTO> factory = new IZoomableRowEditorFactory<CounterGadgetDemoSubDataDTO>() {
                        @Override
                        public ZoomableViewEntityRowEditor<CounterGadgetDemoSubDataDTO> createEditor(ZoomableViewFolder<CounterGadgetDemoSubDataDTO> parent,
                                List<EntityFolderColumnDescriptor> columns) {
                            return new ZoomableViewEntityRowEditor<CounterGadgetDemoSubDataDTO>(CounterGadgetDemoSubDataDTO.class, columns) {
                            };
                        }
                    };
                    content.add(inject(proto().xs(), new ZoomableViewFolder<CounterGadgetDemoSubDataDTO>(CounterGadgetDemoSubDataDTO.class, factory) {
                        @Override
                        public List<EntityFolderColumnDescriptor> columns() {
                            return Arrays.asList(new EntityFolderColumnDescriptor(proto().a(), "10em"), new EntityFolderColumnDescriptor(proto().b(), "10em"));
                        }
                    }));
                    return content;
                }

            }, metadata, CounterGadgetDemoMetadata.class);
        }

        @Override
        protected VoidSerializable prepareSummaryQuery() {
            return null;
        }

        @Override
        protected void bindDetailsFactories() {
            bindDetailsFactory(proto().counterValue(), new CounterDetailsFactory() {
                @Override
                public Widget createDetailsWidget() {
                    return new HTML("BEHOLD THE DETAILS FOR THE COUNTER");
                }
            });
            bindDetailsFactory(proto().xs().$().a(), new CounterDetailsFactory() {
                @Override
                public Widget createDetailsWidget() {
                    return new HTML("asffsafsdfa");
                }
            });
        }
    }

    public CounterGadgetDemoFactory() {
        super(CounterGadgetDemoMetadata.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Demo.toString());
    }

    @Override
    protected GadgetInstanceBase<CounterGadgetDemoMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new CounterGadgetDemo(gadgetMetadata);
    }

}
