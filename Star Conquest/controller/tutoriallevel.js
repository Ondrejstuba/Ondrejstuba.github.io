document.addEventListener('keydown', function(event) {
	if (event.keyCode === 13) {
	  startGame();
	}
  });

  
	

function startGame() {

	document.getElementById("leftwindow").style.display = "none";

	const audioFilePath3 = '../view/sounds/basicshot.mp3';
	const audio3 = new Audio(audioFilePath3);

	const audioFilePath4 = '../view/sounds/defeat.mp3';
	const audio4 = new Audio(audioFilePath4);

	const audioFilePath5 = '../view/sounds/enemyhit.mp3';
	const audio5 = new Audio(audioFilePath5);

	const audioFilePath6 = '../view/sounds/defeatmusic.mp3';
	const audio6 = new Audio(audioFilePath6);

	const audioFilePath7 = '../view/sounds/levelup.mp3';
	const audio7 = new Audio(audioFilePath7);

	// Game canvas and context
	var canvas = document.getElementById("gameCanvas");
	var ctx = canvas.getContext("2d");
	var textElement = document.getElementById("gameText");
	var textElementDefeat = document.getElementById("defeat");

	// Game variables
	var score = 0;
	var obtainedplayer0 = 0;
	var obtainedplayer1 = 0;
	var obtainedplayer2 = 0;
	var obtainedplayer3 = 0;
	var obtainedplayer4 = 0;
	var enemieskilled = 0;
	var missed = 0;
	var paused = false;

	let settingsContainer = document.createElement('div');
	settingsContainer.id = 'settings-container';

	settingsContainer.innerHTML = `
  <div id="settingsmenu">
  <h1>Game paused<br>Press 'Escape' to resume</h1>
  <button onclick="window.location.href = 'game.html';">Main menu</button>
  <button onclick="window.location.href = 'endless.html';">Restart</button>
  </div>
`;


	const style = document.createElement('style');
	style.innerHTML = `
#settingsmenu {
	background-color: rgba(255, 255, 255, 0.1);
	height: 84vh;
	width: 20%;
	color: beige;
	position: fixed;
	top: 1%;
	left: 0;
	border: solid 1px white;
	border-radius: 10px;
	padding: 50px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
  }
  
  #settingsmenu h1 {
	font-size: 2em;
	margin-top: 20px;
	display: flex;
	align-items: center;
	color: grey;
  }
  
  #settingsmenu button {
	font-size: 2em;
	margin-bottom: 10px;
	text-decoration: none;
	color: beige;
	background-color: rgba(255, 255, 255, 0.2);
	border-radius: 5px;
	padding: 10px 20px;
	transition: all 0.2s ease-in-out;
	border: none;
	cursor: pointer;
  }
  
  #settingsmenu button:hover {
	background-color: rgba(255, 255, 255, 0.4);
	transform: scale(1.05);
  }
  
  .settings-buttons {
	display: flex;
	justify-content: space-between;
	margin-bottom: 10px;
  }
  
  .settings-button {
	background-color: rgba(70, 64, 64, 0.8);
	border: none;
	border-radius: 5px;
	color: white;
	cursor: pointer;
	padding: 10px;
	text-align: center;
	width: 33%;
	margin: 6px;
  }
  
  .settings-button.active {
	background-color: #7a9969;
  }
  
  .settings-section {
	margin-bottom: 10px;
  }
  
`;
	document.head.appendChild(style);



	// Game variables
	var baselife = 100;
	var maxfortlife = 100;
	var gameover = false;

	// Key state object
	var keys = {};

	const controlType2 = localStorage.getItem("controls2");
	console.log('Saved shoot control: ', controlType2);
	if (controlType2 === "spacebar") {
		// Key down event listener
	document.addEventListener("keydown", function (event) {
		if (event.keyCode === 32) { // Spacebar key
			player.shoot();
			audio3.currentTime = 0;
			audio3.play();
		}
	});
	} else if (controlType2 === "leftclick") {
		canvas.addEventListener("click", function (event) {
			player.shoot();
			audio3.currentTime = 0;
			audio3.play();
		});
	}

	document.addEventListener("keydown", function(event) {
		if (event.code === 'Enter') {
		  window.location.href = "../view/tutoriallevel.html";
		}
	  });

	// Key down event listener
	document.addEventListener("keydown", function (event) {
		keys[event.code] = true;
	});

	// Key up event listener
	document.addEventListener("keyup", function (event) {
		keys[event.code] = false;
	});

	window.addEventListener("keydown", function (event) {
		if (event.keyCode === 49) { // Enter key code is 13
			player.activateSpecialSkill();
			audio3.currentTime = 0;
			audio3.play();
		}
	});

	window.addEventListener("keydown", function (event) {
		if (event.keyCode === 50) { // Enter key code is 13
			player.dash();
			audio3.currentTime = 0;
			audio3.play();
		}
	});

	window.addEventListener("keydown", function (event) {
		if (event.keyCode === 51) { // Enter key code is 13
			player.timeWarp();
			audio3.currentTime = 0;
			audio3.play();
		}
	});



	var startMessage = "Welcome to the traininig grounds!";

	// Define the audio file path
	const audioFilePath = '../view/sounds/levelstart.mp3';
	const audio = new Audio(audioFilePath);


	const audioFilePaths = [
		'../view/sounds/levelmusic/1.mp3',
		'../view/sounds/levelmusic/2.mp3',
		'../view/sounds/levelmusic/3.mp3',
		'../view/sounds/levelmusic/4.mp3',
		'../view/sounds/levelmusic/5.mp3'
	  ];
	  
	  let currentAudioIndex = 0;
	  
	  // Create and initialize the audio2 object
	  const audio2 = new Audio();
	  audio2.src = audioFilePaths[currentAudioIndex];
	  audio2.load();
	  
	  function playNextRandomSong() {
		
		// Choose a new audio file randomly
		currentAudioIndex = Math.floor(Math.random() * audioFilePaths.length);
		const audioFilePath = audioFilePaths[currentAudioIndex];
		
		// Play the new audio file
		audio2.src = audioFilePath;
	  }
	  
	  // Add an event listener to play the next song when the current one ends
	  audio2.addEventListener('ended', playNextRandomSong);

	var playerImage = new Image();
	playerImage.src = '../view/units/player0.png';

	if (obtainedplayer0 == 0) {
		playerImage.src = '../view/units/player0.png';
		obtainedplayer0 = 1;
		shiplevel = '1 - Shuttlecraft'
	}

	function updateShootDelay() {

		if (score < -150) {
			player.shootDelay = 750;
			moral = -5;

		}


		else if (score >= -150 && score < -50) {
			player.shootDelay = 600;
			moral = -4;
		}
		else if (score >= -50 && score < -10) {
			player.shootDelay = 550;
			moral = -3;
		} else if (score >= -10 && score < 0) {
			player.shootDelay = 500;
			moral = -2;
		}
		else if (score >= -5 && score < 0) {
			player.shootDelay = 450;
			moral = -1;
		} else if (score == 0) {
			player.shootDelay = 400;
			moral = 0;

		} else if (score > 0 && score < 5) {
			player.shootDelay = 300;
			moral = 1;
		} else if (score >= 5 && score < 20) {
			player.shootDelay = 200;
			moral = 2;
		} else if (score >= 20 && score < 40) {
			player.shootDelay = 100;
			moral = 3;
			if (obtainedplayer1 == 0) {
				playerImage.src = '../view/units/player1.png';
				obtainedplayer1 = 1;
				audio7.play();
				shiplevel = '2 - Corvette';
				maxfortlife = maxfortlife + 25;
				baselife = baselife + 25;

			}
		} else if (score >= 40 && score < 100) {
			player.shootDelay = 50;
			moral = 4;
		} else if (score >= 100 && score < 200) {
			player.shootDelay = 30;
			moral = 5;
			if (obtainedplayer2 == 0) {
				playerImage.src = '../view/units/player2.png';
				obtainedplayer2 = 1;
				audio7.play();
				shiplevel = '3 - Cruiser';
				maxfortlife = maxfortlife + 35;
				baselife = baselife + 35;

			}
		} else if (score >= 200 && score < 500) {
			player.shootDelay = 20;
			moral = 6;
		} else if (score >= 500 && score < 1200) {
			player.shootDelay = 10;
			moral = 7;
			if (obtainedplayer3 == 0) {
				playerImage.src = '../view/units/player3.png';
				obtainedplayer3 = 1;
				audio7.play();
				shiplevel = '4 - Battleship';
				maxfortlife = maxfortlife + 50;
				baselife = baselife + 50;

			}
		} else if (score >= 1200 && score < 2000) {
			player.shootDelay = 5;
			moral = 8;
		} else if (score >= 2000) {
			player.shootDelay = 1;
			if (obtainedplayer4 == 0) {
				playerImage.src = '../view/units/player4.png';
				obtainedplayer4 = 1;
				audio7.play();
				shiplevel = '5 - Dreadnought';
				maxfortlife = maxfortlife + 150;
				baselife = baselife + 150;

			}
		}
	}



	const muteButton = document.querySelector('#mute-button');
	muteButton.addEventListener('click', () => {
		// Toggle the "muted" property of all audio objects
		audio3.muted = !audio3.muted;
		audio4.muted = !audio4.muted;
		audio5.muted = !audio5.muted;
		audio7.muted = !audio7.muted;

		// Update the image source of the mute button based on the mute status
		const muteButtonImage = muteButton.querySelector('img');
		muteButtonImage.src = audio3.muted ? '../view/graphics/unmuteaudio.jpg' : '../view/graphics/muteaudio.jpg';
	});


	const muteButton2 = document.querySelector('#mute-button2');
	muteButton2.addEventListener('click', () => {
		// Toggle the "muted" property of another set of audio objects
		audio11.muted = !audio11.muted;
		audio12.muted = !audio12.muted;
		audio2.muted = !audio2.muted;
		audio6.muted = !audio6.muted;

		const muteButtonImage2 = muteButton2.querySelector('img');
		muteButtonImage2.src = audio6.muted ? '../view/graphics/unmutemusic.jpg' : '../view/graphics/mutemusic.jpg';
	});



	// Game loop
	function gameLoop() {

		if (!paused) {



		if (!gameover) {
			// Clear canvas
			ctx.clearRect(0, 0, canvas.width, canvas.height);


			// Display start message
			if (startMessage) {
				audio.play();
				audio2.play();
				ctx.fillStyle = "red";
				ctx.font = "24px Arial";
				ctx.fillText(startMessage, 20, 200);
				setTimeout(function () {
					startMessage = null;
					ctx.clearRect(0, 0, canvas.width, canvas.height);
				}, 5000);
			}

			// Draw player

			ctx.fillStyle = player.color;
			ctx.drawImage(playerImage, player.x - player.size / 2, player.y - player.size / 2, player.size, player.size);


			// Retrieve the controls value from local storage
			const controlType = localStorage.getItem("controls");
			console.log('Saved music volume:', controlType);

			if (controlType === "arrows") {


				if (keys["ArrowUp"]) {
					if (player.y - player.speed - player.size / 2 > 0) {
						player.y -= player.speed;
					}
				}
				if (keys["ArrowDown"]) {
					if (player.y + player.speed + player.size / 2 < canvas.height) {
						player.y += player.speed;
					}
				}
				if (keys["ArrowLeft"]) {
					if (player.x - player.speed - player.size / 2 > 0) {
						player.x -= player.speed;
					}
				}
				if (keys["ArrowRight"]) {
					if (player.x + player.speed + player.size / 2 < canvas.width) {
						player.x += player.speed;
					}
				}

			} else if (controlType === "asdw") {
				if (keys["w"] || keys[119] || keys["W"]) {
					if (player.y - player.speed - player.size / 2 > 0) {
						player.y -= player.speed;
					}
				}
				if (keys["s"] || keys[115] || keys["S"]) {
					if (player.y + player.speed + player.size / 2 < canvas.height) {
						player.y += player.speed;
					}
				}
				if (keys["a"] || keys[97] || keys["A"]) {
					if (player.x - player.speed - player.size / 2 > 0) {
						player.x -= player.speed;
					}
				}
				if (keys["d"] || keys[100] || keys["D"]) {
					if (player.x + player.speed + player.size / 2 < canvas.width) {
						player.x += player.speed;
					}
				}
			}


			for (var i = 0; i < player.bullets.length; i++) {
				ctx.fillStyle = player.bullets[i].color;
				ctx.fillRect(player.bullets[i].x - player.bullets[i].size / 2, player.bullets[i].y - player.bullets[i].size / 2, player.bullets[i].size, player.bullets[i].size);
				player.bullets[i].move();

				for (var j = 0; j < enemies.length; j++) { // <-- Change to 'enemies.length'
					var bullet = player.bullets[i];
					if (bullet && Math.abs(bullet.x - enemies[j].x) < bullet.size / 2 + enemies[j].size / 2 && Math.abs(bullet.y - enemies[j].y) < bullet.size / 2 + enemies[j].size / 2) {
						enemies.splice(j, 1); // <-- Use 'j' to index into 'enemies'
						j--; // <-- Decrement 'j' since we removed an element
						player.bullets.splice(i, 1);
						i--;
						score++;
						enemieskilled++;
						audio5.currentTime = 0;
						audio5.play();
						updateShootDelay();
					}
				}

                for (var j = 0; j < enemies2.length; j++) { // <-- Change to 'enemies2.length'
					var bullet = player.bullets[i];
					if (bullet && Math.abs(bullet.x - enemies2[j].x) < bullet.size / 2 + enemies2[j].size / 2 && Math.abs(bullet.y - enemies2[j].y) < bullet.size / 2 + enemies2[j].size / 2) {
						enemies2.splice(j, 1); // <-- Use 'j' to index into 'enemies2'
						j--; // <-- Decrement 'j' since we removed an element
						player.bullets.splice(i, 1);
						i--;
						score++;
						enemieskilled++;
						audio5.currentTime = 0;
						audio5.play();
						updateShootDelay();
					}
				}
				for (var j = 0; j < enemies3.length; j++) { // <-- Change to 'enemies2.length'
					var bullet = player.bullets[i];
					if (bullet && Math.abs(bullet.x - enemies3[j].x) < bullet.size / 2 + enemies3[j].size / 2 && Math.abs(bullet.y - enemies3[j].y) < bullet.size / 2 + enemies3[j].size / 2) {
						enemies3.splice(j, 1); // <-- Use 'j' to index into 'enemies2'
						j--; // <-- Decrement 'j' since we removed an element
						player.bullets.splice(i, 1);
						i--;
						score++;
						enemieskilled++;
						audio5.currentTime = 0;
						audio5.play();
						updateShootDelay();
					}
				}
				for (var j = 0; j < enemies4.length; j++) { // <-- Change to 'enemies2.length'
					var bullet = player.bullets[i];
					if (bullet && Math.abs(bullet.x - enemies4[j].x) < bullet.size / 2 + enemies4[j].size / 2 && Math.abs(bullet.y - enemies4[j].y) < bullet.size / 2 + enemies4[j].size / 2) {
						enemies4.splice(j, 1); // <-- Use 'j' to index into 'enemies2'
						j--; // <-- Decrement 'j' since we removed an element
						player.bullets.splice(i, 1);
						i--;
						score++;
						enemieskilled++;
						audio5.currentTime = 0;
						audio5.play();;
						updateShootDelay();
					}
				}







				// Remove bullet if it goes offscreen
				if (player.bullets[i] && player.bullets[i].y < -player.bullets[i].size / 2) {
					player.bullets.splice(i, 1);
					i--;
					missed++;
					updateShootDelay();
				}



			}




			// Draw enemies
			var enemyImage = new Image();
			// Generate a random number between 0 and 2
			var randomNum = Math.floor(Math.random() * 3);

			// Set enemyImage source based on random number
			switch (randomNum) {
				case 0:
					enemyImage.src = '../view/units/enemy1.png';
					break;
				case 1:
					enemyImage.src = '../view/units/enemy2.png';
					break;
				case 2:
					enemyImage.src = '../view/units/enemy3.png';
					break;
			}

            // Draw enemies
			var enemy2Image = new Image();
			// Generate a random number between 0 and 2
			var randomNum = Math.floor(Math.random() * 4);

			// Set enemyImage source based on random number
			switch (randomNum) {
				case 0:
					enemy2Image.src = '../view/units/enemy2.1.png';
					break;
				case 1:
					enemy2Image.src = '../view/units/enemy2.2.png';
					break;
				case 2:
					enemy2Image.src = '../view/units/enemy2.3.png';
					break;
				case 3:
					enemy2Image.src = '../view/units/enemy2.4.png';
					break;
			}

			// Draw enemies
			var enemy3Image = new Image();
			// Generate a random number between 0 and 2
			var randomNum = Math.floor(Math.random() * 3);

			// Set enemyImage source based on random number
			switch (randomNum) {
				case 0:
					enemy3Image.src = '../view/units/enemy3.1.png';
					break;
				case 1:
					enemy3Image.src = '../view/units/enemy3.2.png';
					break;
				case 2:
					enemy3Image.src = '../view/units/enemy3.3.png';
					break;
			}

			// Draw enemies
			var enemy4Image = new Image();
			// Generate a random number between 0 and 2
			var randomNum = Math.floor(Math.random() * 4);

			// Set enemyImage source based on random number
			switch (randomNum) {
				case 0:
					enemy4Image.src = '../view/units/enemy4.1.png';
					break;
				case 1:
					enemy4Image.src = '../view/units/enemy4.2.png';
					break;
				case 2:
					enemy4Image.src = '../view/units/enemy4.3.png';
					break;
				case 3:
					enemy4Image.src = '../view/units/enemy4.3.png';
					break;
			}

            for (var i = 0; i < enemies.length; i++) {
				ctx.fillStyle = enemies[i].color;
				ctx.drawImage(enemyImage, enemies[i].x - enemies[i].size / 2, enemies[i].y - enemies[i].size / 2, enemies[i].size, enemies[i].size);

				enemies[i].move();

				// Check for collision with player
				if (Math.abs(player.x - enemies[i].x) < player.size / 2 + enemies[i].size / 2 && Math.abs(player.y - enemies[i].y) < player.size / 2 + enemies[i].size / 2) {
                    var notice = document.createElement('div');
                    notice.textContent = 'You would have died!';
                    notice.style.position = 'absolute';
                    notice.style.top = '5%';
                    notice.style.left = '90%';
                    notice.style.transform = 'translate(-50%, -50%)';
                    notice.style.padding = '5px';
                    notice.style.backgroundColor = 'red';
                    notice.style.color = 'white';
                    notice.style.fontWeight = 'bold';
                    notice.style.fontSize = '16px';
                    document.body.appendChild(notice);
                  
                    // Wait for a short period of time (e.g. 1000ms)
                    setTimeout(function() {
                      // Remove the notice from the DOM
                      notice.parentNode.removeChild(notice);
                    }, 1000);
				}

				// Check for collision with bullets
				for (var j = 0; j < player.bullets.length; j++) {
					if (Math.abs(enemies[i] && player.bullets[j].x - enemies[i].x) < player.bullets[j].size / 2 + enemies[i] && enemies[i].size / 2 && Math.abs(player.bullets[j].y - enemies[i].y) < player.bullets[j].size / 2 + enemies[i].size / 2) {
						//enemies.splice(i, 1);
						i--;
						player.bullets.splice(j, 1);
						j--;
						audio5.play();
						score++;
						updateShootDelay();

					}
				}

				if (player.bullets[i] && player.bullets[i].y < -player.bullets[i].size / 2) {
					player.bullets.splice(i, 1);
					i--;
					score--;
				}

				// Remove enemy if it goes offscreen
				if (enemies[i] && enemies[i].y > canvas.height + enemies[i].size / 2) {
					enemies.splice(i, 1);
					i--;
				}
			}


			for (var i = 0; i < enemies2.length; i++) {
				ctx.fillStyle = enemies2[i].color;
				ctx.drawImage(enemy2Image, enemies2[i].x - enemies2[i].size / 2, enemies2[i].y - enemies2[i].size / 2, enemies2[i].size, enemies2[i].size);

				enemies2[i].move();

				// Check for collision with player
				if (Math.abs(player.x - enemies2[i].x) < player.size / 2 + enemies2[i].size / 2 && Math.abs(player.y - enemies2[i].y) < player.size / 2 + enemies2[i].size / 2) {
                    var notice = document.createElement('div');
                    notice.textContent = 'You would have died!';
                    notice.style.position = 'absolute';
                    notice.style.top = '5%';
                    notice.style.left = '90%';
                    notice.style.transform = 'translate(-50%, -50%)';
                    notice.style.padding = '5px';
                    notice.style.backgroundColor = 'red';
                    notice.style.color = 'white';
                    notice.style.fontWeight = 'bold';
                    notice.style.fontSize = '16px';
                    document.body.appendChild(notice);
                  
                    // Wait for a short period of time (e.g. 1000ms)
                    setTimeout(function() {
                      // Remove the notice from the DOM
                      notice.parentNode.removeChild(notice);
                    }, 1000);
				}

				// Check for collision with bullets
				for (var j = 0; j < player.bullets.length; j++) {
					if (Math.abs(enemies2[i] && player.bullets[j].x - enemies2[i].x) < player.bullets[j].size / 2 + enemies2[i] && enemies2[i].size / 2 && Math.abs(player.bullets[j].y - enemies2[i].y) < player.bullets[j].size / 2 + enemies2[i].size / 2) {
						enemies2.splice(i, 1);
						i--;
						player.bullets.splice(j, 1);
						j--;
						audio5.play();
						score++;
						updateShootDelay();

					}
				}

				if (player.bullets[i] && player.bullets[i].y < -player.bullets[i].size / 2) {
					player.bullets.splice(i, 1);
					i--;
					score--;
				}

				// Remove enemy if it goes offscreen
				if (enemies2[i] && enemies2[i].y > canvas.height + enemies2[i].size / 2) {
					enemies2.splice(i, 1);
					i--;

				}
			}

			for (var i = 0; i < enemies3.length; i++) {
				ctx.fillStyle = enemies3[i].color;
				ctx.drawImage(enemy3Image, enemies3[i].x - enemies3[i].size / 2, enemies3[i].y - enemies3[i].size / 2, enemies3[i].size, enemies3[i].size);

				enemies3[i].move();

				// Check for collision with player
				if (Math.abs(player.x - enemies3[i].x) < player.size / 2 + enemies3[i].size / 2 && Math.abs(player.y - enemies3[i].y) < player.size / 2 + enemies3[i].size / 2) {
                    var notice = document.createElement('div');
                    notice.textContent = 'You would have died!';
                    notice.style.position = 'absolute';
                    notice.style.top = '5%';
                    notice.style.left = '90%';
                    notice.style.transform = 'translate(-50%, -50%)';
                    notice.style.padding = '5px';
                    notice.style.backgroundColor = 'red';
                    notice.style.color = 'white';
                    notice.style.fontWeight = 'bold';
                    notice.style.fontSize = '16px';
                    document.body.appendChild(notice);
                  
                    // Wait for a short period of time (e.g. 1000ms)
                    setTimeout(function() {
                      // Remove the notice from the DOM
                      notice.parentNode.removeChild(notice);
                    }, 1000);
				}

				// Check for collision with bullets
				for (var j = 0; j < player.bullets.length; j++) {
					if (Math.abs(enemies3[i] && player.bullets[j].x - enemies3[i].x) < player.bullets[j].size / 2 + enemies3[i] && enemies3[i].size / 2 && Math.abs(player.bullets[j].y - enemies3[i].y) < player.bullets[j].size / 2 + enemies3[i].size / 2) {
						enemies3.splice(i, 1);
						i--;
						player.bullets.splice(j, 1);
						j--;
						audio5.play();
						score++;
						updateShootDelay();

					}
				}

				if (player.bullets[i] && player.bullets[i].y < -player.bullets[i].size / 2) {
					player.bullets.splice(i, 1);
					i--;
					score--;
				}

				// Remove enemy if it goes offscreen
				if (enemies3[i] && enemies3[i].y > canvas.height + enemies3[i].size / 2) {
					enemies3.splice(i, 1);
					i--;

				}
			}

			for (var i = 0; i < enemies4.length; i++) {
				ctx.fillStyle = enemies4[i].color;
				ctx.drawImage(enemy4Image, enemies4[i].x - enemies4[i].size / 2, enemies4[i].y - enemies4[i].size / 2, enemies4[i].size, enemies4[i].size);

				enemies4[i].move();

				// Check for collision with player
				if (Math.abs(player.x - enemies4[i].x) < player.size / 2 + enemies4[i].size / 2 && Math.abs(player.y - enemies4[i].y) < player.size / 2 + enemies4[i].size / 2) {
                    var notice = document.createElement('div');
                    notice.textContent = 'You would have died!';
                    notice.style.position = 'absolute';
                    notice.style.top = '5%';
                    notice.style.left = '90%';
                    notice.style.transform = 'translate(-50%, -50%)';
                    notice.style.padding = '5px';
                    notice.style.backgroundColor = 'red';
                    notice.style.color = 'white';
                    notice.style.fontWeight = 'bold';
                    notice.style.fontSize = '16px';
                    document.body.appendChild(notice);
                  
                    // Wait for a short period of time (e.g. 1000ms)
                    setTimeout(function() {
                      // Remove the notice from the DOM
                      notice.parentNode.removeChild(notice);
                    }, 1000);
				}

				// Check for collision with bullets
				for (var j = 0; j < player.bullets.length; j++) {
					if (Math.abs(enemies4[i] && player.bullets[j].x - enemies4[i].x) < player.bullets[j].size / 2 + enemies4[i] && enemies4[i].size / 2 && Math.abs(player.bullets[j].y - enemies4[i].y) < player.bullets[j].size / 2 + enemies4[i].size / 2) {
						enemies4.splice(i, 1);
						i--;
						player.bullets.splice(j, 1);
						j--;
						audio5.play();
						score++;
						updateShootDelay();

					}
				}

				if (player.bullets[i] && player.bullets[i].y < -player.bullets[i].size / 2) {
					player.bullets.splice(i, 1);
					i--;
					score--;
				}

				// Remove enemy if it goes offscreen
				if (enemies4[i] && enemies4[i].y > canvas.height + enemies4[i].size / 2) {
					enemies4.splice(i, 1);
					i--;

				}
			}



  



			

			if (Math.random() < 0.009) {
				enemies.push(new Enemy(Math.random() * canvas.width, 0));
				enemies.push(new Enemy(Math.random() * canvas.width, 0));
			}
			if (Math.random() < 0.005) {
				enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
			}
			if (Math.random() < 0.003) {
				enemies3.push(new Enemy3(Math.random() * canvas.width, 0));
			}
			if (Math.random() < 0.002) {
				enemies4.push(new Enemy4(Math.random() * canvas.width, 0));
			}

			player.specialSkillCooldown();
				player.dashCooldown();
				player.warpCooldown();
				getColorByShipLevel(shiplevel);
				getSizeByShipLevel(shiplevel);
				getSpeedByShipLevel(shiplevel);

			textElement.innerHTML = "Training Grounds" +  '<div><span class="material-icons">timeline</span><span>' + score + '</span></div>' +
			'<div><span class="material-icons">military_tech</span><span>' + moral + '</span></div>' +
			'<div><span class="material-icons">favorite</span><span>' + baselife + '/' + maxfortlife + '</span></div>' +
			'<div><span class="material-icons">whatshot</span><span>' + enemieskilled + '</span></div>' +
			'<div><span class="material-icons">redo</span><span>' + missed + '</span></div>' +
			'<div><span class="material-icons">rocket</span><span>' + shiplevel + '</span></div>';

	// Request the next frame of the game loop
requestAnimationFrame(gameLoop);

}







}

else {
let pauseMenu = document.createElement('div');
pauseMenu.id = 'pause-menu';
document.body.appendChild(pauseMenu);
pauseMenu.appendChild(settingsContainer);




}


}

document.addEventListener("keydown", function (event) {
if (event.code === "Escape") {
paused = !paused;
if (!paused) {
	let pauseMenu = document.getElementById('pause-menu');
	if (pauseMenu) {
		pauseMenu.remove();
	}
	requestAnimationFrame(gameLoop);
}
}
});

// Start game loop
requestAnimationFrame(gameLoop);
}
