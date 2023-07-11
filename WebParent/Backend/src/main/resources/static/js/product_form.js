let extraImagesCount = 0;
dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function () {
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function () {
        dropdownCategories.empty();
        getCategories();
    });

    getCategories();

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

    $("input[name='extraImage']").each(function (index) {
        extraImagesCount++;

        $(this).change(function () {
            showExtraImageThumbnail(this, index);
        });
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

function showExtraImageThumbnail(fileInput, index) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onload = function (e) {
        $("#extraThumbnail" + index).attr("src", e.target.result);
    };

    reader.readAsDataURL(file);

    if (index >= extraImagesCount - 1) {
        addNextExtraImageSection(index + 1);
    }
}

function addNextExtraImageSection(index) {
    htmlExtraImage = `
		<div class="col border m-3 p-2" id="divExtraImage${index}">
			<div id="extraImageHeader${index}"><label>Extra Image #${index + 1}:</label></div>
			<div class="m-2">
				<img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid"
					src="${defaultImageThumbnailSrc}"/>
			</div>
			<div>
				<input type="file" name="extraImage"
					onchange="showExtraImageThumbnail(this, ${index})" 
					accept="image/png, image/jpeg" />
			</div>
		</div>	
	`;

    htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-dark float-right"
			href="javascript:removeExtraImage(${index - 1})" 
			title="Remove this image"></a>
	`;

    $("#divProductImages").append(htmlExtraImage);

    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);

    extraImagesCount++;
}

function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
}





