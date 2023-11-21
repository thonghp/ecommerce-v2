var iconNames = {
    'PICKED': 'fa-people-carry',
    'SHIPPING': 'fa-shipping-fast',
    'DELIVERED': 'fa-box-open',
    'RETURNED': 'fa-undo'
};

var confirmText;
var confirmModalDialog;
var yesButton;
var noButton;

$(document).ready(function () {
    confirmText = $("#confirmText");
    confirmModalDialog = $("#confirmModal");
    yesButton = $("#yesButton");
    noButton = $("#noButton");

    $(".linkUpdateStatus").on("click", function (e) {
        e.preventDefault();
        link = $(this);
        showUpdateConfirmModal(link);
    });

    addEventHandlerForYesButton();
});

// Show modal đẻ xác nhận cập nhật trạng thái đơn hàng khi click vào link
function showUpdateConfirmModal(link) {
    noButton.text("Không");
    yesButton.show();

    orderId = link.attr("orderId");
    status = link.attr("status");
    yesButton.attr("href", link.attr("href"));

    confirmText.text("Bạn có chắc chắn muốn cập nhật trạng thái của đơn hàng #" + orderId
        + " sang " + status + "?");

    confirmModalDialog.modal();
}

// Xử lý restful request để cập nhật trạng thái đơn hàng khi click vào nút Yes
function addEventHandlerForYesButton() {
    yesButton.click(function (e) {
        e.preventDefault();
        sendRequestToUpdateOrderStatus($(this));
    });
}

function sendRequestToUpdateOrderStatus(button) {
    requestURL = button.attr("href");

    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        showMessageModal("Đơn hàng được cập nhật thành công");
        updateStatusIconColor(response.orderId, response.status);

        console.log(response);
    }).fail(function (err) {
        showMessageModal("Error updating order status");
    })
}
function showMessageModal(message) {
    noButton.text("Đóng");
    yesButton.hide();
    confirmText.text(message);
}

function updateStatusIconColor(orderId, status) {
    link = $("#link" + status + orderId);
    link.replaceWith("<i class='fas " + iconNames[status] + " fa-2x icon-green'></i>");
}