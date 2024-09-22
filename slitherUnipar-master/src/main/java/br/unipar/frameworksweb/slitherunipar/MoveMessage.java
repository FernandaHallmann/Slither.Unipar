package br.unipar.frameworksweb.slitherunipar;

public class MoveMessage {
    private String name;
    private Position position;

    public MoveMessage() {
    }

    public MoveMessage(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "MoveMessage{" +
                "name='" + name + '\'' +
                ", position=" + position.toString() +
                '}';
    }
}