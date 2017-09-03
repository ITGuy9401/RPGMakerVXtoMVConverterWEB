package tk.vxmvconverter.app.controller.resources;

public class DownloadRequest {

    private String conversionUuid;
    private String requestPassphrase;

    public String getConversionUuid() {
        return conversionUuid;
    }

    public void setConversionUuid(String conversionUuid) {
        this.conversionUuid = conversionUuid;
    }

    public String getRequestPassphrase() {
        return requestPassphrase;
    }

    public void setRequestPassphrase(String requestPassphrase) {
        this.requestPassphrase = requestPassphrase;
    }

}
