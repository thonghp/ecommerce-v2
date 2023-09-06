$(document).ready(function () {
    $(".linkMinus").on("click", function (evt) {
        evt.preventDefault();
        decreaseQuantity($(this));
    });

    $(".linkPlus").on("click", function (evt) {
        evt.preventDefault();
        increaseQuantity($(this));
    });

    $(".linkRemove").on("click", function (evt) {
        evt.preventDefault();
        removeProduct($(this));
    });
});

function decreaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;

    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    }
}

function increaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;

    if (newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    }
}

function updateQuantity(productId, quantity) {
    url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (updatedSubtotal) {
        updateSubtotal(updatedSubtotal, productId);
        updateTotal();
    }).fail(function () {
        showErrorModal("Error while updating product quantity.");
    });
}

function updateSubtotal(updatedSubtotal, productId) {
    formattedSubtotal = $.number(updatedSubtotal, 3);
    $("#subtotal" + productId).text(formattedSubtotal);
}

function updateTotal() {
    total = 0.0;
    productCount = 0;

    $(".subtotal").each(function (index, element) {
        productCount++;
        total += parseFloat(element.innerHTML.replaceAll(",", ""));
    });

    if (productCount < 1) {
        showEmptyShoppingCart();
    } else {
        formattedTotal = $.number(total, 3);
        $("#total").text(formattedTotal);
    }
}

function showEmptyShoppingCart() {
    $("#sectionEmptyCartMessage").removeClass("d-none");
    $("#sumTotal").addClass("d-none");
}

function removeProduct(link) {
    url = link.attr("href");

    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        rowNumber = link.attr("rowNumber");
        removeProductHTML(rowNumber);
        updateTotal();

        showModalDialog("Giỏ hàng", response);
    }).fail(function () {
        showErrorModal("Error while removing product.");
    });
}

function removeProductHTML(rowNumber) {
    $("#row" + rowNumber).remove();
}
