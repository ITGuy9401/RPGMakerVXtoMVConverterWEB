package tk.vxmvconverter.app.batch.jobs;

import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;

import static tk.vxmvconverter.app.exception.ExceptionUtil.resolveConverterException;

public class ConvertImages implements Runnable {

    private ConversionService conversionService;
    private ConversionBatchService conversionBatchService;

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

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

    private void sendEmail(Conversion conversion) throws EmailException, MalformedURLException {
        // load your HTML email template
        String htmlEmailTemplate = ".... <img src=\"http://www.apache.org/images/feather.gif\"> ....";

        // define you base URL to resolve relative resource locations
        URL url = new URL("http://www.apache.org");

        // create the email message
        ImageHtmlEmail email = new ImageHtmlEmail();
        email.setDataSourceResolver(new DataSourceUrlResolver(url));
        email.setHostName(env.getProperty("mail.hostname"));
        email.addTo(conversion.getEmail());
        email.setFrom(env.getProperty("mail.email"), "RPGMaker VX MV Online Converter");
        email.setSubject("You're RPGMaker assets are ready");

        // set the html message
        email.setHtmlMsg(htmlEmailTemplate);

        // set the alternative message
        email.setTextMsg("Your email client does not support HTML messages");

        // send the email
        email.send();

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
