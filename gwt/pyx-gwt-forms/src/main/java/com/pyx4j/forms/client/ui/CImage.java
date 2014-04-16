package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.Image;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.gwt.shared.IFileURLBuilder;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;

/*
 * CImage allows to display and edit a single image or a set of images (using sequential navigation)  
 */
public class CImage extends CField<IFile<?>, NImage> {

    private Image placeholder;

    private Dimension imageSize;

    private final UploadService<?, ?> service;

    private final IFileURLBuilder fileUrlBuilder;

    public CImage(UploadService<?, ?> service, IFileURLBuilder fileURLBuilder) {
        this.service = service;
        this.fileUrlBuilder = fileURLBuilder;
        this.imageSize = new Dimension(250, 250);
        setNativeWidget(new NImage(this));
    }

    public void setThumbnailPlaceholder(Image placeholder) {
        this.placeholder = placeholder;
        getNativeWidget().reset();
    }

    public Image getThumbnailPlaceholder() {
        return placeholder;
    }

    public void setScaleMode(ScaleMode scaleMode) {
        getNativeWidget().setScaleMode(scaleMode);
    }

    public void setImageSize(int width, int height) {
        imageSize = new Dimension(width, height);
        getNativeWidget().resizeToFit();
    }

    public Dimension getImageSize() {
        return imageSize;
    }

    public String getImageUrl(IFile<?> file) {
        if (file == null || file.isNull() || fileUrlBuilder == null) {
            return null;
        } else {
            return fileUrlBuilder.getUrl(file);
        }
    }

    UploadService<?, ?> getUploadService() {
        return service;
    }

}
