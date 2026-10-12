const canvas = document.getElementById("gameCanvas");
const gameText = document.getElementById("gameText");
const fullscreenBtn = document.getElementById("fullscreen-btn");

// Request full screen mode when the button is clicked
fullscreenBtn.addEventListener("click", () => {
  requestFullscreen();
});

// Request full screen mode when the 'f' key is pressed
document.addEventListener("keydown", event => {
  if (event.key === 'f') {
    requestFullscreen();
  }
});

// Exit full screen mode when the user presses the ESC key or the 'f' key
document.addEventListener("keydown", event => {
  if (event.key === 'Escape' || event.key === 'f') {
    exitFullscreen();
  }
});

document.addEventListener("fullscreenchange", () => {
  if (!document.fullscreenElement) {
    exitFullscreen();
  }
});
document.addEventListener("webkitfullscreenchange", () => {
  if (!document.webkitFullscreenElement) {
    exitFullscreen();
  }
});
document.addEventListener("msfullscreenchange", () => {
  if (!document.msFullscreenElement) {
    exitFullscreen();
  }
});

function requestFullscreen() {
    if (document.documentElement.requestFullscreen) {
      document.documentElement.requestFullscreen();
    } else if (document.documentElement.webkitRequestFullscreen) { /* Safari */
      document.documentElement.webkitRequestFullscreen();
    } else if (document.documentElement.msRequestFullscreen) { /* IE11 */
      document.documentElement.msRequestFullscreen();
    }
  }
  
  function exitFullscreen() {
    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if (document.webkitExitFullscreen) { /* Safari */
      document.webkitExitFullscreen();
    } else if (document.msExitFullscreen) { /* IE11 */
      document.msExitFullscreen();
    }
  }
