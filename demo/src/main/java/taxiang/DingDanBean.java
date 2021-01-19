package taxiang;

import java.io.Serializable;
import java.util.List;

public class DingDanBean implements Serializable {
    public String getOrder_order_number() {
        return order_order_number;
    }

    public void setOrder_order_number(String order_order_number) {
        this.order_order_number = order_order_number;
    }

    public String getOrder_place_order_time() {
        return order_place_order_time;
    }

    public void setOrder_place_order_time(String order_place_order_time) {
        this.order_place_order_time = order_place_order_time;
    }

    public String getOrder_method_payment() {
        return order_method_payment;
    }

    public void setOrder_method_payment(String order_method_payment) {
        this.order_method_payment = order_method_payment;
    }

    public String getOrder_payment_channel() {
        return order_payment_channel;
    }

    public void setOrder_payment_channel(String order_payment_channel) {
        this.order_payment_channel = order_payment_channel;
    }

    public String getOrder_estimated_time() {
        return order_estimated_time;
    }

    public void setOrder_estimated_time(String order_estimated_time) {
        this.order_estimated_time = order_estimated_time;
    }

    public String getOrder_actual_time() {
        return order_actual_time;
    }

    public void setOrder_actual_time(String order_actual_time) {
        this.order_actual_time = order_actual_time;
    }

    public String getOrder_remarks() {
        return order_remarks;
    }

    public void setOrder_remarks(String order_remarks) {
        this.order_remarks = order_remarks;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getOrder_customer_id() {
        return order_customer_id;
    }

    public void setOrder_customer_id(int order_customer_id) {
        this.order_customer_id = order_customer_id;
    }

    public int getOrder_commodity_id() {
        return order_commodity_id;
    }

    public void setOrder_commodity_id(int order_commodity_id) {
        this.order_commodity_id = order_commodity_id;
    }

    public int getOrder_address_id() {
        return order_address_id;
    }

    public void setOrder_address_id(int order_address_id) {
        this.order_address_id = order_address_id;
    }

    public int getOrder_number() {
        return order_number;
    }

    public void setOrder_number(int order_number) {
        this.order_number = order_number;
    }

    public int getOrder_isback_barrel() {
        return order_isback_barrel;
    }

    public void setOrder_isback_barrel(int order_isback_barrel) {
        this.order_isback_barrel = order_isback_barrel;
    }

    public int getOrder_staff_id() {
        return order_staff_id;
    }

    public void setOrder_staff_id(int order_staff_id) {
        this.order_staff_id = order_staff_id;
    }

    public double getOrder_money() {
        return order_money;
    }

    public void setOrder_money(double order_money) {
        this.order_money = order_money;
    }

    public int getOrder_state() {
        return order_state;
    }

    public void setOrder_state(int order_state) {
        this.order_state = order_state;
    }

    public int getOrder_del() {
        return order_del;
    }

    public void setOrder_del(int order_del) {
        this.order_del = order_del;
    }

    public String getOrder_place_order_time_starttime() {
        return order_place_order_time_starttime;
    }

    public void setOrder_place_order_time_starttime(String order_place_order_time_starttime) {
        this.order_place_order_time_starttime = order_place_order_time_starttime;
    }

    public String getOrder_place_order_time_endtime() {
        return order_place_order_time_endtime;
    }

    public void setOrder_place_order_time_endtime(String order_place_order_time_endtime) {
        this.order_place_order_time_endtime = order_place_order_time_endtime;
    }
    private String order_customer_name;
    private String order_commodity_name;
    private String order_commodity_picture_url;
    private String order_address_name;
    private String order_staff_name;
    private String order_order_number;
    private String order_place_order_time;
    private String order_method_payment;
    private String order_payment_channel;
    private String order_estimated_time;
    private String order_actual_time;
    private String order_remarks;
    private int order_id;
    private int order_customer_id;
    private int order_commodity_id;
    private int order_address_id;
    private int order_number;
    private int order_isback_barrel;
    private int order_staff_id;
    private double order_money;
    private int order_state;
    private int order_del;
    private int order_type;
    private String order_place_order_time_starttime;
    private String order_place_order_time_endtime;
    private String order_place_order_time_str;
    private String order_estimated_time_str;
    private String order_actual_time_str;
    private String order_customer_telephone;
    private String order_cas_latitude;
    private String order_cas_longitude;
    private List<DingDanShopBean> commodityList;
    public String getOrder_place_order_time_str() {
        return order_place_order_time_str;
    }

    public void setOrder_place_order_time_str(String order_place_order_time_str) {
        this.order_place_order_time_str = order_place_order_time_str;
    }

    public String getOrder_estimated_time_str() {
        return order_estimated_time_str;
    }

    public void setOrder_estimated_time_str(String order_estimated_time_str) {
        this.order_estimated_time_str = order_estimated_time_str;
    }

    public String getOrder_actual_time_str() {
        return order_actual_time_str;
    }

    public void setOrder_actual_time_str(String order_actual_time_str) {
        this.order_actual_time_str = order_actual_time_str;
    }

    public String getOrder_customer_name() {
        return order_customer_name;
    }

    public void setOrder_customer_name(String order_customer_name) {
        this.order_customer_name = order_customer_name;
    }

    public String getOrder_commodity_name() {
        return order_commodity_name;
    }

    public void setOrder_commodity_name(String order_commodity_name) {
        this.order_commodity_name = order_commodity_name;
    }

    public String getOrder_commodity_picture_url() {
        return order_commodity_picture_url;
    }

    public void setOrder_commodity_picture_url(String order_commodity_picture_url) {
        this.order_commodity_picture_url = order_commodity_picture_url;
    }

    public String getOrder_address_name() {
        return order_address_name;
    }

    public void setOrder_address_name(String order_address_name) {
        this.order_address_name = order_address_name;
    }

    public String getOrder_staff_name() {
        return order_staff_name;
    }

    public void setOrder_staff_name(String order_staff_name) {
        this.order_staff_name = order_staff_name;
    }

    public String getOrder_customer_telephone() {
        return order_customer_telephone;
    }

    public void setOrder_customer_telephone(String order_customer_telephone) {
        this.order_customer_telephone = order_customer_telephone;
    }

    public List<DingDanShopBean> getCommodityList() {
        return commodityList;
    }

    public void setCommodityList(List<DingDanShopBean> commodityList) {
        this.commodityList = commodityList;
    }

    public String getOrder_cas_latitude() {
        return order_cas_latitude;
    }

    public void setOrder_cas_latitude(String order_cas_latitude) {
        this.order_cas_latitude = order_cas_latitude;
    }

    public String getOrder_cas_longitude() {
        return order_cas_longitude;
    }

    public void setOrder_cas_longitude(String order_cas_longitude) {
        this.order_cas_longitude = order_cas_longitude;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }
}
