$(document).ready(function () {
    $("#fileImage").change(function () {
        if (!checkFileSize(this)) {
            return;
        }
        showImageThumbnail(this);
    });
});

function checkFileSize(fileInput) {
    let fileSize = fileInput.files[0].size;
    let mb = 1024 * 1024; // 1mb = 1024 x 1024 kb = 1048576 bytes

    if (fileSize > mb) {
        fileInput.setCustomValidity("Bạn phải chọn ảnh có kích thước nhỏ hơn 1MB!");
        fileInput.reportValidity();

        return false;
    } else {
        fileInput.setCustomValidity("");

        return true;
    }
}

function showImageThumbnail(fileInput) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    };

    reader.readAsDataURL(file);
}