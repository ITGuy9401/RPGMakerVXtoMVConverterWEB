package tk.vxmvconverter.app.service;

import org.apache.commons.io.IOUtils;
import tk.vxmvconverter.app.domain.DestinationVersion;
import tk.vxmvconverter.app.dto.FileData;
import tk.vxmvconverter.app.exception.Error;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static tk.vxmvconverter.app.exception.ExceptionUtil.resolveConverterException;

public class ConversionBatchService {

    public void convertSingleImage(FileData fileData, DestinationVersion destinationVersion) {
        try {
            switch (destinationVersion) {
                case MV:
                    toMVImage(fileData);
                    break;
                case VX:
                    toVXImage(fileData);
                    break;
            }
        } catch (Throwable th) {
            throw resolveConverterException(null, th);
        }
    }

    public ByteArrayOutputStream convertZipFile(ByteArrayInputStream bais, DestinationVersion destinationVersion) {
        try {
            List<FileData> fileDataList = new ArrayList<>();
            ZipInputStream zis = new ZipInputStream(bais);
            try {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    FileData fileData = new FileData();
                    fileData.setName(entry.getName());
                    fileData.setData(IOUtils.toByteArray(zis));
                    fileDataList.add(fileData);
                }
            } catch (Exception e) {
                throw resolveConverterException(Error.ERROR_DECOMPRESSING_UPLOADED_FILE, e);
            } finally {
                try {
                    zis.close();
                } catch (IOException e) {
                }
            }

            try {
                switch (destinationVersion) {
                    case MV:
                        fileDataList.forEach(fileData -> toMVImage(fileData));
                        break;
                    case VX:
                        fileDataList.forEach(fileData -> toVXImage(fileData));
                        break;
                }
            } catch (Exception e) {
                throw resolveConverterException(Error.ERROR_CONVERTING_THE_IMAGE, e);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            try {
                fileDataList.forEach(fileData -> {
                    try {
                        ZipEntry entry = new ZipEntry(fileData.getName());
                        entry.setSize(fileData.getData().length);
                        zos.putNextEntry(entry);
                        zos.write(fileData.getData());
                        zos.closeEntry();
                    } catch (Exception e) {
                        throw resolveConverterException(Error.ERROR_COMPRESSING_SINGLE_FILE, e);
                    }
                });
            } catch (Exception e) {
                throw resolveConverterException(Error.ERROR_COMPRESSING_WHOLE_ZIP, e);
            } finally {
                try {
                    zis.close();
                } catch (IOException e) {
                }
            }
            return baos;
        } catch (Throwable th) {
            throw resolveConverterException(null, th);
        }
    }

    private void toMVImage(FileData fileData) {
    }

    private void toVXImage(FileData fileData) {
    }
}
