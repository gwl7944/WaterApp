package taxiang;

import android.net.Uri;

import java.io.Serializable;

public class DingDanShopBean implements Serializable {
    public int getOddr_id() {
        return oddr_id;
    }

    public void setOddr_id(int oddr_id) {
        this.oddr_id = oddr_id;
    }

    public int getOddr_commodity_id() {
        return oddr_commodity_id;
    }

    public void setOddr_commodity_id(int oddr_commodity_id) {
        this.oddr_commodity_id = oddr_commodity_id;
    }

    public String getOddr_commodity_name() {
        return oddr_commodity_name;
    }

    public void setOddr_commodity_name(String oddr_commodity_name) {
        this.oddr_commodity_name = oddr_commodity_name;
    }

    public String getOddr_commodity_picture_url() {
        return oddr_commodity_picture_url;
    }

    public void setOddr_commodity_picture_url(String oddr_commodity_picture_url) {
        this.oddr_commodity_picture_url = oddr_commodity_picture_url;
    }

    public int getOddr_number() {
        return oddr_number;
    }

    public void setOddr_number(int oddr_number) {
        this.oddr_number = oddr_number;
    }

    public int getOddr_isback_barrel() {
        return oddr_isback_barrel;
    }

    public void setOddr_isback_barrel(int oddr_isback_barrel) {
        this.oddr_isback_barrel = oddr_isback_barrel;
    }

    public int getOddr_order_id() {
        return oddr_order_id;
    }

    public void setOddr_order_id(int oddr_order_id) {
        this.oddr_order_id = oddr_order_id;
    }

    private int oddr_id;
    private int oddr_commodity_id;
    private String oddr_commodity_name;
    private String oddr_commodity_picture_url;
    private int oddr_number;
    private int oddr_isback_barrel;
    private int oddr_order_id;
}
