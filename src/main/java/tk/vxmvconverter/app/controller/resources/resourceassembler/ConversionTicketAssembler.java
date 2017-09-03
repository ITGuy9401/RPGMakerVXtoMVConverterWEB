package tk.vxmvconverter.app.controller.resources.resourceassembler;

import org.springframework.stereotype.Component;
import tk.vxmvconverter.app.controller.resources.ConversionTicket;
import tk.vxmvconverter.app.domain.Conversion;

@Component
public class ConversionTicketAssembler {

    public ConversionTicket toResource(Conversion conversion) {
        ConversionTicket ticket = new ConversionTicket();
        ticket.setConversionStatus(conversion.getStatus());
        ticket.setConversionUuid(conversion.getUuid());
        ticket.setEmail(conversion.getEmail());

        return ticket;
    }
}
