// src/main/java/br/unipar/frameworksweb/slitherunipar/GameController.java
package br.unipar.frameworksweb.slitherunipar;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@EnableWebSocketMessageBroker
public class GameController {

    private final GameState gameState = GameState.getInstance();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping(value = "/game", method = RequestMethod.GET)
    public String game() {
        return "game";
    }

    @MessageMapping("/connect")
    @SendTo("/topic/game")
    public Map<String, Object> connect(ConnectMessage message) {
        String username = message.getName();
        if (!gameState.getPlayers().containsKey(username)) {
            // Gerar posição aleatória para o jogador
            double startX = Math.random() * 1700; // Posição X entre 0 e 400
            double startY = Math.random() * 1000; // Posição Y entre 0 e 400

            // Converter posição para int
            int startXInt = (int) startX;
            int startYInt = (int) startY;

            Player player = new Player(username, new Position(startXInt, startYInt), 0.0);
            gameState.getPlayers().put(username, player);
            System.out.println("Player " + username + " connected");
        }

        return gameState.getState();
    }

    @PostConstruct
    public void initializeBots() {
        for (int i = 1; i <= 10; i++) {
            double startX = (int) (Math.random() * 1700);
            double startY = (int) (Math.random() * 1000);

            //converter posição pra int
            int startXInt = (int) startX;
            int startYInt = (int) startY;

            Bot bot = new Bot("Bot" + i, new Position(startXInt, startYInt), Math.random() * 360);
            gameState.getBots().put(bot.getName(), bot);
        }
        // Start a thread to periodically update game state
        new Thread(this::updateGameState).start();
        new Thread(this::respawnBots).start();
    }


    private void respawnBots() {
        while (true) {
            try {
                Thread.sleep(120000);
                for (int i = 1; i <= 10; i++) {
                    double startX = (int) (Math.random() * 1400);
                    double startY = (int) (Math.random() * 1400);

                    int startXInt = (int) startX;
                    int startYInt = (int) startY;

                    Bot bot = new Bot("Bot" + (gameState.getBots().size() + 1), new Position(startXInt, startYInt), Math.random() * 360);
                    gameState.getBots().put(bot.getName(), bot);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void updateGameState() {

        while (true) {
            try {
                for (Bot bot : gameState.getBots().values()) {
                    Position newPosition = bot.move();
                    bot.setPosition(newPosition);
                    gameState.getBots().put(bot.getName(), bot);
                }

                messagingTemplate.convertAndSend("/topic/game", gameState.getState());

                Thread.sleep(500); // Update every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    // src/main/java/br/unipar/frameworksweb/slitherunipar/GameController.java
    private double calculateDistance(Position pos1, Position pos2) {
        int dx = pos1.getX() - pos2.getX();
        int dy = pos1.getY() - pos2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void checkCollisions() {
        final double COLLISION_MARGIN = 10.0;
        List<Bot> botsToRemove = new ArrayList<>();
        List<Player> playersToRemove = new ArrayList<>();

        // Verifica colisões entre jogadores e bots
        for (Player player : new ArrayList<>(gameState.getPlayers().values())) {
            for (Bot bot : new ArrayList<>(gameState.getBots().values())) {
                if (calculateDistance(player.getPosition(), bot.getPosition()) <= COLLISION_MARGIN) {
                    player.addPoints(bot.getPoints());
                    player.incrementBotsEaten(); // Método para incrementar a contagem de bots comidos
                    botsToRemove.add(bot);
                    System.out.println("Player " + player.getName() + " collided with bot " + bot.getName());
                }
            }
        }

        // Verifica colisões entre jogadores
        for (Player player1 : new ArrayList<>(gameState.getPlayers().values())) {
            for (Player player2 : new ArrayList<>(gameState.getPlayers().values())) {
                if (!player1.equals(player2) && calculateDistance(player1.getPosition(), player2.getPosition()) <= COLLISION_MARGIN) {
                    player1.addPoints(player2.getPoints());
                    playersToRemove.add(player2);
                    System.out.println("Player " + player1.getName() + " collided with player " + player2.getName());
                }
            }
        }

        // Remove bots e jogadores conforme necessário
        for (Bot bot : botsToRemove) {
            gameState.getBots().remove(bot.getName());
            System.out.println("Bot " + bot.getName() + " removed");
        }

        for (Player player : playersToRemove) {
            gameState.getPlayers().remove(player.getName());
            System.out.println("Player " + player.getName() + " removed");
        }
    }


    @MessageMapping("/move")
    @SendTo("/topic/game")
    public Map<String, Object> movePlayer(MoveMessage message) {
        String username = message.getName();
        Position newPosition = message.getPosition();

        Player player = gameState.getPlayers().get(username);
        if (player != null) {
            player.setPosition(newPosition);
            gameState.getPlayers().put(username, player);
        }
        // Check for collisions
        checkCollisions();
        messagingTemplate.convertAndSend("/topic/game", gameState.getState());

        return gameState.getState();
    }
}