package erpproject.mappers;

import erpproject.dtos.CreateInvoiceCommand;
import erpproject.dtos.InvoiceDto;
import erpproject.model.Invoice;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    List<InvoiceDto> toDto(List<Invoice> invoices);
    InvoiceDto toDto(Invoice invoice);

    CreateInvoiceCommand toCreateCommand(Invoice invoice);
}
