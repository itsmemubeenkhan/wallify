$.ajaxSetup({
  headers: {
    "X-CSRF-TOKEN": $('meta[name="csrf-token"]').attr("content"),
  },
});

var user_type = $("#user_type").val();

var toastPosition = "topRight";
var transitionInAction = "fadeInLeft";
var transitionOutAction = "fadeOutRight";

$(document).on("hidden.bs.modal", function () {
  // $("form")[0].reset();
  $("form").trigger("reset");
  $(this).data("bs.modal", null);
  $(".saveButton").removeClass("spinning");
  $(".saveButton").removeClass("disabled");
 
  $(".saveButton1").removeClass("spinning");
  $(".saveButton1").removeClass("disabled");
});

$("form").on("submit", function () {
  $(".saveButton").addClass("spinning");
  $(".saveButton").addClass("disabled");

  $(".saveButton1").addClass("spinning");
  $(".saveButton1").addClass("disabled");
});

$("#settingsForm").on("submit", function () {
  $(".saveButton2").addClass("spinning");
  $(".saveButton2").addClass("disabled");

  setTimeout(function() {
    $(".saveButton2").removeClass("spinning");
    $(".saveButton2").removeClass("disabled");
  }, 1000);
}); 


$("#liveWallpaperModal").on("hidden.bs.modal", function () {
  $("#addLiveWallpaperForm").load(
    location.href + " #addLiveWallpaperForm>*",
    ""
  );
});
$("#wallpaperModal").on("hidden.bs.modal", function () {
  $("#image_preview").html("");
});
$("#categoryModal").on("hidden.bs.modal", function () {
  $("#addCategoryForm").load(location.href + " #addCategoryForm>*", "");
});

if ($(window).width() >= 1199) {
  $("table").removeClass("table-responsive");
}
if ($(window).width() <= 1199) {
  $("table").addClass("table-responsive");
}
if ($(window).width() <= 1450) {
  $("#clientsTable").addClass("table-responsive");
  $("#notStartedProjectTable").addClass("table-responsive");
  $("#paymentTable").addClass("table-responsive");
}
// add class on responsive
$(window).on("resize", function () {
  if ($(window).width() >= 1199) {
    $("table").removeClass("table-responsive");
  }
  if ($(window).width() <= 1199) {
    $("table").addClass("table-responsive");
  }
  if ($(window).width() <= 1450) {
    $("#clientsTable").addClass("table-responsive");
  }
});

$(".select2").select2({
  dropdownParent: $("#addClientProjectModal"),
});

$(".logo-name-small").each(function (index) {
  var characters = $(this).text().split("");
  $this = $(this);
  $this.empty();
  $.each(characters, function (i, el) {
    $this.append('<span class="letter-' + i + '">' + el + "</span>");
  });
});
