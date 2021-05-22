package com.example.barcode_generator;

public class DeliveryContents {
    private String Invoice;
    private String Contents;

    public DeliveryContents() { }

    public String getInvoice() {
        return Invoice;
    }

    public void setInvoice(String invoice) {
        Invoice = invoice;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public DeliveryContents(String Invoice, String Contents){
        this.Contents = Contents;
        this.Invoice = Invoice;
    }
}
