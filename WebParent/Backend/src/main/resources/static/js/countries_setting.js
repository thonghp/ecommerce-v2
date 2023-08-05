let buttonLoad;
let dropDownCountry;
let buttonAddCountry;
let buttonUpdateCountry;
let buttonDeleteCountry;
let labelCountryName;
let fieldCountryName;
let fieldCountryCode;

$(document).ready(function () {
    buttonLoad = $("#buttonLoadCountries");
    dropDownCountry = $("#dropDownCountries");
    buttonAddCountry = $("#buttonAddCountry");
    buttonUpdateCountry = $("#buttonUpdateCountry");
    buttonDeleteCountry = $("#buttonDeleteCountry");
    labelCountryName = $("#labelCountryName");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");

    buttonLoad.click(function () {
        loadCountries();
    });

    dropDownCountry.on("change", function () {
        changeFormStateToSelectedCountry();
    });

    buttonAddCountry.click(function () {
        if (buttonAddCountry.val() === "Lưu") {
            addCountry();
        } else {
            changeFormStateToNew();
        }
    });

    buttonUpdateCountry.click(function () {
        updateCountry();
    });

    buttonDeleteCountry.click(function () {
        deleteCountry();
    });
});

function deleteCountry() {
    optionValue = dropDownCountry.val();
    countryId = optionValue.split("-")[0];

    url = contextPath + "countries/delete/" + countryId;

    $.get(url, function () {
        $("#dropDownCountries option[value='" + optionValue + "']").remove();
        changeFormStateToNew();
    }).done(function () {
        showToastMessage("Quốc gia đã bị xoá");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function updateCountry() {
    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();

    countryId = dropDownCountry.val().split("-")[0];

    jsonData = {id: countryId, name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        $("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
        $("#dropDownCountries option:selected").text(countryName);
        showToastMessage("Quốc gia đã được cập nhật");

        changeFormStateToNew();
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function addCountry() {
    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();
    jsonData = {name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        selectNewlyAddedCountry(countryId, countryCode, countryName);
        showToastMessage("The new country has been added");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });

}

function selectNewlyAddedCountry(countryId, countryCode, countryName) {
    optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry);

    $("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

function changeFormStateToNew() {
    buttonAddCountry.val("Lưu");
    labelCountryName.text("Tên Quốc Gia:");

    buttonUpdateCountry.prop("disabled", true);
    buttonDeleteCountry.prop("disabled", true);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "Lưu");
    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected Country:");

    selectedCountryName = $("#dropDownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    countryCode = dropDownCountry.val().split("-")[1];
    fieldCountryCode.val(countryCode);

}

function loadCountries() {
    url = contextPath + "countries/list";
    $.get(url, function (responseJSON) {
        dropDownCountry.empty();

        $.each(responseJSON, function (index, country) {
            optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
        });

    }).done(function () {
        buttonLoad.val("Làm Mới Danh Sách Quốc Gia");
        showToastMessage("Tất cả các quốc gia đã được tải");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}