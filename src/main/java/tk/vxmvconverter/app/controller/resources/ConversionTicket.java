package tk.vxmvconverter.app.controller.resources;

import tk.vxmvconverter.app.domain.Status;

public class ConversionTicket {

    private String conversionUuid;
    private Status conversionStatus;
    private String email;

    public String getConversionUuid() {
        return conversionUuid;
    }

    public void setConversionUuid(String conversionUuid) {
        this.conversionUuid = conversionUuid;
    }

    public Status getConversionStatus() {
        return conversionStatus;
    }

    public void setConversionStatus(Status conversionStatus) {
        this.conversionStatus = conversionStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
