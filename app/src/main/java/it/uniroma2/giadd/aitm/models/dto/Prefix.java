
package it.uniroma2.giadd.aitm.models.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Prefix {

    @SerializedName("timelines")
    @Expose
    private List<Timeline> timelines = new ArrayList<Timeline>();
    @SerializedName("prefix")
    @Expose
    private String prefix;

    /**
     * @return The timelines
     */
    public List<Timeline> getTimelines() {
        return timelines;
    }

    /**
     * @param timelines The timelines
     */
    public void setTimelines(List<Timeline> timelines) {
        this.timelines = timelines;
    }

    /**
     * @return The prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix The prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
