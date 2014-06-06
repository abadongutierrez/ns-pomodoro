self.addEventListener('message', function(e) {
  e.data.play();
}, false);

/*
var audioElement = document.createElement('audio');
audioElement.setAttribute('src', '/audio/Buzz-SoundBible.com-1790490578.mp3');

audioElement.addEventListener("ended", function() {   
});

audioElement.play();
*/