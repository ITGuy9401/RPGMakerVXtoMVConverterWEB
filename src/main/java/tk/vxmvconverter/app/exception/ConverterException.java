package tk.vxmvconverter.app.exception;

public class ConverterException extends RuntimeException {
    private Error error;
    private String moreInfo;

    public ConverterException(Error error) {
        super(String.format("%s - %s", error.getCode(), error.getMessage()));
        this.error = error;
        this.moreInfo = "";
    }

    public ConverterException(Error error, String moreInfo) {
        super(String.format("%s - %s - %s", error.getCode(), error.getMessage(), moreInfo));
        this.error = error;
        this.moreInfo = moreInfo;
    }

    public ConverterException(Error error, String moreInfo, Throwable cause) {
        super(String.format("%s - %s - %s", error.getCode(), error.getMessage(), moreInfo), cause);
        this.error = error;
        this.moreInfo = moreInfo;
    }

    public ConverterException(Error error, Throwable cause) {
        super(String.format("%s - %s", error.getCode(), error.getMessage()));
        this.error = error;
        this.moreInfo = "";
    }

    public Error getError() {
        return error;
    }

    public String getMoreInfo() {
        return moreInfo;
    }
}
