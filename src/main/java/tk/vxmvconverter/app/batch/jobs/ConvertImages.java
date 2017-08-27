package tk.vxmvconverter.app.batch.jobs;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.vxmvconverter.app.domain.Conversion;
import tk.vxmvconverter.app.domain.Status;
import tk.vxmvconverter.app.dto.FileData;
import tk.vxmvconverter.app.service.ConversionBatchService;
import tk.vxmvconverter.app.service.ConversionService;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import static tk.vxmvconverter.app.exception.ExceptionUtil.resolveConverterException;

public class ConvertImages implements Runnable {

    private ConversionService conversionService;
    private ConversionBatchService conversionBatchService;

    @Autowired
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Autowired
    public void setConversionBatchService(ConversionBatchService conversionBatchService) {
        this.conversionBatchService = conversionBatchService;
    }

    @Override
    @Transactional
    public void run() {
        try {
            Conversion conversion = conversionService.findNextConversionToDo();
            conversion = processImage(conversion);
            sendEmail(conversion);
        } catch (Exception e) {
            throw resolveConverterException(null, e);
        }
    }

    private void sendEmail(Conversion conversion) {

    }

    private Conversion processImage(Conversion conversion) throws IOException, SQLException {
        conversion.setStatus(Status.PROCESSING);
        conversion = conversionService.update(conversion);
        Blob data = null;
        byte[] bytes = IOUtils.toByteArray(conversion.getData().getBinaryStream());
        switch (conversion.getElementType()) {
            case SINGLE:
                FileData fileData = new FileData();
                fileData.setName(conversion.getUuid());
                fileData.setData(bytes);
                conversionBatchService.convertSingleImage(fileData, conversion.getDestinationVersion());
                data = new SerialBlob(fileData.getData());
                break;
            case ZIP:
                ByteArrayOutputStream byteArrayOutputStream =
                        conversionBatchService.convertZipFile(new ByteArrayInputStream(bytes), conversion.getDestinationVersion());
                data = new SerialBlob(byteArrayOutputStream.toByteArray());
                break;
        }

        conversion.setData(data);
        conversion.setStatus(Status.PROCESSED);
        conversion = conversionService.update(conversion);
        return conversion;
    }
}
