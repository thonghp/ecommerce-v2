dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function () {
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function () {
        dropdownCategories.empty();
        getCategories();
    });

    getCategoriesForNewForm();

    // xử lý kiểm tra tên sản phẩm
    let nameInput = $('input[name="name"]');
    let nameStatus = $('#name-status');
    let url = "[[@{/products/check_unique}]]";

    nameInput.on('blur', function () {
        let name = $(this).val();
        let csrfToken = $('input[name="_csrf"]').val();
        let productId = $('#id').val();
        if (name !== '') {
            $.ajax({
                url: url,
                type: 'POST',
                data: {id: productId, name: name, _csrf: csrfToken},
                success: function (data) {
                    if (data === 'OK') {
                        nameStatus.html('');
                    } else if (data === 'Duplicated') {
                        nameStatus.html('<span style="color:red">Tên sản phẩm đã tồn tại</span>');
                    }
                }
            });
        } else {
            nameStatus.empty();
        }
    });
});

function getCategories() {
    brandId = dropdownBrands.val();
    url = brandModuleURL + "/" + brandId + "/categories";

    $.get(url, function (responseJson) {
        $.each(responseJson, function (index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
        });
    });
}

function getCategoriesForNewForm() {
    catIdField = $("#categoryId");
    editMode = false;

    if (catIdField.length) {
        editMode = true;
    }

    if (!editMode) getCategories();
}