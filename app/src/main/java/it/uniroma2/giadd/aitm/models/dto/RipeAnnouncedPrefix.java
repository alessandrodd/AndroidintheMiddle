
package it.uniroma2.giadd.aitm.models.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RipeAnnouncedPrefix {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("server_id")
    @Expose
    private String serverId;
    @SerializedName("status_code")
    @Expose
    private Integer statusCode;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("cached")
    @Expose
    private Boolean cached;
    @SerializedName("see_also")
    @Expose
    private List<Object> seeAlso = new ArrayList<Object>();
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("messages")
    @Expose
    private List<List<String>> messages = new ArrayList<List<String>>();
    @SerializedName("data_call_status")
    @Expose
    private String dataCallStatus;
    @SerializedName("process_time")
    @Expose
    private Integer processTime;
    @SerializedName("build_version")
    @Expose
    private String buildVersion;
    @SerializedName("query_id")
    @Expose
    private String queryId;
    @SerializedName("data")
    @Expose
    private RipeAnnouncedPrefixData data;

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The serverId
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * @param serverId The server_id
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    /**
     * @return The statusCode
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode The status_code
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return The version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version The version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return The cached
     */
    public Boolean getCached() {
        return cached;
    }

    /**
     * @param cached The cached
     */
    public void setCached(Boolean cached) {
        this.cached = cached;
    }

    /**
     * @return The seeAlso
     */
    public List<Object> getSeeAlso() {
        return seeAlso;
    }

    /**
     * @param seeAlso The see_also
     */
    public void setSeeAlso(List<Object> seeAlso) {
        this.seeAlso = seeAlso;
    }

    /**
     * @return The time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time The time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return The messages
     */
    public List<List<String>> getMessages() {
        return messages;
    }

    /**
     * @param messages The messages
     */
    public void setMessages(List<List<String>> messages) {
        this.messages = messages;
    }

    /**
     * @return The dataCallStatus
     */
    public String getDataCallStatus() {
        return dataCallStatus;
    }

    /**
     * @param dataCallStatus The data_call_status
     */
    public void setDataCallStatus(String dataCallStatus) {
        this.dataCallStatus = dataCallStatus;
    }

    /**
     * @return The processTime
     */
    public Integer getProcessTime() {
        return processTime;
    }

    /**
     * @param processTime The process_time
     */
    public void setProcessTime(Integer processTime) {
        this.processTime = processTime;
    }

    /**
     * @return The buildVersion
     */
    public String getBuildVersion() {
        return buildVersion;
    }

    /**
     * @param buildVersion The build_version
     */
    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    /**
     * @return The queryId
     */
    public String getQueryId() {
        return queryId;
    }

    /**
     * @param queryId The query_id
     */
    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    /**
     * @return The data
     */
    public RipeAnnouncedPrefixData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(RipeAnnouncedPrefixData data) {
        this.data = data;
    }

}
