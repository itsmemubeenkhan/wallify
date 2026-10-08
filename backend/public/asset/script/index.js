$(document).ready(function () {
  $(".sideBarli").removeClass("activeLi");
  $(".indexSideA").addClass("activeLi");
 

  $("#settingsForm").on("submit", function (event) {
    event.preventDefault();
    var formdata = new FormData($("#settingsForm")[0]);
    console.log(formdata);

    $.ajax({
      url: `${domainUrl}saveSettings`,
      type: "POST",
      data: formdata,
      dataType: "json",
      contentType: false,
      cache: false,
      processData: false,
      success: function (response) {
        console.log(response);
        $(".loader").hide();

        if (response.status == false) {
          iziToast.show({
            title: "Error",
            message: response.message,
            color: "red",
            position: toastPosition,
            transitionIn: transitionInAction,
            transitionOut: transitionOutAction,
            timeout: 3000,
            animateInside: false,
          });
        } else {
          iziToast.show({
            title: "Success",
            message: response.message,
            color: "green",
            position: toastPosition,
            transitionIn: transitionInAction,
            transitionOut: transitionOutAction,
            timeout: 3000,
            animateInside: false, 
          });
        }
      },
      error: function (err) {
        console.log(err);
      },
    });
  });

});
