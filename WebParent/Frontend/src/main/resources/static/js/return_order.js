var returnModal;
var modalTitle;
var fieldNote;
var orderId;
var divReason;
var divMessage;
var firstButton;
var secondButton;

$(document).ready(function () {
    returnModal = $("#returnOrderModal");
    modalTitle = $("#returnOrderModalTitle");
    fieldNote = $("#returnNote");
    divReason = $("#divReason");
    divMessage = $("#divMessage");
    firstButton = $("#firstButton");
    secondButton = $("#secondButton");

    handleReturnOrderLink();
});

function handleReturnOrderLink() {
    $(".linkReturnOrder").on("click", function (e) {
        e.preventDefault();
        showReturnModalDialog($(this));
    });
}

function showReturnModalDialog(link) {
    divMessage.hide();
    divReason.show();
    firstButton.show();
    secondButton.text("Huỷ");
    fieldNote.val("");

    orderId = link.attr("orderId");
    modalTitle.text("Trả Lại Đơn Hàng #" + orderId);
    returnModal.modal("show");
}

function submitReturnOrderForm() {
    reason = $("input[name='returnReason']:checked").val();
    note = fieldNote.val();

    sendReturnOrderRequest(reason, note);

    return false;
}

function sendReturnOrderRequest(reason, note) {
    requestURL = contextPath + "orders/return";
    requestBody = {orderId: orderId, reason: reason, note: note};

    $.ajax({
        type: "POST",
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(requestBody),
        contentType: 'application/json'
    }).done(function (returnResponse) {
        console.log(returnResponse);
        showMessageModal("Yêu cầu trả lại đã được gửi thành công.");
        updateStatusTextAndHideReturnButton(returnResponse.orderId);
    }).fail(function (err) {
        console.log(err);
        showMessageModal(err.responseText);
    });
}

function showMessageModal(message) {
    divReason.hide();
    firstButton.hide();
    secondButton.text("Đóng");
    divMessage.text(message);

    divMessage.show();
}

// xử lý khi gửi yêu cầu trả lại thành công sẽ ẩn nút trả lại và cập nhật trạng thái
function updateStatusTextAndHideReturnButton(orderId) {
    $(".textOrderStatus" + orderId).each(function (index) {
        $(this).text("RETURN_REQUESTED");
    })

    $(".linkReturn" + orderId).each(function (index) {
        $(this).hide();
    })
}