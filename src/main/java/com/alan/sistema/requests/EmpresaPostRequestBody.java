package com.alan.sistema.requests;

public class EmpresaPostRequestBody {

    private String name;
    private String cpfCnpj;
    private String email;

    public EmpresaPostRequestBody(String name, String cpfCnpj, String email) {
        this.name = name;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
    }

    public EmpresaPostRequestBody() {
    }

    public String getName() {
        return name;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public String getEmail() {
        return email;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}



