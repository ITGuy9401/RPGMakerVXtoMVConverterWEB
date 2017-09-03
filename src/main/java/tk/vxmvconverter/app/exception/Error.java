package tk.vxmvconverter.app.exception;

import org.springframework.http.HttpStatus;

public enum Error {
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "VXMV000", "Unknown error"),
    CONCURRENCY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "VXMV001", "Concurrency error saving conversion request"),
    SAVING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "VXMV002", "Error saving conversion request, try again later"),
    CANNOT_FIND_CONVERSION_REQUEST(HttpStatus.BAD_REQUEST, "VXMV003", "Cannot find conversion request"),
    ERROR_COMPRESSING_SINGLE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "VXMV004", "Internal error during re-compressing a single file"),
    ERROR_COMPRESSING_WHOLE_ZIP(HttpStatus.INTERNAL_SERVER_ERROR, "VXMV005", "Internal error during re-building the zip file"),
    ERROR_DECOMPRESSING_UPLOADED_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "VXMV006", "Internal error during the de-compression of the uploaded zip file"),
    ERROR_CONVERTING_THE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "VXMV007", "Internal error while converting the image"),
    CONVERSION_IN_WRONG_STATE(HttpStatus.INTERNAL_SERVER_ERROR, "VXMV008", "Conversion is in wrong state for this operation"),
    WRONG_PASSPHRASE_FOR_TOKEN(HttpStatus.FORBIDDEN, "VXMV009", "Wrong passphrase for the selected conversion");

    private HttpStatus httpStatus;
    private String code;
    private String message;

    private Error(HttpStatus status, String code, String message) {
        this.httpStatus = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
