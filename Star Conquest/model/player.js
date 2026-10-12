var moral = 0;
var shiplevel = 0;
var ctx = canvas.getContext("2d");

function getColorByShipLevel(shipLevel) {
    switch (shipLevel) {
        case '2 - Corvette':
            return 'gray';
        case '3 - Cruiser':
            return 'blue';
        case '4 - Battleship':
            return 'pink';
        case '5 - Dreadnought':
            return 'yellow';
        default:
            return 'purple'; // Default color
    }
}
function getSizeByShipLevel(shipLevel) {
    switch (shipLevel) {
        case '2 - Corvette':
            return 5;
        case '3 - Cruiser':
            return 8;
        case '4 - Battleship':
            return 11;
        case '5 - Dreadnought':
            return 21;
        default:
            return 2;
    }
}
function getSpeedByShipLevel(shipLevel) {
    switch (shipLevel) {
        case '2 - Corvette':
            return 5;
        case '3 - Cruiser':
            return 7;
        case '4 - Battleship':
            return 10;
        case '5 - Dreadnought':
            return 20;
        default:
            return 2;
    }
}

var player = {
    x: canvas.width / 2,
    y: canvas.height / 2,
    size: 35,
    speed: 3,
    color: "blue",
    bullets: [],
    lastShotTime: 0,
    angle: 0,
    shootDelay: moral,
    specialSkillDelay: 45000,
    lastSpecialSkillTime: 0,
    specialSkillActive: false,
    dashDistance: 100,
    dashDelay: 30000,
    lastDashTime: 0,
    dashing: false,
    warpDelay: 30000,
    lastWarpTime: 0,
    warping: false,

    // Add a list of observers to the player object
    observers: [],

    // Attach an observer to the player object
    attach: function(observer) {
        this.observers.push(observer);
    },

    // Notify all observers when the player position changes
    notify: function() {
        for (var i = 0; i < this.observers.length; i++) {
            this.observers[i].update(this.x, this.y);
        }
    },

    // Update the player position
    updatePosition: function(newX, newY) {
        this.x = newX;
        this.y = newY;
        this.notify(); // Notify all observers of the new position
    },

		shoot: function () {
			var currentTime = new Date().getTime();
			if (currentTime - this.lastShotTime >= this.shootDelay) {
				this.bullets.push(new Bullet(this.x, this.y, this.color, shiplevel, shiplevel, shiplevel));
				this.lastShotTime = currentTime;
			}
		},

		activateSpecialSkill: function () {
			var currentTime = new Date().getTime();
			if (currentTime - this.lastSpecialSkillTime >= this.specialSkillDelay && !this.specialSkillActive) {
				this.specialSkillActive = true;
				this.lastSpecialSkillTime = currentTime;

				// Perform the special skill action

				// Display the message on the game screen
				var message = document.createElement("div");
				message.innerText = "1 - Starburst Casted";
				message.style.position = "absolute";
				message.style.top = "20px";
				message.style.right = "20px";
				message.style.color = "purple";
				message.style.fontSize = "24px";
				document.body.appendChild(message);

				// Remove the message from the game screen after 1 second
				setTimeout(function () {
					document.body.removeChild(message);
				}, 3000);

				var bulletCount = 30;
				var angle = 360 / bulletCount;
				for (var i = 0; i < bulletCount; i++) {
					var bullet = new Bullet(this.x, this.y, "purple", 1);
					bullet.speed = 8;
					bullet.size = 6;
					bullet.angle = angle * i;
					bullet.move = function () {
						this.x += this.speed * Math.sin(this.angle * Math.PI / 180);
						this.y -= this.speed * Math.cos(this.angle * Math.PI / 180);
					};
					this.bullets.push(bullet);
					this.bullets.push(bullet);
					this.bullets.push(bullet);
				}

				// Reset the special skill flag after 2 seconds
				setTimeout(function () {
					player.specialSkillActive = false;
				}, 2000);
			}
		},

		dash: function () {
			var currentTime = new Date().getTime();
			if (currentTime - this.lastDashTime >= this.dashDelay) {
				// Calculate the new position after dashing
				var newX = this.x + this.dashDistance * Math.sin(this.angle * Math.PI / 180);
				var newY = this.y - this.dashDistance * Math.cos(this.angle * Math.PI / 180);

				// Check if the new position is within the canvas bounds
				if (newX >= 0 && newX <= canvas.width && newY >= 0 && newY <= canvas.height) {
					// Display the message on the game screen
				var message = document.createElement("div");
				message.innerText = "2 - Vanguard Casted";
				message.style.position = "absolute";
				message.style.top = "40px";
				message.style.right = "20px";
				message.style.color = "purple";
				message.style.fontSize = "24px";
				// Remove the message from the game screen after 1 second
				setTimeout(function () {
					document.body.removeChild(message);
				}, 3000);
				document.body.appendChild(message);
					this.x = newX;
					this.y = newY;
					this.lastDashTime = currentTime;
					this.dashing = true;
					// Reset the special skill flag after 2 seconds
					setTimeout(() => {
						this.dashing = false;
					}, 4000);
				}
			}
		},

		timeWarp: function() {
			var currentTime = new Date().getTime();
			if (currentTime - this.lastWarpTime >= this.warpDelay) {
			  
			  // Perform the time warp action
			  var warpDuration = 6000; // The duration of the time warp in milliseconds
			  var warpSpeed = 1.6; // The speed factor of the time warp
			  var originalSpeed = this.speed;
		  
			  // Increase the player speed by the warpSpeed factor
			  this.speed *= warpSpeed;
		  
			  // Display the message on the game screen
			  var message = document.createElement("div");
			  message.innerText = "3 - Velocity casted";
			  message.style.position = "absolute";
			  message.style.top = "60px";
			  message.style.right = "20px";
			  message.style.color = "purple";
			  message.style.fontSize = "24px";
			  document.body.appendChild(message);
		  
			  // Remove the message from the game screen after 1 second
			  setTimeout(function () {
				document.body.removeChild(message);
			  }, 3000);

			  this.lastWarpTime = currentTime;
					this.warping = true;
		  
			  // Reset the player speed after the time warp duration
			  setTimeout(() => {
				player.speed = originalSpeed;
				player.warping = false;
			  }, warpDuration);
			}
		  },
		  
		specialSkillCooldown: function () {
			var currentTime = new Date().getTime();
			var timeLeft = Math.max(0, this.specialSkillDelay - (currentTime - this.lastSpecialSkillTime));
			var secondsLeft = (timeLeft / 1000).toFixed(1);
			var cooldownText = "1: " + secondsLeft + "s";
			ctx.fillStyle = "white";
			ctx.font = "bold 20px Arial";
			ctx.fillText(cooldownText, 10, canvas.height - 30);
		},

		dashCooldown: function () {
			var currentTime = new Date().getTime();
			var timeLeft = Math.max(0, this.dashDelay - (currentTime - this.lastDashTime));
			var secondsLeft = (timeLeft / 1000).toFixed(1);
			var cooldownText = "2: " + secondsLeft + "s";
			ctx.fillStyle = "white";
			ctx.font = "bold 20px Arial";
			ctx.fillText(cooldownText, 10, canvas.height - 60);
		},

		warpCooldown: function () {
			var currentTime = new Date().getTime();
			var timeLeft = Math.max(0, this.warpDelay - (currentTime - this.lastWarpTime));
			var secondsLeft = (timeLeft / 1000).toFixed(1);
			var cooldownText = "3: " + secondsLeft + "s";
			ctx.fillStyle = "white";
			ctx.font = "bold 20px Arial";
			ctx.fillText(cooldownText, 10, canvas.height - 90);
		},
	};

	

	class Bullet {
		constructor(x, y, color, shipLevel) {
			this.x = x;
			this.y = y;
			this.size = getSizeByShipLevel(shipLevel);
			this.speed = getSpeedByShipLevel(shipLevel);
			this.color = getColorByShipLevel(shipLevel);
			this.angle = player.angle;
			this.move = function () {
				this.x += this.speed * Math.sin(this.angle * Math.PI / 180);
				this.y -= this.speed * Math.cos(this.angle * Math.PI / 180);
			};
		}
	}