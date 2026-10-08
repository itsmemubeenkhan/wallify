$(document).ready(function () {
  $(".sideBarli").removeClass("activeLi");
  $(".privacySideA").addClass("activeLi");

  $("#privacy").on("submit", function (event) {
    event.preventDefault();
    if (user_type == "1") {
      var myEditor = document.querySelector("#privacyContent");
      var htmlContent = myEditor.children[0].innerHTML;

      var formdata = new FormData($("#privacy")[0]);
      formdata.append("content", htmlContent);
      $.ajax({
        url: `${domainUrl}updatePrivacy`,
        type: "POST",
        data: formdata,
        dataType: "json",
        contentType: false,
        cache: false,
        processData: false,
        success: function (response) {
          if (response.status == false) {
            iziToast.show({
              title: "Error",
              message: "Something went wrong",
              color: "red",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/x.svg`,
            });
          } else if (response.status == true) {
            iziToast.show({
              title: "Success",
              message: "Privacy policy Update Successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
          }
        },
        error: (error) => {
          $(".loader").hide();
          console.log(JSON.stringify(error));
        },
      });
    } else {
      $(".loader").hide();
      iziToast.error({
        title: "Error!",
        message: " you are Tester ",
        position: toastPosition,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });
});
