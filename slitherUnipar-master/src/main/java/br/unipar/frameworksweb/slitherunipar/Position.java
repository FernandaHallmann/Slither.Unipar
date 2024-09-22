package br.unipar.frameworksweb.slitherunipar;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;

    //constructor com parametros
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

