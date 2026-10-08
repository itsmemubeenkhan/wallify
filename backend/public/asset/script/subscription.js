$(document).ready(function () {
  $(".sideBarli").removeClass("activeLi");
  $(".subscriptionSideA").addClass("activeLi");

  $("#monthlySubscriptionForm").on("submit", function (event) {
    event.preventDefault();
    if (user_type == 1) {
      var formdata = new FormData($("#monthlySubscriptionForm")[0]);
      $.ajax({
        url: `${domainUrl}monthlySubscription`,
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
              message: "Subscription not found",
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
              message: "Subscription updated Successfully",
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
        error: function (err) {
          console.log(err);
        },
      });
    } else {
      iziToast.error({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: true,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $("#yearlySubscriptionForm").on("submit", function (event) {
    event.preventDefault();
    if (user_type == 1) {
      var formdata = new FormData($("#yearlySubscriptionForm")[0]);
      $.ajax({
        url: `${domainUrl}yearlySubscription`,
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
              message: "Subscription not found",
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
              message: "Subscription updated Successfully",
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
        error: function (err) {
          console.log(err);
        },
      });
    } else {
      iziToast.error({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: true,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });
});
