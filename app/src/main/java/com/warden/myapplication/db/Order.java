package com.warden.myapplication.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hemin on 2017/5/26.
 */

public class Order extends DataSupport {
    private String endLocationName;
    private String startLocationName;
    private String setOutDate;
    private boolean finish;
    private boolean isActive;
    private String previewImageUrl;
    private String orderData;

    public String getSetOutDate() {
        return setOutDate;
    }

    public void setSetOutDate(String setOutDate) {
        this.setOutDate = setOutDate;
    }

    public String getOrderData() {
        return orderData;
    }

    public void setOrderData(String orderData) {
        this.orderData = orderData;
    }

    public String getEndLocationName() {
        return endLocationName;
    }

    public void setEndLocationName(String endLocationName) {
        this.endLocationName = endLocationName;
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public void setStartLocationName(String startLocationName) {
        this.startLocationName = startLocationName;
    }
    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }
}
