package tk.vxmvconverter.app.service;

import org.apache.commons.io.IOUtils;
import tk.vxmvconverter.app.domain.DestinationVersion;
import tk.vxmvconverter.app.dto.FileData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        } finally {
            zis.close();
        }

        switch (destinationVersion) {
            case MV:
                fileDataList.forEach(fileData -> toMVImage(fileData));
            case VX:
                fileDataList.forEach(fileData -> toVXImage(fileData));
        }


    }

    private void toMVImage(FileData fileData) {
    }

    private void toVXImage(FileData fileData) {
    }
}
