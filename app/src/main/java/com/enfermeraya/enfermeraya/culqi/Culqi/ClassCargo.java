package com.enfermeraya.enfermeraya.culqi.Culqi;

public class ClassCargo {
    private  String  amount;
    private  String  currency_code;
    private  String  email;
    private  String  source_id;

    public ClassCargo(String amount, String currency_code, String email, String source_id) {
        this.amount = amount;
        this.currency_code = currency_code;
        this.email = email;
        this.source_id = source_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }
}
