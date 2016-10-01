
package it.uniroma2.giadd.aitm.models.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timeline {

    @SerializedName("endtime")
    @Expose
    private String endtime;
    @SerializedName("starttime")
    @Expose
    private String starttime;

    /**
     * @return The endtime
     */
    public String getEndtime() {
        return endtime;
    }

    /**
     * @param endtime The endtime
     */
    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    /**
     * @return The starttime
     */
    public String getStarttime() {
        return starttime;
    }

    /**
     * @param starttime The starttime
     */
    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

}
