package com.pyx4j.forms.client.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.forms.client.ui.CImage.ImageHolder.ImageDataProvider;
import com.pyx4j.forms.client.ui.CImage.NImage;
import com.pyx4j.widgets.client.IWidget;
import com.pyx4j.widgets.client.ImageFactory;

/*
 * CImage allows to display and edit a single image or a set of images (using sequential navigation)  
 */
public abstract class CImage<T extends IFile> extends CComponent<List<T>, NImage<T>> {

    public enum Type {
        single, multiple
    }

    private final Type type;

    private Image placeholder;

    public CImage(Type type) {
        this.type = type;
    }

    public abstract String getImageUrl(T file);

    public abstract T getNewValue(IFile file);

    public void setPlaceholderImage(Image placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected NImage<T> createWidget() {
        return new NImage<T>(this);
    }

    public static class NImage<T extends IFile> extends NComponent<List<T>, ImageHolder, CImage<T>, ImageHolder> implements ImageDataProvider {
        private final List<IFile> imageFiles;

        private final List<String> imageUrls;

        private ImageHolder widget;

        public NImage(CImage<T> cComponent) {
            super(cComponent);
            imageFiles = new ArrayList<IFile>();
            imageUrls = new ArrayList<String>();
        }

        @Override
        public void setNativeValue(List<T> values) {
            if (values == null) {
                return;
            }
            imageUrls.clear();
            for (T value : values) {
                imageFiles.add(value);
                imageUrls.add(getCComponent().getImageUrl(value));
            }
            createWidget().reset();
        }

        @Override
        public List<T> getNativeValue() throws ParseException {
            List<T> value = new ArrayList<T>();
            for (IFile file : imageFiles) {
                value.add(getCComponent().getNewValue(file));
            }
            return value;
        }

        @Override
        protected ImageHolder createEditor() {
            return createWidget();
        }

        @Override
        protected void onEditorInit() {
            super.onEditorInit();
            widget.setEditable(true);
        }

        @Override
        protected ImageHolder createViewer() {
            return createWidget();
        }

        @Override
        protected void onViewerInit() {
            super.onViewerInit();
            widget.setEditable(false);
        }

        @Override
        public List<String> getImageUrls() {
            return imageUrls;
        }

        private ImageHolder createWidget() {
            if (widget == null) {
                widget = new ImageHolder(Type.multiple, this);
            }
            return widget;
        }
    }

    public static class ImageHolder extends DockPanel implements IWidget {
        interface ImageDataProvider {
            List<String> getImageUrls();
        }

        final Image image = new Image();

        private final Type type;

        private final ViewerControlPanel viewControls = new ViewerControlPanel();

        private final EditorControlPanel editControls = new EditorControlPanel();

        private boolean editable;

        private final ImageDataProvider imageList;

        private int curIdx = 0;

        public ImageHolder(Type type, ImageDataProvider imageList) {
            this.type = type;
            this.imageList = imageList;
            setSize("150px", "150px");
            getElement().getStyle().setProperty("padding", "5px");
            getElement().getStyle().setProperty("border", "1px solid #999");
            // image
            add(image, CENTER);
            setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
            setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);
            // view controls (slideshowLeft, slideshowRight)
            if (type == Type.single) {
                viewControls.setVisible(false);
            }
        }

        public void reset() {
            curIdx = 0;
            onModelChange();
        }

        public void onModelChange() {
            setUrl(ImageHolder.this.imageList.getImageUrls().get(curIdx));
            viewControls.syncState();
        }

        private void setUrl(String url) {
            image.setUrl(url);
            if (image.getWidth() > 0 && image.getHeight() > 0) {
                scaleToFit();
            } else {
                image.setVisible(false);
                image.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                        scaleToFit();
                        image.setVisible(true);
                    }
                });
            }
        }

        private void scaleToFit() {
            if (1.0 * image.getWidth() / image.getHeight() > 1) {
                image.setSize("100%", "auto");
            } else {
                image.setSize("auto", "100%");
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void setEditable(boolean editable) {
            remove(isEditable() ? viewControls : editControls);
            this.editable = editable;
            add(isEditable() ? editControls : viewControls, SOUTH);
        }

        @Override
        public boolean isEditable() {
            return editable;
        }

        class EditorControlPanel extends HorizontalPanel {
            private final Label label;

            public EditorControlPanel() {
                label = new Label("Click to edit");
                label.getElement().getStyle().setProperty("width", "100%");
                label.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                    }
                });
                add(label);
            }
        }

        class ViewerControlPanel extends HorizontalPanel implements ClickHandler {
            private final HTML label;

            private final Image left;

            private final Image right;

            public ViewerControlPanel() {
                label = new HTML();
                label.getElement().getStyle().setProperty("textAlign", "center");

                left = new Image(ImageFactory.getImages().slideshowLeft());
                left.addClickHandler(this);

                right = new Image(ImageFactory.getImages().slideshowRight());
                right.addClickHandler(this);

                add(left);
                add(label);
                add(right);

                setCellHorizontalAlignment(left, HorizontalPanel.ALIGN_LEFT);
                setCellHorizontalAlignment(right, HorizontalPanel.ALIGN_RIGHT);
                setCellHorizontalAlignment(label, HorizontalPanel.ALIGN_CENTER);
                setCellWidth(label, "100%");
                setWidth("100%");
            }

            public void syncState() {
                label.setHTML((curIdx + 1) + " of " + imageList.getImageUrls().size());
            }

            @Override
            public void onClick(ClickEvent event) {
                if (event.getSource() == right && curIdx < imageList.getImageUrls().size() - 1) {
                    curIdx++;
                    onModelChange();
                } else if (event.getSource() == left && curIdx > 0) {
                    curIdx--;
                    onModelChange();
                }
            }
        }
    }
}
