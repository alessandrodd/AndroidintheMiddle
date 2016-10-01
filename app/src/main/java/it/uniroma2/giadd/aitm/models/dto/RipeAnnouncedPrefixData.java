
package it.uniroma2.giadd.aitm.models.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RipeAnnouncedPrefixData {

    @SerializedName("resource")
    @Expose
    private String resource;
    @SerializedName("prefixes")
    @Expose
    private List<Prefix> prefixes = new ArrayList<Prefix>();
    @SerializedName("query_starttime")
    @Expose
    private String queryStarttime;
    @SerializedName("latest_time")
    @Expose
    private String latestTime;
    @SerializedName("query_endtime")
    @Expose
    private String queryEndtime;
    @SerializedName("earliest_time")
    @Expose
    private String earliestTime;

    /**
     * @return The resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * @param resource The resource
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * @return The prefixes
     */
    public List<Prefix> getPrefixes() {
        return prefixes;
    }

    /**
     * @param prefixes The prefixes
     */
    public void setPrefixes(List<Prefix> prefixes) {
        this.prefixes = prefixes;
    }

    /**
     * @return The queryStarttime
     */
    public String getQueryStarttime() {
        return queryStarttime;
    }

    /**
     * @param queryStarttime The query_starttime
     */
    public void setQueryStarttime(String queryStarttime) {
        this.queryStarttime = queryStarttime;
    }

    /**
     * @return The latestTime
     */
    public String getLatestTime() {
        return latestTime;
    }

    /**
     * @param latestTime The latest_time
     */
    public void setLatestTime(String latestTime) {
        this.latestTime = latestTime;
    }

    /**
     * @return The queryEndtime
     */
    public String getQueryEndtime() {
        return queryEndtime;
    }

    /**
     * @param queryEndtime The query_endtime
     */
    public void setQueryEndtime(String queryEndtime) {
        this.queryEndtime = queryEndtime;
    }

    /**
     * @return The earliestTime
     */
    public String getEarliestTime() {
        return earliestTime;
    }

    /**
     * @param earliestTime The earliest_time
     */
    public void setEarliestTime(String earliestTime) {
        this.earliestTime = earliestTime;
    }

}
