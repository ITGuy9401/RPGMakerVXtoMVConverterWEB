package tk.vxmvconverter.app.service;

import org.apache.commons.io.IOUtils;
import tk.vxmvconverter.app.domain.DestinationVersion;
import tk.vxmvconverter.app.dto.FileData;
import tk.vxmvconverter.app.exception.ConverterException;
import tk.vxmvconverter.app.exception.Error;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ConversionBatchService {

    public ByteArrayOutputStream convertSingleImage(ByteArrayInputStream bais, DestinationVersion destinationVersion) throws Exception {

        return null;
    }

    public ByteArrayOutputStream convertZipFile(ByteArrayInputStream bais, DestinationVersion destinationVersion) throws Exception {
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
            throw new ConverterException(Error.ERROR_DECOMPRESSING_UPLOADED_FILE, e);
        } finally {
            zis.close();
        }

        try {
            switch (destinationVersion) {
                case MV:
                    fileDataList.forEach(fileData -> toMVImage(fileData));
                case VX:
                    fileDataList.forEach(fileData -> toVXImage(fileData));
            }
        } catch (Exception e) {
            throw new ConverterException(Error.ERROR_CONVERTING_THE_IMAGE, e);
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
                    throw new ConverterException(Error.ERROR_COMPRESSING_SINGLE_FILE, e);
                }
            });
        } catch (Exception e) {
            throw new ConverterException(Error.ERROR_COMPRESSING_WHOLE_ZIP, e);
        } finally {
            zos.close();
        }
        return baos;
    }

    private void toMVImage(FileData fileData) {
    }

    private void toVXImage(FileData fileData) {
    }
}
