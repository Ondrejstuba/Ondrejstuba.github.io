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
	const audioFilePath11 = '../view/sounds/victory.wav';
	const audio11 = new Audio(audioFilePath11);

	const audioFilePath12 = '../view/sounds/victorymusic.mp3';
	const audio12 = new Audio(audioFilePath12);

	// Game canvas and context
	var canvas = document.getElementById("gameCanvas");
	var ctx = canvas.getContext("2d");
	var textElement = document.getElementById("gameText");
	var textElementDefeat = document.getElementById("defeat");
	var textElementVictory = document.getElementById("victory");

	// Game variables
	var score = 0;
	var obtainedplayer0 = 0;
	var obtainedplayer1 = 0;
	var obtainedplayer2 = 0;
	var obtainedplayer3 = 0;
	var obtainedplayer4 = 0;
	var enemieskilled = 0;
	var missed = 0;
	var playedfanfare = 0;
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
	var baselife = 80;
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

	document.addEventListener("keydown", function (event) {
		if (event.code === 'Enter') {
			window.location.href = "../view/level2.html";
		}
	});

	// Key down event listener
	document.addEventListener("keydown", function (event) {
		keys[event.code] = true;
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


	// Key up event listener
	document.addEventListener("keyup", function (event) {
		keys[event.code] = false;
	});


	var startMessage = "Here they come again!";

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
				maxfortlife = maxfortlife + 40;
				baselife = baselife + 40;

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
				maxfortlife = maxfortlife + 175;
				baselife = baselife + 175;

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
				ctx.fillText(startMessage, 78, 200);
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









				// Remove bullet if it goes offscreen
				if (player.bullets[i] && player.bullets[i].y < -player.bullets[i].size / 2) {
					player.bullets.splice(i, 1);
					i--;
					score--;
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


			for (var i = 0; i < enemies.length; i++) {
				ctx.fillStyle = enemies[i].color;
				ctx.drawImage(enemyImage, enemies[i].x - enemies[i].size / 2, enemies[i].y - enemies[i].size / 2, enemies[i].size, enemies[i].size);

				enemies[i].move();

				if (!player.dashing) {
					// Check for collision with player
					if (Math.abs(player.x - enemies[i].x) < player.size / 2 + enemies[i].size / 2 && Math.abs(player.y - enemies[i].y) < player.size / 2 + enemies[i].size / 2) {
						gameover = true;
					}
				}

				// Check for collision with bullets
				for (var j = 0; j < player.bullets.length; j++) {
					if (Math.abs(enemies[i] && player.bullets[j].x - enemies[i].x) < player.bullets[j].size / 2 + enemies[i] && enemies[i].size / 2 && Math.abs(player.bullets[j].y - enemies[i].y) < player.bullets[j].size / 2 + enemies[i].size / 2) {
						//enemies.splice(i, 1);
						i--;
						player.bullets.splice(j, 1);
						j--;
						score++;

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
					baselife--;
				}
			}


			for (var i = 0; i < enemies2.length; i++) {
				ctx.fillStyle = enemies2[i].color;
				ctx.drawImage(enemy2Image, enemies2[i].x - enemies2[i].size / 2, enemies2[i].y - enemies2[i].size / 2, enemies2[i].size, enemies2[i].size);

				enemies2[i].move();

				if (!player.dashing) {
					// Check for collision with player
					if (Math.abs(player.x - enemies2[i].x) < player.size / 2 + enemies2[i].size / 2 && Math.abs(player.y - enemies2[i].y) < player.size / 2 + enemies2[i].size / 2) {
						gameover = true;
					}

				}

				// Check for collision with bullets
				for (var j = 0; j < player.bullets.length; j++) {
					if (Math.abs(enemies2[i] && player.bullets[j].x - enemies2[i].x) < player.bullets[j].size / 2 + enemies2[i] && enemies2[i].size / 2 && Math.abs(player.bullets[j].y - enemies2[i].y) < player.bullets[j].size / 2 + enemies2[i].size / 2) {
						enemies2.splice(i, 1);
						i--;
						player.bullets.splice(j, 1);
						j--;
						score++;

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
					baselife--;
				}
			}




			if (baselife <= 0) {
				gameover = true;
			}
			if (gameover) { //ah uz hra skoncila
				document.getElementById("gameContainer").style.display = "none";
				document.getElementById("gameText").style.display = "none";
				document.getElementById("gameText").style.display = "none";
				audio4.play();
				audio6.play();
				audio2.pause();
				audio3.pause();
				textElementDefeat.innerHTML =
					'<div>Mission two</div>' +
					'<div>You have failed in your stand against the evil...</div>' +
					'<div>Your score was ' + score + '</span></div>' +
					'<div>Soldiers fought with a morale of ' + moral + '</span></div>' +
					'<div>Fort life left was ' + baselife + '/' + maxfortlife + '</span></div>' +
					'<div>You have slain ' + enemieskilled + ' aliens' + '</span></div>' +
					'<div>You have missed ' + missed + ' aliens' + '</span></div>' +
					'<div>Ship level obtained: ' + shiplevel + '</span></div>' +
					'<div>Please press enter to leave death screen...</div>';

			}


			if (enemieskilled <= 1500) {
				// Spawn enemies randomly
				if (Math.random() < 0.01) {
					enemies.push(new Enemy());
				}
				// huge wave of enemies
				if (Math.random() < 0.001) {
					if (Math.random() < 0.5) {
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));

						if (Math.random() < 0.5) {
							enemies.push(new Enemy(Math.random() * canvas.width, 0));
							enemies.push(new Enemy(Math.random() * canvas.width, 0));
							enemies.push(new Enemy(Math.random() * canvas.width, 0));
							enemies.push(new Enemy(Math.random() * canvas.width, 0));
							enemies.push(new Enemy(Math.random() * canvas.width, 0));
							enemies.push(new Enemy(Math.random() * canvas.width, 0));
							enemies.push(new Enemy(Math.random() * canvas.width, 0));
							enemies.push(new Enemy(Math.random() * canvas.width, 0));
							
							if (Math.random() < 0.5) {
								enemies.push(new Enemy(Math.random() * canvas.width, 0));
								enemies.push(new Enemy(Math.random() * canvas.width, 0));
								enemies.push(new Enemy(Math.random() * canvas.width, 0));
								

							}

						}
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						enemies.push(new Enemy(Math.random() * canvas.width, 0));
						
					}
				}

				// Spawn enemies2 randomly
				if (Math.random() < 0.005) {
					enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
				}
				if (Math.random() < 0.0005) {
					enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
					enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
					enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
					enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
					enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
					enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
					enemies2.push(new Enemy2(Math.random() * canvas.width, 0));
					
				}

			}




			player.specialSkillCooldown();
			player.dashCooldown();
			player.warpCooldown();
			getColorByShipLevel(shiplevel);
			getSizeByShipLevel(shiplevel);
			getSpeedByShipLevel(shiplevel);

			if (enemies2.length === 0 && enemies.length === 0 && enemieskilled >= 1500) {
				clearInterval(gameLoop);
				gameover = true;
				audio2.pause();
				audio3.pause();
				if (playedfanfare == 0) {
					audio11.play();
					playedfanfare = 1;
				}
				if (playedfanfare == 1) {
					audio12.play();
				}
				document.getElementById("gameContainer").style.display = "none";
				document.getElementById("gameText").style.display = "none";
				document.getElementById("gameText").style.display = "none";
				document.addEventListener("keydown", function (event) {
					if (event.code === 'Enter') {
						window.location.href = "../view/level3intro.html";
					}
				});
				textElementVictory.innerHTML =
					'<div>Mission two won!</div>' +
					'<div>You managed to counter the enemy ambush against all odds!</div>' +
					'<div>Your score was ' + score + '</span></div>' +
					'<div>Soldiers fought with a morale of ' + moral + '</span></div>' +
					'<div>Fort life left was ' + baselife + '/' + maxfortlife + '</span></div>' +
					'<div>You have slain ' + enemieskilled + ' aliens' + '</span></div>' +
					'<div>You have missed ' + missed + ' aliens' + '</span></div>' +
					'<div>Ship level obtained: ' + shiplevel + '</span></div>' +
					'<div>Please press enter to proceed into upcoming battles...</div>';
			}











			textElement.innerHTML = "Mission two" +
				'<div>Objective! Green aliens slain: ' + enemieskilled + '/500</div>' +
				'<div><span class="material-icons">timeline</span><span>' + score + '</span></div>' +
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

