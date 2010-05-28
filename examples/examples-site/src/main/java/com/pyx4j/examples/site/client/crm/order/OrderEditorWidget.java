/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.order;

import java.util.EnumSet;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.CEntityHyperlink;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.client.crud.EntityEditorPanel;
import com.pyx4j.essentials.client.crud.EntityEditorWidget;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.domain.crm.DomainUtils;
import com.pyx4j.examples.domain.crm.Order;
import com.pyx4j.examples.domain.crm.OrderPhoto;
import com.pyx4j.examples.domain.crm.Resource;
import com.pyx4j.examples.domain.crm.Order.OrderStatus;
import com.pyx4j.examples.domain.crm.Resource.RepStatus;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.shared.meta.NavigUtils;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.photoalbum.BasicPhotoAlbumModel;
import com.pyx4j.widgets.client.photoalbum.Photo;
import com.pyx4j.widgets.client.photoalbum.PhotoAlbum;
import com.pyx4j.widgets.client.util.BrowserType;

public class OrderEditorWidget extends EntityEditorWidget<Order> {

    private static Logger log = LoggerFactory.getLogger(OrderEditorWidget.class);

    private final PhotoAlbumModel model;

    public OrderEditorWidget() {
        super(Order.class, ExamplesSiteMap.Crm.Orders.Edit.class, new EntityEditorPanel<Order>(Order.class) {

            @Override
            protected IObject<?>[][] getFormMembers() {

                getForm().bind(new CEntityHyperlink("Customer", NavigUtils.getPageUri(ExamplesSiteMap.Crm.Customers.Edit.class) + "?entity_id="),
                        meta().customer().getPath());

                return new IObject[][] {

                { meta().customer(), null },

                { meta().orderNumber(), meta().description() },

                { meta().resource(), meta().cost() },

                { meta().receivedDate(), meta().completedDate() },

                { meta().dueDate(), meta().status() },

                { meta().notes(), meta().notes() },

                };
            }

            @Override
            protected void enhanceComponents(CEntityForm<Order> form) {
                ((CComboBox<Order.OrderStatus>) get(meta().status())).setOptions(EnumSet.allOf(Order.OrderStatus.class));
                get(meta().notes()).setWidth("100%");

                ((CEntityComboBox<Resource>) get(meta().resource())).setOptionsFilter(new OptionsFilter<Resource>() {

                    @Override
                    public boolean acceptOption(Resource entity) {
                        return RepStatus.ACTIVE.equals(entity.status().getValue());
                    }
                });
            }

            @Override
            public void onDetach() {
                // HACK
                ((CEntityComboBox<Resource>) get(meta().resource())).resetOptions();
                super.onDetach();
            }

        });

        PhotoAlbum photoAlbum = new PhotoAlbum("") {

            @Override
            public void addPhotoCommand() {
                new PhotoUploadDialog(getEditorPanel().getEntity()) {
                    @Override
                    public void onComplete() {
                        populatePhotoList();
                    }
                }.show();
            }

            @Override
            public void updateCaptionCommand(final int index) {
                new EditCaptionDialog(model.getOrderPhoto(index).description().getValue()) {
                    @Override
                    public boolean onClickOk() {
                        getPhotoAlbumModel().updateCaption(index, getCaption());
                        return true;
                    }
                }.show();
            }

        };

        photoAlbum.setWidth("700px");
        model = new PhotoAlbumModel();
        photoAlbum.setPhotoAlbumModel(model);

        add(photoAlbum, DockPanel.SOUTH);

    }

    @Override
    public void populateForm(Order order) {
        if (order == null) {
            final Order finalOrder = EntityFactory.create(Order.class);
            finalOrder.status().setValue(OrderStatus.ACTIVE);
            Map<String, String> args = getRequestArgs();

            if (args == null) {
                throw new RuntimeException("Missing args in URL");
            }

            String entityIdStr = args.get("parent_id");

            AsyncCallback<IEntity> callback = new RecoverableAsyncCallback<IEntity>() {

                public void onSuccess(IEntity result) {
                    if (result != null) {
                        Customer customer = (Customer) result;
                        finalOrder.customer().set(customer);
                        DomainUtils.denormalizationOrder(finalOrder, customer);
                        getEditorPanel().get(getEditorPanel().meta().orderNumber()).setEditable(true);
                        getEditorPanel().populateForm(finalOrder);
                    } else {
                        log.warn(" not found");
                    }
                }

                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            };

            RPCManager.execute(EntityServices.Retrieve.class, EntityCriteriaByPK.create(Customer.class, Long.parseLong(entityIdStr)), callback);
        } else {
            getEditorPanel().populateForm(order);
            getEditorPanel().get(getEditorPanel().meta().orderNumber()).setEditable(false);
            updateHistoryToken(order);

            populatePhotoList();

        }

    }

    @Override
    protected void onUnload() {
        model.clear();
        super.onUnload();
    }

    private void populatePhotoList() {

        // Load all Order images.
        AsyncCallback<Vector<? extends IEntity>> callback = new AsyncCallback<Vector<? extends IEntity>>() {
            @Override
            public void onFailure(Throwable caught) {
                MessageDialog.warn("Error loading images from server", caught.getMessage());
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(Vector<? extends IEntity> result) {
                model.populate((Vector<OrderPhoto>) result);
            }
        };
        EntityQueryCriteria<OrderPhoto> criteria = EntityQueryCriteria.create(OrderPhoto.class);
        criteria.add(PropertyCriterion.eq(criteria.meta().order(), getEditorPanel().getEntity().getPrimaryKey()));
        RPCManager.execute(EntityServices.Query.class, criteria, callback);
    }

    class PhotoAlbumModel extends BasicPhotoAlbumModel {

        private Vector<OrderPhoto> orderPhotoList;

        @Override
        public void removePhoto(final int index) {
            final AsyncCallback reloadcallback = new AsyncCallback<VoidSerializable>() {
                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);

                }

                @Override
                public void onSuccess(VoidSerializable result) {
                    getPhotoList().remove(index);
                    getPhotoAlbum().onPhotoRemoved(index);
                }
            };

            RPCManager.execute(EntityServices.Delete.class, orderPhotoList.get(index), reloadcallback);

        }

        OrderPhoto getOrderPhoto(int index) {
            return orderPhotoList.get(index);
        }

        public void populate(Vector<OrderPhoto> result) {
            orderPhotoList = result;
            model.clear();
            for (final OrderPhoto tn : result) {
                String thumbnailUrl;
                if (BrowserType.isIE()) {
                    thumbnailUrl = "/orderphoto?tn=1&id=" + tn.getPrimaryKey();
                } else {
                    thumbnailUrl = "data:image/png;base64," + tn.thumbnailBase64().getValue();
                }
                model.addPhoto(new Photo(thumbnailUrl, "/orderphoto?id=" + tn.getPrimaryKey(), tn.description().getValue()));
            }

        }

        @Override
        public void updateCaption(final int index, String caption) {

            AsyncCallback callback = new AsyncCallback<OrderPhoto>() {
                @Override
                public void onFailure(Throwable caught) {
                    MessageDialog.warn("Error loading images from server", caught.getMessage());
                }

                @Override
                public void onSuccess(OrderPhoto result) {
                    PhotoAlbumModel.super.updateCaption(index, result.description().getValue());
                }
            };

            orderPhotoList.get(index).description().setValue(caption);
            RPCManager.execute(EntityServices.Save.class, orderPhotoList.get(index), callback);

        }

    }

}