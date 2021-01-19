package taxiang;

import java.io.Serializable;

public class JieSuan implements Serializable {
    public String getDst_check_results() {
        return dst_check_results;
    }

    public void setDst_check_results(String dst_check_results) {
        this.dst_check_results = dst_check_results;
    }

    public String getDst_applicant_name() {
        return dst_applicant_name;
    }

    public void setDst_applicant_name(String dst_applicant_name) {
        this.dst_applicant_name = dst_applicant_name;
    }

    public String getDst_application_time_str() {
        return dst_application_time_str;
    }

    public void setDst_application_time_str(String dst_application_time_str) {
        this.dst_application_time_str = dst_application_time_str;
    }

    public int getDst_applicant() {
        return dst_applicant;
    }

    public void setDst_applicant(int dst_applicant) {
        this.dst_applicant = dst_applicant;
    }

    public int getDst_barrelsdelivered_number() {
        return dst_barrelsdelivered_number;
    }

    public void setDst_barrelsdelivered_number(int dst_barrelsdelivered_number) {
        this.dst_barrelsdelivered_number = dst_barrelsdelivered_number;
    }

    public int getDst_approved() {
        return dst_approved;
    }

    public void setDst_approved(int dst_approved) {
        this.dst_approved = dst_approved;
    }

    public int getDst_barrels_empty() {
        return dst_barrels_empty;
    }

    public void setDst_barrels_empty(int dst_barrels_empty) {
        this.dst_barrels_empty = dst_barrels_empty;
    }

    public int getDst_document_status() {
        return dst_document_status;
    }

    public void setDst_document_status(int dst_document_status) {
        this.dst_document_status = dst_document_status;
    }

    public int getDst_del() {
        return dst_del;
    }

    public void setDst_del(int dst_del) {
        this.dst_del = dst_del;
    }

    public int getDst_id() {
        return dst_id;
    }

    public void setDst_id(int dst_id) {
        this.dst_id = dst_id;
    }

    private String  dst_check_results;
    private String  dst_applicant_name;
    private String  dst_application_time_str;

    private  int  dst_applicant;
    private  int  dst_barrelsdelivered_number;
    private  int  dst_approved;
    private  int  dst_barrels_empty;
    private  int  dst_document_status;
    private  int  dst_del;
    private  int  dst_id;
    private  int  dst_charged_barrels;

    public int getDst_charged_barrels() {
        return dst_charged_barrels;
    }

    public void setDst_charged_barrels(int dst_charged_barrels) {
        this.dst_charged_barrels = dst_charged_barrels;
    }
}
