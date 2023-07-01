function hideMessageInListView() {
    const message = document.getElementById('hideMessage');
    if (message !== null) {
        function hideMessage() {
            message.style.display = 'none';
        }

        setTimeout(hideMessage, 5000);
    }
}

function showDeleteConfirmModal(link, entityName) {
    let entityId = link.attr("objectId");

    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text("Bạn có chắc muốn xoá " + entityName + " có ID là " + entityId + " này không ?");
    $("#confirmModal").modal();
}

function clearFilter() {
    window.location = moduleURL;
}
