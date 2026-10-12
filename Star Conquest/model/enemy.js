var ctx = canvas.getContext("2d");
var enemies = [];
	var enemies2 = [];
	var enemies3 = [];
	var enemies4 = [];
	var enemies5 = [];
    var enemies6 = [];

    function Enemy6() {
        this.x = Math.floor(Math.random() * canvas.width);
        this.y = 25;
        this.size = Math.floor(Math.random() * (60 - 30 + 1) + 20);
        this.speed = 0.3;
        this.color = "purple";
        this.health = 10;
        this.direction = Math.random() < 0.5 ? -1 : 1; // randomize the initial movement direction
        this.sizeChange = 10; // maximum amount the size can change in each frame
        this.teleportCounter = 0; // initialize teleport counter
        this.teleportInterval = 340; // set teleport interval to 100 frames
        this.spawnRate = 0.00001; // starting spawn rate
    
    
        this.move = function () {
            if (this.teleportCounter == this.teleportInterval) {
                this.x = Math.floor(Math.random() * canvas.width); // teleport to a random x position
                this.y = Math.floor(Math.random() * canvas.height / 2); // teleport to a random y position
                this.teleportCounter = 0; // reset the teleport counter
            } else {
                this.teleportCounter++; // increment the teleport counter
            }
    
            if (this.x < 0 || this.x > canvas.width - this.size) {
                this.direction = -this.direction; // reverse the direction when hitting the edge of the canvas
            }
            this.x += this.direction * this.speed; // move horizontally
            this.size += 0.09; // increase the size by 0.05 in each frame
    
            // Increase spawn rate over time
            this.spawnRate += 0.00001;
    
            // Special behavior: periodically spawn smaller enemies from this enemy's position
            if (Math.random() < this.spawnRate) {
                enemies.push(new Enemy(this.x, this.y));
            }
            if (Math.random() < this.spawnRate * 0.5) {
                enemies2.push(new Enemy2(this.x, this.y));
            }
            if (Math.random() < this.spawnRate * 0.4) {
                enemies3.push(new Enemy3(this.x, this.y));
            }
            if (Math.random() < this.spawnRate * 0.3) {
                enemies4.push(new Enemy4(this.x, this.y));
            }
        };
    }


function Enemy5() {
    this.x = Math.floor(Math.random() * canvas.width);
    this.y = 25;
    this.size = Math.floor(Math.random() * (60 - 30 + 1) + 20);
    this.speed = 0.3;
    this.color = "purple";
    this.health = 1000;
    this.direction = Math.random() < 0.5 ? -1 : 1; // randomize the initial movement direction
    this.sizeChange = 4; // maximum amount the size can change in each frame
    this.teleportCounter = 0; // initialize teleport counter
    this.teleportInterval = 540; // set teleport interval to 100 frames
    this.spawnRate = 0.001; // starting spawn rate


    this.move = function () {
        if (this.teleportCounter == this.teleportInterval) {
            this.x = Math.floor(Math.random() * canvas.width); // teleport to a random x position
            this.y = Math.floor(Math.random() * canvas.height / 2); // teleport to a random y position
            this.teleportCounter = 0; // reset the teleport counter
        } else {
            this.teleportCounter++; // increment the teleport counter
        }

        if (this.x < 0 || this.x > canvas.width - this.size) {
            this.direction = -this.direction; // reverse the direction when hitting the edge of the canvas
        }
        this.x += this.direction * this.speed; // move horizontally
        this.size += 0.01; // increase the size by 0.05 in each frame

        // Increase spawn rate over time
        this.spawnRate += 0.000005;

        // Special behavior: periodically spawn smaller enemies from this enemy's position
        if (Math.random() < this.spawnRate) {
            enemies.push(new Enemy(this.x, this.y));
        }
        if (Math.random() < this.spawnRate * 0.5) {
            enemies2.push(new Enemy2(this.x, this.y));
        }
        if (Math.random() < this.spawnRate * 0.4) {
            enemies3.push(new Enemy3(this.x, this.y));
        }
        if (Math.random() < this.spawnRate * 0.3) {
            enemies4.push(new Enemy4(this.x, this.y));
        }
    };
}







function Enemy(x, y) {
    this.x = x;
    this.y = y;
    this.size = Math.floor(Math.random() * (50 - 20 + 1) + 22);
    this.speed = 1;
    this.color = "red";
  
    this.move = function() {
      // Enemy follows the player's y position
      if (player.y < this.y) {
        this.y -= this.speed;
      } else if (player.y > this.y) {
        this.y += this.speed;
      }
  
      // Enemy follows the player's x position
      if (player.x < this.x) {
        this.x -= this.speed;
      } else if (player.x > this.x) {
        this.x += this.speed;
      }
    };
  
    // Update the enemy position when notified by the player object
    this.update = function(playerX, playerY) {
      this.move();
    };
  
    // Attach the enemy object as an observer to the player object
    player.attach(this);
  }
  
function Enemy2(x, y) {
    this.x = x;
    this.y = y;
    this.size = Math.floor(Math.random() * (50 - 20 + 1) + 15);
    this.speed = 1.3;
    this.color = "green";
    this.move = function () {
        if (this.y < 100) {
            this.y += this.speed;
        } else {
            this.x += Math.sin(this.y / 10) * 5;
            this.y += this.speed;
        }
    };
}
function Enemy3(x, y) {
    this.x = x;
    this.y = y;
    this.size = Math.floor(Math.random() * (50 - 20 + 1) + 13);
    this.speed = 2;
    this.color = "blue";
    this.move = function () {
        if (this.y < 100) {
            this.y += this.speed + 15;
        } else {
            this.y += this.speed - 0.5;
        }
    };
}
function Enemy4(x, y) {
    this.x = x;
    this.y = y;
    this.size = Math.floor(Math.random() * (60 - 30 + 1) + 20);
    this.speed = 2.7;
    this.color = "purple";
    this.direction = Math.random() < 0.5 ? -1 : 1; // randomize the initial movement direction
    this.sizeChange = 4; // maximum amount the size can change in each frame
    this.move = function () {
        if (this.x < 0 || this.x > canvas.width - this.size) {
            this.direction = -this.direction; // reverse the direction when hitting the edge of the canvas
        }
        this.x += this.direction * this.speed; // move horizontally
        this.y += this.speed - 1.3; // move vertically
        this.size += (Math.random() * this.sizeChange * 2 - this.sizeChange); // add a random value to the current size
    };
}