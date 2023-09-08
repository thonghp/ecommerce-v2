function loadStatesForCountry() {
    selectedCountry = $("#country option:selected");
    countryId = selectedCountry.val();
    url = contextPath + "settings/list_states_by_country/" + countryId;

    $.get(url, function(responseJSON) {
        dataListState.empty();

        $.each(responseJSON, function(index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dataListState);
        });

    }).fail(function() {
        alert('failed to connect to the server!');
    });
}

function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value !== $("#password").val()) {
        confirmPassword.setCustomValidity("Mật khẩu không trùng khớp!");
    } else {
        confirmPassword.setCustomValidity("");
    }
}