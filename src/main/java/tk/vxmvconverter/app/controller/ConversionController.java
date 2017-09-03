package tk.vxmvconverter.app.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tk.vxmvconverter.app.controller.resources.ConversionTicket;
import tk.vxmvconverter.app.controller.resources.DownloadRequest;
import tk.vxmvconverter.app.controller.resources.resourceassembler.ConversionTicketAssembler;
import tk.vxmvconverter.app.domain.Conversion;
import tk.vxmvconverter.app.domain.ElementType;
import tk.vxmvconverter.app.exception.ConverterException;
import tk.vxmvconverter.app.exception.Error;
import tk.vxmvconverter.app.service.ConversionService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Controller
public class ConversionController {

    private ConversionService conversionService;
    private ConversionTicketAssembler conversionTicketAssembler;

    @Autowired
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Autowired
    public void setConversionTicketAssembler(ConversionTicketAssembler conversionTicketAssembler) {
        this.conversionTicketAssembler = conversionTicketAssembler;
    }

    @RequestMapping(value = "conversion/{conversionUuid}", method = RequestMethod.GET, produces = "application/json")
    public ConversionTicket getConversionStatus(@PathVariable("conversionUuid") String uuid) throws Exception {
        ConversionTicket ticket = conversionTicketAssembler.toResource(conversionService.getConversion(uuid));
        return ticket;
    }

    @RequestMapping(value = "conversion/{conversionUuid}/result", method = RequestMethod.POST, produces = {
            "application/json", "image/png", "application/zip"
    })
    public ResponseEntity getProcessResult(@PathVariable("conversionUuid") String uuid, @ModelAttribute DownloadRequest downloadRequest) throws Exception {
        Conversion conversion = conversionService.getConversion(downloadRequest.getConversionUuid());
        if (!conversion.getTicketPassphrase().equals(downloadRequest.getRequestPassphrase())) {
            throw new ConverterException(Error.WRONG_PASSPHRASE_FOR_TOKEN);
        }

        byte[] bytes = IOUtils.toByteArray(conversion.getData().getBinaryStream());

        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(bytes.length);
        headers.setContentType(conversion.getElementType() == ElementType.ZIP ?
                MediaType.APPLICATION_OCTET_STREAM : MediaType.IMAGE_PNG);
        return new ResponseEntity(inputStreamResource, headers, HttpStatus.OK);
    }
}
