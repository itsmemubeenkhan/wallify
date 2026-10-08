$(document).ready(function () {
  const updateWallpaperPreview = (selectSelector, imgSelector, wrapSelector) => {
    const selected = $(`${selectSelector} option:selected`);
    const image = selected.data("image");
    if (image) {
      $(imgSelector).attr("src", image);
      $(wrapSelector).show();
    } else {
      $(imgSelector).attr("src", "");
      $(wrapSelector).hide();
    }
  };

  $("#notificationWallpaper").on("change", function () {
    updateWallpaperPreview(
      "#notificationWallpaper",
      "#notificationWallpaperPreview",
      "#notificationWallpaperPreviewWrap"
    );
  });

  $("#editNotificationWallpaper").on("change", function () {
    updateWallpaperPreview(
      "#editNotificationWallpaper",
      "#editNotificationWallpaperPreview",
      "#editNotificationWallpaperPreviewWrap"
    );
  });

  $(".sideBarli").removeClass("activeLi");
  $(".notificationSideA").addClass("activeLi");

  $("#NotificationTable").dataTable({
    processing: true,
    serverSide: true,
    serverMethod: "post",
    aaSorting: [[0, "desc"]],
    language: {
      paginate: {
        next: '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="9 18 15 12 9 6"></polyline></svg>',
        previous:
          '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="15 18 9 12 15 6"></polyline></svg>',
      },
    },
    columnDefs: [
      {
        targets: [0, 1],
        orderable: false,
      },
    ],
    ajax: {
      url: `${domainUrl}notificationList`,
      data: function (data) {},
      error: (error) => {
        console.log(error);
      },
    },
  });

  $("#addNotificationForm").on("submit", function (event) {
    event.preventDefault();
    if (user_type == 1) {
      var formdata = new FormData($("#addNotificationForm")[0]);
      console.log(formdata);

      $.ajax({
        url: `${domainUrl}sendNotification`,
        type: "POST",
        data: formdata,
        dataType: "json",
        contentType: false,
        cache: false,
        processData: false,
        success: function (response) {
          console.log(response);
          $(".loader").hide();
          $("#addNotificationForm")[0].reset();
          if (response.status == false) {
            iziToast.show({
              title: "Error!",
              message: response.message,
              color: "red",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
          } else {
            iziToast.show({
              title: "Success!",
              message: response.message,
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#notificationModal").modal("hide");
            $("#NotificationTable").DataTable().ajax.reload(null, false);
            $("#notificationModal").load(location.href + " #notificationModal>*","");
            updateWallpaperPreview(
              "#notificationWallpaper",
              "#notificationWallpaperPreview",
              "#notificationWallpaperPreviewWrap"
            );

          }
        },
        error: function (err) {
          console.log(err);
        },
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $("#NotificationTable").on("click", ".edit", function (e) {
    e.preventDefault();

    var id = $(this).attr("rel");
    var title = $(this).data("title");
    var description = $(this).data("description");
    var wallpaperId = $(this).data("wallpaper_id");

    $("#notificationID").val(id);
    $("#editNotificationTitle").val(title);
    $("#editNotificationDesc").val(description);
    $("#editNotificationWallpaper").val(wallpaperId || "");
    $("#editNotificationWallpaper").trigger("change");

    $("#editNotificationModal").modal("show");
  });

  $(document).on("submit", "#editNotificationForm", function (e) {
    e.preventDefault();
    var id = $("#notificationID").val();
    // console.log(id);
    if (user_type == 1) {
      let EditformData = new FormData($("#editNotificationForm")[0]);
      EditformData.append("notificationID", id);
      $.ajax({
        type: "POST",
        url: `${domainUrl}updateNotification`,
        data: EditformData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == false) {
            console.log(response.message);
            iziToast.show({
              title: "Same as Previous",
              message: "Notification Not Update",
              color: "red",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/copy.svg`,
            });
          } else if (response.status == true) {
            iziToast.show({
              title: "Updated",
              message: "Notification Update Succesfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#NotificationTable").DataTable().ajax.reload(null, false);
            $("#editNotificationModal").modal("hide");
          }
        },
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $("#NotificationTable").on("click", ".delete", function (e) {
    e.preventDefault();
    var id = $(this).attr("rel");
    console.log(id);
    if (user_type == 1) {
      swal({
        title: "Are you sure?",
        icon: "error",
        buttons: true,
        dangerMode: true,
        buttons: ["Cancel", "Yes"],
      }).then((deleteValue) => {
        if (deleteValue) {
          if (deleteValue == true) {
            $.ajax({
              type: "POST",
              url: `${domainUrl}deleteNotification`,
              dataType: "json",
              data: {
                notification_id: id,
              },
              success: function (response) {
                if (response.status == false) {
                  console.log(response.message);
                } else if (response.status == true) {
                  iziToast.show({
                    title: "Deleted",
                    message: "Notification Delete Succesfully",
                    color: "green",
                    position: toastPosition,
                    transitionIn: transitionInAction,
                    transitionOut: transitionOutAction,
                    timeout: 3000,
                    animateInside: false,
                    iconUrl: `${domainUrl}asset/img/check-circle.svg`,
                  });
                  $("#NotificationTable").DataTable().ajax.reload(null, false);
                  console.log(response.message);
                }
              },
            });
          }
        }
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $("#NotificationTable").on("click", ".repeat", function (e) {
    e.preventDefault();
    var id = $(this).attr("rel");
    var title = $(this).data("title");
    var description = $(this).data("description");
    var wallpaperId = $(this).data("wallpaper_id");

    var button = $(this);
    button.addClass("spinning");
    button.addClass("disabled");

    if (user_type == 1) {
      let editformData = new FormData();
      editformData.append("title", title);
      editformData.append("description", description);
      editformData.append("wallpaper_id", wallpaperId || "");
      // for (var pair of editformData.entries()) {
      //     console.log(pair[0]+ ', ' + pair[1]);
      // }
      $.ajax({
        type: "POST",
        url: `${domainUrl}repeatNotification`,
        data: editformData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == false) {
            iziToast.show({
              title: "Same as Previous",
              message: response.message,
              color: "red",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/copy.svg`,
            });
          } else if (response.status == true) {
            console.log(response.message);
            iziToast.show({
              title: "Success",
              message: "Notification send successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
             button.removeClass("spinning");
             button.removeClass("disabled");
          }
        },
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });
  
});
