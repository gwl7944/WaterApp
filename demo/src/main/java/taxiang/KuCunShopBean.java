package taxiang;

public class KuCunShopBean {
    private int commodity_id;
    private String commodity_name;
    private String commodity_picture_url;
    private int sddd_margin;
    private int sddd_empty_barrels;
    private int sddd_charged_barrels;
    public int getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(int commodity_id) {
        this.commodity_id = commodity_id;
    }

    public String getCommodity_name() {
        return commodity_name;
    }

    public void setCommodity_name(String commodity_name) {
        this.commodity_name = commodity_name;
    }

    public String getCommodity_picture_url() {
        return commodity_picture_url;
    }

    public void setCommodity_picture_url(String commodity_picture_url) {
        this.commodity_picture_url = commodity_picture_url;
    }

    public int getSddd_margin() {
        return sddd_margin;
    }

    public void setSddd_margin(int sddd_margin) {
        this.sddd_margin = sddd_margin;
    }

    public int getSddd_empty_barrels() {
        return sddd_empty_barrels;
    }

    public void setSddd_empty_barrels(int sddd_empty_barrels) {
        this.sddd_empty_barrels = sddd_empty_barrels;
    }

    public int getSddd_charged_barrels() {
        return sddd_charged_barrels;
    }

    public void setSddd_charged_barrels(int sddd_charged_barrels) {
        this.sddd_charged_barrels = sddd_charged_barrels;
    }
}
