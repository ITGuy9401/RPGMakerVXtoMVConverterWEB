package tk.vxmvconverter.app.exception;

public class ExceptionUtil {
    public static ConverterException resolveConverterException(Error error, Throwable th) {
        if (th instanceof ConverterException) return (ConverterException) th;
        return new ConverterException(error != null ? error : Error.UNKNOWN_ERROR, th);
    }
}
