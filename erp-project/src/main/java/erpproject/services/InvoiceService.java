package erpproject.services;

import erpproject.dtos.CreateInvoiceCommand;
import erpproject.dtos.InvoiceDto;
import erpproject.exceptions.CannotCreateInvoiceException;
import erpproject.exceptions.InvoiceNotFoundException;
import erpproject.exceptions.OrderNotFoundException;
import erpproject.mappers.InvoiceMapper;
import erpproject.model.Invoice;
import erpproject.model.Order;
import erpproject.repository.InvoiceRepository;
import erpproject.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final InvoiceMapper mapper;

    public List<InvoiceDto> getAllInvoices() {
         return mapper.toDto(invoiceRepository.findAll());
    }

    public InvoiceDto createInvoice(long orderId, CreateInvoiceCommand command) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        if(invoiceRepository.findInvoiceByOrderId(orderId).isPresent()){
            throw new CannotCreateInvoiceException();
        }
        Invoice invoice= Invoice.builder()
                .invoiceNumber(command.getInvoiceNumber())
                .invoiceDate(command.getInvoiceDate())
                .fulfilmentDate(command.getFulfilmentDate())
                .dueDate(command.getDueDate())
                .vat(command.getVat())
                .methodOfPayment(command.getMethodOfPayment())
                .order(order)
                .build();
        invoiceRepository.save(invoice);
        return mapper.toDto(invoice);
    }

    public InvoiceDto createCreditNote(long invoiceId, long orderId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new InvoiceNotFoundException(orderId));
        CreateInvoiceCommand commandForCreditNote=mapper.toCreateCommand(invoice);
        commandForCreditNote.setInvoiceNumber("C/"+invoice.getInvoiceNumber());
        return createInvoice(orderId, commandForCreditNote);
    }


}
