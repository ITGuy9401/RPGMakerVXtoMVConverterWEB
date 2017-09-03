package tk.vxmvconverter.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tk.vxmvconverter.app.controller.resources.ConversionTicket;
import tk.vxmvconverter.app.controller.resources.resourceassembler.ConversionTicketAssembler;
import tk.vxmvconverter.app.service.ConversionService;

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
}
