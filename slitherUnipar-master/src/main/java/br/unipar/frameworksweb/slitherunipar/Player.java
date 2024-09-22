// src/main/java/br/unipar/frameworksweb/slitherunipar/Player.java
package br.unipar.frameworksweb.slitherunipar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String name;
    private Position position;
    private double angle;
    private int points;

    public Player(String name, Position position, double angle) {
        this.name = name;
        this.position = position;
        this.angle = angle;
        this.points = 0;
    }

    public void addPoints(int points) {
        this.points += points;
    }
}