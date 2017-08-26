package tk.vxmvconverter.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.vxmvconverter.app.domain.*;
import tk.vxmvconverter.app.exception.ConverterException;
import tk.vxmvconverter.app.exception.Error;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.sql.Blob;
import java.time.ZonedDateTime;

@Service
public class ConversionService {

    private ConversionDao conversionDao;

    @Autowired
    public void setConversionDao(ConversionDao conversionDao) {
        this.conversionDao = conversionDao;
    }

    @Transactional
    public String save(byte[] data, ElementType type, DestinationVersion destinationVersion, String email) throws Exception {
        try {
            Conversion conversion = new Conversion();
            conversion.setConversionRequest(ZonedDateTime.now());
            conversion.setLastEdit(ZonedDateTime.now());
            conversion.setElementType(type);
            conversion.setDestinationVersion(destinationVersion);
            conversion.setStatus(Status.RECEIVED);
            conversion.setEmail(email);

            Blob blobData = new SerialBlob(data);
            conversion.setData(blobData);

            return conversionDao.save(conversion).getUuid();
        } catch (Exception e) {
            throw new ConverterException(Error.SAVING_ERROR, e);
        }
    }

    @Transactional
    public Conversion update(Conversion conversion) {
        Conversion originalConversion = get(conversion.getUuid());
        if (originalConversion == null) {
            throw new ConverterException(Error.CANNOT_FIND_CONVERSION_REQUEST, String.format("UUID %s", conversion.getUuid()));
        } else if (!originalConversion.getLastEdit().equals(conversion.getLastEdit())) {
            throw new ConverterException(Error.CONCURRENCY_ERROR, String.format("UUID %s", conversion.getUuid()));
        }

        switch (originalConversion.getStatus()) {
            case RECEIVED:
                if (conversion.getStatus() == Status.PROCESSED)
                    throw new ConverterException(Error.CONVERSION_IN_WRONG_STATE, String.format("Status change from %s to %s", originalConversion.getStatus(), conversion.getStatus()));
                break;
            case PROCESSING:
                if (conversion.getStatus() == Status.RECEIVED)
                    throw new ConverterException(Error.CONVERSION_IN_WRONG_STATE, String.format("Status change from %s to %s", originalConversion.getStatus(), conversion.getStatus()));
                break;
            case PROCESSED:
                if (conversion.getStatus() != Status.PROCESSED)
                    throw new ConverterException(Error.CONVERSION_IN_WRONG_STATE, String.format("Status change from %s to %s", originalConversion.getStatus(), conversion.getStatus()));
                break;
        }
        return conversionDao.save(conversion);
    }

    @Transactional
    public void delete(String uuid) {
        if (get(uuid) == null) {
            throw new ConverterException(Error.CANNOT_FIND_CONVERSION_REQUEST, String.format("UUID %s", uuid));
        }
        conversionDao.delete(uuid);
    }

    public Conversion get(String uuid) {
        return conversionDao.findOne(uuid);
    }
}
