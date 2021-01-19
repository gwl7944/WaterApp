package taxiang;

public class ShopBean {
    public String getWsdd_commodity_name() {
        return wsdd_commodity_name;
    }

    public void setWsdd_commodity_name(String wsdd_commodity_name) {
        this.wsdd_commodity_name = wsdd_commodity_name;
    }

    public String getWsdd_commodity_url() {
        return wsdd_commodity_url;
    }

    public void setWsdd_commodity_url(String wsdd_commodity_url) {
        this.wsdd_commodity_url = wsdd_commodity_url;
    }

    public int getWsdd_number() {
        return wsdd_number;
    }

    public void setWsdd_number(int wsdd_number) {
        this.wsdd_number = wsdd_number;
    }

    public int getWsdd_id() {
        return wsdd_id;
    }

    public void setWsdd_id(int wsdd_id) {
        this.wsdd_id = wsdd_id;
    }

    public int getWsdd_water_station_id() {
        return wsdd_water_station_id;
    }

    public void setWsdd_water_station_id(int wsdd_water_station_id) {
        this.wsdd_water_station_id = wsdd_water_station_id;
    }

    public int getWsdd_commodity_id() {
        return wsdd_commodity_id;
    }

    public void setWsdd_commodity_id(int wsdd_commodity_id) {
        this.wsdd_commodity_id = wsdd_commodity_id;
    }

    public int getWsdd_barrels_empty() {
        return wsdd_barrels_empty;
    }

    public void setWsdd_barrels_empty(int wsdd_barrels_empty) {
        this.wsdd_barrels_empty = wsdd_barrels_empty;
    }

    private String wsdd_commodity_name;
    private String wsdd_commodity_url;
    private int wsdd_number;
    private int wsdd_id;
    private int wsdd_water_station_id;
    private int wsdd_commodity_id;
    private int wsdd_barrels_empty;
    private int shop_num;
    private int hui_shop_num;
    public int getShop_num() {
        return shop_num;
    }

    public void setShop_num(int shop_num) {
        this.shop_num = shop_num;
    }

    public int getHui_shop_num() {
        return hui_shop_num;
    }

    public void setHui_shop_num(int hui_shop_num) {
        this.hui_shop_num = hui_shop_num;
    }
}
