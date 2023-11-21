$(document).ready(function () {
    $("#productList").on("click", ".linkRemove", function (e) {
        e.preventDefault();

        if (doesOrderHaveOnlyOneProduct()) {
            showWarningModal("Không thể xoá sản phẩm. Đơn hàng phải có ít nhất 1 đơn hàng.");
        } else {
            removeProduct($(this));
            updateOrderAmounts();
        }
    });
});

function removeProduct(link) {
    rowNumber = link.attr("rowNumber");
    $("#row" + rowNumber).remove();

    // xử lý khi xoá hết sản phẩm và xoá đến sản phẩm thêm mới vào khi có 1 sản phẩm sẽ hiển thị thông báo
    $("#blankLine" + rowNumber).remove();
    $(".divCount").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

function doesOrderHaveOnlyOneProduct() {
    productCount = $(".hiddenProductId").length;
    return productCount === 1;
}