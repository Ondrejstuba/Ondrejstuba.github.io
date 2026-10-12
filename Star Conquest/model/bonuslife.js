var ctx = canvas.getContext("2d");

var bonuslifes = [];

function bonuslife(x, y) {
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
