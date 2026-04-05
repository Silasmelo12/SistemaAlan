package com.alan.sistema.requests;

public class EmpresaPostRequestBody {

    private String name;
    private String cpfCnpj;
    private String email;
    private String telefone;

    public EmpresaPostRequestBody(String name, String cpfCnpj, String email, String telefone) {
        this.name = name;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.telefone = telefone;

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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}



