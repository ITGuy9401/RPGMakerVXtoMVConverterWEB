package tk.vxmvconverter.app.service;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import tk.vxmvconverter.app.domain.DestinationVersion;
import tk.vxmvconverter.app.dto.FileData;
import tk.vxmvconverter.app.exception.ConverterException;
import tk.vxmvconverter.app.exception.Error;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static tk.vxmvconverter.app.exception.ExceptionUtil.resolveConverterException;

@Service
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
        ByteArrayInputStream in = new ByteArrayInputStream(fileData.getData());
        try {
            int width = 0, height = 0;
            BufferedImage img = ImageIO.read(in);

            height = (img.getHeight() * 150) / 100;
            width = (img.getWidth() * 150) / 100;

            Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imageBuff.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, width, height);

            g2d.setComposite(AlphaComposite.Src);
            g2d.drawImage(scaledImage, 0, 0, null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(imageBuff, "png", buffer);

            fileData.setData(buffer.toByteArray());
        } catch (IOException e) {
            throw new ConverterException(Error.ERROR_CONVERTING_THE_IMAGE, e);
        }
    }

    private void toVXImage(FileData fileData) {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData.getData());
        try {
            int width = 0, height = 0;
            BufferedImage img = ImageIO.read(in);

            height = (img.getHeight() * 100) / 150;
            width = (img.getWidth() * 100) / 150;

            Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imageBuff.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, width, height);

            g2d.setComposite(AlphaComposite.Src);
            g2d.drawImage(scaledImage, 0, 0, null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(imageBuff, "png", buffer);

            fileData.setData(buffer.toByteArray());
        } catch (IOException e) {
            throw new ConverterException(Error.ERROR_CONVERTING_THE_IMAGE, e);
        }
    }
}
