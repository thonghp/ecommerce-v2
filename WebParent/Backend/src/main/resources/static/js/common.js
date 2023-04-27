$(document).ready(function () {
    // logout
    $("#logoutLink").on("click", function (e) {
        e.preventDefault();
        document.logoutForm.submit();
    });

    // hide message
    const message = document.getElementById('hideMessage');
    if (message !== null) {
        function hideMessage() {
            message.style.display = 'none';
        }
        setTimeout(hideMessage, 5000);
    }
});



