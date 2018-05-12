var authService = 'http://localhost:8080/authservice';
var contactService = 'http://localhost:8080/contactservice';
var galleryService = 'http://localhost:8080/galleryservice';

$('body').append(`
<div id="loaderContainer" style="display: none">
    <div class="loader">Loading...</div>
</div>
`);

var token = localStorage.getItem("token");
var isLoggedIn = token != null ? true : false;

$.ajaxSetup({
    beforeSend: function(xhr) {
        $("#loaderContainer").show();
        if (isLoggedIn) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        }
    },
    complete: function() {
        $("#loaderContainer").hide();
    }
});

function logout() {
    localStorage.removeItem("token");
    window.location.href = 'index.html';
}