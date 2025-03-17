  package com.increff.pos.model.data;

  import java.time.ZonedDateTime;
  import java.util.List;

  import lombok.Getter;
  import lombok.Setter;
  @Getter
  @Setter
  public class InvoiceData {
      private String invoiceId;
      private String customerName;
      private String customerEmail;
      private ZonedDateTime invoiceDate;
      private Double orderTotal;
      private List<InvoiceItemData> invoiceItems;
  }
