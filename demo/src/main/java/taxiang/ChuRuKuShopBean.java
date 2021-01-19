package taxiang;

public class ChuRuKuShopBean {
    public int getSwdd_id() {
        return swdd_id;
    }

    public void setSwdd_id(int swdd_id) {
        this.swdd_id = swdd_id;
    }

    public int getSwdd_commodity_id() {
        return swdd_commodity_id;
    }

    public void setSwdd_commodity_id(int swdd_commodity_id) {
        this.swdd_commodity_id = swdd_commodity_id;
    }

    public String getSwdd_commodity_name() {
        return swdd_commodity_name;
    }

    public void setSwdd_commodity_name(String swdd_commodity_name) {
        this.swdd_commodity_name = swdd_commodity_name;
    }

    public int getSwdd_staff_warehousing_id() {
        return swdd_staff_warehousing_id;
    }

    public void setSwdd_staff_warehousing_id(int swdd_staff_warehousing_id) {
        this.swdd_staff_warehousing_id = swdd_staff_warehousing_id;
    }

    public int getSwdd_number() {
        return swdd_number;
    }

    public void setSwdd_number(int swdd_number) {
        this.swdd_number = swdd_number;
    }

    public int getSwdd_barrels_empty() {
        return swdd_barrels_empty;
    }

    public void setSwdd_barrels_empty(int swdd_barrels_empty) {
        this.swdd_barrels_empty = swdd_barrels_empty;
    }

    private int swdd_id;
    private int swdd_commodity_id;
    private String swdd_commodity_name;
    private int swdd_staff_warehousing_id;
    private int swdd_number;
    private int swdd_barrels_empty;
    private String swdd_commodity_url;

    public String getSwdd_commodity_url() {
        return swdd_commodity_url;
    }

    public void setSwdd_commodity_url(String swdd_commodity_url) {
        this.swdd_commodity_url = swdd_commodity_url;
    }
}
