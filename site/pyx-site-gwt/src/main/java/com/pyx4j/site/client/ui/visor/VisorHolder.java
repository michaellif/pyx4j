/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.visor;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutCommand;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.ui.IPane;

public class VisorHolder extends ComplexPanel implements RequiresResize, ProvidesResize {

    private int animationDuration = 0;

    private final Layout layout;

    private final LayoutCommand vizorCmd;

    private IsWidget visorPaneWidget;

    private IsWidget previousVisorPaneWidget;

    private final IPane parentPane;

    public VisorHolder(IPane parentPane) {
        this.parentPane = parentPane;
        setElement(Document.get().createDivElement());
        layout = new Layout(getElement());
        vizorCmd = new VisorShowLayoutCommand();
    }

    public void setContentPane(IsWidget widget) {
        assert widget != null : "Content Pane Widget should not be null";
        assert getWidgetCount() == 0 : "Content Pane is already set";

        widget.asWidget().removeFromParent();

        getChildren().add(widget.asWidget());

        // Physical attach.
        Layer layer = layout.attachChild(widget.asWidget().getElement(), widget);
        layer.setTopHeight(0.0, Unit.PCT, 100.0, Unit.PCT);
        layer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
        widget.asWidget().setLayoutData(layer);

        adopt(widget.asWidget());

        layout.layout();
    }

    public void showVisorPane(IVisor visor) {
        assert visor != null : "Visor Pane Widget should not be null";
        assert getWidgetCount() >= 1 : "Content Pane should be set first";

        if (visorPaneWidget == visor) {
            return;
        }

        previousVisorPaneWidget = visorPaneWidget;
        visorPaneWidget = visor;

        visor.asWidget().removeFromParent();

        getChildren().insert(visor.asWidget(), 1);

        // Physical attach.
        Layer layer = layout.attachChild(visor.asWidget().getElement(), visor);
        setWidgetVisible(visor.asWidget(), layer, false);
        visor.asWidget().setLayoutData(layer);

        adopt(visor.asWidget());
        if (visor instanceof AbstractVisorPane) {
            ((AbstractVisorPane) visor).setParentPane(parentPane);
        }

        vizorCmd.schedule(animationDuration, null);
    }

    public void hideVisorPane() {
        previousVisorPaneWidget = visorPaneWidget;
        visorPaneWidget = null;
        if (previousVisorPaneWidget != null) {
            vizorCmd.schedule(animationDuration, null);
        }
    }

    public boolean isVisorShown() {
        return (visorPaneWidget != null);
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(int duration) {
        this.animationDuration = duration;
    }

    @Override
    public void onResize() {
        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }

    @Override
    public boolean remove(IsWidget w) {
        boolean removed = super.remove(w);
        if (removed) {
            Layer layer = (Layer) w.asWidget().getLayoutData();
            layout.removeChild(layer);
            w.asWidget().setLayoutData(null);

            if (visorPaneWidget == w) {
                visorPaneWidget = null;
            }

            if (previousVisorPaneWidget == w) {
                previousVisorPaneWidget = null;
            }
        }
        return removed;
    }

    private void setWidgetVisible(IsWidget w, Layer layer, boolean visible) {
        layer.setVisible(visible);
        w.asWidget().setVisible(visible);
    }

    private class VisorShowLayoutCommand extends LayoutCommand {
        public VisorShowLayoutCommand() {
            super(layout);
        }

        @Override
        public void schedule(int duration, final AnimationCallback callback) {
            super.schedule(duration, new AnimationCallback() {
                @Override
                public void onAnimationComplete() {
                    if (previousVisorPaneWidget != null) {
                        remove(previousVisorPaneWidget);
                    }
                    if (callback != null) {
                        callback.onAnimationComplete();
                    }
                }

                @Override
                public void onLayout(Layer layer, double progress) {
                    if (callback != null) {
                        callback.onLayout(layer, progress);
                    }
                }
            });
        }

        @Override
        protected void doBeforeLayout() {
            Layer visorPaneLayer = null;
            Layer previousVisorPaneLayer = null;

            if (visorPaneWidget != null) {
                visorPaneLayer = (Layer) visorPaneWidget.asWidget().getLayoutData();

                visorPaneLayer.setTopHeight(-100.0, Unit.PCT, 100.0, Unit.PCT);
                visorPaneLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
                setWidgetVisible(visorPaneWidget, visorPaneLayer, true);
            }

            if (previousVisorPaneWidget != null) {
                previousVisorPaneLayer = (Layer) previousVisorPaneWidget.asWidget().getLayoutData();

                previousVisorPaneLayer.setTopHeight(0.0, Unit.PCT, 100.0, Unit.PCT);
                previousVisorPaneLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
                setWidgetVisible(previousVisorPaneWidget, previousVisorPaneLayer, true);
            }

            layout.layout();

            if (visorPaneLayer != null) {
                visorPaneLayer.setTopHeight(0.0, Unit.PCT, 100.0, Unit.PCT);
                visorPaneLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
            }

            if (previousVisorPaneLayer != null) {
                if (visorPaneLayer == null) {
                    previousVisorPaneLayer.setTopHeight(-100.0, Unit.PCT, 100.0, Unit.PCT);
                    previousVisorPaneLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
                } else {
                    previousVisorPaneLayer.setTopHeight(100.0, Unit.PCT, 100.0, Unit.PCT);
                    previousVisorPaneLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
                }
            }
        }
    }
}