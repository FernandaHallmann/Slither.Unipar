document.addEventListener("DOMContentLoaded", function() {
    const loginContainer = document.getElementById('login-container');
    const gameContainer = document.getElementById('game-container');
    const connectButton = document.getElementById('connect-button');
    const playerNameInput = document.getElementById('player-name');
    const gameArea = document.getElementById('game-area');
    const playerImages = {};
    const botImages = {};
    let loggedInPlayer = null;
    const playerSpeed = 50;
    const followDistance = 50;
    const lerpFactor = 0.1;
    const updateInterval = 16;
   
    let mousePosition = { x: 0, y: 0 };
    const socket = new SockJS('http://localhost:8080/game', {
        headers: {
            'ngrok-skip-browser-warning': 'true'
        }
    });
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/game', function(response) {
            const data = JSON.parse(response.body);
            updateGameState(data, gameArea, loggedInPlayer, playerImages, botImages);
        });
    });

    connectButton.addEventListener('click', function() {
        const playerName = playerNameInput.value.trim();

        if (playerName) {
            stompClient.send("/app/connect", {}, JSON.stringify({ name: playerName }));
            loggedInPlayer = { name: playerName, position: { x: 0, y: 0 } };
            loginContainer.style.display = 'none';
            gameContainer.style.display = 'block';

            startContinuousUpdate();
        } else {
            alert('Please enter a player name.');
        }
    });

    document.addEventListener('mousemove', function(event) {
        mousePosition.x = event.clientX;
        mousePosition.y = event.clientY;
    });

    function startContinuousUpdate() {
        setInterval(function() {
            if (loggedInPlayer) {
                const targetX = mousePosition.x;
                const targetY = mousePosition.y;
       
                const dx = targetX - loggedInPlayer.position.x;
               
                const dy = targetY - loggedInPlayer.position.y;
                const distance = Math.sqrt(dx * dx + dy * dy);
       
                if (distance >= followDistance) {
                    const moveX = dx / distance * playerSpeed;
                    const moveY = dy / distance * playerSpeed;
       
                    loggedInPlayer.position.x += moveX * lerpFactor;
                    loggedInPlayer.position.y += moveY * lerpFactor;

                    updatePlayerPosition(loggedInPlayer);

                    if (stompClient) {
                        const move = {
                            name: loggedInPlayer.name,
                            position: loggedInPlayer.position
                        };

                        console.log('Enviando nova posição: ', move);
                        stompClient.send("/app/move", {}, JSON.stringify(move));
                    }
                }
            }
        }, updateInterval);
    }
});

function updatePlayerPosition(player) {
    const playerElement = document.querySelector(`[data-player-name="${player.name}"]`);
    if (playerElement) {
        playerElement.style.left = `${player.position.x}px`;
        playerElement.style.top = `${player.position.y}px`;

        // const dx = mousePosition.x - player.position.x;
        // const dy = mousePosition.y - player.position.y;
        // const angle = Math.atan2(dy, dx) * (180 / Math.PI);

        playerElement.style.transform = `rotate(90deg)`;
    }
}


function updateGameState(data, gameArea, loggedInPlayer, playerImages, botImages) {
    gameArea.innerHTML = '';

    data.players.forEach(player => {
        const playerElement = document.createElement('div');
        playerElement.textContent = `${player.name}`;
        playerElement.classList.add('player');
        playerElement.setAttribute('data-player-name', player.name);
        playerElement.style.left = `${player.position.x}px`;
        playerElement.style.top = `${player.position.y}px`;
        playerElement.style.backgroundImage = `url(${getOrCreateImage(player.name, playerImages)})`;
        playerElement.style.backgroundSize = 'contain';
        playerElement.style.backgroundRepeat = 'no-repeat';
        playerElement.style.backgroundPosition = 'center';
        gameArea.appendChild(playerElement);

        if (player.name === loggedInPlayer.name) {
            loggedInPlayer.position = player.position;
        }
    });

    data.bots.forEach(bot => {
        const botElement = document.createElement('div');
        botElement.textContent = `${bot.name}`;
        botElement.classList.add('bot');
        botElement.setAttribute('data-bot-name', bot.name);
        botElement.style.left = `${bot.position.x}px`;
        botElement.style.top = `${bot.position.y}px`;
        botElement.style.backgroundImage = `url(${getOrCreateImage(bot.name, botImages)})`;
        botElement.style.backgroundSize = 'contain';
        botElement.style.backgroundRepeat = 'no-repeat';
        botElement.style.backgroundPosition = 'center';
        gameArea.appendChild(botElement);
    });
}

function getOrCreateImage(id, imageStorage) {
    if (!imageStorage[id]) {
        imageStorage[id] = generateRandomImage();
    }
    return imageStorage[id];
}

function generateRandomImage() {
    const randomIndex = Math.floor(Math.random() * 31) + 1;
    return `/img/naves/nave${randomIndex}.png`;
}