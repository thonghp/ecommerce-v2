const message = document.getElementById('hideMessage');
if (message !== null) {
    function hideMessage() {
        message.style.display = 'none';
    }

    setTimeout(hideMessage, 5000);
}