$(document).ready(function () {
    $.ajax({
        url: "https://api.github.com/users"
    }).then(function (data) {
        $('.greeting-id').append(data[0].login);
        //$('.greeting-content').append(data.content);
    });
});