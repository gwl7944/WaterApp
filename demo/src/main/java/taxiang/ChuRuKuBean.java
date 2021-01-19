package taxiang;

import java.io.Serializable;

public class ChuRuKuBean implements Serializable {
    public int getSwg_id() {
        return swg_id;
    }

    public void setSwg_id(int swg_id) {
        this.swg_id = swg_id;
    }

    public int getSwg_water_station() {
        return swg_water_station;
    }

    public void setSwg_water_station(int swg_water_station) {
        this.swg_water_station = swg_water_station;
    }

    public int getSwg_applicant() {
        return swg_applicant;
    }

    public void setSwg_applicant(int swg_applicant) {
        this.swg_applicant = swg_applicant;
    }

    public int getSwg_total_merchandise() {
        return swg_total_merchandise;
    }

    public void setSwg_total_merchandise(int swg_total_merchandise) {
        this.swg_total_merchandise = swg_total_merchandise;
    }

    public int getSwg_empty_barrels() {
        return swg_empty_barrels;
    }

    public void setSwg_empty_barrels(int swg_empty_barrels) {
        this.swg_empty_barrels = swg_empty_barrels;
    }

    public int getSwg_approved() {
        return swg_approved;
    }

    public void setSwg_approved(int swg_approved) {
        this.swg_approved = swg_approved;
    }

    public int getSwg_state() {
        return swg_state;
    }

    public void setSwg_state(int swg_state) {
        this.swg_state = swg_state;
    }

    public String getSwg_code() {
        return swg_code;
    }

    public void setSwg_code(String swg_code) {
        this.swg_code = swg_code;
    }

    public String getSwg_water_station_name() {
        return swg_water_station_name;
    }

    public void setSwg_water_station_name(String swg_water_station_name) {
        this.swg_water_station_name = swg_water_station_name;
    }

    public String getSwg_application_time_str() {
        return swg_application_time_str;
    }

    public void setSwg_application_time_str(String swg_application_time_str) {
        this.swg_application_time_str = swg_application_time_str;
    }

    public String getSwg_applicant_name() {
        return swg_applicant_name;
    }

    public void setSwg_applicant_name(String swg_applicant_name) {
        this.swg_applicant_name = swg_applicant_name;
    }

    public String getSwg_approval_time_str() {
        return swg_approval_time_str;
    }

    public void setSwg_approval_time_str(String swg_approval_time_str) {
        this.swg_approval_time_str = swg_approval_time_str;
    }

    public String getSwg_approved_name() {
        return swg_approved_name;
    }

    public void setSwg_approved_name(String swg_approved_name) {
        this.swg_approved_name = swg_approved_name;
    }

    public String getSwg_approval_opinions() {
        return swg_approval_opinions;
    }

    public void setSwg_approval_opinions(String swg_approval_opinions) {
        this.swg_approval_opinions = swg_approval_opinions;
    }

    private int swg_id;
    private int swg_water_station;
    private int swg_applicant;
    private int swg_total_merchandise;
    private int swg_empty_barrels;
    private int swg_approved;
    private int swg_state;
    private String swg_code;
    private String swg_water_station_name;
    private String swg_application_time_str;
    private String swg_applicant_name;
    private String swg_approval_time_str;
    private String swg_approved_name;
    private String swg_approval_opinions;
    private String swg_type;
    private String staff_identification_code;
    public String getSwg_type() {
        return swg_type;
    }

    public void setSwg_type(String swg_type) {
        this.swg_type = swg_type;
    }

    public String getStaff_identification_code() {
        return staff_identification_code;
    }

    public void setStaff_identification_code(String staff_identification_code) {
        this.staff_identification_code = staff_identification_code;
    }
}
