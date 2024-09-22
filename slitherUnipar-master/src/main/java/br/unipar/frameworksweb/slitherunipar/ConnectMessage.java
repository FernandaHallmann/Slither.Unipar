package br.unipar.frameworksweb.slitherunipar;

public class ConnectMessage {
    private String name;

    // Construtor padrão
    public ConnectMessage() {
    }

    // Construtor com parâmetros
    public ConnectMessage(String name) {
        this.name = name;
    }

    // Getter e setter para 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ConnectMessage{" +
                "name='" + name + '\'' +
                '}';
    }
}

