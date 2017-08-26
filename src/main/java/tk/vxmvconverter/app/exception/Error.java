package tk.vxmvconverter.app.exception;

public enum Error {
    CONCURRENCY_ERROR("VXMV001", "Concurrency error saving conversion request"),
    SAVING_ERROR("VXMV002", "Error saving conversion request, try again later"),
    CANNOT_FIND_CONVERSION_REQUEST("VXMV003", "Cannot find conversion request"),
    ERROR_COMPRESSING_SINGLE_FILE("VXMV004", "Internal error during re-compressing a single file"),
    ERROR_COMPRESSING_WHOLE_ZIP("VXMV005", "Internal error during re-building the zip file"),
    ERROR_DECOMPRESSING_UPLOADED_FILE("VXMV006", "Internal error during the de-compression of the uploaded zip file"),
    ERROR_CONVERTING_THE_IMAGE("VXMV007", "Internal error while converting the image");

    private String code;
    private String message;

    private Error(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
