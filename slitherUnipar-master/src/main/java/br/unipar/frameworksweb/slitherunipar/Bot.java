package br.unipar.frameworksweb.slitherunipar;

import java.util.LinkedList;
import java.util.Queue;

public class Bot extends Player {
    private static final double MOVE_DISTANCE = 20.0; // Aumentar a distância de movimento
    private static final int BODY_LENGTH = 5; // Tamanho do corpo da cobrinha
    private Queue<Position> bodyPositions;

    public Bot(String name, Position position, double angle) {
        super(name, position, angle);
        this.bodyPositions = new LinkedList<>();
        this.bodyPositions.add(new Position(position.getX(), position.getY()));
    }

    public Position move() {
        // Guardar a posição atual no corpo da cobrinha
        if (bodyPositions.size() >= BODY_LENGTH) {
            bodyPositions.poll(); // Remove a posição mais antiga
        }
        bodyPositions.add(new Position(getPosition().getX(), getPosition().getY()));

        // Movimento simples: mover em uma direção aleatória
        double newX = getPosition().getX() + (Math.random() * 2 - 1) * MOVE_DISTANCE;
        double newY = getPosition().getY() + (Math.random() * 2 - 1) * MOVE_DISTANCE;

        // Garantir que o bot permaneça dentro dos limites
        newX = Math.max(0, Math.min(newX, 1700));
        newY = Math.max(0, Math.min(newY, 1000));

        //converter posição pra int
        int newXInt = (int) newX;
        int newYInt = (int) newY;

        getPosition().setX(newXInt);
        getPosition().setY(newYInt);

        return getPosition();
    }

    public Queue<Position> getBodyPositions() {
        return bodyPositions;
    }
}